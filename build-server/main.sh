#!/bin/bash

export GIT_REPOSITORY_URL="$GIT_REPOSITORY_URL"

# Clone the repository
git clone "$GIT_REPOSITORY_URL" /home/app/output

# Build the project
mvn clean install

# Run the project
java -jar target/build-server-0.0.1-SNAPSHOT.jar
