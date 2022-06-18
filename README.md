# ultrasound-api-v2
<!-- ![](ultrasound-app2.gif) -->

## Overview
This app was developed for a fellowship program through my local hospital. It provides a way to easily navagate 3000+ .mp4 files and allows editing and restructuring of data through a simple user interface. Files are parsed by name and the resulting data is modeled and stored in a mongo database.

The backend was built with Spring Boot to interact with the Mongo database. Authentication is done through Spring Security + JWT for role-based routing and privelages.

See the repository for the frontend here: 
[Frontend code](https://github.com/CalebTracey/ultrasound-ui-v2)
<br />

## Usage
Currently, you'll need to create and populate an S3 Bucket with .mp4 files and connect it to the backend through the AWS CLI.

Next, run the docker-compose file in the mongodb directory and create a collection called "roles" and add documents "ROLE_USER" and "ROLE_ADMIN" as shown below.

Uploading [this file](./roles) through Mongo Express/Compass should do the trick.
<img src=./document-example.png />
<br />
<br />
If you want to use the admin privileges, uncomment the @CommandlineRunner method in the application's main method and then hit run. You'll then be able to login with the same credentials.
<br />
<br />
Also, make sure your application.properties file is configured as such:
<img src=./application-properties.png />

1. Fork/clone this repo

2. Download [Docker Desktop App](https://www.docker.com/products/docker-desktop)

3. Run the [Docker Compose](./mongodb/docker-compose.yaml) file found in the mongodb directory.

4. Access the Mongo Express UI at: [localhost:8081](http://localhost:8081/)

5. Create the "roles" collection as mentioned above.

6. To populate the database, make this POST request with [Postman](https://www.postman.com/): http://localhost:6080/api/S3/update


## Available Scripts
If you would rather skip the Docker proccess for the front/backend, follow the steps below. 

You will still need to run the docker-compose file to get mongodb running locally. Just comment out the frontend/backend sections in the file.
<br />
<br />
`mvn clean install`
<br />
<br />
and:
<br />
<br />
`mvn spring-boot:run`
<br />
<br />

