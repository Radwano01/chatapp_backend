package com.project.chatApp.config;

import com.project.chatApp.user.Role;

import com.project.chatApp.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrId) throws UsernameNotFoundException {
        var savedUser = userRepository.findById(usernameOrId)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found"));
        return new User(savedUser.getId(),savedUser.getPassword(), mapAuthority(savedUser.getRole()));
    }

    private Collection<GrantedAuthority> mapAuthority(Role role){
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }
}
