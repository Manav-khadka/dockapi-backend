FROM ubuntu:focal

RUN apt-get update && apt-get install -y openjdk-17-jdk
RUN apt-get upgrade -y
RUN apt-get install -y maven

RUN apt-get install -y curl
RUN apt-get install -y git

RUN curl -sL https://deb.nodesource.com/setup_20.x | bash -
RUN apt-get install -y nodejs

WORKDIR /home/app

COPY main.sh main.sh
COPY target/build-server-1.0-SNAPSHOT.jar build-server-1.0-SNAPSHOT.jar

RUN chmod +x main.sh
RUN chmod +x build-server-1.0-SNAPSHOT.jar

ENTRYPOINT ["/home/app/main.sh"]