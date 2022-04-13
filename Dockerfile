#### Stage 1: Build the application
FROM maven:3-jdk-11 as maven
WORKDIR /app
# copy the Project Object Model file
COPY pom.xml pom.xml

# fetch all dependencies
RUN mvn dependency:go-offline -B

# copy your other files
COPY src src/

# build for release
# NOTE: my-project-* should be replaced with the proper prefix
RUN mvn package && cp target/app-*.jar app.jar

# smaller, final base image
FROM openjdk:11
COPY --from=maven /root/.m2 /root/.m2
WORKDIR /app
COPY --from=maven /app/app.jar app.jar
CMD ["java", "-jar", "/app/app.jar"]
#
#ARG CI_USER
#ARG CI_TOKEN
#
## Copy maven executable to the image
#COPY mvnw .
#COPY .mvn .mvn
#
## Copy the pom.xml file
#COPY pom.xml .
#
#WORKDIR /app
#RUN git config --global http.sslVerify false
#RUN git config --global url."https://${CI_USER}:${CI_TOKEN}@gitlab.com".insteadOf "https://gitlab.com"
## Build all the dependencies in preparation to go offline.
## This is a separate step so the dependencies will be cached unless
## the pom.xml file has changed.
#RUN ./mvnw dependency:go-offline -B
#
## Copy the project source
#COPY src src
#
## Package the application
#RUN ./mvnw package -DskipTests
#RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
#
##### Stage 2: A minimal docker image with command to run the app
#FROM openjdk:11-alpine
#
#ARG DEPENDENCY=/app/target/dependency
#
## Copy project dependencies from the build stage
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#
#ENTRYPOINT ["java", "-Dserver.port=8080","-classpath","app:app/lib/*","com.ultrasound.app.AppApplication"]
#
## docker-compose pull
## docker-compose up --force-recreate --build -d
## heroku container:push web --app employee-dashboard