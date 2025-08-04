# 😻 Catch a Cat With Me - Plataforma de Adopción Felina

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.java.com/)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/Licencia-MIT-blue.svg)](https://opensource.org/licenses/MIT)

<div align="center">
  <img src="https://img.youtube.com/vi/uNihlwdW0tc/maxresdefault.jpg" alt="Demo de la aplicación" width="80%">
  <br>
  <a href="https://youtu.be/uNihlwdW0tc">▶️ Ver demostración en video</a>
</div>

Conectamos adoptantes con cuidadores felinos para crear hogares llenos de ronroneos. ¡Tu próximo compañero felino te espera!

## ✨ Funcionalidades Destacadas

| Módulo           | Características                                                                 |
|------------------|---------------------------------------------------------------------------------|
| **🐱 Gatos**     | Catálogo interactivo • Búsqueda inteligente • Filtros avanzados • Galería multimedia |
| **🤝 Adopciones**| Proceso guiado • Validación en tiempo real • Seguimiento de solicitudes • Certificado digital |
| **💬 Chat**      | Comunicación en tiempo real • Notificaciones push • Historial de conversaciones |
| **⭐ Reseñas**   | Sistema de calificaciones • Comentarios verificados • Perfil de reputación      |
| **👤 Usuarios**  | Gestión de perfiles • Roles de acceso • Autenticación segura • Preferencias    |

## 🛠 Stack Tecnológico

**Backend**
```mermaid
graph LR
    A[Spring Boot 3] --> B[Spring Security]
    A --> C[Spring Data MongoDB]
    A --> D[WebSockets/STOMP]
    A --> E[JWT Authentication]
