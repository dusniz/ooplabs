FROM tomcat:11.0-jdk21-temurin

ENV CATALINA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"

RUN rm -rf /usr/local/tomcat/webapps/*

COPY .smarttomcat/enjoyers-ooplabs/conf/* $CATALINA_HOME/conf/
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

# Tomcat автоматически развернет WAR при запуске
CMD ["catalina.sh", "run"]