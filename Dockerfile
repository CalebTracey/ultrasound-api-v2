FROM maven:3.8-openjdk-11 AS build
COPY src ./app/src
COPY pom.xml ./app
RUN mvn -f ./app/pom.xml clean package

FROM openjdk:11
RUN useradd -ms /bin/bash myuser
RUN apk add --no-cache bash
USER myuser
VOLUME /tmp
COPY --from=build app/target/app-0.0.1-SNAPSHOT.jar ./app.jar
#RUN -c /bin/bash 'touch /app.jar'
ENTRYPOINT ["java", "-Dserver.port=$PORT", "-Dspring.profiles.active=prod", "-jar", "/app.jar", "--host=0.0.0.0"]
