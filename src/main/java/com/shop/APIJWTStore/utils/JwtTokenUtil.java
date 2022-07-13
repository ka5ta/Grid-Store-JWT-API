package com.shop.APIJWTStore.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shop.APIJWTStore.dto.ErrorResponseDTO;
import com.shop.APIJWTStore.failureHandler.InstantAdapter;
import com.shop.APIJWTStore.model.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import org.springframework.http.HttpHeaders;

@Slf4j
@Component
public class JwtTokenUtil implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567L;
    //5 minutes
    public static final long JWT_TOKEN_VALIDITY_TIME = 5 * 60 * 1000;

    @Value("${jwt.secret}")
    private String keyString;

    private final String PREFIX = "Bearer ";

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(keyString));
    }

    //generate token for authenticated user
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY_TIME))
                .signWith(getSecretKey())
                .compact();
    }

    public String getToken(HttpServletRequest request)  {
        String requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // Removes "Bearer "  and get only the Token String
        if (requestTokenHeader != null && requestTokenHeader.startsWith(PREFIX)) {
            return requestTokenHeader.replace(PREFIX, "");
        } else {
            return null;
        }
    }

    //for retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsExtraction) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsExtraction.apply(claims);
    }

    //retrieve email from jwt token
    public String getEmailFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getTokenExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        Date expiration = getTokenExpirationDate(token);
        return expiration.before(new Date());
    }

    //validate token
    public Boolean isValidToken(String token, UserDetails userDetails) {
        String emailFromToken = getEmailFromToken(token);
        return (emailFromToken.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public UsernamePasswordAuthenticationToken createAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        usernamePasswordAuthenticationToken
                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return usernamePasswordAuthenticationToken;
    }

    public void invalidateTokens(Account account){
        //todo delete token or invalidate
    }


}
