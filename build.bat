
:: Build the image
:: TODO

:: Release on Heroku
:: TODO

:: Optional: Remove the image locally
:: call docker rmi <image tag>

call ./gradlew clean build
docker build -t registry.heroku.com/justapp123/web .
docker push registry.heroku.com/justapp123/web
heroku container:release web -a justapp123
docker rmi registry.heroku.com/justapp123/web