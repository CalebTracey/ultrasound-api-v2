FROM maven:3.8-openjdk-11 AS build
COPY src ./app/src
COPY pom.xml ./app
RUN mvn -f ./app/pom.xml clean package

FROM alpine:3.15
RUN apk add openjdk11
RUN apk add --no-cache bash
RUN ln -sf /bin/bash /bin/sh
RUN addgroup -S appgroup && adduser -S myuser -G appgroup
VOLUME /tmp
COPY --from=build app/target/app-0.0.1-SNAPSHOT.jar ./app.jar
RUN touch /app.jar
USER myuser
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Xmx300m", "-Xss512k", "-XX:CICompilerCount=2", "-Dserver.port=$PORT", "-Dspring.profiles.active=prod", "-jar", "/app.jar"]
