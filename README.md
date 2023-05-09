# Grid-Store-JWT-API
Simple app store with Spring Security, JWT tokens and unit testing.

## Technologies
* Spring Boot & Security
* H2 database
* JUnit 5
* Cucumber
* Jwt token generator

## Endpoints
1. [Register](http://localhost:8080/shop/api/auth/register)
2. [Signin](http://localhost:8080/shop/api/auth)
3. [Basket](http://localhost:8080/shop/api/basket/)
4. [All Baskets](http://localhost:8080/shop/api/basket/all) -admin only
5. [Create order from basket](http://localhost:8080/shop/api/order/checkout)
6. [Change Password](http://localhost:8080/shop/api/auth/change-password")
7. [Get all users](http://localhost:8080/shop/api/auth/all) -admin only
8. [Get all products available](http://localhost:8080/shop/api/products)
9. [Change password](http://localhost:8080/shop/api/account/change-password)

Basket actions:</br>
[add product to basket](http://localhost:8080/shop/api/basket/add?productId=3) where product-id = 3</br>
[remove product from basket](http://localhost:8080/shop/api/basket/remove?productId=3) where product-id = 3</br>
[modify basket](http://localhost:8080/shop/api/basket/modify?productId=3&quantity=0) where product-id = 3 & new-quantity = 0</br>

## Instructions
Just run the project and navigate to 'http://localhost:8080/shop/api/auth/register'. Register new user. Create Orders. Enjoy.

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
:heavy_check_mark:  Get all products in store.</br>
:heavy_check_mark:  Respond with JSON list of items you have, e.g.: </br>
{“id”:”2411”, “title”:”Nail gun”, “available”:8, “price”: “23.95”} </br>
:heavy_check_mark:  Add item to basketDTO. Example request: {“id”:”363”, “quantity”:”2”}</br>
:heavy_check_mark: Allow adding only one position at a time. If you don’t have this quantity in store - respond with an error. The information has to be session-scoped: once session expires - user will get new empty basketDTO.</br>
:heavy_check_mark: Display your basketDTO content.</br>
:heavy_check_mark: Respond with list of product names with their quantities added. Calculate subtotal. Assign an ordinal to each basketDTO item. </br>
:heavy_check_mark: Remove an item from user’s basketDTO.</br>
:heavy_check_mark: Modify basketDTO item. Example request: {“id”:2, quantity: 3} - user should be able to modify number of some items in his basketDTO.</br>
:heavy_check_mark: Checkout: verify your prices in basketDTO, ensure you still have desired amount of goods. If all is good - send a user confirmation about successful order. </br>
*(optional) Cancel order: return all products from order back to available status. </br>
:heavy_check_mark: *(optional) Get user’s order list. Should contain order id, date, total, status.</br>

