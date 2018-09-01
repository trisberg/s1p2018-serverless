FROM openjdk:8-jre
ARG FUNCTION_JAR=/functions/upper.jar
ARG FUNCTION_BEAN=functions.Upper
COPY java-function-invoker-0.0.7-exec.jar /
COPY upper.jar $FUNCTION_JAR
ENV FUNCTION_LOCATION file://${FUNCTION_JAR}
ENV FUNCTION_BEAN functions.Upper
ENTRYPOINT ["java","-Xmx128m","-Djava.security.egd=file:/dev/./urandom", "-jar", "java-function-invoker-0.0.7-exec.jar"]
