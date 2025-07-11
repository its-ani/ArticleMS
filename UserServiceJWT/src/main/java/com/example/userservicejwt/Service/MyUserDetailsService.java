package com.example.userservicejwt.Service;

import com.example.userservicejwt.models.User;
import com.example.userservicejwt.models.UserPrincipal;
import com.example.userservicejwt.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isEmpty()) throw new UsernameNotFoundException("No user found with username: " + username);

        return new UserPrincipal(user.get());
    }
}
