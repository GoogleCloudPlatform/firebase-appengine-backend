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

package com.google.cloud.solutions.managedvm.common;

import com.firebase.client.ServerValue;

import java.util.HashMap;

/*
 * An instance of LogEntry represents a user event log, such as signin/out and switching a channel. 
 */
public class LogEntry {
    private String tag;
    private String log;
    private HashMap<String, Object> time;

    public LogEntry() {}

    public LogEntry(String tag, String log) {
        this.tag = tag;
        this.log = log;
        time = new HashMap<String, Object>();
        time.put("date", ServerValue.TIMESTAMP);
    }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    public String getLog() { return log; }
    public void setLog(String log) { this.log = log; }

    public HashMap<String, Object> getTime() { return time; }
}
