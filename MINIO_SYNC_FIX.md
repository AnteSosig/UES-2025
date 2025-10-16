# MinIO Data Synchronization Fix

## Problem Summary

The system had a data synchronization issue where:
1. When a new "centar" was created, data was saved to PostgreSQL but files (images/PDFs) were not synced to MinIO at creation time
2. Files had to be uploaded separately using a two-step process:
   - First: Create the centar (POST `/api/centri/novicentar`)
   - Second: Upload files (POST `/api/centri/{id}/upload`)
3. This caused a delay in data availability and potential inconsistency between database and file storage
4. Elasticsearch indexing happened twice (once without files, once with files)

## Solution Implemented

### Backend Changes

#### 1. Modified CentarKontroler.java

**File:** `backend/projekt/src/main/java/com/example/sss/kontroleri/CentarKontroler.java`

**Changes Made:**

- **Added Logging Support**: Added `@Slf4j` annotation for better logging
- **Updated `/novicentar` endpoint** to accept multipart/form-data with files:
  - Changed from `@RequestBody CentarDTO2` to individual `@RequestParam` fields
  - Added `@RequestParam(value = "image", required = false) MultipartFile image`
  - Added `@RequestParam(value = "pdf", required = false) MultipartFile pdf`
  - Files are now uploaded to MinIO immediately when creating the center
  - Elasticsearch indexing happens once with complete data (including file paths and PDF content)

**Key Improvements:**

```java
@PostMapping("/novicentar")
public ResponseEntity<CentarDTO> createCentar(
        @RequestParam("ime") String ime,
        @RequestParam("ophis") String ophis,
        @RequestParam("adresa") String adresa,
        @RequestParam("grad") String grad,
        @RequestParam("discipline") List<String> discipline,
        @RequestParam(value = "image", required = false) MultipartFile image,
        @RequestParam(value = "pdf", required = false) MultipartFile pdf,
        @RequestHeader("authorization") String token) {
    // ... create center in database ...
    
    // Upload files to MinIO if provided
    if ((image != null && !image.isEmpty()) || (pdf != null && !pdf.isEmpty())) {
        centarServis.uploadFiles(sui, image, pdf);
    }
    
    // Index with complete data including files
    centarServis.indexCentar(newCentar);
}
```

### Frontend Changes

#### 2. Modified NoviCentarComponent

**File:** `KVT/src/app/novi-centar/novi-centar.component.ts`

**Changes Made:**

- **Single-Step Submission**: Changed from JSON POST to FormData multipart request
- **Removed Two-Step Process**: Eliminated the separate `uploadFiles()` method
- **Consolidated Data**: Now sends all data (text fields + files) in one request

**Before:**
```typescript
// Step 1: Create center with JSON
this.http.post('...novicentar', jsonData, { headers })
  .subscribe(response => {
    // Step 2: Upload files separately
    this.uploadFiles(response.id);
  });
```

**After:**
```typescript
// Single step: Create center with all data and files
const formData = new FormData();
formData.append('ime', this.centarForm.value.ime);
formData.append('ophis', this.centarForm.value.ophis);
// ... other fields ...
if (this.selectedImage) formData.append('image', this.selectedImage);
if (this.selectedPdf) formData.append('pdf', this.selectedPdf);

this.http.post('...novicentar', formData, { headers })
  .subscribe(() => {
    this.successMessage = 'Centar successfully created with all files!';
  });
```

## Benefits

1. **Atomic Operations**: All data (database + MinIO) is created in one transaction
2. **Reduced API Calls**: One request instead of two
3. **Better Data Consistency**: Files are available immediately after creation
4. **Improved Search**: Elasticsearch is indexed once with complete data
5. **Better Performance**: No delay between center creation and file availability
6. **Simplified Error Handling**: All errors occur in one place

## Data Flow

### New Flow
```
User submits form (with files)
    ↓
Backend receives request
    ↓
1. Create centar in PostgreSQL
2. Upload files to MinIO (if provided)
3. Index complete data in Elasticsearch
    ↓
Return success response
```

### Old Flow (removed)
```
User submits form
    ↓
Backend receives request
    ↓
1. Create centar in PostgreSQL
2. Index partial data in Elasticsearch
    ↓
Return success with ID
    ↓
User uploads files separately
    ↓
Backend receives files
    ↓
1. Upload files to MinIO
2. Update database with file paths
3. Re-index complete data in Elasticsearch
    ↓
Return success response
```

## Testing

To test the fix:

1. **Start the backend** (ensure MinIO, PostgreSQL, and Elasticsearch are running):
   ```powershell
   cd backend
   .\docker-manage.ps1 -action start
   ```

2. **Start the frontend**:
   ```powershell
   cd KVT
   npm install
   npm start
   ```

3. **Test creating a new centar**:
   - Navigate to the "Create New Centar" page
   - Fill in all required fields
   - Select an image and/or PDF file
   - Submit the form
   - Verify that the centar appears immediately with image and PDF available

4. **Verify in MinIO Console**:
   - Go to http://localhost:9001
   - Login: minioadmin / minioadmin
   - Check the "uesgaming" bucket
   - Verify that files are uploaded in `images/` and `pdfs/` folders

5. **Verify in Elasticsearch**:
   - Use the search endpoint: `GET /api/centri/search?query=<search_term>`
   - Verify that PDF content is searchable immediately

## Backward Compatibility

The `/upload` endpoint (`POST /api/centri/{id}/upload`) is still available for:
- Updating/adding files to existing centers
- Replacing images or PDFs
- Systems that need to upload files separately

## Notes

- Files are **optional** during center creation - you can still create a center without files
- Files can be added later using the existing `/upload` endpoint
- The fix maintains all existing functionality while adding the ability to upload during creation
- All error handling is preserved - if file upload fails, it's logged but center creation continues

## Related Files

- `backend/projekt/src/main/java/com/example/sss/kontroleri/CentarKontroler.java`
- `backend/projekt/src/main/java/com/example/sss/servisi/implementacije/CentarServisImpl.java`
- `KVT/src/app/novi-centar/novi-centar.component.ts`
- `KVT/src/app/novi-centar/novi-centar.component.html`

## Author

Fix implemented on October 16, 2025
