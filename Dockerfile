# Prepare environment
FROM openjdk:16

# Setup the environment
RUN mkdir -p /app
COPY build/libs/HolovidServer-1.0-SNAPSHOT.jar /app
WORKDIR /app

# Start up the process
CMD java -Xms2G -Xmx2G -jar /app/HolovidServer-1.0-SNAPSHOT.jar
