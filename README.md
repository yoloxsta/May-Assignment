# Todo App - Full Stack Project

A simple full-stack todo application with separated frontend and backend, fully dockerized.

## Tech Stack

| Layer | Technology |
|-------|------------|
| Frontend | HTML, CSS, JavaScript, Nginx |
| Backend | Java (HttpServer), JDBC |
| Database | PostgreSQL |
| Containerization | Docker, Docker Compose |

## Project Structure

```
todo-app/
├── docker-compose.yml      # Docker orchestration
├── init.sql                # Database initialization script
├── README.md               # This file
│
├── frontend/               # Frontend application
│   ├── Dockerfile          # Nginx container
│   ├── index.html          # Main HTML page
│   ├── style.css           # Styling
│   └── app.js              # JavaScript logic
│
└── backend/                # Backend application
    ├── Dockerfile          # Java container
    └── src/main/java/app/
        ├── Main.java       # HTTP server & API routes
        ├── Database.java   # PostgreSQL connection
        └── Task.java       # Task model
```

## Architecture

```
┌─────────────────┐         ┌─────────────────┐         ┌─────────────────┐
│    Frontend     │         │     Backend     │         │    Database     │
│     (Nginx)     │ ──────▶ │     (Java)      │ ──────▶ │  (PostgreSQL)   │
│   Port: 3000    │         │   Port: 8080    │         │   Port: 5432    │
└─────────────────┘         └─────────────────┘         └─────────────────┘
        │                           │                           │
   Serves static              REST API                    Stores tasks
   HTML/CSS/JS                /api/tasks                  in tables
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/tasks | Get all tasks |
| POST | /api/tasks | Create a new task |
| PUT | /api/tasks?id={id} | Toggle task completion |
| DELETE | /api/tasks?id={id} | Delete a task |

## Prerequisites

- Docker Desktop installed
- Docker Compose installed

## How to Run

### 1. Start all containers

```bash
docker-compose up --build
```

### 2. Access the application

- **Frontend UI**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/tasks
- **Database**: localhost:5432

### 3. Stop containers

Press `Ctrl+C` then:

```bash
docker-compose down
```

## Features

- Add new tasks
- Mark tasks as complete/incomplete
- Delete tasks
- Persistent storage in PostgreSQL
- Responsive UI with modern design
- Stats showing completed/pending tasks

## Database Schema

```sql
CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Development

### Run Backend Locally (without Docker)

```bash
cd backend
javac -cp postgresql.jar -d ./out src/main/java/app/*.java
java -cp ./out:postgresql.jar app.Main
```

### Run Frontend Locally

Open `frontend/index.html` in a browser, or use a simple HTTP server:

```bash
cd frontend
python -m http.server 3000
```

## Useful Docker Commands

| Command | Description |
|---------|-------------|
| `docker-compose up -d` | Start in background |
| `docker-compose logs -f` | View all logs |
| `docker-compose logs -f backend` | View backend logs |
| `docker-compose down -v` | Remove containers and volumes |
| `docker exec -it todo-app-db-1 psql -U postgres -d tododb` | Access database |

## Learning Points

This project demonstrates:

1. **Frontend**: Basic HTML/CSS/JS without frameworks
2. **Backend**: Java HTTP server without Spring/other frameworks
3. **Database**: Raw JDBC for database operations
4. **Docker**: Multi-container orchestration
5. **Networking**: Container-to-container communication
6. **Separation of Concerns**: Frontend and backend as independent services

## Troubleshooting

### Frontend shows "Failed to load tasks"

- Check if backend is running: `docker-compose logs backend`
- Verify backend is accessible: visit http://localhost:8080/api/tasks

### Database connection error

- Wait for PostgreSQL to be ready (healthcheck handles this)
- Check database logs: `docker-compose logs db`

### Port already in use

- Stop conflicting services on ports 3000, 8080, or 5432
- Or change ports in `docker-compose.yml`
