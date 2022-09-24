FROM maven:3.8.6-jdk-11

WORKDIR /app
RUN git clone https://github.com/kuisathaverat/ecommerce-antifraud-demo.git
RUN cd ecommerce-antifraud-demo \
    && PUBLISH_DOCKER=false mvn -Dsnyk.skip=true clean compile
WORKDIR /root
RUN rm -fr /app