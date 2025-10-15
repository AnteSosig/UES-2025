# API Reference - Elasticsearch & MinIO Endpoints

## Base URL
```
http://localhost:8080/api/centri
```

## Authentication
All endpoints require JWT authentication via the `Authorization` header:
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## File Management Endpoints

### 1. Upload Files to Center

Upload image and/or PDF document to a specific center.

**Endpoint:** `POST /api/centri/{id}/upload`

**Authorization:** ADMIN only

**Parameters:**
- `id` (path) - Center ID (Integer)
- `image` (multipart/form-data, optional) - Image file (JPG, PNG)
- `pdf` (multipart/form-data, optional) - PDF document

**Request Example:**
```bash
curl -X POST http://localhost:8080/api/centri/1/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "image=@/path/to/image.jpg" \
  -F "pdf=@/path/to/document.pdf"
```

**Response (200 OK):**
```json
{
  "id": 1,
  "ime": "Sportski Centar",
  "ophis": "Description",
  "datumKreacije": "2024-01-01T00:00:00",
  "adresa": "Address 123",
  "grad": "Belgrade",
  "rating": 4.5,
  "active": true,
  "imagePath": "images/uuid-12345.jpg",
  "pdfPath": "pdfs/uuid-67890.pdf",
  "pdfContent": "Extracted text from PDF..."
}
```

**Error Responses:**
- `403 Forbidden` - Not authenticated or not an admin
- `404 Not Found` - Center doesn't exist
- `500 Internal Server Error` - Upload failed (with error message)

---

### 2. Download PDF Document

Download the PDF document associated with a center.

**Endpoint:** `GET /api/centri/{id}/pdf`

**Authorization:** All authenticated users

**Parameters:**
- `id` (path) - Center ID (Integer)

**Request Example:**
```bash
curl -X GET http://localhost:8080/api/centri/1/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output center-document.pdf
```

**Response (200 OK):**
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="centar-{id}.pdf"`
- Body: PDF file binary data

**Error Responses:**
- `403 Forbidden` - Not authenticated
- `404 Not Found` - Center doesn't exist or has no PDF
- `500 Internal Server Error` - Download failed

---

## Search Endpoints

### 3. Search Centers

Search centers using Elasticsearch with custom Serbian analyzer.

**Endpoint:** `GET /api/centri/search`

**Authorization:** All authenticated users

**Query Parameters:**
- `query` (required, String) - Search query text
- `type` (optional, String) - Search type, one of:
  - `all` (default) - Search all fields
  - `naziv` - Search by name only
  - `opis` - Search by description only
  - `pdf` - Search by PDF content only

**Request Examples:**

#### Search All Fields (Latin)
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=sport&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Search All Fields (Cyrillic)
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=спорт&type=all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Search by Name Only
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=partizan&type=naziv" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Search by Description Only
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=moderan&type=opis" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Search by PDF Content Only
```bash
curl -X GET "http://localhost:8080/api/centri/search?query=basketball&type=pdf" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "ime": "Sportski Centar Partizan",
    "ophis": "Moderan sportski centar",
    "datumKreacije": "2024-01-01T00:00:00",
    "adresa": "Humska 1",
    "grad": "Beograd",
    "rating": 4.5,
    "imagePath": "images/uuid.jpg",
    "pdfPath": "pdfs/uuid.pdf",
    "hasPdf": true
  },
  {
    "id": 2,
    "ime": "Arena Sport",
    "ophis": "Vrhunski sportski objekti",
    "datumKreacije": "2024-01-15T00:00:00",
    "adresa": "Narodnih heroja 23",
    "grad": "Novi Sad",
    "rating": 4.8,
    "imagePath": "images/uuid2.jpg",
    "pdfPath": null,
    "hasPdf": false
  }
]
```

**Search Features:**
- ✅ Case-insensitive (СПОРТ = спорт = Sport = sport)
- ✅ Cyrillic ↔ Latin (спорт = sport, кошарка = kosarka)
- ✅ Serbian stemming (играчи → играч)
- ✅ Stop words filtering
- ✅ Multi-field search

**Error Responses:**
- `403 Forbidden` - Not authenticated
- `400 Bad Request` - Invalid search parameters
- `500 Internal Server Error` - Search failed

---

## Admin Endpoints

### 4. Reindex All Centers

Reindex all centers in Elasticsearch. Use after bulk data imports or if search results are out of sync.

**Endpoint:** `POST /api/centri/reindex`

**Authorization:** ADMIN only

**Request Example:**
```bash
curl -X POST http://localhost:8080/api/centri/reindex \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

**Response (200 OK):**
```
All centers reindexed successfully
```

**Error Responses:**
- `403 Forbidden` - Not authenticated or not an admin
- `500 Internal Server Error` - Reindexing failed

---

## Search Query Examples

### Example 1: Basic Search
```
Query: "фудбал"
Matches: "фудбал", "ФУДБАЛ", "fudbal", "Fudbal", "FUDBAL"
```

### Example 2: Mixed Script
```
Query: "košarka"
Matches: "кошарка", "košarka", "KOŠARKA", "Košarka"
```

### Example 3: Phrase Search
```
Query: "спортски центар"
Matches centers with "спортски центар", "sportski centar", etc.
```

