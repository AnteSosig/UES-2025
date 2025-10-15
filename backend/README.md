# UES Gaming - Backend Setup Guide

## Overview

This Spring Boot application implements:
- **Elasticsearch** for full-text search with custom Serbian analyzer (Cyrillic/Latin, case-insensitive)
- **MinIO** for storing images and PDF documents
- **PDF parsing** to extract and index PDF content
- **Docker** containerization for easy deployment

## Features Implemented

### 1. Elasticsearch Indexing
- Custom Serbian analyzer that handles:
  - Case-insensitive search
  - Cyrillic to Latin transliteration
  - Serbian stop words and stemming
- Full-text search on:
  - Center name (naziv)
  - Center description (opis)
  - PDF document content (parsed text)

### 2. MinIO File Storage
- Image upload and storage
- PDF document upload and storage
- File download with presigned URLs

### 3. Search Endpoints
- **Search by name**: `GET /api/centri/search?query=YOUR_QUERY&type=naziv`
- **Search by description**: `GET /api/centri/search?query=YOUR_QUERY&type=opis`
- **Search by PDF content**: `GET /api/centri/search?query=YOUR_QUERY&type=pdf`
- **Search all fields**: `GET /api/centri/search?query=YOUR_QUERY&type=all`

### 4. File Management Endpoints
- **Upload files**: `POST /api/centri/{id}/upload` (multipart/form-data with `image` and/or `pdf` fields)
- **Download PDF**: `GET /api/centri/{id}/pdf`
- **Reindex all centers**: `POST /api/centri/reindex` (Admin only)

## Running with Docker

### Prerequisites
- Docker Desktop installed
- Docker Compose installed

### Start All Services

1. Navigate to the backend folder:
```bash
cd backend
```

2. Start all services:
```bash
docker-compose up -d
```

This will start:
- **MySQL** on port 3306
- **Elasticsearch** on ports 9200 (API) and 9300 (cluster)
- **MinIO** on ports 9000 (API) and 9001 (Console)
- **Spring Boot Backend** on port 8080

### Access Services

- **Backend API**: http://localhost:8080
- **Elasticsearch**: http://localhost:9200
- **MinIO Console**: http://localhost:9001 (login: minioadmin/minioadmin)

### Stop Services

```bash
docker-compose down
```

To also remove volumes (data will be lost):
```bash
docker-compose down -v
```

## Running Locally (Without Docker)

### Prerequisites
1. Java 17
2. Maven
3. MySQL (running on port 3306)
4. Elasticsearch (running on port 9200)
5. MinIO (running on port 9000)

### Setup

1. Create MySQL database:
```sql
CREATE DATABASE svtkvt;
```

2. Update `application.properties` if needed

3. Build and run:
```bash
cd backend/projekt
mvn clean install
mvn spring-boot:run
```

## API Usage Examples

### 1. Upload Files to a Center (Admin only)

```bash
curl -X POST http://localhost:8080/api/centri/1/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "image=@/path/to/image.jpg" \
  -F "pdf=@/path/to/document.pdf"
```

### 2. Search Centers

```bash
# Search all fields (name, description, PDF content)
curl -X GET "http://localhost:8080/api/centri/search?query=football&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search only by name
curl -X GET "http://localhost:8080/api/centri/search?query=спорт&type=naziv" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search only in PDF content
curl -X GET "http://localhost:8080/api/centri/search?query=баскет&type=pdf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

The search supports:
- Both Latin and Cyrillic scripts
- Case-insensitive matching
- Serbian language specifics (stop words, stemming)

### 3. Download PDF

```bash
curl -X GET http://localhost:8080/api/centri/1/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output center-document.pdf
```

### 4. Reindex All Centers (Admin only)

```bash
curl -X POST http://localhost:8080/api/centri/reindex \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Database Schema Changes

The `Centar` entity now includes:
- `imagePath` - Path to the image in MinIO
- `pdfPath` - Path to the PDF document in MinIO
- `pdfContent` - Extracted text from PDF (indexed in Elasticsearch)

## Custom Serbian Analyzer

The Elasticsearch analyzer (`elasticsearch-settings.json`) includes:
- **Character filter**: Cyrillic to Latin transliteration
- **Tokenizer**: Standard tokenizer
- **Filters**: 
  - Lowercase filter
  - Serbian stop words filter
  - Serbian stemmer

This allows searches like:
- "фудбал" (Cyrillic) will match "fudbal" (Latin)
- "KOŠARKA" (uppercase) will match "košarka" (lowercase)
- "играчи" will be stemmed and matched appropriately

## Troubleshooting

### Elasticsearch Connection Issues
- Ensure Elasticsearch is running: `curl http://localhost:9200`
- Check Docker logs: `docker logs uesgaming-elasticsearch`

### MinIO Connection Issues
- Access MinIO console: http://localhost:9001
- Check credentials in docker-compose.yml
- Check Docker logs: `docker logs uesgaming-minio`

### Backend Issues
- Check application logs: `docker logs uesgaming-backend`
- Verify all services are healthy: `docker-compose ps`

### Rebuild Backend Container
```bash
docker-compose down
docker-compose build backend
docker-compose up -d
```

## Development Notes

- PDF files are automatically parsed when uploaded
- All centers are automatically indexed in Elasticsearch when created/updated
- MinIO buckets are created automatically if they don't exist
- File size limit: 10MB (configurable in application.properties)

## Architecture

```
┌─────────────┐     ┌──────────────┐     ┌───────────────┐
│   Angular   │────▶│  Spring Boot │────▶│     MySQL     │
│   Frontend  │     │   Backend    │     │   Database    │
└─────────────┘     └──────────────┘     └───────────────┘
                           │
                           ├──────────────▶┌───────────────┐
                           │               │ Elasticsearch │
                           │               │    Search     │
                           │               └───────────────┘
                           │
                           └──────────────▶┌───────────────┐
                                           │     MinIO     │
                                           │ File Storage  │
                                           └───────────────┘
```

## Technologies Used

- **Spring Boot 3.2.0**
- **Java 17**
- **MySQL 8.0**
- **Elasticsearch 8.11.0**
- **MinIO** (latest)
- **Apache PDFBox 2.0.29**
- **Docker & Docker Compose**
