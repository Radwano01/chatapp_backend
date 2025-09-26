# ChatApp Backend

A comprehensive real-time chat application backend built with Spring Boot, featuring private messaging, group chats, friend management, and file sharing capabilities.

## 🚀 Features

### Core Chat Features
- **Real-time Messaging**: WebSocket-based instant messaging
- **Private Chats**: One-on-one conversations between users
- **Group Chats**: Multi-user group conversations with role management
- **Message Types**: Support for text, voice, image, video, and file messages
- **Message Management**: Delete messages, message history
- **Online Status**: Real-time user presence indicators

### User Management
- **User Registration & Authentication**: JWT-based secure authentication
- **User Profiles**: Customizable avatars, descriptions, and personal information
- **Password Management**: Secure password change functionality
- **User Search**: Find users by username
- **Status Management**: Online/Offline status tracking

### Social Features
- **Friend System**: Add, accept, decline, and remove friends
- **Friend Requests**: Send and manage friend requests
- **User Discovery**: Search and find other users

### Group Management
- **Group Creation**: Create chat groups with custom names and descriptions
- **Member Management**: Add/remove users from groups
- **Role-based Access**: Owner, moderator, and member roles
- **Group Settings**: Customize group avatars and descriptions

### File Management
- **AWS S3 Integration**: Secure file storage and retrieval
- **Multiple File Types**: Support for images, videos, audio, and documents
- **File Upload/Download**: RESTful API for file operations
- **Media Organization**: Automatic categorization by file type

## 🛠️ Technology Stack

### Backend Framework
- **Spring Boot 3.5.4**: Main application framework
- **Java 17**: Programming language
- **Maven**: Dependency management and build tool

### Database
- **MongoDB**: Primary database for chat messages and real-time data
- **MySQL**: Relational database for users, groups, and relationships
- **JPA/Hibernate**: ORM for database operations

### Security
- **Spring Security**: Authentication and authorization
- **JWT (JSON Web Tokens)**: Stateless authentication
- **BCrypt**: Password encryption

### Real-time Communication
- **WebSocket**: Real-time bidirectional communication
- **STOMP Protocol**: Messaging protocol over WebSocket
- **Spring Messaging**: WebSocket message handling

### Cloud Services
- **AWS S3**: File storage and management
- **AWS RDS**: Managed MySQL database

### Additional Libraries
- **Lombok**: Reduces boilerplate code
- **Jackson**: JSON serialization/deserialization

## 📁 Project Structure

```
src/main/java/com/project/chatApp/
├── chat/                    # Chat message functionality
│   ├── ChatMessage.java     # Message entity
│   ├── MessageType.java     # Message type enum
│   ├── ChatSocketController.java  # WebSocket message handling
│   └── dto/                 # Data Transfer Objects
├── chatRoom/               # Chat room management
│   ├── ChatRoom.java       # Chat room entity
│   └── ChatRoomService.java
├── user/                   # User management
│   ├── User.java           # User entity
│   ├── UserController.java # User REST endpoints
│   ├── UserService.java    # User business logic
│   └── dto/                # User DTOs
├── group/                  # Group chat functionality
│   ├── Group.java          # Group entity
│   ├── GroupMember.java    # Group membership entity
│   └── GroupService.java   # Group business logic
├── friend/                 # Friend management
│   ├── Friend.java         # Friend relationship entity
│   └── FriendService.java  # Friend business logic
├── s3/                     # File storage
│   ├── S3Service.java      # AWS S3 operations
│   └── S3Controller.java   # File upload/download endpoints
├── config/                 # Configuration classes
│   ├── SecurityConfig.java # Security configuration
│   ├── WebSocketConfig.java # WebSocket configuration
│   ├── JWTGenerator.java   # JWT token management
│   └── S3Config.java       # AWS S3 configuration
└── exception/              # Custom exception handling
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MongoDB
- MySQL 8.0+
- AWS Account (for S3 file storage)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Radwano01/chatapp_backend.git
   cd chatapp_backend
   ```

