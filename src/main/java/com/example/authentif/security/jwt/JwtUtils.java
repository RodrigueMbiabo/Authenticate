package com.example.authentif.security.jwt;

import com.example.authentif.security.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

//Classe d'utilitaire JWT
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${example.app.jwtSecret}")//Cette valeur est contenu dans le fichier application.propperties
    private String jwtSecret;

    @Value("${example.app.jwtExpirationMs}")//Cette valeur est contenu dans le fichier application.propperties
    private int jwtExpirationMs;

    //Fonction permettant de generer le token
    public String generateJwtToken(Authentication authentication){
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+ jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    //Fonction permettant d'avoir le nom d'un utilisateur a partir du token
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();

    }

    //Fonction permettant de valider un token
    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e){
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
