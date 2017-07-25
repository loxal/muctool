FROM openjdk:8-jre-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ADD build/install/* .

EXPOSE 8300

CMD /bin/muctool
