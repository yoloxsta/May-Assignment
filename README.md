# Todo App - Full Stack Project

> **Java Project Assignment - May 2026**

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

## Environment Variables

Create a `.env` file in the project root (or copy from `.env.example`):

```bash
cp .env.example .env
```

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database hostname | `db` |
| `DB_PORT` | Database port | `5432` |
| `DB_NAME` | Database name | `tododb` |
| `DB_USER` | Database user | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `BACKEND_PORT` | Backend API port | `8080` |
| `FRONTEND_PORT` | Frontend UI port | `3000` |

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


---

# Nexus Repository Integration

## What is Nexus Repository?

Nexus Repository is a binary artifact manager. It stores and manages your build artifacts (JAR files, Docker images, npm packages, etc.) in a central location.

## Why Use Nexus?

| Benefit | Description |
|---------|-------------|
| **Centralized Storage** | All artifacts in one place - no more "where is that JAR?" |
| **Version Control** | Track versions, never lose old builds |
| **Faster Builds** | Cache dependencies locally, no need to download from internet every time |
| **Security** | Scan artifacts for vulnerabilities before use |
| **Team Collaboration** | Share artifacts across teams easily |
| **Offline Builds** | Work without internet connection using cached dependencies |

## Nexus Repository Types

| Type | Used For | This Project |
|------|----------|--------------|
| **Maven (Hosted)** | Store your JAR files | Backend Java artifacts |
| **Docker (Hosted)** | Store Docker images | Frontend & Backend images |
| **Docker (Proxy)** | Cache Docker Hub images | Speed up pulls |
| **npm (Proxy)** | Cache npm packages | Future npm dependencies |
| **Maven (Proxy)** | Cache Maven Central | Java dependencies |

## How to Use Nexus in This Project

### Option 1: Store Java Artifacts (JAR) in Nexus

#### Step 1: Configure Maven Settings

Create `.m2/settings.xml` in your project root with your Nexus credentials:

```xml
<settings>
  <servers>
    <server>
      <id>nexus-releases</id>
      <username>admin</username>
      <password>your_password</password>
    </server>
    <server>
      <id>nexus-snapshots</id>
      <username>admin</username>
      <password>your_password</password>
    </server>
  </servers>

  <profiles>
    <profile>
      <id>nexus</id>
      <repositories>
        <repository>
          <id>nexus-releases</id>
          <url>https://your-nexus-url/repository/maven-releases/</url>
        </repository>
        <repository>
          <id>nexus-snapshots</id>
          <url>https://your-nexus-url/repository/maven-snapshots/</url>
        </repository>
      </repositories>
    </profile>
  </profiles>

  <activeProfiles>
    <activeProfile>nexus</activeProfile>
  </activeProfiles>
</settings>
```

> **Note**: Add `.m2/` to `.gitignore` to avoid committing credentials.

#### Step 2: Publish to Nexus

**Option A: Using Docker (recommended if Maven is not installed)**

```bash
docker run --rm \
  -v $(pwd)/backend:/app \
  -v $(pwd)/.m2:/root/.m2 \
  -w /app \
  maven:3.9-eclipse-temurin-21 \
  mvn deploy -s /root/.m2/settings.xml
```

**Command breakdown:**

| Flag | Description |
|------|-------------|
| `--rm` | Remove container after execution |
| `-v backend:/app` | Mount project source code |
| `-v .m2:/root/.m2` | Mount Maven settings with credentials |
| `-w /app` | Set working directory |
| `maven:3.9-eclipse-temurin-21` | Maven image with Java 21 |
| `mvn deploy` | Build and upload to Nexus |

**Option B: Using installed Maven**

If you have Maven installed locally:

```bash
cd backend
mvn deploy -s ../.m2/settings.xml
```

#### Step 3: Verify Upload

After successful deployment, your artifact will be available at:

```
https://your-nexus-url/repository/maven-releases/com/todoapp/todo-backend/1.0.0/
```

#### Artifacts Produced

| File | Description |
|------|-------------|
| `todo-backend-1.0.0.jar` | Regular JAR |
| `todo-backend-1.0.0-all.jar` | Fat JAR (with all dependencies bundled) |

---

## How to Use the Artifact

### What is the Artifact?

The artifact is your compiled Java application stored in Nexus. Think of it like a "package" of your code that others can download and use.

### Why Store Artifacts in Nexus?

| Without Nexus | With Nexus |
|---------------|------------|
| Share JAR files via email/Slack | Central repository - everyone knows where to find it |
| "Which version is latest?" confusion | Version tracking - `1.0.0`, `1.0.1`, `1.1.0` |
| Manual file copying | One command to download |
| No dependency management | Automatic dependency resolution |

### Usage 1: As a Dependency in Another Java Project

If another team wants to use your todo-backend as a library, they add this to their `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>com.todoapp</groupId>
        <artifactId>todo-backend</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

They also need your Nexus in their `settings.xml`:

```xml
<repositories>
    <repository>
        <id>nexus-releases</id>
        <url>https://your-nexus-url/repository/maven-releases/</url>
    </repository>
</repositories>
```

Then Maven will automatically download your JAR from Nexus.

### Usage 2: Download Manually

Download the artifact directly from Nexus:

```
https://your-nexus-url/repository/maven-releases/com/todoapp/todo-backend/1.0.0/todo-backend-1.0.0.jar
```

### Usage 3: Run the Application

```bash
# Download the fat JAR (includes all dependencies)
wget https://your-nexus-url/repository/maven-releases/com/todoapp/todo-backend/1.0.0/todo-backend-1.0.0-all.jar

