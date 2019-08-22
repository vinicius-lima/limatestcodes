FROM openjdk:8-jre-slim

ENV MYSQL_DB_URL jdbc:mysql://localhost:3306/todo_list
ENV MYSQL_DB_USERNAME root
ENV MYSQL_DB_PASSWORD ""
ENV MYSQL_DB_PASSWORD_FILE ""

WORKDIR /usr/src/app
EXPOSE 5000

COPY . /usr/src/app
ENTRYPOINT ["./wait-for-it.sh", "db:3306", "--", "java", "-jar", "todolist.jar"]
