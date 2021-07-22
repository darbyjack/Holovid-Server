# Prepare environment
FROM alpine:3.13
RUN apk add openjdk16

# Download source code
RUN git clone https://github.com/darbyjack/Holovid-Server.git /app
WORKDIR /app

# Build the source into a binary
RUN ./gradlew clean build

# Package the application
CMD /bin/sh -c "java -Xms64M -Xmx64M -jar build/libs/HolovidServer-1.0-SNAPSHOT.jar"
