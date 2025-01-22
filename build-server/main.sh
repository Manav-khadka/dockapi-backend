#!/bin/bash

export GIT_REPOSITORY_URL="$GIT_REPOSITORY_URL"

echo "Cloning repository: $GIT_REPOSITORY_URL"

# Clone the repository
git clone "$GIT_REPOSITORY_URL" /home/app/output


exec java -jar build-server-1.0-SNAPSHOT.jar
