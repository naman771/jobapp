# JobPortal

A full-stack job portal built with **Spring Boot**, **React**, and **Flask**, featuring AI-powered resume parsing and skill-based job matching.

Candidates upload PDF resumes, which are automatically parsed by an NLP microservice to extract skills, contact information, and experience. The system then computes match scores against job postings, helping recruiters identify the best-fit candidates.

## Architecture

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────────┐
│   React UI   │────▶│  Spring Boot API  │────▶│  Flask AI Parser │
│  (Vite)      │◀────│  (JWT + JPA)      │◀────│  (pdfplumber)    │
│  :5173       │     │  :8080            │     │  :5001           │
└──────────────┘     └────────┬─────────┘     └──────────────────┘
                              │
                     ┌────────▼─────────┐
                     │    H2 Database    │
                     │   (In-Memory)     │
                     └──────────────────┘
```

## Features

- **JWT Authentication** — Secure registration and login with role-based access (Candidate / Recruiter)
- **AI Resume Parsing** — Upload a PDF resume and extract name, email, phone, and skills automatically using NLP
- **Skill-Based Matching** — Match scores computed by comparing parsed resume skills against job requirements
- **Job Management** — Recruiters post, edit, and delete job listings with required skills, salary, and job type
- **Application Tracking** — Candidates apply to jobs; recruiters review, acknowledge, or reject applications
- **Responsive UI** — Minimalist black & white design with light/dark theme toggle
- **One-Command Startup** — Single `start.sh` script launches all three services concurrently

## Tech Stack

| Layer       | Technology                                      |
|-------------|------------------------------------------------|
| Frontend    | React 19, Vite, React Router, Lucide Icons     |
| Backend     | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Auth        | JWT (jjwt 0.11.5), BCrypt password hashing     |
| Database    | H2 (in-memory, dev) / MySQL (production-ready) |
| AI Service  | Flask, pdfplumber, regex-based NLP extraction   |
| DevOps      | Bash startup script, Maven, npm, pip            |

## Quick Start

**Prerequisites:** Java 17+, Node.js 18+, Python 3.9+, Maven

```bash
git clone https://github.com/naman771/jobapp.git
cd jobapp
chmod +x start.sh
./start.sh
```

The script will:
1. Build and start the Spring Boot backend on **:8080**
2. Create a Python venv, install dependencies, and start the Flask AI service on **:5001**
3. Start the Vite dev server on **:5173**

Open **http://localhost:5173** in your browser.

## Project Structure

```
jobapp/
├── backend/                  # Spring Boot API
│   └── src/main/java/com/jobportal/
│       ├── controller/       # REST endpoints (Auth, Jobs, Resume, Applications)
│       ├── service/          # Business logic & AI service integration
│       ├── model/            # JPA entities (User, Job, Resume, Application)
│       ├── dto/              # Data transfer objects
│       ├── repository/       # Spring Data JPA repositories
│       └── security/         # JWT filter, SecurityConfig, UserDetailsService
├── frontend/                 # React (Vite) UI
│   └── src/
│       ├── pages/            # Home, Login, Register, Jobs, JobDetail, Dashboard
│       ├── components/       # Navbar, Footer
│       ├── AuthContext.jsx    # Authentication state management
│       ├── ThemeContext.jsx   # Light/Dark theme context
│       └── api.js            # API client layer
├── ai-service/               # Flask resume parser
│   ├── app.py                # /parse endpoint — PDF → structured JSON
│   └── requirements.txt
├── start.sh                  # One-command launcher for all services
└── .gitignore
```

## API Endpoints

### Auth
| Method | Endpoint                       | Description              |
|--------|-------------------------------|--------------------------|
| POST   | `/auth/register`              | Register a new user       |
| POST   | `/auth/login`                 | Login, returns JWT token  |
| GET    | `/auth/me`                    | Get current user profile  |

### Jobs
| Method | Endpoint              | Description                    |
|--------|-----------------------|-------------------------------|
| GET    | `/jobs?q=&page=&size=`| Search/list jobs (paginated)   |
| GET    | `/jobs/{id}`          | Get job details                |
| GET    | `/jobs/my`            | Recruiter's posted jobs        |
| POST   | `/jobs`               | Create a new job posting       |
| DELETE | `/jobs/{id}`          | Delete a job posting           |

### Resume
| Method | Endpoint               | Description                      |
|--------|------------------------|----------------------------------|
| POST   | `/api/resume/upload`   | Upload PDF → AI parse → save     |
| GET    | `/api/resume/my`       | Get current user's resumes       |

### Applications
| Method | Endpoint                              | Description              |
|--------|--------------------------------------|--------------------------|
| POST   | `/applications/apply`                | Apply to a job            |
| GET    | `/applications/my`                   | Candidate's applications  |
| GET    | `/applications/job/{jobId}`          | Applicants for a job      |
| POST   | `/applications/{id}/acknowledge`     | Accept an application     |
| POST   | `/applications/{id}/reject`          | Reject an application     |

## Resume Parsing

The AI service extracts structured data from PDF resumes:

```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1 555 123 4567",
  "skills": ["Java", "Python", "React", "Spring Boot", "Docker", "AWS"],
  "experience": [],
  "education": [],
  "certifications": []
}
```

**Supported skills:** Java, Python, C++, JavaScript, TypeScript, React, Angular, Vue, Spring Boot, Node.js, Django, Flask, SQL, MongoDB, PostgreSQL, AWS, Azure, Docker, Kubernetes, Git, CI/CD, Machine Learning, and more.

## License

MIT