<h1 align="center"> 🎒 NEXO - Academic Management Platform </h1>

<p align="center">
  <b>Intelligent learning, personalized study paths, and continuous student evolution.</b>
</p>

## 📝 About the Project
*NEXO is an intelligent academic management solution developed to combat demotivation and disorganization in the contemporary educational landscape. The project was born from the perception that traditional "one-size-fits-all" methodologies fail by ignoring each student's individual pace, resulting in procrastination and school dropout.*  
<br>
*Through integration with Gemini AI, the platform transforms dense content into personalized learning paths, offering continuous support, immediate feedback, and efficient time management. The goal is to restore student autonomy, turning information overload into a continuous and motivating evolution process.*

---

### 🧠 Meaning of NEXO
- **NEXO** – Represents the *intelligent connection between knowledge, technology, and student evolution.*

### 🔗 Symbolism:
- 📚 **Content** – *(What to learn)*  
- 🤖 **Artificial Intelligence** – *(How to learn)*  
- 🎯 **Student Goals** – *(Why to learn)*  

<p align="center">
More than just a word, <b>NEXO</b> represents the idea of transforming information into progress, connecting data, performance, and personalization into a single educational experience.
</p>

<div align="center">

### **_Less overload. More evolution. 🚀_**

</div>

---

## 📖 NEXO Features

### 📱 General Features 
- **Dynamic Agenda**: Centralized management of tasks and schedules, connecting deadlines and priorities.  
- **Adaptive Learning Paths (Gemini AI)**: Personalized study plans that identify weaknesses and create tailored learning roadmaps.  
- **Performance Dashboards**: Real-time progress monitoring with clear daily goals.  
- **Gamification**: Achievement system, XP rewards, streaks, and smart notifications to increase engagement.  
- **Simulations with Feedback**: Integrated tests with instant AI-generated feedback.  
- **Responsive Experience**: Interface optimized for smooth mobile user experience.  

---

## 🖥️ Front-End
- **Android Studio**: Native Android mobile application development.
- **Dynamic Dashboards**: Real-time visualization of streaks, XP progression, and pending study topics.
- **Interactive Roadmaps**: Visual step-by-step rendering of AI-generated study paths.

---

## 🔧 Back-End
- **Spring Boot 4.x (Java)**: Robust REST API core handling authentication, domain logic, and JPA mappings.
- **Spring Security & OAuth2 / JWT**: Stateless JWT token authentication with BCrypt password hashing.

---

## 🗃️ Database
- 🐘 **PostgreSQL**: Relational database storing users, achievements, settings, and study trails.  
- 📊 **Structured Relational Schema**: Optimized foreign key constraints (`ON DELETE CASCADE`) and custom ENUM types (`order_status`, `test_status`, `requirement_type`).  
- ⚡ **Performance Indexes**: Strategic indexes on foreign keys (`user_id`, `achievement_id`) for ultra-fast query execution.  

---

## 🔒 Security
- 🔐 BCrypt password hashing  
- 🛡️ Stateless JWT authentication & Refresh Tokens  
- 🔑 Role-based authorization  
- ✅ Strict JPA and Bean Validation (`jakarta.validation`)  
- 🚫 Protection against unauthorized resource access  
- 📡 Secure HTTPS/TLS communication  

---

## 📂 File Structure

```
Nexo-Project-Academy/
├── BackEnd/
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/
│   │       │       └── nexo/
│   │       │           ├── config/
│   │       │           ├── controller/
│   │       │           ├── dto/
│   │       │           ├── exceptions/
│   │       │           ├── integration/
│   │       │           │   ├── dto/
│   │       │           │   ├── gemini/
│   │       │           │   └── schemas/
│   │       │           ├── models/
│   │       │           │   ├── classes/
│   │       │           │   └── enums/
│   │       │           ├── repository/
│   │       │           ├── security/
│   │       │           ├── service/
│   │       │           └── NexoApplication.java   # Main application entry point
│   │       └── resources/
│   │           └── application.properties
│   ├── .env                                       # Environment variables (DB, JWT, Gemini API)
│   ├── .env.example
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
└── README.md
```

## 👥 Development Team

<div align="center">

<table>
<tr>

<td align="center" style="width: 230px; padding: 20px; vertical-align: top;">

<div style="height: 200px; display: flex; flex-direction: column; align-items: center;">
  <a href="https://github.com/AlmirCesar8">
    <img src="https://avatars.githubusercontent.com/u/171291938?v=4" width="100px"
    style="border-radius: 50%; border: 3px solid #64BD72;" />
  </a>

  <div style="height: 50px; display: flex; align-items: center; justify-content: center; text-align: center;">
    <sub><b>Almir César da Silva Filho</b></sub>
  </div>
</div>

<div>
  <sub>Frontend Engineer</sub><br>
  <sub>UI/UX Designer</sub><br>
  <sub>Creative Director</sub><br>
  <sub>Product Designer</sub>
</div>
</td>

<td align="center" style="width: 230px; padding: 20px; vertical-align: top;">

<div style="height: 200px; display: flex; flex-direction: column; align-items: center;">
  <a href="https://github.com/FabioBenjamin">
    <img src="https://avatars.githubusercontent.com/u/214406684?v=4" width="100px"
    style="border-radius: 50%; border: 3px solid #2A4DE6;" />
  </a>

  <div style="height: 50px; display: flex; align-items: center; justify-content: center; text-align: center;">
    <sub><b>Fabio Benjamin Oliveira</b></sub>
  </div>
</div>

<div>
  <sub>Chief Executive Officer (CEO)</sub><br>
  <sub>Backend Engineer</sub><br>
  <sub>Software Development Analyst</sub><br>
  <sub>Head of Documentation</sub><br>
  <sub>Database Administrator</sub>
</div>
</td>

<td align="center" style="width: 230px; padding: 20px; vertical-align: top;">

<div style="height: 200px; display: flex; flex-direction: column; align-items: center;">
  <a href="https://github.com/LLemosDev">
    <img src="https://avatars.githubusercontent.com/u/183765739?v=4" width="100px"
    style="border-radius: 50%; border: 3px solid #EBE300;" />
  </a>

  <div style="height: 50px; display: flex; align-items: center; justify-content: center; text-align: center;">
    <sub><b>Lucas de Lemos Bovi Amate</b></sub>
  </div>
</div>

<div>
  <sub>Chief Operations Officer (COO)</sub><br>
  <sub>Database Administrator</sub><br>
  <sub>Backend Engineer</sub><br>
  <sub>Software Development Analyst</sub><br>
  <sub>Information Security Analyst</sub>
</div>
</td>

<td align="center" style="width: 230px; padding: 20px; vertical-align: top;">
<div style="height: 200px; display: flex; flex-direction: column; align-items: center;">
  <a href="https://github.com/thiago-arine">
    <img src="https://avatars.githubusercontent.com/u/177758999?v=4" width="100px"
    style="border-radius: 50%; border: 3px solid #AA231C;" />
  </a>

  <div style="height: 50px; display: flex; align-items: center; justify-content: center; text-align: center;">
    <sub><b>Thiago Pereira Arine</b></sub>
  </div>
</div>

<div>
  <sub>Chief Design Officer (CDO)</sub><br>
  <sub>Frontend Engineer</sub><br>
  <sub>UI/UX Designer</sub>
</div>

</td>

</tr>
</table>

</div>

## 🎯 Project Status

<div align="center"> 🚧 In Development 🚧 </div>
