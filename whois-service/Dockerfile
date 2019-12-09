FROM openjdk:13-buster AS service

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

#RUN apt-get update && \
#    apt-get install curl iputils-ping -y

RUN groupadd -g 1200 svc && \
    useradd --create-home --system -u 1200 -g svc svc
USER svc

WORKDIR /home/svc
COPY build/libs/*-all.jar .

COPY static static
RUN mkdir -p src/main/resources
RUN mkdir logs
COPY src/main/resources/*.mmdb src/main/resources/

VOLUME /logs /data
EXPOSE 1180

CMD ["java", "-jar", "-Xms256m", "-Xmx256m", "whois-service-1.0.0-all.jar"]
