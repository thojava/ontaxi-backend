FROM tomcat

RUN apt-get update && apt-get install -y vim

COPY target/*.war /usr/local/tomcat/webapps/api.war
ADD context.xml /usr/local/tomcat/conf
