TOPIC: Recipe Service

SCREENSHOTS of demonstrations are in the screenshot directory

REQIEREMENTS IMPLEMENTED TASK 7:

1.	Stream API was used inside REST endpoints to process and return filtered data (at least 2 /filter endpoints to get filtered data from Elasticsearch by request param/body by building custom queries (no need to build complicated ones))
![image](https://github.com/user-attachments/assets/3f93894d-9f65-4ef2-a301-44057ecab3ba)

3.	Implemented lambda expressions & functional interface
![image](https://github.com/user-attachments/assets/805c466c-14df-4af9-a7bf-e0d198c16cb0)

4.	Optional to handle null values safely (15%).
![image](https://github.com/user-attachments/assets/0c9ba336-d868-4cb9-98bc-ab86cbdd99a2)

5.	LocalDateTime was used to add field "createdAt" in the recipes.
6.	Compared sequential vs. parallel streams for performance.
 ![image](https://github.com/user-attachments/assets/607fe859-3c9c-499e-8cc4-2969e51b037d)

7.	Applied reduction operations with reduce() .
![image](https://github.com/user-attachments/assets/0a362a4e-05bc-4030-8a2e-5d150798584e)

8.	Group and partition data using Collectors (RecipesController class.
![image](https://github.com/user-attachments/assets/0d0ae1a0-9b1f-4718-8dc6-11fa1d73bbf5)
![image](https://github.com/user-attachments/assets/2208f5f8-f7fa-44bd-a5d8-060dc63750af)
![image](https://github.com/user-attachments/assets/274f8605-09cd-4964-a89d-2bd1d086f53d)

9.	JUnit tests for Java 8 features, coverage is 95%.

Launch instructions:
1) Run Kafka
2) Run elasticsearch in docker
3) Run MySQL database
4) Change database connection properties to your ones in application.yml in user-service and recipe-service
5) Run discovery-service
6) Run user-service
7) Run recipes-service
8) Run auth-service (if auth service sends "403 forbidden" as a response for login, just terminate it and run again.

REQIEREMENTS IMPLEMENTED TASK 6:
1) Implemented user-service, recipe-service, auth-service as separate microservices with REST controllers. Almost all communication is through REST
2) Kafka used only in once process. When user adds some product to his favourite product, due to kafka background proccess occurs (in user servuce user's favourite products updates -> in recipe service new recommended recipes are found and written in database, so then when user asks recommended tasks through endpoint they are already calculated and just read from database)
(Extra screenshots are in screenshots folder)
![image](https://github.com/user-attachments/assets/faf8347c-33dd-4420-892b-797334a1ca19)
![image](https://github.com/user-attachments/assets/44518fef-b6a8-41fd-8c2e-57a7127040af)
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
6) Run recipe-service
7) Run auth-service (if auth service sends "403 forbidden" as a response for login, just terminate it and run again.

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
