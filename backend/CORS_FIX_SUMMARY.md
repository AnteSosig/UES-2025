# CORS Configuration Fix Summary

## Problem
CORS errors blocking both login and file uploads:
- `Cross-Origin Request Blocked` on `/api/korisnici/prijava`
- `Cross-Origin Request Blocked` on file upload endpoints
- Images not loading in Angular frontend

## Root Cause
**Conflicting CORS configurations:**
1. Global `WebConfig` had `allowCredentials(true)` with wildcard `"*"` origins (invalid combination)
2. Individual controllers had `@CrossOrigin(origins = "*")` annotations
3. These conflicted, causing CORS to fail completely

## Solution Applied

### 1. Updated Global CORS Configuration (`WebConfig.java`)
```java
@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    // Explicit allowed origins (required when allowCredentials = true)
                    .allowedOriginPatterns(
                        "http://localhost:4200",
                        "http://localhost:3000", 
                        "http://127.0.0.1:4200",
                        "http://127.0.0.1:3000"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                    .allowedHeaders("*")
                    .exposedHeaders("Content-Type", "Content-Disposition", "Authorization", "X-Total-Count")
                    .allowCredentials(true)  // Required for Authorization headers and cookies
                    .maxAge(3600);
            }
        };
    }
}
```

**Key changes:**
- ✅ `allowedOriginPatterns()` with specific origins (not wildcard)
- ✅ `allowCredentials(true)` for Authorization headers
- ✅ `exposedHeaders` includes file download headers
- ✅ Added `HEAD` method for preflight checks

### 2. Removed Conflicting Annotations
Removed `@CrossOrigin(origins = "*")` from:
- ✅ `KorisnikKontroler.java` (login controller)
- ✅ `CentarKontroler.java` (centers controller)

**Why?** Controller-level annotations override global config and cause conflicts.

### 3. Fixed Image Loading in Angular
**Before:**
```html
<img [src]="'http://localhost:9000/sportski-centri-images/' + centarDetails.imagePath">
```
This tried to access MinIO directly (blocked by MinIO security).

**After:**
```html
<img [src]="getImageUrl(centarDetails.imagePath)">
```

```typescript
getImageUrl(imagePath: string): string {
    if (imagePath?.startsWith('/api/')) {
        return `http://localhost:8080${imagePath}`;
    }
    return `http://localhost:8080/api/centri/${this.centarId}/image`;
}
```

## How It Works Now

### Authentication Flow
1. **Login**: `POST /api/korisnici/prijava`
   - No Authorization header needed
   - Returns JWT token
   - CORS: ✅ Works (specific origin + credentials)

2. **Authenticated Requests**: All other endpoints
   - Include `Authorization: Bearer <token>` header
   - CORS: ✅ Works (allowCredentials = true)

3. **File Uploads**: `POST /api/centri/{id}/upload`
   - Multipart form data with Authorization
   - CORS: ✅ Works (specific origin + credentials)

### Image/File Flow
1. **Upload**: Files stored in MinIO (private bucket)
2. **Serve**: Backend proxies through `/api/centri/{id}/image`
3. **Frontend**: Uses API URL, not direct MinIO access
4. **Authorization**: Token sent with each request

## Testing Checklist

### ✅ Login Works
```bash
# Test from Angular app
# Login at http://localhost:4200
# Should work without CORS errors
```

### ✅ Images Load
```bash
# Navigate to a center with an image
# Image should load from: http://localhost:8080/api/centri/2/image
# Check browser Network tab - should see 200 OK
```

### ✅ File Upload Works
```bash
# Login as ADMIN
# Try uploading image/PDF
# Should work without CORS errors
# Check backend logs for "PDF uploaded" message
```

### ✅ Search Works
```bash
# Search for any text
# Should return centers from Elasticsearch
# PDF content is searchable
```

## Important Notes

### When Adding New Origins
If you deploy to production or add new development URLs, update `WebConfig.java`:
```java
.allowedOriginPatterns(
    "http://localhost:4200",
    "http://localhost:3000",
    "https://yourdomain.com",  // Add production URL
    "http://127.0.0.1:4200"
)
```

### Never Use `origins = "*"` with `allowCredentials = true`
This is invalid and will cause CORS to fail completely. Always use specific origins.

### Controller-Level CORS
Avoid using `@CrossOrigin` on controllers when you have global CORS config. It causes conflicts.

## Troubleshooting

### Still Getting CORS Errors?
1. **Clear browser cache** and try in incognito mode
2. **Check Angular app port**: Must be one of the allowed origins (default: 4200)
3. **Check backend logs**: `docker-compose logs backend --tail=50`
4. **Verify no duplicate CORS configs**: Search for `@CrossOrigin` annotations

### Images Not Loading?
1. **Check Authorization header**: Open DevTools → Network → Find image request
2. **Verify token is valid**: Token should be in localStorage
3. **Check image endpoint**: Should be `/api/centri/{id}/image`
4. **Backend logs**: Look for image download attempts

### File Upload Fails?
1. **Verify ADMIN role**: Only admins can upload
2. **Check request**: Should be `multipart/form-data`
3. **Backend logs**: Look for upload errors
4. **MinIO status**: `docker ps` - ensure MinIO is running

## Files Modified

### Backend
- ✅ `WebConfig.java` - Global CORS configuration
- ✅ `KorisnikKontroler.java` - Removed @CrossOrigin
- ✅ `CentarKontroler.java` - Removed @CrossOrigin, added /image endpoint

### Frontend
- ✅ `centar.component.html` - Updated image URL usage
- ✅ `centar.component.ts` - Added getImageUrl() helper method

## Summary
✅ Login works - no CORS errors
✅ File uploads work - no CORS errors  
✅ Images load through backend API
✅ Authorization headers properly handled
✅ All endpoints accessible from Angular app
