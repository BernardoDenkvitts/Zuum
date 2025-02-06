# Zuum 🚗⚡

<p align="center">
  <a href="#about">About</a> &#xa0; | &#xa0;
  <a href="#🌟-main-features">Main Features</a> &#xa0; | &#xa0;
  <a href="#🚀-technologies">Technologies</a> &#xa0; | &#xa0;
  <a href="#📃-rules-to-determine-the-ride-price">Rules to determine the ride price</a> &#xa0; | &#xa0;
  <a href="#how-to-use-the-real-time-feature">How to use the real-time feature</a> &#xa0; | &#xa0;
  <a href="#🚩-getting-started">Getting Started</a> &#xa0; | &#xa0;
  <a href="#📖-api-reference">API Reference</a> &#xa0;
</p>

Zuum is a backend application for a real-time ride-hailing system. Passengers request rides, drivers get instant notifications, and once a ride is accepted, passengers can track their driver’s location in real-time.

## 🌟 Main Features

 - <b>Ride request</b> - Users can request rides easily and fast. Obs - If a ride takes more than 5 minutes to be accepted, it is removed from the database and the user can request another one.

 - <b>Real Time Notification</b> - Drivers receive instantaneous alerts of new ride requests if they are within a range of 6 kilometers of the Origin point, and users receive real-time updates about their current ride.
 
 - <b>Location Updated in Real Time</b> - Drivers can update their location in real-time, and passengers can track their location.
 
 - <b>Dynamic Price Rules</b> - The ride’s price is calculated dynamically, based on factors, such as time, frequency, and demand.
 
 - <b>Secure Authentication</b> - JSON Web Token (JWT) to guarantee security access to the API.

## 🚀 Technologies

- <b>Java 21 + Spring Boot 3.3.7</b> - Efficient and scalable backend framework, providing easy configuration, and built-in tools for developing high-performance RESTful APIs. 

- <b>PostgreSQL</b> - Robust and reliable database. The application benefits from PostGIS, an extension that enables advanced geographic location processing.

- <b>WebSocket & STOMP</b> - STOMP is a messaging protocol that allows clients to communicate with message brokers. In Spring Boot, STOMP is often used over WebSocket to facilitate seamless real-time communication between clients and servers. 

- <b>Drools Rule Engine</b> - A powerful business rule management system used to dynamically determine ride prices based on various factors such as time of day, ride frequency, and demand. It allows flexible, scalable, and maintainable rule configuration.

- <b>Flyway</b> - Manages database versioning and migrations

- <b>Docker</b> - Ensures consistency across environment

- <b>Swagger</b> - Interactive API documentation that simplifies exploring and understanding the API functionalities.

## 📃 Rules to determine the ride price
Obs - The default price is 0.8 multiplied by the distance in kilometers

- If a passenger has completed three rides in the last 24 hours, the next ride gets a 5% discount.

-	If the user has completed two or more rides in the last hour, an 8% discount is applied.

-	If the user has completed a ride lasting at least 30 minutes in the last 2 hours, he receives an 8% discount.

-	Rides between 7 PM and 11 PM are 3% more expensive.

-	Rides after 11 PM until 7 AM are 6% more expensive

-	If there are more than 30 active rides (non-pending, non-completed) in the last hour, all rides will be 10% more expensive.

## How to use the real-time feature ?
Zuum leverages STOMP over WebSocket to provide real-time communication. STOMP follows a publish-subscribe messaging model, where clients subscribe to specific queues or topics to receive updates

### 📡 Connection Details
-  <b>Base URL :</b> ws://localhost:8080/ws/zuum
-  <b>Authentication :</b> A valid JWT token must be provided in the connection

### 🚗 For Drivers
<b>Queues to Receive Data</b>

-  <b>/user/queue/driver/ride-request</b> → Receives new ride requests
-  <b>/user/queue/driver/reply</b> → Receives replies from the server

<b>Endpoint to Send Location Updates</b>

-  <b>URL :</b> /ws/drivers/location
-  <b>Payload format :</b>
    
        {
           "driver_id": Integer,
           "curr_location": {
               "x": (longitude),
               "y": (latitude)
            }
        }

### 🚗 For Passengers
<b>Queues to Receive Ride Updates</b>

-  <b>/user/queue/ride</b> → Receives real-time updates about the ride status and driver location


### 📌 Message Format
All messages sent through STOMP queues follow the format:

        {
           "type": "String",
           "data": Object
        }

-  <b>type :</b> Defines the type of event. Possible values:
    -  <b>RIDE_REQUEST</b>

    -  <b>RIDE_UPDATE</b>

    -  <b>DRIVER_LOCATION_UPDATE</b>

    -  <b>ERROR</b>

-  <b>data</b> Contains the event payload, which can be one of:
    -  <b>RideRequestNotificationDTO</b>

    -  <b>RideResponseDTO</b>

    -  <b>Point </b>

    -  <b>String (e.g., "Updated" or "Error: error message" for location updates)</b>

For a full reference on DTOs, check the Swagger documentation page.

## 🚩 Getting Started

- <b>1</b> - Clone the project

```bash
    git clone https://github.com/BernardoDenkvitts/Zuum.git
```

- <b>2</b> - Go to the project root

```bash
cd /Zuum/zuum
```

- <b>3</b> - Start PostgreSQL

```bash
docker build -t zuum-postgres .
```

```bash
docker run -p 5432:5432 -d zuum-postgres
```

- <b>4</b> - Build the project

```bash
mvn clean install
```

- <b>5</b> - Run the application

```bash
mvn spring-boot:run
```

## 📖 API Reference
-  <b>Swagger URL :</b> http://localhost:8080/swagger-ui/index.html#/


#### <a href="#top">Back to top</a>
