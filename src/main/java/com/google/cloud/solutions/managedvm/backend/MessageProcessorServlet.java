/**
# Copyright Google Inc. 2016
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
**/

package com.google.cloud.solutions.managedvm.backend;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import com.google.cloud.solutions.managedvm.common.*;

import java.io.IOException;
import java.lang.Override;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/*
 * MessageProcessorServlet is responsible for receiving user event logs from Android clients and
 * print logs whenever requested.
 *  
 * @author teppeiy
 */
public class MessageProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 8126789192972477663L;

	// Firebase keys shared with client applications
	private static final String IBX = "inbox";
	private static final String CH = "channels";
	private static final String REQLOG = "requestLogger";

	private static Logger localLog = Logger.getLogger("com.google.cloud.solutions.managedvm.backend.MessageProcessorServlet");
	private Firebase firebase;

	private String channels;
	private String inbox;
	
	// If number of messages in each channel or user events exceeds "purgeLogs", it will be purged.
	private int purgeLogs;  
	// Purger is invoked with every "purgeInterval".
	private int purgeInterval;

	private ConcurrentLinkedQueue<LogEntry> logs;

	private MessagePurger purger;
	
	@Override
	public void init(ServletConfig config) {
		channels = config.getInitParameter("channels");
		firebase = new Firebase(config.getInitParameter("endpoint"));
		purgeLogs = Integer.parseInt(config.getInitParameter("purgeLogs"));
		purgeInterval = Integer.parseInt(config.getInitParameter("purgeInterval"));
		String token = config.getInitParameter("token");

		authenticate(token);

		logs = new ConcurrentLinkedQueue<LogEntry>();
		generateUniqueId();

		/*
		 * Receive a request from Android client and reply back its inbox ID.
		 * Use of transaction ensures that only single Servlet instance replies back to the client
		 * so the client knows to which Servlet instance to send consecutive user event logs.
		 */
		firebase.child(REQLOG).addChildEventListener(new ChildEventListener() {
			public void onChildAdded(DataSnapshot snapshot, String prevKey) {
				firebase.child(IBX + "/" + snapshot.getValue()).runTransaction(new Transaction.Handler() {
					public Transaction.Result doTransaction(MutableData currentData) {
						// The only first Servlet instance will write its ID to the client inbox.
						if (currentData.getValue() == null) {
							currentData.setValue(inbox);
						}
						return Transaction.success(currentData);
					}

					public void onComplete(FirebaseError error, boolean committed, DataSnapshot currentData) {
					}
				});
				firebase.child(REQLOG).removeValue();
			}

			public void onCancelled(FirebaseError error) {
				localLog.warning(error.getDetails());
			}

			public void onChildChanged(DataSnapshot arg0, String arg1) {}

			public void onChildMoved(DataSnapshot arg0, String arg1) {}

			public void onChildRemoved(DataSnapshot arg0) {}
		});

		purger = new MessagePurger(firebase, purgeInterval, purgeLogs);
		String[] channelArray = channels.split(",");
		for(int i = 0; i < channelArray.length; i++) {
			purger.registerBranch(CH + "/" + channelArray[i]);
		}
		initLogger();
		purger.setPriority(Thread.MIN_PRIORITY);
		purger.start();
	}

	private void authenticate(String token) {
		firebase.authWithCustomToken(token, new Firebase.AuthResultHandler() {
			public void onAuthenticationError(FirebaseError error) {
				localLog.info(error.getDetails());
			}

			public void onAuthenticated(AuthData arg0) {
				localLog.info("Authenticated : " + arg0.getToken());
			}
		});
	}

	/*
	 * To generate a unique ID for each Servlet instance and clients push messages to "/inbox/<inbox>".
	 */
	private void generateUniqueId() {
		Random rand = new Random();
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < 16; i++) {
			buf.append(Integer.toString(rand.nextInt(10)));
		}
		inbox = buf.toString();
	}

	/*
	 * Initialize user event logger. This is just a sample implementation to demonstrate receiving updates. 
	 * The production application should transform, filter or load to other data store such as BigQuery.
	 */
	private void initLogger() {
		String loggerKey = IBX + "/" + inbox + "/logs";
		purger.registerBranch(loggerKey);
		firebase.child(loggerKey).addChildEventListener(new ChildEventListener() {
			public void onChildAdded(DataSnapshot snapshot, String prevKey) {
				if (snapshot.exists()) {
					LogEntry entry = snapshot.getValue(LogEntry.class);
					logs.add(entry);
				}
			}

			public void onCancelled(FirebaseError error) {
					localLog.warning(error.getDetails());
				}

			public void onChildChanged(DataSnapshot arg0, String arg1) {}

			public void onChildMoved(DataSnapshot arg0, String arg1) {}

			public void onChildRemoved(DataSnapshot arg0) {}
		});
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doPost(req, resp);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * Just printing all user event logs stored in memory of this Servlet instance.
	 */
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("Inbox : " + inbox);

		for(Iterator<LogEntry> iter = logs.iterator(); iter.hasNext();) {
			LogEntry entry = (LogEntry)iter.next();
			resp.getWriter().println(new Date((Long)entry.getTime().get("date")).toString() + "(id=" + entry.getTag() + ")" +  " : " + entry.getLog());
		}
	}

	@Override
	public void destroy() {
		purger.interrupt();
		firebase.child(IBX + "/" + inbox).removeValue();
	}
}