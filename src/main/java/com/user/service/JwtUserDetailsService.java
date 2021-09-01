package com.user.service;

import com.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.user.model.User user;
        List<com.user.model.User> userList = userRepository.findByUsername(username);
        if (userList == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else {
            user = userList.get(0);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                new ArrayList<>());
    }

    public com.user.model.User createUser(com.user.model.User user) {
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public com.user.model.User findByUsername(String username) {
        List<com.user.model.User> list = userRepository.findByUsername(username);
        return list.stream().filter(x -> !x.getUsername().isEmpty())
                .findFirst().orElse(new com.user.model.User());
    }
}
