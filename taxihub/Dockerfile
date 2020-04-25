FROM tomcat

RUN apt-get update && apt-get install -y vim

COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
ADD context.xml /usr/local/tomcat/conf
