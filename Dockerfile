# Prepare environment
FROM arm64v8/alpine:3.14.0
RUN apk add --no-cache openjdk11 git

# Download source code
RUN git clone https://github.com/darbyjack/Holovid-Server.git /app
WORKDIR /app

# Build the source into a binary
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

# Package the application
CMD /bin/sh -c "java -Xms2G -Xmx2G -jar build/libs/HolovidServer-1.0-SNAPSHOT.jar"
