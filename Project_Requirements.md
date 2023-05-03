Create store application. 
Design your API in RESTful manner. In the examples you get samples of JSONs that your api should receive or send. 

Your store has to support following methods:

DONE - Register new user. Example request: {“email”:”my@email.com”, “password”:”123”}
DONE - Respond with an appropriate HTTP codes (200 for ok, 409 for existing user)
DONE - Your app must not store password as plain text, use some good approach to identify user.
DONE - Login into system. Example request: {“email”:”my@email.com”, “password”:”123”}
DONE - Respond with JSON containing sessionId. *(optional) Think about preventing an intruder from bruteforcing. (Used JWT token instead sessionID)
DONE - *(optional) Reset password.
DONE - Get all products in store.
DONE - Respond with JSON list of items you have, e.g.: {“id”:”2411”, “title”:”Nail gun”, “available”:8, “price”: “23.
95”}
DONE - Add item to cart. Example request: {“id”:”363”, “quantity”:”2”}
DONE - Allow adding only one position at a time. If you don’t have this quantity in store - respond with an error. 
The information has to be session-scoped: once session expires - user will get new empty cart.
DONE - Display your cart content. Respond with list of product names with their quantities added. Calculate subtotal. Assign an ordinal to each cart item.
DONE - Remove an item from user’s cart.
DONE - Modify cart item. Example request: {“id”:2, quantity: 3} - user should be able to modify number of some items in 
his cart. 
DONE - Checkout: verify your prices in cart, ensure you still have desired amount of goods. If all is good - send a 
user confirmation about successful order. 
*(optional) Cancel order: return all products from order back to available status. 
*(optional) Get user’s order list. Should contain order id, date, total, status.