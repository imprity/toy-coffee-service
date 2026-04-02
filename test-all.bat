docker compose -f docker-compose-test.yml down
docker compose -f docker-compose-test.yml up -d

IF %ERRORLEVEL% NEQ 0 EXIT 1

echo "WAITING FOR DB TO START"

timeout /t 10

call gradlew test

REM docker compose -f docker-compose-test.yml down

