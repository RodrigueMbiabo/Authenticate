package com.example.authentif.security.jwt;

import com.example.authentif.security.services.UserDetailsServiceImpl;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@NoArgsConstructor
//Cette Classe est utiliser pour filtrer les demandes qui transite via les requetes http. Donc Ce  filtre s'exécute une fois par requête.
public class AuthTokenFilter extends OncePerRequestFilter {

    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService){
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    //La methode doFilterInternal redefini ici a pour objectif de
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = parseJwt(request);//ici on obtient un JWT de l' en- tête d' autorisation (en supprimant le préfixe "Bearer")
            if (jwt != null && jwtUtils.validateJwtToken(jwt)){//Verifie si le token est valide
                //proceder a l'extraction du userDetails et a son authentification.
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);//Stock toutes les informations du user au grand complet(User+autorisation)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        }catch (Exception e){
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);

    }

    //Cette méthode rend un token auquel on a retirer le prefixe "Bearer "(7 caracteres sans oublié le caractère blanc de la fin)
    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }

}
