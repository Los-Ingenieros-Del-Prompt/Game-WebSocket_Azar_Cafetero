# Real-Time Table Communication with WebSockets

This implementation provides a real-time communication infrastructure for multiplayer game tables using Spring WebSocket. The system follows **Hexagonal Architecture** principles to maintain clean separation of concerns.

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│   (WebSocket Clients - HTML/JavaScript via SockJS)          │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│              Infrastructure Layer - Adapters                 │
│  ┌──────────────────────┐      ┌──────────────────────────┐ │
│  │ TableWebSocketCtrl   │      │ SpringMessagingPublisher │ │
│  │ (Incoming Adapter)   │      │ (Outgoing Adapter)       │ │
│  └──────────────────────┘      └──────────────────────────┘ │
│  WebSocket Configuration       TableSessionManager (Repo)   │
└────────────────────────┬────────────────────────────────────┘
                         │
        ┌────────────────┴────────────────┐
        ▼                                  ▼
┌──────────────────────────┐    ┌─────────────────────────┐
│  Application Layer       │    │  Domain Layer           │
│  ┌────────────────────┐  │    │ ┌───────────────────┐   │
│  │ JoinTableService   │  │    │ │ Table             │   │
│  │ SendMessageService │  │    │ │ Player            │   │
│  │ LeaveTableService  │  │    │ │ TableMessage      │   │
│  │ BroadcastMsgSrvc   │  │    │ │ TableSession      │   │
│  └────────────────────┘  │    │ └───────────────────┘   │
│                           │    │                         │
│ Input Ports (Interfaces)  │    └─────────────────────────┘
│ - JoinTableUseCase        │
│ - LeaveTableUseCase       │
│ - SendMessageUseCase      │
│ - BroadcastMessageUseCase │
│                           │
│ Output Ports (Interfaces) │
│ - TableSessionRepository  │
│ - MessagePublisher        │
└──────────────────────────┘
```

## Project Structure

```
src/main/java/com/aguardientes/azarcafetero/game/
├── domain/                          # Domain Models
│   ├── Table.java                  # Represents a game table
│   ├── Player.java                 # Represents a player
│   ├── TableMessage.java           # Message within a table
│   └── TableSession.java           # Session managing players at a table
│
├── application/
│   └── port/
│       ├── in/                      # Input Ports (Use Cases)
│       │   ├── JoinTableUseCase.java
│       │   ├── LeaveTableUseCase.java
│       │   ├── SendMessageUseCase.java
│       │   └── BroadcastMessageUseCase.java
│       │
│       ├── out/                     # Output Ports (Interfaces)
│       │   ├── TableSessionRepository.java
│       │   └── MessagePublisher.java
│       │
│       └── service/                 # Service Implementations
│           ├── JoinTableService.java
│           ├── LeaveTableService.java
│           ├── SendMessageService.java
│           └── BroadcastMessageService.java
│
└── infrastructure/
    ├── websocket/                   # WebSocket Layer
    │   ├── WebSocketConfig.java    # STOMP Configuration
    │   ├── TableWebSocketController.java  # Message Handler
    │   └── dto/
    │       ├── TableMessageDTO.java
    │       └── JoinTableDTO.java
    │
    └── messaging/                   # Messaging Implementation
        ├── TableSessionManager.java # In-Memory Repository
        └── SpringMessagingPublisher.java
```

## Features

### 1. Real-Time Communication
- **WebSocket Connection**: Persistent connection using SockJS with STOMP protocol
- **Topic-Based Broadcasting**: Messages broadcast to all players at a specific table
- **Automatic Routing**: Messages automatically routed to correct table sessions

### 2. Table Management
- **Dynamic Table Creation**: Tables created automatically when first player joins
- **Concurrent Tables**: Multiple independent tables operating simultaneously
- **Table Cleanup**: Tables automatically removed when last player leaves

### 3. Player Management
- **Join/Leave**: Players can dynamically join or leave tables
- **Session Tracking**: Each player identified by unique ID and WebSocket session
- **Player List**: Track all players connected to each table

### 4. Message Broadcasting
- **Instant Delivery**: Messages delivered to all connected players
- **System Notifications**: Join/leave events broadcast as system messages
- **Message Ordering**: STOMP ensures reliable message ordering

## WebSocket Endpoints

### Connection Endpoint
```
WS: /ws
```
Establish WebSocket connection using SockJS fallback.

### Message Destinations
```
SEND /app/table/{tableId}/join
SEND /app/table/{tableId}/leave  
SEND /app/table/{tableId}/message

SUBSCRIBE /topic/table/{tableId}
```

## Message Format

### Join Table Request
```json
{
  "playerId": "player-1",
  "playerName": "Alice",
  "tableId": "table-1"
}
```

### Table Message
```json
{
  "playerId": "player-1",
  "tableId": "table-1",
  "content": "Hello, everyone!",
  "timestamp": "2026-03-14T12:00:00Z"
}
```

## Usage Example (JavaScript Client)

```javascript
// 1. Create WebSocket connection
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

