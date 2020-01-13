FROM tomcat

COPY taxihub/target/*.war /usr/local/tomcat/webapps/hub.war
ADD context.xml /usr/local/tomcat/conf
