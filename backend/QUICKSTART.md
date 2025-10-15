# Quick Start Guide

## Using PowerShell Script (Easiest Method)

### Start all services:
```powershell
.\docker-manage.ps1 start
```

### Stop all services:
```powershell
.\docker-manage.ps1 stop
```

### View logs:
```powershell
.\docker-manage.ps1 logs
```

### Check service status:
```powershell
.\docker-manage.ps1 status
```

### Rebuild backend after code changes:
```powershell
.\docker-manage.ps1 rebuild
```

## Using Docker Compose Directly

### Start all services:
```bash
docker-compose up -d
```

### Stop all services:
```bash
docker-compose down
```

### View logs:
```bash
docker-compose logs -f backend
```

### Rebuild after code changes:
```bash
docker-compose down
docker-compose build backend
docker-compose up -d
```

## First Time Setup

1. Make sure Docker Desktop is running
2. Navigate to the backend folder
3. Run: `docker-compose up -d`
4. Wait for all services to start (takes 2-3 minutes on first run)
5. Access the API at http://localhost:8080

## Testing the Setup

### Test Backend:
```bash
curl http://localhost:8080/api/centri/sve
```

### Test Elasticsearch:
```bash
curl http://localhost:9200
```

### Test MinIO Console:
Open http://localhost:9001 in browser (login: minioadmin/minioadmin)

## Common Issues

### Port Already in Use
If you get "port already in use" errors, stop any running MySQL, Elasticsearch, or MinIO instances on your machine.

### Elasticsearch Yellow Health
This is normal for single-node setup. The application will work fine.

### Backend Won't Start
Check logs: `docker logs uesgaming-backend`
Common causes:
- MySQL not ready yet (wait 30 seconds and restart backend)
- Elasticsearch not ready yet (wait 30 seconds and restart backend)

## Accessing Services

| Service | URL | Credentials |
|---------|-----|-------------|
| Backend API | http://localhost:8080 | JWT Token required |
| Elasticsearch | http://localhost:9200 | None |
| MinIO Console | http://localhost:9001 | minioadmin/minioadmin |
| MySQL | localhost:3306 | root/root |

## Next Steps

1. Use Postman or curl to test the new endpoints
2. Upload some images and PDFs to centers
3. Try searching with Cyrillic and Latin text
4. Access MinIO console to see uploaded files
