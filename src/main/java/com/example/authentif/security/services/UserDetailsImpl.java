package com.example.authentif.security.services;

import com.example.authentif.model.User;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//Cette classe est implementer pour avoir des informations supplementaire sur l'utilisateur(Hors mis le nom et le mot de passe deja present dans le UserDetails) comme l'email et l'id
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long idUser;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    //Utilisé pour les authorité qui ne sont rien d'autre que les differents roles assigné a un utilisateur
    public Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password, Collection<? extends GrantedAuthority> authorities){
        this.idUser = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    //methode qui nous permet d'avoir les informations au grand complet d'un utilisation (Utilisateur + role ou autorité)
    public static UserDetailsImpl build(User user){
        List<GrantedAuthority> authorities = user.getRoles().stream() //Et oui les autorité ne sont que les differents role d'un utilisateurs
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getIdUser(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    public Long getIdUser(){
        return idUser;
    }

    public String getEmail(){
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o){
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(idUser, user.idUser);
    }
}
