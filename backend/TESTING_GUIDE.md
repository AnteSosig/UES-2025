# Testing Guide for Elasticsearch and MinIO Features

## Prerequisites

1. Start all services:
```powershell
cd backend
.\docker-manage.ps1 start
```

2. Wait for all services to be healthy (check with `docker-compose ps`)

3. Have a valid JWT token (login through existing endpoints)

## Test 1: File Upload

### Upload Image and PDF to a Center

**Request:**
```bash
curl -X POST http://localhost:8080/api/centri/1/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "image=@test-image.jpg" \
  -F "pdf=@test-document.pdf"
```

**Expected Response:**
- Status: 200 OK
- Response body contains updated Centar object with `imagePath` and `pdfPath` populated

**Verification:**
1. Check MinIO console: http://localhost:9001
   - Login: minioadmin / minioadmin
   - Browse to `uesgaming` bucket
   - Verify files exist in `images/` and `pdfs/` folders

2. Check database:
```sql
SELECT id, ime, image_path, pdf_path, LENGTH(pdf_content) as pdf_content_length 
FROM centri WHERE id = 1;
```

## Test 2: Search Functionality

### Test 2.1: Search with Latin Text

**Request:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=sport&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Returns centers with "sport", "Sport", "SPORT" in name, description, or PDF
- Also matches Serbian "спорт" (Cyrillic)

### Test 2.2: Search with Cyrillic Text

**Request:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=спорт&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Returns same results as Test 2.1
- Demonstrates Cyrillic ↔ Latin equivalence

### Test 2.3: Case Insensitive Search

**Request:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=ФУДБАЛ&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Matches "фудбал", "Фудбал", "ФУДБАЛ", "fudbal", "Fudbal", "FUDBAL"

### Test 2.4: Search by Name Only

**Request:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=centar&type=naziv" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Returns centers with "centar" in the name field only

### Test 2.5: Search by Description Only

**Request:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=moderan&type=opis" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Returns centers with "moderan" in the description field only

### Test 2.6: Search by PDF Content Only

**Request:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=basketball&type=pdf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Returns centers where uploaded PDF contains "basketball"

## Test 3: PDF Download

**Request:**
```bash
curl -X GET http://localhost:8080/api/centri/1/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output downloaded.pdf
```

**Expected Response:**
- Status: 200 OK
- Content-Type: application/pdf
- File downloaded as `downloaded.pdf`

**Verification:**
- Open `downloaded.pdf` and verify it's the same file you uploaded

## Test 4: Reindexing

**Request:**
```bash
curl -X POST http://localhost:8080/api/centri/reindex \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Status: 200 OK
- Message: "All centers reindexed successfully"

**Use Case:**
- Run this after bulk data import
- Run this if search results seem out of sync

## Test 5: Error Handling

### Test 5.1: Upload Without Authentication

**Request:**
```bash
curl -X POST http://localhost:8080/api/centri/1/upload \
  -F "image=@test-image.jpg"
```

**Expected Response:**
- Status: 403 Forbidden

### Test 5.2: Upload to Non-Existent Center

**Request:**
```bash
curl -X POST http://localhost:8080/api/centri/99999/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "image=@test-image.jpg"
```

**Expected Response:**
- Status: 500 Internal Server Error
- Message: "Center not found with id: 99999"

### Test 5.3: Download PDF from Center Without PDF

**Request:**
```bash
curl -X GET http://localhost:8080/api/centri/2/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Response:**
- Status: 404 Not Found
- Message: "No PDF document found for center: 2"

### Test 5.4: Upload Non-PDF File as PDF

**Request:**
```bash
curl -X POST http://localhost:8080/api/centri/1/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "pdf=@test-image.jpg"
```

**Expected Response:**
- Status: 500 Internal Server Error
- Message: "Invalid PDF file"

## Test 6: Serbian Language Specifics

### Test Data Setup

Create a test center with mixed content:

**Name:** "Спортски центар Партизан"
**Description:** "Модеран центар са теренима за фудбал и кошарку"
**PDF Content:** "Овај центар нуди најбоље услове за тренинге. Basketball and football available."

### Test 6.1: Cyrillic Search

```bash
curl -X GET "http://localhost:8080/api/centri/search?query=кошарка&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Should Return:** The test center above

### Test 6.2: Latin Search

```bash
curl -X GET "http://localhost:8080/api/centri/search?query=kosarka&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Should Return:** Same center as Test 6.1

### Test 6.3: Mixed Content Search

