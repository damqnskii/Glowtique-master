package com.glowtique.glowtique.user.model;

import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new AuthenticationMetadata(user.getId(), user.getEmail(), user.getPassword(), user.getRole(), user.isActive());
    }
}
