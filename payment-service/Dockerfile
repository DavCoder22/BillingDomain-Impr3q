FROM maven:3.8.6-jdk-11

WORKDIR /app

# Copiar solo el pom.xml primero para aprovechar la caché de Maven
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación
RUN mvn package -DskipTests

# Puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/payment-service-0.0.1-SNAPSHOT.jar"]
