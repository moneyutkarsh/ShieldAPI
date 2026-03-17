@echo off
echo [1/6] Starting Discovery Server (Eureka) on Port 8761...
start "Discovery Server" cmd /k "mvnw.cmd spring-boot:run -pl discovery-server"

echo Waiting for Discovery Server to initialize (20 seconds)...
timeout /t 20

echo [2/6] Starting Auth Service on Port 8084...
start "Auth Service" cmd /k "mvnw.cmd spring-boot:run -pl auth-service"

echo [3/6] Starting Security Service on Port 8081...
start "Security Service" cmd /k "mvnw.cmd spring-boot:run -pl security-service"

echo Waiting for Services to register (15 seconds)...
timeout /t 15

echo [4/6] Starting Gateway Service on Port 8080...
start "Gateway Service" cmd /k "mvnw.cmd spring-boot:run -pl gateway-service"

echo [5/6] Starting Analytics Service on Port 8082...
start "Analytics Service" cmd /k "mvnw.cmd spring-boot:run -pl analytics-service"

echo [6/6] Starting Notification Service on Port 8083...
start "Notification Service" cmd /k "mvnw.cmd spring-boot:run -pl notification-service"

echo.
echo All services triggered. 
echo Eureka Dashboard: http://localhost:8761
echo Auth Login: curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"password\"}"
pause
