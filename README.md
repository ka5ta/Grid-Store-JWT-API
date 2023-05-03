# Grid-Store-JWT-API
Simple app store with Spring Security, JWT tokens and unit testing.

## Technologies
* Spring Boot
* H2 database
* JUnit 5
* Cucumber
* Jacoco

## Endpoints
1. [Register](http://localhost:8080/shop/api/auth/register)
2. [Signin](http://localhost:8080/shop/api/authenticate)
3. [Basket](http://localhost:8080/shop/api/auth/all)
4. [Change Password](http://localhost:8080/shop/api/auth/change-password")
5. [Get all users](http://localhost:8080/shop/api/auth/all) -admin only

## Instructions
Just run the project and navigate to 'http://localhost:8080/shop/api/auth/register'. Create Orders. Enjoy.

## Database
h2 db. To login to database: http://localhost:8080/h2-console/
```
user: sa
password: pass
```

## Requirements

Create store application. Design your API in RESTful manner. In the examples you get samples of JSONs that your api should receive or send. 
Your store has to support following methods: 

:heavy_check_mark:  Register new user. Example request: {“email”:”my@email.com”, “password”:”123”} </br>
:heavy_check_mark:  Respond with an appropriate HTTP codes (200 for ok, 409 for existing user) </br>
:heavy_check_mark:  Your app must not store password as plain text, use some good approach to identify user. </br>
:heavy_check_mark:  Login into system. Example request: {“email”:”my@email.com”, “password”:”123”} </br>
:heavy_check_mark:  Respond with JSON containing sessionId. </br>
*(optional) Think about preventing an intruder from bruteforcing. </br>
:heavy_check_mark:  (optional) Reset password.</br>
- Get all products in store.
- Respond with JSON list of items you have, e.g.: 
{“id”:”2411”, “title”:”Nail gun”, “available”:8, “price”: “23.95”} 
- Add item to basketDTO. Example request: {“id”:”363”, “quantity”:”2”}
- Allow adding only one position at a time. If you don’t have this quantity in store - respond with an error. The information has to be session-scoped: once session expires - user will get new empty basketDTO.
- Display your basketDTO content.
- Respond with list of product names with their quantities added. Calculate subtotal. Assign an ordinal to each basketDTO item. 
- Remove an item from user’s basketDTO.
- Modify basketDTO item. Example request: {“id”:2, quantity: 3} - user should be able to modify number of some items in his basketDTO.
Checkout: verify your prices in basketDTO, ensure you still have desired amount of goods. If all is good - send a user confirmation about successful order. 
*(optional) Cancel order: return all products from order back to available status. 
*(optional) Get user’s order list. Should contain order id, date, total, status.

