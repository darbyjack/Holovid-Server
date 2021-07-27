# Prepare environment
FROM adoptopenjdk/openjdk16:alpine-slim

# Build the source into a binary
COPY build/libs/HolovidServer-1.0-SNAPSHOT.jar /app
WORKDIR /app

# Package the application
CMD java -Xms2G -Xmx2G -jar build/libs/HolovidServer-1.0-SNAPSHOT.jar
