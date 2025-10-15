# Image Display Fix - Simple Solution

## The Problem
Images not displaying because the `/api/centri/{id}/image` endpoint required authentication, but HTML `<img>` tags can't send Authorization headers.

## The Solution
**Made the image endpoint public** - removed authentication requirement so images can be accessed by anyone.

### Backend Change (`CentarKontroler.java`)

**Before:**
```java
@GetMapping("/{id}/image")
public ResponseEntity<?> getImage(
        @PathVariable Integer id,
        @RequestHeader("authorization") String token) {
    
    // Validate token...
    // Check user is active...
    
    // Then serve image
}
```

**After:**
```java
@GetMapping("/{id}/image")
public ResponseEntity<?> getImage(@PathVariable Integer id) {
    // No authentication - public access
    // Directly serve image from MinIO
}
```

### Angular (`centar.component.ts`)

```typescript
getImageUrl(imagePath: string): string {
  if (!imagePath) {
    return '';
  }
  // Simple - just return the public API URL
  return `http://localhost:8080/api/centri/${this.centarId}/image`;
}
```

## Why This Works

1. **Backend serves images publicly** at `/api/centri/{id}/image`
2. **Angular `<img>` tag** can make simple GET request (no auth needed)
3. **Images display** without any CORS or authentication issues

## Testing

1. **Refresh your Angular app**
2. **Navigate to any center** with an image
3. **Image should display** immediately

The image URL will be: `http://localhost:8080/api/centri/2/image`

## Note
Images are now publicly accessible - anyone can view center images without logging in. This is fine for a public-facing sports center app where images are meant to be viewed by everyone.

