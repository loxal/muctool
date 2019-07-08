FROM ubuntu:19.10 AS service

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

RUN apt-get update && \
    apt-get install -y openjdk-13-jre-headless

RUN groupadd -g 1200 svc && \
    useradd --create-home --system -u 1200 -g svc svc
USER svc

WORKDIR $HOME
COPY build/libs/*-all.jar .

COPY static static
RUN mkdir -p src/main/resources
RUN mkdir logs
COPY src/main/resources/*.mmdb src/main/resources/

VOLUME /logs /data
EXPOSE 1180
HEALTHCHECK CMD curl --fail http://localhost:1180 || exit 1

CMD ["java", "-jar", "-Xms256m", "-Xmx256m", "service-1.0.0-all.jar"]
