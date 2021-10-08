package com.example.authentif.security;

import com.example.authentif.security.jwt.AuthEntryPointJwt;
import com.example.authentif.security.jwt.AuthTokenFilter;
import com.example.authentif.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration permet de signaler a spring que le fichier en question (WebSecurityConfig.java) est un fichier de configuration. par consequent il sera traiter differement des autres fichiers et bien sur les dependances neccessaire lui seront affecter.
@Configuration
//
@EnableWebSecurity
//@EnableWebSecurity permet à Spring de trouver et d'appliquer automatiquement à la classe la sécurité Web globale.
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@EnableGlobalMethodSecurity fournit la sécurité AOP(Lire la doc Spring security pour plus d'infos) sur les méthodes
//Il permet @PreAuthorize, @PostAuthorizeil prend également en charge JSR-250 .
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{

    private UserDetailsServiceImpl userDetailsService;

    private AuthEntryPointJwt unauthorizedHandler;

    //methode d'injection de dependace. Conseiller et bien plus pratique le @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    //Methode qui retourne le AuthTokenFilter() (voir le fichier AuthTokenFilter.java pour mieux comprendre le fonctionnement de la méthode)
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
       return new AuthTokenFilter();
   }

   //Methode qui permet en quelque sorte d'authentifier l'utlisateur a travers ses information charger dans le userDetailsService
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    //Methode d'Authentification utiliser par spring security pour renforcer la securité (toujour pour l'identification des utilisateurs)
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return super.authenticationManagerBean();
    }

    //Methode permettant de crypter le mot de passe
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*ici nous definissons a spring:
    * Comment nous souhaitons configurer notre cors et csrf
    * Si nou voulons a nos utilisateur d'utiliser un prefixe a chaque fois qu'il voudrons acceder a une service via une url
    * par exemple "antMatchers("/api/test/**").permitAll()" specifie que tout utilisateur a la possibilité et l'obligation de commencer par ce prefixe si jamais une service est demander via une url*/
    /*
    *.antMatchers("/**").permitAll() specifie tout simplement qu'aucun prefixe n'est exiger
    * .anyRequest().authenticated() specifie que tout autre access autre que ceux definie neccessite une autentification au prealable
    *
    * Si nous souhaitons ou pas exiger une authentification de nos utilisateur
    * Si oui quel filtre utiliser (ici nous utilisons 2 filtres a savoir "AuthTokenFilter" et "UsernamePasswordAuthenticationFilter"). La description de ces 2 filtre est contenu dans leur fichier respectif
    * Les gestionnaires d'exception(Dans ce cas c'est "unauthorizedHandler" qui grace a la methode "commence" empeche l'utilisateur d'accedeer a une url dont il n'a pas le droit ou l'autorisation)
    * De charger les informations de l'utilisateur afin d'effectuer l'authentification et l'autorisation. Les information de l'utilisateur sont charger grace a l'implementation du fichier "UserDetailService"
    * */
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
