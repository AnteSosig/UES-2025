# Fixed: MinIO Data Sync Issues (Updated)

## Issues Found and Fixed

### Issue 1: Files Not Added Upon Creation ❌
**Problem:** The `/novicentar` endpoint was declared to accept multipart form data, but was missing the `consumes` attribute, causing the server to reject the multipart requests.

**Solution:** Added `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` to the endpoint annotation.

### Issue 2: Upload Endpoint Failing ❌
**Problem:** The `/{id}/upload` endpoint was returning "Http failure response: 0 Unknown Error" due to:
- Missing `consumes` attribute for multipart data
- Poor error handling and logging
- Potential token parsing issues

**Solution:** 
- Added `consumes = MediaType.MULTIPART_FORM_DATA_VALUE`
- Improved error handling with detailed error messages
- Added comprehensive logging
- Made authorization header optional in annotation (still checked in code)

## Changes Made

### Backend: `CentarKontroler.java`

#### 1. Fixed `/novicentar` Endpoint

**Before:**
```java
@PostMapping("/novicentar")
public ResponseEntity<CentarDTO> createCentar(
    @RequestParam("ime") String ime,
    // ... other params
    @RequestParam(value = "image", required = false) MultipartFile image,
    @RequestParam(value = "pdf", required = false) MultipartFile pdf,
    @RequestHeader("authorization") String token) {
```

**After:**
```java
@PostMapping(value = "/novicentar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<CentarDTO> createCentar(
    @RequestParam("ime") String ime,
    // ... other params
    @RequestParam(value = "image", required = false) MultipartFile image,
    @RequestParam(value = "pdf", required = false) MultipartFile pdf,
    @RequestHeader("authorization") String token) {
    
    log.info("Create centar request received: ime={}, grad={}", ime, grad);
    log.info("Files provided - Image: {}, PDF: {}", 
        image != null && !image.isEmpty(), 
        pdf != null && !pdf.isEmpty());
    // ... rest of implementation with better logging
```

#### 2. Fixed `/{id}/upload` Endpoint

**Before:**
```java
@PostMapping("/{id}/upload")
public ResponseEntity<?> uploadFiles(
    @PathVariable Integer id,
    @RequestParam(value = "image", required = false) MultipartFile image,
    @RequestParam(value = "pdf", required = false) MultipartFile pdf,
    @RequestHeader("authorization") String token) {
    
    // Minimal error handling
    try {
        Centar updatedCentar = centarServis.uploadFiles(id, image, pdf);
        return ResponseEntity.ok(updatedCentar);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error uploading files: " + e.getMessage());
    }
}
```

**After:**
```java
@PostMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> uploadFiles(
    @PathVariable Integer id,
    @RequestParam(value = "image", required = false) MultipartFile image,
    @RequestParam(value = "pdf", required = false) MultipartFile pdf,
    @RequestHeader(value = "authorization", required = false) String token) {
    
    log.info("Upload request received for centar ID: {}", id);
    log.info("Image present: {}, PDF present: {}", 
        image != null && !image.isEmpty(), 
        pdf != null && !pdf.isEmpty());

    String email = null;
    try {
        email = tokenUtils.getClaimsFromToken(token).getSubject();
    } catch (Exception e) {
        log.error("Error extracting email from token", e);
    }

    if (email == null) {
        log.warn("No email found in token");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authentication required");
    }
    
    // ... better error handling and logging throughout
```

## Key Improvements

✅ **Added `consumes = MediaType.MULTIPART_FORM_DATA_VALUE`** to both endpoints
✅ **Comprehensive logging** for debugging
✅ **Better error messages** returned to client
✅ **Proper exception handling** with stack traces in logs
✅ **Token validation logging** to identify auth issues

## Testing the Fix

### 1. Test Creating New Centar with Files

```bash
# Using curl (PowerShell)
$token = "your-jwt-token-here"
$uri = "http://localhost:8080/api/centri/novicentar"

# Create form data
$formData = @{
    ime = "Test Gaming Center"
    ophis = "Best center in town"
    adresa = "Main Street 123"
    grad = "Belgrade"
    discipline = @("CS:GO", "Dota 2")
    image = Get-Item "C:\path\to\image.jpg"
    pdf = Get-Item "C:\path\to\document.pdf"
}

# Send request
Invoke-RestMethod -Uri $uri -Method POST -Headers @{authorization=$token} -Form $formData
```

