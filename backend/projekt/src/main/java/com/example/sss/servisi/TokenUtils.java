package com.example.sss.servisi;

import com.example.sss.model.Korisnik;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenUtils {

    @Value("${jwt.secretKey}") // Na?
    private String jwtSecretKey = "aB3xZ8q1kL9oP2yH6rT7iG4jN5sD0fE1cV9bM8uY3wX2aB3xZ8q1kL9oP2yH6rT7iG4jN5sD0fE1cV9bM8uY3wX2";

    // JWT token metod
    public String generateJwtToken(Korisnik korisnik) {
        long expirationTime = 300000;
        System.out.println(jwtSecretKey);
        for (int m = 0; m < 10; m++) {
            System.out.println("MMMMMMMMMMMM");
        }

        return Jwts.builder()
                .setSubject(korisnik.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, jwtSecretKey)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(this.jwtSecretKey).parseClaimsJws(token).getBody();
            System.out.println(claims.getExpiration());
            System.out.println(claims.getSubject());
            System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKK");
        } catch (Exception e) {
            claims = null;
            System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKKK");
        }
        return claims;
    }

}
