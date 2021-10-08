# ultrasound-app
<!-- ![](ultrasound-app2.gif) -->

## Overview
This app was developed for a fellowship program through my local hospital. It provides an intuative and organized user interface for navagating 3000+ .mp4 files stored in an S3 bucket. The backend parses files by name and appends them to the database on the initial upload or when a file is added.

Created with React/TypeScript + Spring Boot + MongoDB. Authentication through Spring Security + JWT for role-based routing and privelages.

[Frontend code](https://github.com/CalebTracey/ultrasound-app-fullstack/tree/main/src/main/react-client)
<br />
[Backend code](https://github.com/CalebTracey/ultrasound-app-fullstack/tree/main/src/main/java/com/ultrasound/app)

## Usage
If you would like to try out the app for yourself, follow the steps below. 

You will need to create a collection in mongodb called "roles" and add documents "ROLE_USER" and optionally "ROLE_ADMIN" as shown below.

Uploading [this file](./roles) through Mongo Express/Compass should do the trick.
<img src=./document-example.png />
<br />
<br />
If you want to use the admin privelages, uncomment the @CommandlineRunner method in the application's main method and then hit run. You'll then be able to login with the same credentials.
<br />
<br />
Also, make sure your application.properties file is configured as such:
<img src=./app.properties-example-local.png />

1. Fork/clone this repo

2. Download [Docker Desktop App](https://www.docker.com/products/docker-desktop)

3. Run the [Docker Compose](./docker-compose.yaml) file found in the root directory.

4. Access the Mongo Express UI at: [localhost:8081](http://localhost:8081/)

5. Create the "roles" collection as mentioned above.

6. To populate the database,  make this POST request with [Postman](https://www.postman.com/): http://localhost:8080/api/S3/update

7. Access the app on [localhost:80](http://localhost:80/) and register!

## Available Scripts
If you would rather skip the Docker proccess for the front/backend, follow the steps below. 

You will still need to run the docker-compose file to get mongodb running locally. Just comment out the frontend/backend sections in the file.
<br />
<br />
`mvn spring-boot:run`
<br />
<br />
for the client, navigate to "react-client" and run:
<br />
<br />
`yarn start`
<br />
<br />
Then use the app locally on [localhost:3000](http://localhost:3000/)
<br />
<br />