### 2. Test Uploading Files to Existing Centar

```bash
# Using curl (PowerShell)
$token = "your-jwt-token-here"
$centarId = 5
$uri = "http://localhost:8080/api/centri/$centarId/upload"

$formData = @{
    image = Get-Item "C:\path\to\image.jpg"
    pdf = Get-Item "C:\path\to\document.pdf"
}

Invoke-RestMethod -Uri $uri -Method POST -Headers @{authorization=$token} -Form $formData
```

### 3. Check Backend Logs

After making requests, check the backend console for logs like:
```
INFO - Create centar request received: ime=Test Gaming Center, grad=Belgrade
INFO - Files provided - Image: true, PDF: true
INFO - Centar created with ID: 5
INFO - Uploading files for centar ID: 5
INFO - Files uploaded successfully for centar ID: 5
INFO - Centar indexed successfully in Elasticsearch: 5
```

Or for uploads:
```
INFO - Upload request received for centar ID: 5
INFO - Image present: true, PDF present: true
INFO - Files uploaded successfully for centar ID: 5
```

### 4. Verify in MinIO

1. Open MinIO Console: http://localhost:9001
2. Login: minioadmin / minioadmin
3. Navigate to `uesgaming` bucket
4. Check `images/` and `pdfs/` folders for uploaded files

### 5. Test from Frontend

1. **Create New Centar:**
   - Go to "Create New Centar" page
   - Fill in all fields
   - Select image and PDF
   - Submit
   - Check browser console and backend logs for any errors

2. **Edit Existing Centar:**
   - Go to edit page for a centar
   - Select new image and/or PDF
   - Click upload
   - Should see success message

## Common Issues and Solutions

### Issue: "Http failure response: 0 Unknown Error"

**Causes:**
1. Backend not running
2. CORS preflight failure
3. Network connectivity issue
4. Backend crashed during request

**Solutions:**
1. Check backend is running: `docker ps` or check console
2. Check backend logs for errors
3. Verify URL is correct: `http://localhost:8080`
4. Check browser console for CORS errors
5. Restart backend if needed

### Issue: "Authentication required" or 403 Forbidden

**Causes:**
1. Invalid or expired token
2. User not logged in as admin

**Solutions:**
1. Log out and log back in to refresh token
2. Verify you're logged in as an admin user
3. Check browser localStorage for token: `localStorage.getItem('token')`

### Issue: Files not appearing in MinIO

**Causes:**
1. MinIO service not running
2. MinIO connection configuration wrong
3. Upload failed silently

**Solutions:**
1. Check MinIO is running: `docker ps | Select-String minio`
2. Check MinIO logs: `docker logs uesgaming-minio`
3. Check backend logs for upload errors
4. Verify MinIO configuration in application.properties

## Backend Logs to Monitor

When testing, watch for these log messages:

**Success Pattern:**
```
INFO - Create centar request received: ime=..., grad=...
INFO - Files provided - Image: true, PDF: true
INFO - Centar created with ID: X
INFO - Uploading files for centar ID: X
INFO - Files uploaded successfully for centar ID: X
INFO - Centar indexed successfully in Elasticsearch: X
```

**Error Pattern:**
```
ERROR - Error extracting email from token
ERROR - Failed to upload files for new center: ...
ERROR - Failed to index new center: ...
```

## Summary of Fixes

| Issue | Root Cause | Fix Applied |
|-------|-----------|-------------|
| Files not added on creation | Missing `consumes` attribute | Added `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` |
| Upload endpoint failing | Missing `consumes` + poor error handling | Added `consumes` + comprehensive logging |
| Unclear errors | Generic error messages | Detailed error messages and logging |
| Debugging difficulty | No logging | Added log statements at every step |

## Files Modified

- `backend/projekt/src/main/java/com/example/sss/kontroleri/CentarKontroler.java`

## Next Steps

1. Restart the backend application
2. Test creating a new centar with files
3. Test uploading files to existing centar
4. Check backend logs for success messages
5. Verify files in MinIO console
6. Verify data is searchable via Elasticsearch

If issues persist, check the backend logs first - they now provide detailed information about what's happening at each step.
