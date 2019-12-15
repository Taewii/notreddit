# notreddit <sub><sup><sub><sup>(totally not a bad [reddit](https://www.reddit.com/) copy attempt)</sup></sub></sup></sub>
##### Final project for the course [Spring MVC Frameworks - Spring](https://softuni.bg/trainings/2628/java-mvc-frameworks-spring-november-2019) at [Software University](https://softuni.bg/).
------------
Runnable using [docker's](https://www.docker.com/) *docker-compose up* (docker required), but you have to implement  
your own  enviorments in a new **server-variables.env** file using the [server-variables.env.example](https://github.com/Taewii/notreddit/blob/master/server-variables.env.example) as  
an example. (front-end client available at [localhost:3001](http://localhost:3001), back-end server at [localhost:8001](http://localhost:8001))

### notreddit functionality:
##### Baisc functionalliy:
- creating users
- creating/subscribing/unsubscribing to subreddits
- creating/editing/deleting the currently logged in user posts
  - uploading local files (uploaded to [dropbox](https://www.dropbox.com/))
  - uploading files via url
- creating/editing/deleting the currently logged in user comments
- upvoting/downvoting all posts/comments
- mentions when someone replies to someones comment/post
- public chatroom using websockets
- pagination and sorting for posts/comments

##### Admin functionalliy:
- deleting all posts
- changing user roles up to **admin** role

##### Root functionalliy:
- same as admin but with the ability to change the admin's role
- deleting users
------------
### Running on:
- Java **8**
- Spring Boot **2.2.1.RELEASE**
------------

### Some of the technologies used:
#### Front-End:
- React + [Ant Design](https://ant.design/)

#### Back-End
- Spring MVC
- JWT + Spring Security
- Hibernate as ORM
- Websockets using STOMP and [SockJS](https://github.com/sockjs)
- [Screenshot Machine](https://www.screenshotmachine.com) for thumbnail generation
- [PostgreSQL](https://www.postgresql.org/) as database
- [Dropbox](https://www.dropbox.com/) for cloud storage
- [Lombok](https://projectlombok.org/) as a boilerplate remover
- [Model Mapper](ModelMapper) for object mapping

### Testing
- JUnit as a testing framework
- Mockito for mocking
- Spring Security testing
- Spring MVC Mock integration testing using  
[Testcontainers](https://www.testcontainers.org/) and [Flyway](https://flywaydb.org/)  to embed and seed the PostgreSQL database.