# Run it
java -jar todo-backend-1.0.0-all.jar
```

### Real-World Use Cases

| Scenario | How Nexus Helps |
|----------|-----------------|
| **CI/CD Pipeline** | Jenkins/GitHub Actions downloads artifact from Nexus to deploy |
| **Team Collaboration** | Developer A builds, uploads to Nexus. Developer B downloads and tests |
| **Rollback** | Bug in v1.1.0? Redeploy v1.0.0 from Nexus |
| **Microservices** | Service A depends on Service B's library - fetches from Nexus |
| **Offline Development** | Nexus caches dependencies locally, no need to hit Maven Central every time |

### The Flow

```
┌──────────────┐     mvn deploy      ┌──────────────┐
│   Developer  │ ──────────────────▶ │    Nexus     │
│   (You)      │                     │  Repository  │
└──────────────┘                     └──────────────┘
                                            │
                         ┌──────────────────┼──────────────────┐
                         ▼                  ▼                  ▼
                   ┌──────────┐       ┌──────────┐       ┌──────────┐
                   │  CI/CD   │       │ Other    │       │  Prod    │
                   │  Server  │       │ Devs     │       │  Server  │
                   └──────────┘       └──────────┘       └──────────┘
                         │                  │                  │
                         └──────────────────┴──────────────────┘
                                    Downloads artifact
                                      mvn dependency
```

---

### Option 2: Store Docker Images in Nexus

#### Step 1: Create Docker Repository in Nexus

1. Open Nexus UI: `http://localhost:8081`
2. Login (default: admin / admin123)
3. Go to **Repository → Repositories → Create repository**
4. Select **docker (hosted)**
5. Configure:
   - Name: `docker-hosted`
   - HTTP Port: `8082`
   - Enable Docker V1: Yes

#### Step 2: Configure Docker to Trust Nexus

```bash
# Add Nexus to insecure registries (for HTTP)
# Edit /etc/docker/daemon.json:
{
  "insecure-registries": ["localhost:8082"]
}

# Restart Docker
sudo systemctl restart docker
```

#### Step 3: Login to Nexus Docker Registry

```bash
docker login localhost:8082
# Username: admin
# Password: your_password
```

#### Step 4: Tag and Push Images

```bash
# Build images
docker-compose build

# Tag for Nexus
docker tag todo-app-backend localhost:8082/todo-backend:1.0.0
docker tag todo-app-frontend localhost:8082/todo-frontend:1.0.0

# Push to Nexus
docker push localhost:8082/todo-backend:1.0.0
docker push localhost:8082/todo-frontend:1.0.0
```

#### Step 5: Pull from Nexus

```bash
docker pull localhost:8082/todo-backend:1.0.0
docker pull localhost:8082/todo-frontend:1.0.0
```

---

### Option 3: Use Nexus as Docker Proxy (Cache)

Speed up builds by caching images from Docker Hub.

#### Step 1: Create Docker Proxy Repository

1. In Nexus UI, create **docker (proxy)** repository
2. Configure:
   - Name: `docker-proxy`
   - Remote URL: `https://registry-1.docker.io`
   - HTTP Port: `8083`

#### Step 2: Configure Docker to Use Proxy

```bash
# Edit /etc/docker/daemon.json:
{
  "registry-mirrors": ["http://localhost:8083"]
}

# Restart Docker
sudo systemctl restart docker
```

Now all `docker pull` commands go through Nexus cache!

---

## GitHub Actions: Push to Nexus

Add to `.github/workflows/ci.yml` for automated publishing:

```yaml
  push-to-nexus:
    name: Push Docker Images to Nexus
    runs-on: ubuntu-latest
    needs: [backend-scan, frontend-scan]
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Login to Nexus
        uses: docker/login-action@v3
        with:
          registry: ${{ secrets.NEXUS_URL }}
          username: ${{ secrets.NEXUS_USER }}
          password: ${{ secrets.NEXUS_PASSWORD }}

      - name: Build and Push Backend
        uses: docker/build-push-action@v5
        with:
          context: ./backend
          push: true
          tags: ${{ secrets.NEXUS_URL }}/todo-backend:${{ github.sha }}

      - name: Build and Push Frontend
        uses: docker/build-push-action@v5
        with:
          context: ./frontend
          push: true
          tags: ${{ secrets.NEXUS_URL }}/todo-frontend:${{ github.sha }}
```

Add these secrets to your GitHub repository:
- `NEXUS_URL` - Your Nexus URL (e.g., `nexus.example.com:8082`)
- `NEXUS_USER` - Nexus username
- `NEXUS_PASSWORD` - Nexus password

---

## Running Nexus Locally (Docker)

```bash
# Start Nexus
docker run -d \
  --name nexus \
  -p 8081:8081 \
  -p 8082:8082 \
  -p 8083:8083 \
  sonatype/nexus3:latest

# Wait for startup (takes 1-2 minutes)
docker logs -f nexus

# Access Nexus UI
# URL: http://localhost:8081
# Default user: admin
# Password: Check docker logs for initial password
```

---

## Quick Reference

| Task | Command |
|------|---------|
| Login to Nexus | `docker login localhost:8082` |
| Push image | `docker push localhost:8082/image:tag` |
| Pull image | `docker pull localhost:8082/image:tag` |
| List images in Nexus | Nexus UI → Browse → docker-hosted |
| Deploy JAR | `mvn deploy` |
