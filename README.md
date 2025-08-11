# Book Library Spring Boot Application

A simple RESTful API for managing a book library built with Spring Boot, Maven, and Docker.

## Prerequisites

### For Docker-based Development (Recommended)
- Docker Engine 20.10+
- Docker Compose 1.29+

### For Local Development (Optional)
- Java 17 (OpenJDK or Oracle JDK)
- Maven 3.9.5+
- PostgreSQL 15+ (optional, can use H2 for development)

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.0** - Application framework
- **Maven 3.9.5** - Build tool
- **PostgreSQL 15** - Production database
- **H2 Database** - Development/test database
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **SpringDoc OpenAPI** - API documentation
- **Docker & Docker Compose** - Containerization

## Project Structure

```
spring-boot-book-library/
├── src/
│   ├── main/
│   │   ├── java/com/example/booklibrary/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── model/          # Entity classes
│   │   │   ├── repository/     # Data repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── BookLibraryApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-dev.properties
│   └── test/
│       └── java/com/example/booklibrary/
├── docker-compose.yml          # Main Docker configuration
├── docker-compose.test.yml     # Test Docker configuration
├── Dockerfile                  # Multi-stage build for app
├── Dockerfile.test            # Test container configuration
├── pom.xml                    # Maven configuration
├── Makefile                   # Convenience commands
└── README.md
```

## Quick Start with Docker

### 1. Clone the repository
```bash
git clone <repository-url>
cd spring-boot-book-library
```

### 2. Build and run the application
```bash
# Using Make (recommended)
make build
make run

# OR using Docker Compose directly
docker-compose build
docker-compose up -d
```

### 3. Verify the application is running
```bash
# Check container status
docker-compose ps

# View application logs
make logs
# OR
docker-compose logs -f app
```

### 4. Access the application
- API Base URL: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/api-docs

## Available Commands

### Using Make
```bash
make help    # Show all available commands
make build   # Build Docker images
make run     # Start the application
make stop    # Stop the application
make test    # Run tests in Docker
make clean   # Remove containers and volumes
make logs    # Show application logs
```

### Using Docker Compose
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v

# Run tests
docker-compose -f docker-compose.test.yml up --build --abort-on-container-exit

# View logs
docker-compose logs -f app
docker-compose logs -f db
```

## API Endpoints

### Book Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/isbn/{isbn}` | Get book by ISBN |
| GET | `/api/books/author/{author}` | Search books by author |
| GET | `/api/books/title/{title}` | Search books by title |
| GET | `/api/books/genre/{genre}` | Get books by genre |
| GET | `/api/books/available` | Get all available books |
| POST | `/api/books` | Create a new book |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |
| PATCH | `/api/books/{id}/availability` | Toggle book availability |

### Example API Requests

#### Create a new book
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "isbn": "978-0-7432-7356-5",
    "publicationDate": "1925-04-10",
    "genre": "Classic",
    "description": "A classic American novel",
    "available": true
  }'
```

#### Get all books
```bash
curl http://localhost:8080/api/books
```

#### Search by author
```bash
curl http://localhost:8080/api/books/author/Fitzgerald
```

#### Update book availability
```bash
curl -X PATCH http://localhost:8080/api/books/1/availability
```

## Database Configuration

### Production (Docker)
- Database: PostgreSQL 15
- Host: db (Docker service name)
- Port: 5432
- Database Name: bookdb
- Username: bookuser
- Password: bookpass

### Development (Local with H2)
To run with H2 in-memory database for local development:
```bash
# Set the active profile to 'dev'
java -jar -Dspring.profiles.active=dev target/book-library-*.jar
```

H2 Console will be available at: http://localhost:8080/h2-console

## Maven Commands (Local Development)

For developers who prefer to work without Docker, here are the essential Maven commands:

### Build Commands
```bash
# Clean and compile the project
mvn clean compile

# Package the application (creates JAR file)
mvn clean package

# Package without running tests
mvn clean package -DskipTests

# Install to local Maven repository
mvn clean install
```

### Running the Application
```bash
# Run using Spring Boot Maven plugin
mvn spring-boot:run

# Run with specific profile (e.g., dev profile with H2 database)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with custom port
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Testing Commands
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=BookControllerTest

# Run tests with specific pattern
mvn test -Dtest=*ControllerTest

# Run integration tests only
mvn verify

# Generate test coverage report (if configured)
mvn jacoco:report
```

### Useful Maven Options
```bash
# Skip tests during build
mvn clean package -DskipTests

# Skip test compilation
mvn clean package -Dmaven.test.skip=true

# Run with debug output
mvn -X clean package

# Use specific settings file
mvn -s settings.xml clean package

# Update dependencies
mvn dependency:resolve
mvn versions:display-dependency-updates
```

## Testing

### Run tests in Docker
```bash
make test
# OR
docker-compose -f docker-compose.test.yml up --build --abort-on-container-exit
```

### Run tests locally (requires Java and Maven)
```bash
mvn test
```

## Building from Source

### With Docker (Recommended)
The application uses a multi-stage Docker build:
```bash
docker build -t book-library:latest .
```

### Without Docker (Requires Java 17 and Maven 3.9.5+)
```bash
# Build the JAR file
mvn clean package

# Run the application
java -jar target/book-library-1.0.0.jar
```

## Environment Variables

The application can be configured using the following environment variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Application port | 8080 |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | default |
| `SPRING_DATASOURCE_URL` | Database connection URL | jdbc:postgresql://db:5432/bookdb |
| `SPRING_DATASOURCE_USERNAME` | Database username | bookuser |
| `SPRING_DATASOURCE_PASSWORD` | Database password | bookpass |

## Troubleshooting

### Port already in use
If port 8080 or 5432 is already in use:
```bash
# Change the port in docker-compose.yml
# For example, change "8080:8080" to "8081:8080"
```

### Database connection issues
```bash
# Check if the database is healthy
docker-compose ps
docker-compose logs db

# Restart the services
docker-compose restart
```

### Build failures
```bash
# Clean everything and rebuild
make clean
make build
make run
```

## Development Tips

1. **API Documentation**: Always check Swagger UI for the latest API documentation
2. **Database Persistence**: Data persists in Docker volumes between restarts
3. **Clean State**: Use `make clean` to remove all data and start fresh
4. **Logs**: Use `docker-compose logs -f` to monitor real-time logs
5. **Health Checks**: Both app and database have health checks configured

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or contributions, please open an issue in the repository.