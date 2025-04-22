# LearnSwift

**LearnSwift** is an e-learning platform where students can book lessons with instructors to carry out online teaching. The project allows the management of lessons, bookings, and user profiles, providing a seamless experience for both students and instructors.

Developed using **Java**, **Spring Boot**, **PostgreSQL** for the backend, and **HTML/CSS/JS** for the frontend, the project utilizes **Docker** for containerized development and execution.

---

## Requirements

To run this project, make sure you have the following tools installed:

- **Docker Desktop**: [Download Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Java 8+** (for the backend)
- **PostgreSQL** (for database management, configured via Docker)

---

## How to Run the Project

### 1. Clone the repository

First, clone the project repository to your local machine:

```bash
git clone https://github.com/yourusername/LearnSwift.git
cd LearnSwift
```

### 2. Configuration Changes
Before running the project, you'll need to modify some configuration files:

Frontend Dockerfile: Adjust the Dockerfile to ensure the frontend is served properly.

application.properties: Update the database connection settings with the correct PostgreSQL credentials (e.g., URL, username, password).

WebConfig Class: If necessary, make adjustments to handle CORS and ensure the backend and frontend communicate properly.

I have included comments within the code to guide you through these changes.

### 3. After opening Docker Desktop, run the following command in the folder where the docker-compose.yml file is located:
```bash
docker-compose up --build
```

### 4. Running the Application
Once the containers are built and running, navigate to the frontend URL in your browser (usually http://localhost:8080, or another port if configured differently) to start using the application.

## 5. Contributing
If you have ideas for improvements or new features, please submit a pull request or open an issue.
Create your own fork of the project.
Make a new branch for your feature or bug fix.
Run tests to ensure the app works as expected.
Submit a Pull Request. Provide a clear description of your changes and any relevant context.

## License
LearnSwift is licensed under the MIT License. See the file LICENSE for more information.
