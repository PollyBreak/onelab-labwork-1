TOPIC: Recipe Service

REQIEREMENTS IMPLEMENTED:
1) Implemented user-service, recipe-service, auth-service as separate microservices with REST controllers. Almost all communication is through REST
2) Kafka used only in once process. When user adds some product to his favourite product, due to kafka background proccess occurs (in user servuce user's favourite products updates -> in recipe service new recommended recipes are found and written in database, so then when user asks recommended tasks through endpoint they are already calculated and just read from database)
(Extra screenshots are in screenshots folder)
![image](https://github.com/user-attachments/assets/faf8347c-33dd-4420-892b-797334a1ca19)
![image](https://github.com/user-attachments/assets/b6d1d4a0-1274-4219-bb7a-dcd3dbdfcc0a)
4) discovery-service based on Eureka Server was implemented, Feign Clients are used for inter-service communication
5) each service has service, and controller layers. services are injected in controllers
6) For authentication a separate service was created. This auth service generates and validate jwt token. BUT IN FUNCTIONS only create recipe requires authentication (it check that user logged in and that he tries to add a recipe with right user id -> so user with id 1 cannot create a recipe where user id = 4)
7) Impelemented AOP logging for controllers, capturing every request, response, IP address, requested URL, and user id instead of username
8) Wrote unit tests, achieved 93% coverage. no integration tests.


Launch instructions:
1) Run Kafka
2) Run MySQL database
3) Change database connection properties to your ones in application.yml in user-service and recipe-service
4) Run discovery-service
5) Run user-service
6) Run auth-service (if auth service sends "403 forbidden" as a response for login, just terminate it and run again.

ENDPOINTS and requests for testiing
1) if you use postman for testing, you can download collection for testing in postman dekstop app. Just import this json and you get all neccesary requests with responce bodies and necessary headers. You only need to change token to actual one after login in "add recipe"
![image](https://github.com/user-attachments/assets/2ee33097-48e2-4d56-ab3b-e11e52d00513)
![image](https://github.com/user-attachments/assets/9c016e66-d8a6-4862-a710-6246dea42f1a)
2) Otherwise, there are endpoints and neccessary bodies:
- GET http://localhost:8082/recipes
- POST http://localhost:8081/users/register   - to register a new user
  request body

{
    "username":"bob2",
    "email":"bob2@gmail.com",
    "password":"testing"
}
- POST http://localhost:8083/auth/login   - to get token
request body:

{
    "username":"bob2",
    "password":"testing"
}
- POST http://localhost:8082/recipes  - to add recipe (checks token and userId)
  request body:

{
    "title":"bannofii pie",
    "description":"tasty yummy",
    "instructions":"take ......",
    "authorId":1,
    "products":["heavy cream", "condensed milk", "biscuits"]
}

Header- Authorization: Bearer _TOKEN_

- POST http://localhost:8082/recipes/1/review  - to post review

{
    "userId":1,
    "rating":4
}

- POST http://localhost:8081/users/preferences

{
    "userId":"1",
    "favoriteIngredients":["chocolate"]
}
  
- DELETE http://localhost:8081/users/preferences/1

["eggs"]
  
- GET http://localhost:8081/users/preferences/1 
- GET http://localhost:8082/recipes/recommend/1
- GET http://localhost:8083/auth/validate    Header - Authorization: Bearer _TOKEN_ 
