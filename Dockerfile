FROM ibm-semeru-runtimes:open-11-jre-jammy
COPY build/libs/*.jar app.jar
COPY start.sh /start.sh
ENV TZ="Asia/Tehran"
RUN cp /usr/share/zoneinfo/Asia/Tehran /etc/localtime
ENTRYPOINT ["sh","/start.sh"]