2. **Configure the application**
   ```bash
   # Copy the example configuration
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

3. **Update configuration**
   Edit `src/main/resources/application.properties` with your database and AWS credentials:
   ```properties
   # Database Configuration
   spring.data.mongodb.uri=mongodb://localhost:27017/chatApp
   spring.datasource.url=jdbc:mysql://localhost:3306/chatapp
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JWT Configuration
   JWT_SECRET=your_jwt_secret_key
   JWT_EXPIRATION=3600000
   
   # AWS S3 Configuration
   cloud.aws.credentials.accessKey=your_aws_access_key
   cloud.aws.credentials.secretKey=your_aws_secret_key
   aws.s3.bucket=your_s3_bucket_name
   cloud.aws.region.static=us-east-1
   ```

4. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/v1/users/register` - User registration
- `POST /api/v1/users/login` - User login
- `POST /api/v1/users/logout` - User logout

### User Management
- `GET /api/v1/users/details` - Get current user details
- `PUT /api/v1/users` - Update user profile
- `PUT /api/v1/users/password` - Change password
- `DELETE /api/v1/users` - Delete user account

### Friend Management
- `POST /api/v1/friends/{friendId}` - Send friend request
- `PUT /api/v1/friends/{friendId}?status={status}` - Accept/decline friend request
- `DELETE /api/v1/friends/{friendId}` - Remove friend
- `GET /api/v1/friends` - Get friends list

### Group Management
- `POST /api/v1/groups` - Create group
- `PUT /api/v1/groups/{groupId}` - Update group
- `DELETE /api/v1/groups/{groupId}` - Delete group
- `POST /api/v1/groups/{groupId}/members` - Add member to group
- `DELETE /api/v1/groups/{groupId}/members/{userId}` - Remove member from group

### File Management
- `POST /api/v1/s3/upload` - Upload file to S3
- `DELETE /api/v1/s3/delete` - Delete file from S3

### WebSocket Endpoints
- `/ws/chat` - WebSocket connection endpoint
- `/app/chat.sendMessage` - Send private message
- `/app/chat.group` - Send group message
- `/app/chat.deletePrivateMessage` - Delete private message
- `/app/chat.deleteGroupMessage` - Delete group message

## 🔧 Configuration

### Database Setup

1. **MongoDB Setup**
   ```bash
   # Install MongoDB
   # Create database
   use chatApp
   ```

2. **MySQL Setup**
   ```sql
   CREATE DATABASE chatapp;
   CREATE USER 'your_username'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON chatapp.* TO 'your_username'@'localhost';
   FLUSH PRIVILEGES;
   ```

### AWS S3 Setup

1. Create an S3 bucket
2. Configure IAM user with S3 permissions
3. Update the configuration with your credentials

## 🧪 Testing

Run the test suite:
```bash
mvn test
```

The project includes comprehensive unit tests for all major components:
- User service tests
- Chat message tests
- Group management tests
- Friend management tests
- Repository tests

## 🔒 Security Features

- **JWT Authentication**: Stateless authentication with configurable expiration
- **Password Encryption**: BCrypt hashing for secure password storage
- **CORS Configuration**: Configurable cross-origin resource sharing
- **Input Validation**: Comprehensive input validation and sanitization
- **Role-based Access**: Different permission levels for group members

## 🚀 Deployment

### Environment Variables
Set the following environment variables for production:
- `SPRING_PROFILES_ACTIVE=prod`
- `JWT_SECRET=your_production_jwt_secret`
- `MONGODB_URI=your_production_mongodb_uri`
- `MYSQL_URL=your_production_mysql_url`
- `AWS_ACCESS_KEY_ID=your_aws_access_key`
- `AWS_SECRET_ACCESS_KEY=your_aws_secret_key`

## 👨‍💻 Author

**Radwan**
- GitHub: [@Radwano01](https://github.com/Radwano01)

## 🙏 Acknowledgments

- Spring Boot community for the excellent framework
- MongoDB and MySQL for database support
- AWS for cloud services
- All contributors and testers

**Note**: This is a backend application. You'll need a frontend client to interact with the chat features. The WebSocket endpoints are designed to work with modern web frameworks like React, Vue.js, or Angular.
