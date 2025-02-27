Lab 1. Author: Polina Batova

Project Topic: Service for collecting cooking recipes

Description: it is a service that allows users to create their own recipes. Each user can have many own recipes so it is One-to-Many relationship. 

Currently, the application works through console. 
Available functions are: 
-  create a user
-  create a recipe
-  show all users
-  show recipes by user id.

There is no DB connection, as it will be in next modules. Now lists are used for data storage.

As part of the first assignment, the following classes were implemented:
DTO: 
- UserDTO
- RecipeDTO
- ProductDTO

Repositories:
- UserRepository
- RecipeRepository
- ProductRepository

Service:
- UserService

Demonstation of work:

![image](https://github.com/user-attachments/assets/30a9744d-1819-4e80-9db4-1f4deda446c8) 1 - creating a user 


![image](https://github.com/user-attachments/assets/03b4669f-e090-4354-af54-3688c87129b9) 2 - create a recipe


![image](https://github.com/user-attachments/assets/19992d06-5034-422a-96b3-411b8046be0a)  3 - show all users 



![image](https://github.com/user-attachments/assets/630b91dd-9ebf-463a-b9cd-e25c534ad75b)  4 - show all recipes of a user



