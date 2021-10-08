package com.example.authentif.security.services;

import com.example.authentif.model.User;
import com.example.authentif.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    //Cette méthode est la redéfinition de la méthode loadUserByUsername de UserDetailsService
    //Elle est redefini ici pour pouvoir avoir ample information sur l'utilisateur
    //Car la methode loadUserByUsername de UserDetailsService ne donne que tres peu d'information sur l'utilisateur(nom+password)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)//A travers le repository on a acces a plein d'autre info du user. Car la méthode findByUsername de UserRepository nous retourne un Utilisateur(et un user c'est la donnée du "nom,email,password...")
                .orElseThrow(()->new UsernameNotFoundException("User not found with username: "+username));

        return UserDetailsImpl.build(user);//Puis a travers le "build" on peut retourner l'utilisateur au grand complet(User+role(Autorité))
    }
}
