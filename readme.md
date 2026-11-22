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

----------

## 📥 Download & Installation

The **NEXO** Android application is ready to use and already connected to our live cloud infrastructure (**Render** + **Neon.tech**).

1. Go to the **[Releases](https://github.com/FabioBenjamin/Nexo-Project-Academy/releases)** section of this repository and download the latest `.apk` file on your Android device (**Android 7.0 / API 24+**).
2. Allow installation from unknown sources when prompted by Android.
3. Open the app, create your account, and start building your personalized study trails!

---

## 📖 NEXO Features

### 📱 General Features 
- **Authentication & Account Recovery**: Account registration, secure login via JWT (*Access* & *Refresh Tokens*), and password recovery via e-mail.
- **Personalized Profiling Quiz**: Interactive onboarding questionnaire that captures the student's learning profile, goals, and pace before generating study paths.
- **AI-Generated Study Trails (Gemini API)**: Dynamic creation of structured study trails and topics powered by Google Gemini with structured JSON schemas.
- **Trail Management & Final Exams**: Full trail editing and deletion, step-by-step topic completion tracking, and final assessments unlocked upon completing all trail topics.
- **Gamification & Progress Metrics**: XP system, unlockable achievements with custom mascot avatars, daily study streaks (*ofensivas*), and a dashboard tracking total XP, completed topics, and finished trails.
- **Smart Daily Notifications**: Scheduled background reminders (`WorkManager`) to help students maintain consistency and protect their daily streak.
- **Theme & Multi-Language Customization**: Light/Dark theme and multi-language support, automatically adapting to device settings or persisting custom user preferences.
- **Full Profile Management**: Custom mascot/profile picture selection, username, e-mail, and password updates, plus complete account deletion from the database.

---

## 🖥️ Front-End
- **Kotlin & Jetpack Compose**: Modern, declarative native Android UI built with Material 3 and MVVM architecture (`StateFlow` + `Navigation Compose`).
- **Dynamic Dashboards**: Real-time visualization of daily streaks, XP progression, unlocked achievements, and completed trails.
- **Resilient Networking**: Retrofit & OkHttp client featuring automatic JWT injection (`AuthInterceptor`) and seamless token refresh (`TokenAuthenticator`).

---

## 🔧 Back-End
- **Java 21 & Spring Boot 4**: Robust REST API core handling authentication, gamification rules, e-mail dispatch (SMTP), and JPA persistence.
- **Google GenAI SDK**: Structured integration with Gemini API for deterministic generation of study trails, topics, and assessment questions.
- **Cloud Deployment**: Containerized via multi-stage **Docker** build and deployed on **Render**.

---

## 🗃️ Database
- 🐘 **PostgreSQL (Neon.tech)**: Serverless relational database with connection pooling (HikariCP) storing users, trails, topics, test attempts, achievements, and settings.  
- 📊 **Structured Relational Schema**: Enforced referential integrity (`ON DELETE CASCADE`) and domain ENUM types (`StudyTrailStatus`, `StudyTestStatus`, `TopicProgressStatus`, `AchievementRequirementType`).  
- ⚡ **Performance Indexes**: Strategic indexing on foreign keys (`user_id`, `achievement_id`, `study_trail_id`) for fast query execution.  

---

## 🔒 Security
- 🔐 BCrypt password hashing  
- 🛡️ Stateless JWT authentication & Refresh Token rotation  
- 🔑 Endpoint-level authorization via Spring Security OAuth2 Resource Server  
- ✅ Strict DTO and Bean Validation (`jakarta.validation`)  
- 🚫 Protection against unauthorized resource access (`Jwt` subject ownership checks)  
- 📡 Secure HTTPS/TLS communication
---

## 📂 File Structure

```
Nexo-Project-Academy/
├── BackEnd/                                      # Spring Boot REST API
│   ├── src/main/
│   │   ├── java/com/nexo/
│   │   │   ├── config/                           # Security (Spring Security) and JWT configurations
│   │   │   ├── controller/                       # REST endpoints (Auth, Trails, Tests, Achievements, User)
│   │   │   ├── dto/                              # Data Transfer Objects (Requests/Responses)
│   │   │   ├── exceptions/                       # Global exception handling and custom errors
│   │   │   ├── integration/                      # Google Gemini API integration (Prompts and Schemas)
│   │   │   ├── models/                           # JPA entities and domain Enums
│   │   │   ├── repository/                       # Database access interfaces (Spring Data JPA)
│   │   │   ├── security/                         # JWT token generation and validation
│   │   │   ├── service/                          # Business logic, gamification, and email services
│   │   │   └── NexoApplication.java              # Main application class
│   │   └── resources/
│   │       └── application.properties            # Database, JWT, Gemini, and SMTP configurations
│   ├── Dockerfile                                # Multi-stage build for Render deployment
│   ├── pom.xml                                   # Maven dependencies
│   └── .env.example                              # Environment variables template
│
├── FrontEnd/                                     # Native Android application (Kotlin + Compose)
│   ├── app/src/main/
│   │   ├── java/com/nexo/
│   │   │   ├── config/                           # RetrofitClient configuration
│   │   │   ├── data/api/                         # REST service interfaces (Retrofit)
│   │   │   ├── dto/                              # Request and response data models
│   │   │   ├── notifications/                    # Daily notifications scheduler and Worker
│   │   │   ├── security/                         # Session management, Interceptor, and JWT Authenticator
│   │   │   ├── ui/                               # Screens, ViewModels, and states (Auth, Home, Trails, Tests, Profile)
│   │   │   └── MainActivity.kt                   # Main app Activity
│   │   ├── res/                                  # Drawables (mascots and logos), fonts (Nunito), and themes
│   │   └── AndroidManifest.xml                   # Android app permissions and configurations
│   ├── gradle/
│   │   └── libs.versions.toml                    # Gradle dependencies version catalog
│   └── build.gradle.kts                          # Android project build configuration
│
└── readme.md                                     # Project documentation
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

<div align="center"> ✅ Completed ✅ </div>

<!-- <div align="center"> 🚧 In Development 🚧 </div> -->
