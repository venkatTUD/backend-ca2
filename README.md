[![Deploy Backend to Prod](https://github.com/venkatTUD/backend-ca2/actions/workflows/deploy-prod.yml/badge.svg)](https://github.com/venkatTUD/backend-ca2/actions/workflows/deploy-prod.yml)

# Recipe Backend Service

This Spring Boot application provides a backend API for managing recipes.

## Prerequisites

- Java 17 or higher
- Maven
- MongoDB (DigitalOcean Managed MongoDB or other MongoDB instance)
- Docker (for containerized deployment)

## Configuration

The application uses environment variables for MongoDB configuration:

- `MONGO_DB_URL`: MongoDB connection URL
- `MONGO_DB_NAME`: MongoDB database name
- `MONGO_DB_COLLECTION`: MongoDB collection name

## GitHub Actions CI/CD

This project includes a GitHub Actions workflow to build and publish a Docker image to GitHub Container Registry.

### Setting up GitHub Secrets

For the workflow to run successfully, you need to configure the following secrets in your GitHub repository:

1. Go to your GitHub repository
2. Navigate to Settings > Secrets and variables > Actions
3. Click "New repository secret" and add the following secrets:

| Secret Name | Description |
|-------------|-------------|
| `REPO_USERNAME` | GitHub username or personal access token for GitHub Container Registry |
| `REPO_TOKEN` | GitHub personal access token with `packages:write` permissions |
| `MONGO_DB_URL` | MongoDB connection URL (e.g. `mongodb+srv://user:password@cluster.example.com/database?tls=true`) |
| `MONGO_DB_NAME` | MongoDB database name |
| `MONGO_DB_COLLECTION` | MongoDB collection name |

## Running Locally

To run the application locally:

```bash
# Set environment variables
export MONGO_DB_URL="your-mongodb-url"
export MONGO_DB_NAME="your-database-name"
export MONGO_DB_COLLECTION="recipes"

# Run with Maven
mvn spring-boot:run
```

## Building with Jib

To build and push the Docker image manually:

```bash
mvn clean package jib:build \
  -Dmongodb.url="your-mongodb-url" \
  -Dmongodb.dbname="your-database-name" \
  -Dmongodb.collection="recipes" \
  -Denv.REPO_USERNAME="your-github-username" \
  -Denv.REPO_TOKEN="your-github-token"
```

## API Endpoints

- `GET /` - Health check and initializes sample data
- `GET /recipes` - Get all recipes
- `POST /recipe` - Create a new recipe
- `DELETE /recipe/{name}` - Delete a recipe by name 