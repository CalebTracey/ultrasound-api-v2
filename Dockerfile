FROM maven:3.8-openjdk-11 AS build
COPY src ./app/src
COPY pom.xml ./app
RUN mvn -f ./app/pom.xml clean package

FROM openjdk:11
VOLUME /tmp
COPY --from=build app/target/app-0.0.1-SNAPSHOT.jar ./app.jar
RUN sh -c 'touch /app.jar'
ENTRYPOINT ["java", "-Dserver.port=6080", "-Dspring.profiles.active=dev", "-jar", "/app.jar", "--host=0.0.0.0"]
