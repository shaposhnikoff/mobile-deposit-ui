FROM java:8

RUN curl -O http://repo.spring.io/release/org/springframework/boot/spring-boot-cli/1.2.4.RELEASE/spring-boot-cli-1.2.4.RELEASE-bin.tar.gz 

RUN tar -xvf *.gz

RUN rm -rf *.gz

ENV SPRING_HOME /spring-1.2.4.RELEASE

ENV PATH $SPRING_HOME/bin:$PATH

ADD app.groovy app.groovy
ADD public /public

RUN spring jar app.jar app.groovy

CMD java -jar app.jar