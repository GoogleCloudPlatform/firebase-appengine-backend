# Build a mobile app using  Firebase and App Engine Flexible Environment

This repository contains backend sample code for "[Build a mobile app using  Firebase and App Engine Flexible Environment backend](https://cloud.google.com/solutions/mobile/mobile-app-backend-on-cloud-platform#firebase-managed-vms)" paper.


## Deployment Requirements
Following Google APIs are needed to be enabled from Google Developers Console.
- Google App Engine
- Google Compute Engine
- Sign up with [Firebase](https://www.firebase.com/) and obtain Firebase URL.

Apache Maven is required in build environment. Firebase is a Google product, independent from Google Cloud Platform.


## Google Cloud SDK setup
Get credentials and configure properties. This is optional and you may skip if it's already being set up.

- Get credentials for the tools:
```bash
% gcloud auth login
```

- Set project:
```bash
% gcloud configure set project <Project ID>
```


## Configuration
- Login to Firebase console and check "Enable Google Authentication" from "Login & Auth" tab.

- Add a Google Client ID. Detailed instructions are [here](https://www.firebase.com/docs/web/guide/login/google.html).

- Add a new Firebase Secret from "Secrets" tab for custom authentication.

- Replace following initial parameters in "<WEB-INF>/web.xml".

```xml
<init-param>
	<param-name>endpoint</param-name>
	<param-value>"Firebase URL"</param-value>
</init-param>

...

<init-param>
	<param-name>secret</param-name>
	<param-value>"Firebase Secret"</param-value>
</init-param>
```

- Go back to Firebase console and add "Authorized Domains for OAuth Redirects"


## Build and deploy
- To build and run the backend module locally:
```bash
% mvn clean gcloud:run
```

- To deploy the backend module to App Engine:
```bash
% mvn gcloud:deploy
```


## View user event logs
- Run Android client apps and access following URLs to view user event logs.

- For local build environment :
```bash
http://localhost:8080/printLogs
```

- For App Engine Flexible Environment, replace "http://localhost:8080" to the assigned URL accordingly.


## License
 Copyright 2016 Google Inc. All Rights Reserved.

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
      http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS-IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the specific language governing permissions and limitations under the License.

This is not an official Google product.
