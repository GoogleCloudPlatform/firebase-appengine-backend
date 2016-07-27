# Build a Mobile App Using  Firebase and App Engine Flexible Environment

This repository contains Android client sample code for the "[Build a Mobile App Using Firebase and App Engine Flexible Environment](https://cloud.google.com/solutions/mobile/mobile-firebase-app-engine-flexible)" solution. Sample client code can be found
[here](https://github.com/GoogleCloudPlatform/firebase-android-client).

## Deployment Requirements

- Enable the following services in the Cloud console: https://console.cloud.google.com
  - Google App Engine
  - Google Compute Engine
- Sign up for [Firebase](https://firebase.google.com/) and create a new project in the [Firebase console](htps://console.firebase.google.com/).

Apache Maven is required in build environment. Firebase is a Google product, independent from Google Cloud Platform.

## Google Cloud SDK setup
Get credentials and configure properties. This step is optional and you may
skip it if it's already been set up.

- Get credentials for the tools:
```bash
% gcloud auth login
```

- Set project:
```bash
% gcloud configure set project <Project ID>
```


## Configuration
- Login to Firebase console and enable "Google" sign in provider from "SIGN IN METHOD" tab in "Auth" menu.

- From "Settings", click "Permissions" and move to "IAM & Admin" menu, then
create a new service account and download JSON file
([more details](https://firebase.google.com/docs/server/setup#add_firebase_to_your_app)).
Note that this JSON file is different from the "google-services.json"
file used in the Android client.

- Copy the JSON file under "WEB-INF" source directory.

- Replace following initial parameters in "WEB-INF/web.xml".

```xml
<init-param>
	<param-name>credential</param-name>
	<param-value>/WEB-INF/JSON_FILE_NAME</param-value>
</init-param>
<init-param>
	<param-name>databaseUrl</param-name>
	<param-value>FIREBASE_URL</param-value>
</init-param>
```


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

 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at
      http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS-IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.

This is not an official Google product.