### Example 4: Partial Match
```
Query: "мод"
Matches: "модеран", "модерно", "модернизован"
```

### Example 5: Multi-Word Search
```
Query: "фудбал баскет"
Matches centers containing both "фудбал" AND "баскет" (in any field)
```

---

## Response Codes Summary

| Code | Description |
|------|-------------|
| 200 OK | Request successful |
| 400 Bad Request | Invalid parameters |
| 403 Forbidden | Authentication failed or insufficient permissions |
| 404 Not Found | Resource not found |
| 500 Internal Server Error | Server error (check logs) |

---

## Rate Limiting

Currently no rate limiting is implemented. Consider adding rate limiting in production.

---

## File Size Limits

- Maximum file size: 10MB (configurable in application.properties)
- Maximum request size: 10MB
- Supported image formats: JPG, PNG, GIF
- Supported document format: PDF only

---

## Best Practices

### 1. File Upload
- Always upload both image and PDF when creating a new center
- Ensure PDFs are not encrypted
- Compress images before upload
- Use descriptive filenames

### 2. Search
- Use `type=all` for general searches
- Use specific types (`naziv`, `opis`, `pdf`) when you know what you're looking for
- Keep queries concise (2-4 words optimal)
- Use Cyrillic or Latin based on your preference (both work equally)

### 3. Error Handling
- Always check response status codes
- Implement retry logic for 500 errors
- Cache search results when possible
- Handle 404 errors gracefully (center may have been deleted)

### 4. Performance
- Reindex only when necessary (after bulk imports)
- Limit search result size if needed
- Consider pagination for large result sets
- Monitor Elasticsearch cluster health

---

## Elasticsearch Analyzer Details

### Custom Serbian Analyzer Configuration

**Character Filters:**
- Cyrillic to Latin transliteration

**Tokenizer:**
- Standard tokenizer

**Token Filters:**
1. Lowercase filter
2. Serbian stop words filter
3. Serbian stemmer (light)

**Example Analysis:**
```
Input:  "КОШАРКА играчи"
Output: ["kosarka", "igrac"]
```

---

## MinIO Storage Structure

```
uesgaming/
├── images/
│   ├── uuid-1.jpg
│   ├── uuid-2.png
│   └── ...
└── pdfs/
    ├── uuid-1.pdf
    ├── uuid-2.pdf
    └── ...
```

Files are stored with UUID-based names to prevent conflicts.

---

## Integration Examples

### JavaScript/TypeScript (Angular)
```typescript
// Search centers
async searchCenters(query: string): Promise<Center[]> {
  const response = await fetch(
    `http://localhost:8080/api/centri/search?query=${encodeURIComponent(query)}&type=all`,
    {
      headers: {
        'Authorization': `Bearer ${this.token}`
      }
    }
  );
  return response.json();
}

// Upload files
async uploadFiles(centerId: number, image: File, pdf: File): Promise<Center> {
  const formData = new FormData();
  if (image) formData.append('image', image);
  if (pdf) formData.append('pdf', pdf);
  
  const response = await fetch(
    `http://localhost:8080/api/centri/${centerId}/upload`,
    {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${this.token}`
      },
      body: formData
    }
  );
  return response.json();
}

// Download PDF
downloadPdf(centerId: number): void {
  window.open(
    `http://localhost:8080/api/centri/${centerId}/pdf`,
    '_blank'
  );
}
```

### Python
```python
import requests

BASE_URL = "http://localhost:8080/api/centri"
TOKEN = "your_jwt_token"

# Search centers
def search_centers(query, search_type="all"):
    response = requests.get(
        f"{BASE_URL}/search",
        params={"query": query, "type": search_type},
        headers={"Authorization": f"Bearer {TOKEN}"}
    )
    return response.json()

# Upload files
def upload_files(center_id, image_path=None, pdf_path=None):
    files = {}
    if image_path:
        files['image'] = open(image_path, 'rb')
    if pdf_path:
        files['pdf'] = open(pdf_path, 'rb')
    
    response = requests.post(
        f"{BASE_URL}/{center_id}/upload",
        headers={"Authorization": f"Bearer {TOKEN}"},
        files=files
    )
    return response.json()

# Download PDF
def download_pdf(center_id, output_path):
    response = requests.get(
        f"{BASE_URL}/{center_id}/pdf",
        headers={"Authorization": f"Bearer {TOKEN}"}
    )
    with open(output_path, 'wb') as f:
        f.write(response.content)
```

---

## Monitoring & Debugging

### Check Elasticsearch Index
```bash
curl http://localhost:9200/centri/_count
curl http://localhost:9200/centri/_search?pretty
```

### Check MinIO Storage
```bash
# Access MinIO console
http://localhost:9001

# Or use MinIO client
mc ls local/uesgaming/images/
mc ls local/uesgaming/pdfs/
```

### Backend Logs
```bash
docker logs uesgaming-backend -f
```

---

## Support

For issues or questions:
1. Check logs: `docker logs uesgaming-backend`
2. Verify Elasticsearch: `curl http://localhost:9200/_cluster/health`
3. Verify MinIO: Open http://localhost:9001
4. Review TESTING_GUIDE.md for troubleshooting
