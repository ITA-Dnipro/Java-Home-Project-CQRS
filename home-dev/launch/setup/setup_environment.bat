@echo off
rem 1) install curl using the following link and add to PATH: https://curl.se/windows/dl-7.83.1_3/curl-7.83.1_3-win64-mingw.zip
rem 2) install jq 1.6 via the following link and add to PATH: https://github.com/stedolan/jq/releases/download/jq-1.6/jq-win64.exe
rem 3) make sure docker desktop is running - todo test if it's really necessary

echo ">>> Stopping the running services"
docker-compose -f "../docker-compose.yml" down

echo ">>> Running the services"
docker-compose -f "../docker-compose.yml" up -d --force-recreate
timeout 60

echo ">>> Creating the cooperation"
curl -X "POST" "http://localhost:8080/api/0/cooperations" -H "accept: application/json" -H "Content-Type: application/json" -d @test_cooperation.json
timeout 5

echo ">>> Getting invitation token from the mailhog"
curl --http0.9 "http://localhost:8025/api/v2/messages" | jq -c ".items[0].MIME.Parts[0].MIME.Parts[0].Body | gsub(\"\r\n\";\"\") | @base64d | match(\": ([a-zA-Z0-9-]+) \") | .captures[0].string" > temp_invitation_token.txt
<temp_invitation_token.txt ( set /p invitation_token=)

echo ">>> Invitation token is: %invitation_token%"
if %invitation_token% == [] (
  echo ">>> ERROR: Didn't receive invitation token! Try to increase the timeout after creating the cooperation."
  exit /b 1
)

echo ">>> Creating the user json"
more test_user.json | jq ". | tostring | sub(\"f8e20775-da55-11eb-a58d-775374b8f3a1\"; \"%invitation_token%\") | fromjson" > temp_user.json

echo ">>> Creating the user"
curl -X "POST" "http://localhost:8080/api/0/users" -H "accept: application/json" ^-H "Content-Type: application/json" -d @temp_user.json
timeout 5

echo ">>> Getting access token"
curl -X "POST" "http://localhost:9000/api/0/oauth2/server/login" -H "accept: application/json" -H "Content-Type: application/json" -d "{\"email\": \"testadmin@gmail.com\",\"password\": \"mySuperStrongPass\"}" | jq .access_token > temp_access_token.txt
<temp_access_token.txt ( set /p access_token=)

echo ">>> Access token is: %access_token%"
if %access_token% == [] (
  echo ">>> ERROR: Didn't receive access token! Try to increase the timeout after creating the user."
  exit /b 1
)

echo ">>> Adding news"
curl -X "POST" "http://localhost:8080/api/0/news" -H "Authorization: Bearer %access_token%" -H "accept: application/json" -H "Content-Type: application/json" -d @test_news.json
curl -X "POST" "http://localhost:8080/api/0/news" -H "Authorization: Bearer %access_token%" -H "accept: application/json" -H "Content-Type: application/json" -d @test_news2.json
curl -X "POST" "http://localhost:8080/api/0/news" -H "Authorization: Bearer %access_token%" -H "accept: application/json" -H "Content-Type: application/json" -d @test_news3.json

rem echo ">>> Cleaning up"
rem del temp_user.json
rem del temp_invitation_token.txt
rem del temp_access_token.txt
