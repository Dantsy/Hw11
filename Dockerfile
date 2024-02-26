FROM openjdk:17

ADD build/libs/your-application.jar your-application.jar

ENTRYPOINT ["java", "-jar", "your-application.jar"]