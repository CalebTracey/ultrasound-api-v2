##### Stage 1: Build the application
#FROM openjdk:11 as build
#
#WORKDIR /app
## Copy maven executable to the image
#COPY mvnw .
#COPY .mvn .mvn
## Copy the pom.xml file
#COPY pom.xml .
## Build all the dependencies in preparation to go offline.
## This is a separate step so the dependencies will be cached unless
## the pom.xml file has changed.
#RUN ./mvnw dependency:go-offline -B
## Copy the project source
#COPY src src
## Package the application
#RUN ./mvnw package -DskipTests
#RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#
##### Stage 2: A minimal docker image with command to run the app
#FROM openjdk:11
#ARG DEPENDENCY=/app/target/dependency
## Copy project dependencies from the build stage
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#
#ENTRYPOINT ["java", "-Dserver.port=8080","-classpath","app:app/lib/*","com.ultrasound.app.AppApplication"]
FROM maven:3.8-openjdk-11 AS build
COPY src ./app/src
COPY pom.xml ./app
RUN mvn -f ./app/pom.xml clean package

FROM openjdk:11
VOLUME /tmp
COPY --from=build app/target/app-0.0.1-SNAPSHOT.jar ./app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
