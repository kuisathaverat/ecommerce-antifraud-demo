FROM maven:3.8.6-jdk-11

WORKDIR /home/jenkins
RUN useradd -m -d /home/jenkins -s /bin/bash jenkins
RUN chown -R jenkins:jenkins /home/jenkins
USER jenkins
RUN git clone https://github.com/kuisathaverat/ecommerce-antifraud-demo.git
RUN cd ecommerce-antifraud-demo \
    && mvn dependency:go-offline
RUN rm -fr /home/jenkins/ecommerce-antifraud-demo