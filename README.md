# 💬 WhatsApp Web Clone
A functional clone of WhatsApp web, built as a full-stack learning project with Angular and Spring boot, including real-time messaging, OAuth2 login and file sharing.

---

## 🎯 Project Overview

This project started as a YouTube tutorial but evolved into a custom full-stack application.  
The main goals were to learn about **real-time communication**, **modern authentication**, and **reactive architecture**.

### 🔍 Key Differences from the Original Tutorial

- 🔐 **No Keycloak with OAuth2** – replaced with direct **OAuth2 Google authentication**  
- 🧠 **Custom AuthService** – uses `BehaviorSubject` and `Observables` for reactive user state  
- 🎨 **Custom design** – UI rebuilt with **TailwindCSS** + SCSS
- ⚡ **Reactive architecture** – app-wide subscriptions to authentication state  
- 📎 **Image upload** – users can send media files in chats  
- 😄 **Emoji picker integration** with `@ctrl/ngx-emoji-mart`

---

## 🛠️ Tech Stack

### Frontend
- **Angular 20.2.0** with **TypeScript 5.9**
- **RxJS 7.8** for reactive programming
- **Tailwind CSS 4.1.17** + **SCSS**
- **Font Awesome 7.1.0**
- **@ctrl/ngx-emoji-mart 9.3.0** (emoji picker)
- **WebSockets** with **SockJS 1.6.1** and **STOMP.js 2.3.3**

#### OpenAPI Integration
- Angular client code was generated automatically from the backend OpenAPI specification using `ng-openapi-gen`, allowing strongly-typed API calls and up-to-date API documentation.

### Backend
- **Spring Boot 3.5.7** with **Java 21**
- **Spring Security & OAuth2** — Google OAuth2 client, secured backend endpoints.
- **Spring Data JPA** + **PostgreSQL**
- **WebSockets** for real-time messaging
- **SpringDoc OpenAPI 2.3.0** for API documentation
- **Dev Tools & Lombok** for faster development and reduced boilerplate
- **spring-dotenv** for environment variable management

## ⚙️ Features

- 🔐 OAuth2 authentication with Google
- 💬 Real-time messaging via WebSockets
- 👥 Contacts and chat list
- ✅ Read receipts
- 📱 Responsive UI
- ⚡ Reactive architecture: frontend subscribes to backend streams
- 🖼️ Emoji reactions (partially implemented)
- 📖 API Documentation via Swagger (OpenAPI)

---

## Instalattion & Setup

### Prerequisites
- Node.js v 16+
- Java JDK 21+
- Maven
- PostgreSQL database

### Frontend
```
cd frontend
npm install
npm start
```
Runs at http://localhost:4200

### Backend
```
cd backend
mvn clean install
mvn spring-boot:run
```
Runs at http://localhost:8080

### Environment Variables
Create a .env file in the backend folder:
```
# OAuth2
OAUTH_CLIENT_ID=your_client_id
OAUTH_CLIENT_SECRET=your_client_secret

# Database
DB_URL=jdbc:postgresql://localhost:5432/whatsapp_clone
DB_USERNAME=your_db_user
DB_PASSWORD=your_db_password
```

---

## Lessons Learned
- Implemented Websockets for real-time messaging.
- Configured OAuth2 without keycloak.
- Built a custom reactive AuthService using RxJS BehaviorSubject.
- Learned to integrate Tailwind CSS with Angular and SCSS.
- Managed messaging, read receipts, and reactive UI updates.

---

## ⚠️ Notes & Limitations

- Emoji reactions are not fully implemented.
- Some WhatsApp features like message numbers, typing indicators, or media preview are incomplete, among others.
- No full authorization server functionality is currently implemented

---

## 🚀 Future Improvements

These are some enhancements that could be implemented in the future to improve scalability, maintainability, and user experience:

- **Reactive Endpoints with Spring WebFlux**  
  Convert selected REST endpoints to use `Mono` and `Flux` for fully non-blocking, reactive behavior. This can help handle a higher number of concurrent users and enable streaming data scenarios, like live chat updates or notifications.

- **Global Exception Handling**  
  Implement a centralized error-handling mechanism using `@ControllerAdvice` with `@ExceptionHandler` methods (or a `GlobalErrorController`) to manage errors consistently across all endpoints. This improves maintainability and ensures that clients receive structured, informative error responses.

- **Message Reactions / Emojis**  
  Enable users to react to messages with emojis, similar to WhatsApp. This would involve updating the backend to handle reactions and propagating changes via WebSockets to all clients in real time.

- **Media Handling Enhancements**  
  Support sending/receiving additional media types, optimize file uploads, and add progress indicators for large media transfers.

- **Input Validation** 
Enforce Input Validation in controllers using `@Valid` and constraint annotations (`@NotNull`, `@Size`, etc.) to ensure data integrity.

---

## 🎓 Credits

This project was developed following the tutorial by [Ali Bouali](https://www.youtube.com/watch?v=BFtCtRKhLfA&list=LL&index=3),
but significant modifications were made to the frontend and architecture for learning purposes.
