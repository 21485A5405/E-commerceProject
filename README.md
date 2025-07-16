# E-commerceProject
Team Surya
<br>
Surya Prakash
<br>
Kasagani sandeep
<br>
Kothapuli Srinivas reddy
<br>
Karumanchi sri venkat


E-commerceProject-Microservices/
│
├── eureka-server/                ← Service registry
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/eurekaserver/
│           │   └── EurekaServerApplication.java
│           └── resources/
│               └── application.yml
│
├── api-gateway/                  ← Single entry point for all services
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/apigateway/
│           │   └── ApiGatewayApplication.java
│           └── resources/
│               └── application.yml
│
├── product-service/             ← Microservice for product operations
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/productservice/
│           │   ├── ProductServiceApplication.java
│           │   ├── controller/
│           │   ├── service/
│           │   ├── repository/
│           │   └── model/
│           └── resources/
│               └── application.yml
│
├── user-service/                ← Microservice for user operations
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/userservice/
│           │   ├── UserServiceApplication.java
│           │   ├── controller/
│           │   ├── service/
│           │   ├── repository/
│           │   └── model/
│           └── resources/
│               └── application.yml
│
├── order-service/               ← Handles placing and viewing orders
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/orderservice/
│           │   ├── OrderServiceApplication.java
│           │   ├── controller/
│           │   ├── service/
│           │   ├── repository/
│           │   └── model/
│           └── resources/
│               └── application.yml
│
├── cart-service/                ← Handles cart items
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/cartservice/
│           │   ├── CartServiceApplication.java
│           │   ├── controller/
│           │   ├── service/
│           │   ├── repository/
│           │   └── model/
│           └── resources/
│               └── application.yml
│
└── admin-service/               ← Admin operations (like CRUD on products)
    ├── pom.xml
    └── src/
        └── main/
            ├── java/com/ecommerce/adminservice/
            │   ├── AdminServiceApplication.java
            │   ├── controller/
            │   ├── service/
            │   ├── repository/
            │   └── model/
            └── resources/
                └── application.yml
