# Troubleshooting Guide - MinIO File Upload Issues

## Quick Diagnosis Checklist

### 1. Is the Backend Running?
```powershell
# Check if backend container is running
docker ps | Select-String backend

# Check backend logs
docker logs uesgaming-backend --tail 50
```

### 2. Is MinIO Running?
```powershell
# Check if MinIO is running
docker ps | Select-String minio

# Check MinIO logs
docker logs uesgaming-minio --tail 50

# Test MinIO console access
# Open browser: http://localhost:9001
# Login: minioadmin / minioadmin
```

### 3. Check Browser Console
Open browser Developer Tools (F12) and look for:
- Network tab: Check if request is being sent
- Console tab: Look for JavaScript errors
- Look for CORS errors

### 4. Check Backend Logs for File Upload
After attempting to create/upload, look for these messages:

**Expected Success Messages:**
```
INFO - Create centar request received: ime=XXX, grad=YYY
INFO - Files provided - Image: true, PDF: true
INFO - Centar created with ID: 5
INFO - Uploading files for centar ID: 5
INFO - Files uploaded successfully for centar ID: 5
```

**Expected Error Messages (if something fails):**
```
ERROR - Error extracting email from token
ERROR - Failed to upload files for new center: [error details]
WARN - No email found in token
WARN - User not found or inactive
```

## Common Error Scenarios

### Error: "Http failure response: 0 Unknown Error"

**What it means:** Browser couldn't connect to backend or got CORS error

**Checklist:**
- [ ] Is backend running? (`docker ps`)
- [ ] Can you access http://localhost:8080 in browser?
- [ ] Check browser console for CORS errors
- [ ] Check if backend crashed (look at logs)
- [ ] Try restarting backend: `docker restart uesgaming-backend`

### Error: "Authentication required" or 403 Forbidden

**What it means:** Token is invalid or user doesn't have admin rights

**Checklist:**
- [ ] Are you logged in?
- [ ] Check token: Open browser console, type: `localStorage.getItem('token')`
- [ ] Is token expired? Try logging out and back in
- [ ] Are you logged in as ADMIN user?
- [ ] Check backend logs for "No email found in token"

### Error: "Failed to upload files: [some error]"

**What it means:** Upload started but failed during processing

**Checklist:**
- [ ] Check backend logs for detailed error
- [ ] Is MinIO running? (`docker ps | Select-String minio`)
- [ ] Can you access MinIO console? (http://localhost:9001)
- [ ] Are files too large? (Max 10MB per file)
- [ ] Is file actually a PDF/Image? Check file extension

### Files Not Appearing in MinIO

**Checklist:**
- [ ] Check backend logs for "Files uploaded successfully"
- [ ] Open MinIO console: http://localhost:9001
- [ ] Login: minioadmin / minioadmin
- [ ] Click on "uesgaming" bucket
- [ ] Look in "images/" and "pdfs/" folders
- [ ] If bucket doesn't exist, backend will create it on first upload

### Frontend Says Success but Files Not Uploaded

**Checklist:**
- [ ] Check if frontend is actually sending files
- [ ] Open browser Network tab, find the request
- [ ] Check Request Payload - should contain FormData with files
- [ ] Check backend logs to see if files were received
- [ ] Look for "Image present: true, PDF present: true" in logs

## How to Restart Everything

```powershell
# Stop all containers
cd backend
.\docker-manage.ps1 -action stop

# Start all containers
.\docker-manage.ps1 -action start

# Wait for everything to start (30 seconds)
Start-Sleep -Seconds 30

# Check all containers are running
docker ps
```

## How to Check if Request is Being Sent Correctly

1. Open browser Developer Tools (F12)
2. Go to Network tab
3. Attempt to create centar with files
4. Find the request to `/api/centri/novicentar`
5. Check:
   - **Request Method:** Should be POST
   - **Content-Type:** Should be `multipart/form-data`
   - **Request Payload:** Should show FormData with all fields including files
   - **Authorization header:** Should be present

Example of correct request:
```
POST http://localhost:8080/api/centri/novicentar
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary...

Headers:
  authorization: Bearer eyJhbGc...

Form Data:
  ime: Test Center
  ophis: Description
  adresa: Address 123
  grad: Belgrade
  discipline: CS:GO
  discipline: Dota 2
  image: (binary) logo.png
  pdf: (binary) info.pdf
```

## How to Test Backend Directly with PowerShell

```powershell
# First, get your token (login via frontend, then):
# Open browser console and run: localStorage.getItem('token')
# Copy the token

# Test upload endpoint
$token = "paste-your-token-here"
$centarId = 5

# Create multipart form
$uri = "http://localhost:8080/api/centri/$centarId/upload"
$headers = @{
    "authorization" = $token
}

# Prepare files
$imagePath = "C:\path\to\your\image.jpg"
$pdfPath = "C:\path\to\your\document.pdf"

# Build form data
$formData = @{
    image = Get-Item $imagePath
    pdf = Get-Item $pdfPath
}

# Send request
try {
    $response = Invoke-RestMethod -Uri $uri -Method POST -Headers $headers -Form $formData
    Write-Host "Success! Response:" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "Error:" -ForegroundColor Red
    Write-Host $_.Exception.Message
    Write-Host "Status Code:" $_.Exception.Response.StatusCode.value__
}
```

## Verify End-to-End

1. **Create Centar with Files:**
   - Go to "Create New Centar" form
   - Fill in: Name, Description, Address, City, Disciplines
   - Select an image (JPG/PNG)
   - Select a PDF
   - Click Submit
   - Wait for success message

2. **Check Backend Logs:**
   ```powershell
   docker logs uesgaming-backend --tail 20
   ```
   Should see:
   ```
   INFO - Create centar request received...
   INFO - Files provided - Image: true, PDF: true
   INFO - Centar created with ID: X
   INFO - Uploading files for centar ID: X
   INFO - Files uploaded successfully for centar ID: X
   ```

3. **Check MinIO:**
   - Open http://localhost:9001
   - Login: minioadmin / minioadmin
   - Click "uesgaming" bucket
   - Navigate to "images/" - should see uploaded image
   - Navigate to "pdfs/" - should see uploaded PDF

4. **Check Database:**
   ```sql
   SELECT id, ime, image_path, pdf_path FROM centri WHERE id = X;
   ```
   Should show paths like:
   - `images/uuid.jpg`
   - `pdfs/uuid.pdf`

5. **Check Elasticsearch:**
   - Use search endpoint or check logs
   - Should be indexed with PDF content

## Still Not Working?

If you've checked everything above and it's still not working:

1. **Restart everything:**
   ```powershell
   cd backend
   .\docker-manage.ps1 -action restart
   ```

2. **Check Docker logs for all services:**
   ```powershell
   docker logs uesgaming-backend
   docker logs uesgaming-minio
   docker logs uesgaming-elasticsearch
   docker logs uesgaming-db
   ```

3. **Verify file in backend container:**
   ```powershell
   docker exec -it uesgaming-backend ls -la /
   ```

4. **Check application.properties:**
   Make sure these settings are present:
   ```properties
   spring.servlet.multipart.enabled=true
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=10MB
   ```

5. **If all else fails, share:**
   - Backend logs (last 50 lines)
   - Browser console errors
   - Network request details (from browser dev tools)
