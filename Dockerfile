FROM jenkins/jenkins:lts
USER root
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
RUN echo 2.0 > /usr/share/jenkins/ref/jenkins.install.UpgradeWizard.state

#USUARIO DE JENKINS
ENV JENKINS_USER ricardcutzh
ENV JENKINS_PASS jenkins

#INSTALANDO DOCKER DENTRO DE JENKINS
USER root
RUN apt-get update && \
apt-get -y install apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common && \
curl -fsSL https://download.docker.com/linux/$(. /etc/os-release; echo "$ID")/gpg > /tmp/dkey; apt-key add /tmp/dkey && \
add-apt-repository \
    "deb [arch=amd64] https://download.docker.com/linux/$(. /etc/os-release; echo "$ID") \
    $(lsb_release -cs) \
    stable" && \
apt-get update && \
apt-get -y install docker-ce
RUN apt-get install -y docker-ce
RUN usermod -a -G docker jenkins
USER jenkins

#AWS CLI
USER root
RUN apt-get update
RUN apt install python3-pip -y
RUN pip3 install awscli --upgrade
USER jenkins

#INSTALANDO LOS PLUGINS
COPY ./Plugins/plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

#AGREGANDO UN JOB
COPY ./INIT/example-job.groovy /usr/share/jenkins/ref/init.groovy.d/

COPY ./DSL/*.groovy /usr/share/jenkins/ref/jobs/primer_job/workspace/

COPY ./example ./usr/share/jenkins/ref/jobs/Docker/workspace/

# AWS CREDENCIALES
ARG ACCESS_KEY  

ARG SECRET_KEY

RUN echo ${ACCESS_KEY}

RUN echo ${SECRET_KEY}

RUN aws configure set aws_access_key_id ${ACCESS_KEY}

RUN aws configure set aws_secret_access_key ${SECRET_KEY}

ENV A_KEY=akey 

ENV S_KEY=skey

RUN echo 'Instalando JENKINS'