// 2. Connect to STOMP
stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame.headers.server);
    
    // 3. Subscribe to table topic
    stompClient.subscribe('/topic/table/table-1', function(message) {
        const body = JSON.parse(message.body);
        console.log('Message received:', body);
    });
});

// 4. Join table
stompClient.send('/app/table/table-1/join', {}, JSON.stringify({
    playerId: 'player-1',
    playerName: 'Alice',
    tableId: 'table-1'
}));

// 5. Send message
stompClient.send('/app/table/table-1/message', {}, JSON.stringify({
    playerId: 'player-1',
    tableId: 'table-1',
    content: 'Hello everyone!',
    timestamp: new Date().toISOString()
}));

// 6. Leave table
stompClient.send('/app/table/table-1/leave', {}, JSON.stringify({
    playerId: 'player-1',
    playerName: 'Alice',
    tableId: 'table-1'
}));

// 7. Disconnect
stompClient.disconnect(function() {
    console.log('Disconnected');
});
```

## Running the Application

### Build
```bash
./mvnw clean package
```

### Run
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Access Web Interface
Open your browser and navigate to `http://localhost:8080/`

## Running Tests

```bash
./mvnw test
```

All 17 integration tests verify:
- ✅ Player joining/leaving tables
- ✅ Multiple concurrent tables
- ✅ Message broadcasting
- ✅ Table session isolation
- ✅ Entity validation
- ✅ Null safety

## Key Components

### Domain Layer
- **Table**: Represents a game table with unique identifier
- **Player**: Represents a player with ID, name, and WebSocket session
- **TableMessage**: Message with sender, content, and timestamp
- **TableSession**: Manages players and interactions for a specific table

### Application Layer
- **Use Cases**: Well-defined interfaces for joining, leaving, and messaging
- **Services**: Implementations orchestrating domain logic and port interactions
- **Ports**: Clean separation between application and infrastructure layers

### Infrastructure Layer
- **WebSocketConfig**: Configures STOMP messaging broker and endpoints
- **TableWebSocketController**: Handles incoming WebSocket messages
- **SpringMessagingPublisher**: Publishes messages to STOMP broker
- **TableSessionManager**: In-memory repository for table sessions

## Configuration

### application.properties
```properties
spring.application.name=game
server.port=8080
logging.level.org.springframework.messaging=DEBUG
```

### STOMP Configuration
- **Application Prefix**: `/app` - where clients send messages
- **Message Broker**: `/topic`, `/queue` - where messages are published
- **User Prefix**: `/user` - for individual message delivery
- **SockJS Fallback**: WebSocket with SockJS protocol

## Thread Safety

- **ConcurrentHashMap**: Used for thread-safe table session storage
- **Collections.synchronizedSet**: Used for thread-safe player tracking
- **Spring Messaging**: Handles concurrent message processing safely

## Next Steps

This WebSocket infrastructure provides a foundation for adding:

1. **Game Logic Layer**: Implement Parqués and Brisca game rules
2. **Turn Management**: Implement turn-based gameplay
3. **State Management**: Add game state persistence
4. **Player Actions**: Handle game-specific actions (cards, pieces)
5. **Scoring**: Implement scoring and result tracking
6. **Authentication**: Add player authentication and authorization

## Design Patterns

- **Hexagonal Architecture**: Clean separation of concerns
- **Adapter Pattern**: WebSocket controllers adapt messages to use cases
- **Repository Pattern**: Abstract table session storage
- **Observer Pattern**: STOMP pub/sub messaging
- **Factory Pattern**: Automatic table creation on first join

## Performance Considerations

- **In-Memory Storage**: Fast table session lookups
- **Concurrent Collections**: Thread-safe without explicit synchronization
- **STOMP Protocol**: Efficient message routing
- **SockJS Fallback**: Compatibility with older browsers

## Error Handling

- **Null Validation**: All domain objects require non-null parameters
- **Invalid States**: Proper handling of join/leave operations
- **Connection Failures**: Graceful disconnection handling
- **Message Errors**: Invalid messages ignored with logging

## Logging

Debug logging can be enabled:
```properties
logging.level.org.springframework.messaging=DEBUG
logging.level.org.springframework.web.socket=DEBUG
```

## Security Considerations

Current implementation focuses on communication infrastructure. Future phases should add:
- Player authentication
- Message validation
- Rate limiting
- Access control
- HTTPS/WSS support

---

**Version**: 1.0  
**Status**: Real-time communication infrastructure complete and tested  
**Next Phase**: Game logic implementation for Parqués and Brisca
