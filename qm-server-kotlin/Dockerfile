# With this file we create a Docker image that contains the application
FROM gradle:8-jdk17 AS build
# We create a directory for the application and copy the build.gradle file
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# We create a new image with the application
FROM openjdk:17-jdk-slim-buster
EXPOSE 3993:8080
# Directory to store the application
RUN mkdir /app

# Copy the jar file to the container
COPY --from=build /home/gradle/src/build/libs/qm-server-kotlin-all.jar /app/qm-server-kotlin-all.jar
# Run the application
ENTRYPOINT ["java","-jar","/app/qm-server-kotlin-all.jar"]