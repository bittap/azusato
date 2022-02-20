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

# maven install
RUN apt-get update
RUN apt-get install -y maven
RUN mvn -version

# install curl
RUN apt update && apt upgrade
RUN apt install curl
RUN curl --version

# install wget
RUN apt-get update && apt-get install wget && wget --version

# install jenkins
# https://www.digitalocean.com/community/tutorials/how-to-install-jenkins-on-ubuntu-20-04
RUN wget -q -O - https://pkg.jenkins.io/debian-stable/jenkins.io.key | apt-key add -
RUN curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io.key | tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
RUN sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
RUN apt-get update
RUN apt-get install jenkins
# enable the Jenkins service to start at boot
RUN systemctl enable jenkins
# start the Jenkins service
RUN systemctl start jenkins
# check the status of the Jenkins service
RUN systemctl status jenkins
