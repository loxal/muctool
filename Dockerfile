FROM jetty:9-jre8-alpine

MAINTAINER Alexander Orlov <alexander.orlov@loxal.net>

ADD build/libs/ROOT.war webapps
ENV appdirect_oAuth_consumer_key appdirect_oAuth_consumer_key
ENV appdirect_oAuth_consumer_secret $appdirect_oAuth_consumer_secret

EXPOSE 8080

CMD java -jar -Xmx32m /usr/local/jetty/start.jar
#CMD ["java", "-jar", "-Xmx32m", "/usr/local/jetty/start.jar"]
