**HomeAway test**
===================

Usage
-----

Use sbt to compile and run the server
**sbt run** 
or to run tests
**sbt test**

I also wrote a bash script that run the server and perform some basic curl requests. It needs *curl* to send http requests and *jq* to parse/extract data from the response. It is not a fully exhaustive integration test, but it shows how to interact with the REST API. It is a prototype (for example, it has a function for starting the server but it is not invoked), I used it for test, just look at it if you have doubts.

Example of  curls that work with the server:
curl --request GET http://localhost:8080/size
curl --request GET http://localhost:8080/listings
curl --request GET http://localhost:8080/listing/<id>
curl --request DELETE http://localhost:8080/del/id
curl --request DELETE http://localhost:8080/delall
curl --request POST --data '<valid new listing json withoud id>' http://localhost:8080/new
curl --request PUT --data '<valid json with a valid id>' http://localhost:8080/update


----------


Choices
-------------
I tried to *keep it simple* (KISS approach) and to try to use the same stack it is used at HomeAway. I have never worked with Finagle, so it took a bit of time to me to get used.
As you can see from the *build.sbt* file the stack is really simple:

 - Scala
 - Finagle as REST library (RPC)
 - Jackson for parsing json
 - scalatest for test
 - java for Synchronized Map

Architecture
------------------

The application is basically setting up a server with Finagle. I didn't need any specific filters for the  request because it was not required to have authentication or any other validation of the request (making this more RESTful).
The request is handled by the **RestService** that check it and, based on the *method* of the request (GET, POST, PUT or DELETE), dispatch it to one specific **RequestHandler** (so we have four of them: **GetRequestHandler**, **DelRequestHandler**, **PostRequestHandler** and **PutRequestHandler**). One of the duties of the RestService is also to handle failures during the execution, especially the ones that are not caught by the speific handlers. **RestService** has no internal state (purely functional).
Even the four **RequestHandler** have no internal state: they are basically like DAOs, they match on the request path and invoke the right method of the **DataStore**, and eventually they use the **JasonUtil** to convert from and to JSON (please note that I could have created a case object for errors in order to return error message as json...).

The state of the application is kept in the **DataStore**. I gave only one implementation of it, **SimpleMapStore**, based on a Synchronized Map. I wanted to try to give an implementation based on *finagle-redis*, but it was time-expensive.

---------------

Analysis of the solution
===================

In this chapter I want to try to give my point of view on the topic that were important for the test

Concurrency
-----------------
I try to use the best functional principles for approaching the concurrency problem. Everything is purely functional and uses immutable objects, except the **DataStore**, that is unique per instance and synchronized. In the implementation I gave, only the *delAll* method is made by two requests: the first because I wanted to give back the list of elements we cleaned, the second is the clear itself. This means that if we receive a request in the middle, the data deleted could be different from the one returned by the REST service. I could have synchronized the method but I don't see an issue in that, also because the *delAll* was not required by the test and I would not leave it in a real scenario, too dangerous (I coded it only to help me with tests).

Reuse
--------
All the relationships among objects are kept *abstract*, meaning that **RequestHandlers** are using **DataStores**, and not the implementation **SimpleMapStore**. Also **RestService** is using generic **RequestHandlers** for handling the requests. This means that the code is less coupled, more testable (easier to mock) and more reusable. The use of factories (**DataStore** object or **RequestHandler** object) makes it easy to change the implementation of the different behaviour without having to change the objects that use it (for example, I could implement a **DataStore** based on *redis* or a database, and it is enough to change the factory method to have the whole application use the new object).
At an architectural level, the REST api could easily be used to store different type of data because some of its functions, like the ones related to creating or parsing JSON, have generic implementations. It is enough to create a different set of *case classes* to change the data stored by the component.

Performance
------------------

The REST API relies on Finagle to the performance. The bottle neck is the synchronisation of the map. It is easy to substitute the **SimpleMapStore** with a store based on *Redis* or on a database, if needed. Using factories (like the **DataStore** object) it is easy to adapt the full application to different stores, with more complex behaviour and different capabilities.

Scalability
--------------
The REST API is generic and reusable. You can replicate it in several instances and you will have only the problem of scaling the **DataStore**. If you use *redis* or a database, this is easily done. If you want to keep the in memory store you have to provide a scalable policy for the data: for example you can run several instances and keep on each of them a subset of the data. In this case the function *delAll* can become useful for scaling again: the router based on data can change its policy and simply request to clean one instance (this gives back the list of Listing contained before the clean), start more instances and then resubmit the Listings to the new instances with the new policy.

---------------------------------------------------------------------------

Further readings
==============

I may write more information on the steps I had to follow to create my app in [a post on my blog](http://www.blog.andrea.lorenzani.name/?p=109&preview=true)