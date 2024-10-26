# Usar la imagen base de Amazon Corretto 21
FROM amazoncorretto:21

# Establecer el directorio de trabajo en el contenedor
WORKDIR /app

# Copiar el archivo JAR de tu aplicación al contenedor
COPY target/*.jar bankingapp.jar

# Exponer el puerto en el que tu aplicación va a correr
EXPOSE 3000

# Comando para ejecutar tu aplicación
CMD ["java", "-jar", "bankingapp.jar"]
