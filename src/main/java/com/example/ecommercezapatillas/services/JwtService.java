package com.example.ecommercezapatillas.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.security.Key;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET_KEY = "59725073367638792F423F4428472B4B6250655368566D597133743677397A2443";

    public String getToken(UserDetails user){
        return getToken(new HashMap<>(), user);
    }
    public String getToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 1 día
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Claims getAllClaims(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())  // Define 'getKey' to get the secret key for JWT
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    
        public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = getAllClaims(token);
            return claimsResolver.apply(claims);
        }
    
        private Date getExpiration(String token) {
            return getClaim(token, Claims::getExpiration);
        }
    
        public boolean isTokenValid(String token, UserDetails userDetails) {
            final String username = getUsernameFromToken(token);
            return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        }
    
        public boolean isTokenExpired(String token) {
            return getExpiration(token).before(new Date(System.currentTimeMillis()));
        }
        public String getUsernameFromToken(String token) {
            return getClaim(token, Claims::getSubject);
        }
    
}