```bash
curl -X GET "http://localhost:8080/api/centri/search?query=basketball&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Should Return:** The test center (matches PDF content)

### Test 6.4: Stemming Test

```bash
# Search for plural form
curl -X GET "http://localhost:8080/api/centri/search?query=терени&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Search for singular form
curl -X GET "http://localhost:8080/api/centri/search?query=терен&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** Both should return similar results due to stemming

## Test 7: Performance Testing

### Test 7.1: Large PDF Upload

Create a PDF with ~50 pages of text and upload it.

**Expected:**
- Upload completes within 10 seconds
- Text extraction completes successfully
- Elasticsearch indexing completes

### Test 7.2: Search Performance

Index 100+ centers with PDFs and test search speed.

**Expected:**
- Search results return in < 1 second
- Results are relevant and properly ranked

## Test 8: Integration Testing

### Complete Workflow Test

1. **Create a new center:**
```bash
curl -X POST http://localhost:8080/api/centri/novicentar \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ime": "Test Gaming Center",
    "ophis": "Најбољи центар за гејминг",
    "adresa": "Testna 123",
    "grad": "Beograd",
    "discipline": ["eSports", "PC Gaming"]
  }'
```

2. **Upload files to the new center:**
```bash
curl -X POST http://localhost:8080/api/centri/{NEW_CENTER_ID}/upload \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -F "image=@gaming-center.jpg" \
  -F "pdf=@center-info.pdf"
```

3. **Search for the center:**
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=гејминг&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

4. **Download the PDF:**
```bash
curl -X GET http://localhost:8080/api/centri/{NEW_CENTER_ID}/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output retrieved.pdf
```

5. **Verify all steps succeeded**

## Test 9: Docker Environment Testing

### Test 9.1: Service Health

```bash
docker-compose ps
```

**Expected:** All services should be "healthy"

### Test 9.2: Service Communication

```bash
# Test backend can reach MySQL
docker exec uesgaming-backend nc -zv mysql 3306

# Test backend can reach Elasticsearch
docker exec uesgaming-backend nc -zv elasticsearch 9200

# Test backend can reach MinIO
docker exec uesgaming-backend nc -zv minio 9000
```

**Expected:** All connections should succeed

### Test 9.3: Data Persistence

1. Upload files and create test data
2. Stop containers: `docker-compose down`
3. Start containers: `docker-compose up -d`
4. Verify data still exists

## Test 10: Elasticsearch Index Verification

### Check Index Structure

```bash
curl http://localhost:9200/centri/_mapping
```

**Expected:** Shows mapping with `serbian_custom_analyzer` on text fields

### Check Analyzer Configuration

```bash
curl http://localhost:9200/centri/_settings
```

**Expected:** Shows custom analyzer settings with char_filter and filters

### Test Analyzer Directly

```bash
curl -X POST "http://localhost:9200/centri/_analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "analyzer": "serbian_custom_analyzer",
    "text": "КОШАРКА играчи ФУДБАЛ"
  }'
```

**Expected:** Returns tokens showing lowercase, stemmed, Cyrillic-to-Latin conversion

## Troubleshooting

### If Search Returns No Results

1. Check if data is indexed:
```bash
curl http://localhost:9200/centri/_count
```

2. Reindex all data:
```bash
curl -X POST http://localhost:8080/api/centri/reindex \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

3. Check Elasticsearch logs:
```bash
docker logs uesgaming-elasticsearch
```

### If File Upload Fails

1. Check MinIO is running:
```bash
docker logs uesgaming-minio
```

2. Check MinIO console: http://localhost:9001

3. Verify bucket exists or can be created

### If PDF Parsing Fails

1. Check backend logs:
```bash
docker logs uesgaming-backend
```

2. Verify PDF is not encrypted or corrupted

3. Try with a simple test PDF

## Success Criteria

✅ All file uploads complete successfully
✅ Cyrillic and Latin searches return same results
✅ Case-insensitive search works
✅ PDF content is searchable
✅ Files are downloadable
✅ All Docker services are healthy
✅ Data persists after container restart
✅ Search returns results in < 1 second
✅ Error handling works as expected

## Test Coverage Summary

- ✅ File upload (images and PDFs)
- ✅ File download
- ✅ PDF text extraction
- ✅ Elasticsearch indexing
- ✅ Search by name
- ✅ Search by description
- ✅ Search by PDF content
- ✅ Combined search
- ✅ Cyrillic/Latin equivalence
- ✅ Case-insensitive search
- ✅ Serbian stemming
- ✅ Error handling
- ✅ Authentication/Authorization
- ✅ Docker containerization
- ✅ Data persistence
- ✅ Service integration
