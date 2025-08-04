# 😻 Catch a Cat With Me – Cat Adoption Platform

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-green.svg)](https://spring.io/projects/spring-boot)  
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.java.com/)  
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)  
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

<div align="center">
  <img src="https://img.youtube.com/vi/uNihlwdW0tc/maxresdefault.jpg" alt="App demo screenshot" width="80%">
  <br>
  <a href="https://youtu.be/uNihlwdW0tc">▶️ Watch the demo video</a>
</div>

We connect adopters with feline caregivers to create homes full of purrs. Your next feline companion is waiting!

## ✨ Key Features

| Module         | Features                                                                                 |
|----------------|------------------------------------------------------------------------------------------|
| **🐱 Cats**    | Interactive catalog • Smart search • Advanced filters • Multimedia gallery               |
| **🤝 Adoptions**| Guided process • Real-time validation • Request tracking • Digital certificate            |
| **💬 Chat**    | Real-time communication • Push notifications • Conversation history                       |
| **⭐ Reviews** | Rating system • Verified comments • Reputation profiles                                  |
| **👤 Users**   | Profile management • Access roles • Secure authentication • Preferences                   |

## 🛠 Tech Stack

**Backend**
```mermaid
graph LR
    A[Spring Boot 3] --> B[Spring Security]
    A --> C[Spring Data MongoDB]
    A --> D[WebSockets/STOMP]
    A --> E[JWT Authentication]

