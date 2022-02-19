FROM ubuntu:20.04

# do not interactive for install
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update
RUN apt-get install -y apache2 # Install Apache web server

# Install OpenJDK-11
RUN apt-get update && \
    apt-get install -y openjdk-11-jdk && \
    apt-get install -y ant && \
    apt-get clean;

# open HTTP POST
EXPOSE 80
# apache daemon
CMD ["apachectl", "-D", "FOREGROUND"]
