# Using Firebase and App Engine Managed VM as mobile app backend

This repository contains backend sample code for "[Using Firebase and App Engine Managed VM as mobile app backend](https://cloud.google.com/solutions/mobile/mobile-app-backend-on-cloud-platform#firebase-managed-vms)" paper.

## Deployment Requirements
Following Google APIs are needed to be enabled from Google Developers Console.
* Google App Engine
* Google Compute Engine
* Sign up with [Firebase](https://www.firebase.com/) and obtain Firebase URL.

Apache Maven is required in build environment. 


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
- Login to Firebase console and add a new Firebase token from "Secrets" tab for custom authentication.

- Replace following initial parameters in "<WEB-INF>/web.xml". 

```xml
<init-param>
	<param-name>endpoint</param-name>
	<param-value>"Firebase URL"</param-value>
</init-param>

...

<init-param>
	<param-name>token</param-name>
	<param-value>"Firebase custom token"</param-value>
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
 
- For App Engine Managed VM, replace "http://localhost:8080" to the assigned URL accordingly.


