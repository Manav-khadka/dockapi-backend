FROM ubuntu:focal

RUN apt-get update && apt-get install -y openjdk-17-jdk
RUN apt-get upgrade -y
RUN apt-get install -y maven

RUN apt-get install -y git

WORKDIR /home/app

COPY main.sh main.sh

ENTRYPOINT ["/home/app/main.sh"]