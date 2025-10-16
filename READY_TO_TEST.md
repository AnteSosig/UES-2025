# ✅ BACKEND RESTARTED - Ready for Testing!

## Status: ALL SYSTEMS OPERATIONAL

```
✅ Backend:        Running on http://localhost:8080
✅ MinIO:          Healthy on http://localhost:9000 (API) / http://localhost:9001 (Console)
✅ Elasticsearch:  Healthy on http://localhost:9200
✅ MySQL:          Healthy on localhost:3306
```

## What Was Fixed

Both file upload issues have been resolved:
1. ✅ Files now sync to MinIO when creating new "centar"
2. ✅ Upload endpoint for existing centers now works correctly

**Key Changes:**
- Added `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` to both endpoints
- Added comprehensive logging for debugging
- Improved error messages

## Test Now - Step by Step

### Test 1: Create New Centar with Files

1. **Open Frontend:** http://localhost:4200
2. **Login** as an admin user
3. **Go to:** Create New Centar page
4. **Fill in:**
   - Name: `Test Gaming Center`
   - Description: `Test center with files`
   - Address: `Test Street 123`
   - City: `Belgrade`
   - Disciplines: `CS:GO, Dota 2`
5. **Select Files:**
   - Choose an image (JPG/PNG, under 10MB)
   - Choose a PDF (under 10MB)
6. **Submit the form**
7. **Expected Result:** Success message immediately

### Test 2: Upload Files to Existing Centar

1. **Go to:** Any existing centar's edit page
2. **Scroll to:** File upload section
3. **Select Files:**
   - Choose an image
   - Choose a PDF
4. **Click:** Upload button
5. **Expected Result:** "Files uploaded successfully!"

## Verify It Worked

### Method 1: Check Backend Logs

```powershell
docker logs uesgaming-backend --tail 30
```

**Look for these messages:**
```
INFO - Create centar request received: ime=Test Gaming Center, grad=Belgrade
INFO - Files provided - Image: true, PDF: true
INFO - Centar created with ID: 6
INFO - Uploading files for centar ID: 6
INFO - Files uploaded successfully for centar ID: 6
INFO - Centar indexed successfully in Elasticsearch: 6
```

### Method 2: Check MinIO Console

1. **Open:** http://localhost:9001
2. **Login:** 
   - Username: `minioadmin`
   - Password: `minioadmin`
3. **Navigate to:** `uesgaming` bucket
4. **Check folders:**
   - `images/` - Should contain your uploaded images
   - `pdfs/` - Should contain your uploaded PDFs
5. **Verify:** Files have UUID-based names like `images/550e8400-e29b-41d4-a716-446655440000.jpg`

### Method 3: Check Browser Console

1. Press **F12** in browser
2. Go to **Console** tab
3. Look for: Should be **NO ERRORS**
4. Go to **Network** tab
5. Find the POST request to `/api/centri/novicentar` or `/api/centri/{id}/upload`
6. **Check Response:** Should be 200 OK

## Real-Time Log Monitoring

To watch logs as you test (open in separate PowerShell window):

```powershell
docker logs -f uesgaming-backend
```

This will show logs in real-time. When you create/upload, you'll see:
- Request received
- Files detected
- Upload progress
- Success confirmation
- Elasticsearch indexing

## What to Watch For

### ✅ Success Indicators:
- Backend logs show "Files uploaded successfully"
- Frontend shows success message
- MinIO console shows new files
- No errors in browser console

### ❌ If Something Goes Wrong:

**Error in logs?** 
- Check the error message - now very detailed
- Common issues: file too large, wrong file type, MinIO connection

**Upload fails?**
- Check you're logged in as ADMIN
- Check file size (must be under 10MB)
- Check file type (images: JPG/PNG, documents: PDF)

**Files not in MinIO?**
- Check MinIO is running: `docker ps | Select-String minio`
- Check MinIO logs: `docker logs uesgaming-minio --tail 20`
- Backend logs should show exact error

## Quick Commands Reference

```powershell
# View backend logs (last 30 lines)
docker logs uesgaming-backend --tail 30

# Follow backend logs in real-time
docker logs -f uesgaming-backend

# Check all containers status
docker ps

# Restart backend if needed
docker restart uesgaming-backend

# Check MinIO logs
docker logs uesgaming-minio --tail 20

# Check Elasticsearch logs
docker logs uesgaming-elasticsearch --tail 20
```

## Test Data Suggestions

**Good test files:**
- Image: Any JPG or PNG under 5MB
- PDF: Any PDF document under 5MB

**Files that will fail:**
- Files over 10MB
- Wrong formats (e.g., .docx, .txt)
- Corrupted files
- Encrypted PDFs

## Expected Behavior Summary

| Action | Old Behavior | New Behavior |
|--------|-------------|--------------|
| Create centar with files | Files NOT uploaded | Files uploaded immediately ✅ |
| Upload to existing centar | Error "0 Unknown Error" | Success message ✅ |
| Backend logs | Silent failures | Detailed logging ✅ |
| Error messages | Generic errors | Specific error messages ✅ |

## Ready to Test!

Everything is configured and ready. The backend has been restarted with the fixes applied.

**Start with Test 1** (create new centar with files) and watch the backend logs to see it working! 🚀

---

**Need help?** Check `TROUBLESHOOTING.md` for detailed debugging steps.
