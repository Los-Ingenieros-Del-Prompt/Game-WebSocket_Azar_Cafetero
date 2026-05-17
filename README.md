# Azar Cafetero - WebSocket Gateway Service

Welcome to the **WebSocket Gateway Service** repository for Azar Cafetero. In our microservices architecture, this service acts as the central nervous system. It is a highly concurrent, real-time message broker that manages all persistent connections between the Next.js frontend and the backend game engines.

## 🚀 Technology Stack

- **[Java & Spring Boot](https://spring.io/projects/spring-boot)**: The foundation of the service.
- **`spring-boot-starter-websocket`**: Provides the core WebSocket server implementation.
- **[STOMP over SockJS](https://docs.spring.io/spring-framework/reference/web/websocket/stomp.html)**: We utilize the Simple Text Oriented Messaging Protocol (STOMP) to structure our real-time messages. SockJS is implemented as a fallback mechanism to ensure connectivity even in restricted network environments (like aggressive corporate firewalls).
- **[Maven](https://maven.apache.org/)**: Build and dependency management.
- **[Docker](https://www.docker.com/)**: For scalable, reliable deployments.

## 🛠️ Architecture & Responsibilities

Unlike traditional REST APIs, this Gateway maintains stateful, bidirectional connections:

### 1. Connection Management & Security
- **Client Handshake**: When the frontend connects, this service negotiates the WebSocket handshake and establishes the SockJS session.
- **Authentication Interceptors**: It can validate sessions or tokens during the STOMP `CONNECT` frame, ensuring that only authenticated players can subscribe to game topics or send events.
- **Disconnection Handling**: Actively listens for `DISCONNECT` frames or TCP drops to trigger cleanup events (e.g., notifying the Lobby or Game services that a player has unexpectedly disconnected).

### 2. Message Routing (The Broker)
- **Inbound Routing (App DestinationPrefix)**: Messages sent from the client (e.g., `/app/game/roll-dice`) are received by the Gateway and securely routed to the internal Parqués or Brisca services for processing.
- **Outbound Broadcasting (Topic Prefix)**: The Gateway manages STOMP topics (e.g., `/topic/table/{tableId}`). When a game service updates its state, it pushes the update to the Gateway, which in turn broadcasts it simultaneously to all clients subscribed to that specific table.

## 🏃‍♂️ Getting Started

### Prerequisites
- Java 17+ (JDK)
- Maven 3.8+

### Running Locally

To start the WebSocket broker:

```bash
./mvnw spring-boot:run
```

The STOMP endpoint will typically be available at `ws://localhost:8082/ws` (or similar, depending on your `application.properties` configuration).

### Docker Deployment

```bash
docker build -t azarcafetero-websocket .
docker run -p 8082:8082 azarcafetero-websocket
```

## 🧪 Testing & Diagnostics

A `test-client.html` file is included in the repository. This is a critical debugging tool that allows developers to manually establish a SockJS/STOMP connection to the running broker, subscribe to topics, and send arbitrary JSON payloads without needing the full Next.js frontend to be running.

Run the Spring test suite:
```bash
./mvnw test
```