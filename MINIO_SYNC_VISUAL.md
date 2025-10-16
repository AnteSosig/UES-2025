# MinIO Sync Fix - Visual Flow

## Before the Fix ❌

```
┌─────────────┐
│   Frontend  │
│  (Angular)  │
└──────┬──────┘
       │ 1. POST /novicentar (JSON only)
       │    { ime, ophis, adresa, grad, discipline }
       ↓
┌──────────────────────┐
│   Backend (Java)     │
│  CentarKontroler     │
└──────┬───────────────┘
       │ 2. Save to PostgreSQL
       ↓
┌──────────────────────┐
│    PostgreSQL DB     │
│  ✓ Centar created    │
│  ✗ No image path     │
│  ✗ No PDF path       │
└──────┬───────────────┘
       │ 3. Index to Elasticsearch
       ↓
┌──────────────────────┐
│   Elasticsearch      │
│  ✓ Partial data      │
│  ✗ No image path     │
│  ✗ No PDF content    │
└──────────────────────┘

Then separately...

┌─────────────┐
│   Frontend  │
└──────┬──────┘
       │ 4. POST /api/centri/{id}/upload
       │    (FormData with image + PDF)
       ↓
┌──────────────────────┐
│   Backend (Java)     │
└──────┬───────────────┘
       │ 5. Upload to MinIO
       ↓
┌──────────────────────┐
│      MinIO           │
│  ✓ Image stored      │
│  ✓ PDF stored        │
└──────┬───────────────┘
       │ 6. Update PostgreSQL with paths
       ↓
┌──────────────────────┐
│    PostgreSQL DB     │
│  ✓ Image path added  │
│  ✓ PDF path added    │
└──────┬───────────────┘
       │ 7. Re-index Elasticsearch
       ↓
┌──────────────────────┐
│   Elasticsearch      │
│  ✓ Complete data     │
│  ✓ Image path        │
│  ✓ PDF content       │
└──────────────────────┘

⚠️ PROBLEMS:
- Two separate API calls
- Data inconsistency between calls
- Elasticsearch indexed twice
- Delay in file availability
- Complex error handling
```

## After the Fix ✅

```
┌─────────────┐
│   Frontend  │
│  (Angular)  │
└──────┬──────┘
       │ 1. POST /novicentar (FormData)
       │    { ime, ophis, adresa, grad, discipline, image, pdf }
       ↓
┌──────────────────────┐
│   Backend (Java)     │
│  CentarKontroler     │
└──────┬───────────────┘
       │
       ├─→ 2a. Save to PostgreSQL
       │   ┌──────────────────────┐
       │   │    PostgreSQL DB     │
       │   │  ✓ Centar created    │
       │   └──────────────────────┘
       │
       ├─→ 2b. Upload to MinIO (parallel)
       │   ┌──────────────────────┐
       │   │      MinIO           │
       │   │  ✓ Image stored      │
       │   │  ✓ PDF stored        │
       │   │  ✓ PDF content extracted │
       │   └──────────────────────┘
       │
       └─→ 2c. Update PostgreSQL with paths
           ┌──────────────────────┐
           │    PostgreSQL DB     │
           │  ✓ Image path added  │
           │  ✓ PDF path added    │
           │  ✓ PDF content added │
           └──────┬───────────────┘
                  │ 3. Index to Elasticsearch (once)
                  ↓
           ┌──────────────────────┐
           │   Elasticsearch      │
           │  ✓ Complete data     │
           │  ✓ Image path        │
           │  ✓ PDF content       │
           │  ✓ All searchable    │
           └──────────────────────┘

✅ BENEFITS:
- Single atomic operation
- Immediate data consistency
- Elasticsearch indexed once with complete data
- Files available immediately
- Simple error handling
- Better performance
```

## Data Synchronization

### Before Fix
```
Time: 0s  → Centar in DB (no files)
Time: 1s  → Indexed in ES (partial)
Time: 2s  → User uploads files
Time: 3s  → Files in MinIO
Time: 4s  → DB updated with paths
Time: 5s  → Re-indexed in ES (complete)

⚠️ 5 seconds of inconsistent data!
```

### After Fix
```
Time: 0s  → User submits form
Time: 1s  → Centar in DB + Files in MinIO + Indexed in ES
         → All systems synchronized!

✅ Instant consistency!
```

## Technical Details

### Request Format Change

**Before (JSON):**
```json
POST /api/centri/novicentar
Content-Type: application/json

{
  "ime": "Gaming Center",
  "ophis": "Best gaming center",
  "adresa": "Main Street 123",
  "grad": "Belgrade",
  "discipline": ["CS:GO", "Dota 2"]
}
```

**After (FormData):**
```
POST /api/centri/novicentar
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary...

------WebKitFormBoundary...
Content-Disposition: form-data; name="ime"

Gaming Center
------WebKitFormBoundary...
Content-Disposition: form-data; name="ophis"

Best gaming center
------WebKitFormBoundary...
Content-Disposition: form-data; name="discipline"

CS:GO
------WebKitFormBoundary...
Content-Disposition: form-data; name="discipline"

Dota 2
------WebKitFormBoundary...
Content-Disposition: form-data; name="image"; filename="logo.png"
Content-Type: image/png

[binary data]
------WebKitFormBoundary...
Content-Disposition: form-data; name="pdf"; filename="info.pdf"
Content-Type: application/pdf

[binary data]
------WebKitFormBoundary...--
```

## Summary

| Aspect | Before | After |
|--------|--------|-------|
| API Calls | 2 | 1 |
| Data Consistency | Delayed | Immediate |
| ES Indexing | 2x | 1x |
| File Availability | Delayed | Immediate |
| Error Handling | Complex | Simple |
| Performance | Slower | Faster |
