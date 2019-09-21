package com.kakaopay.homework.service;

import com.kakaopay.homework.model.User;
import com.kakaopay.homework.model.UserDetailsImpl;
import com.kakaopay.homework.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    enum ROLE {
        ADMIN(1, "ROLE_ADMIN"),
        USER(2, "ROLE_USER"),
        UNAUTHORIZED(3, "");

        private final int roleCode;

        private final String roleString;

        ROLE (int roleCode, String roleString) {
            this.roleCode = roleCode;
            this.roleString = roleString;
        }

        public int getRoleCode () {
            return roleCode;
        }

        public String getRoleString () {
            return roleString;
        }

        public Set<GrantedAuthority> toGrantedAuthority () {
            return getRoleSet(this).stream()
                    .map(role -> new SimpleGrantedAuthority(role))
                    .collect(Collectors.toSet());
        }

        public static ROLE of (int roleCode) {
            return Stream.of(ROLE.values())
                    .filter(role -> role.roleCode == roleCode)
                    .findFirst()
                    .orElse(UNAUTHORIZED);
        }

        public static Set<String> getRoleSet (int roleCode) {
            return getRoleSet(of(roleCode));
        }

        public static Set<String> getRoleSet (ROLE role) {
            String[] arr = role.roleString.split(",");

            Set<String> result = new LinkedHashSet<>();

            Stream.of(arr).filter(r ->!r.isEmpty()).forEach(r -> result.add(r));

            return result;
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Not found user : " + username);
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        if (user.getUserType() == ROLE.ADMIN.getRoleCode()) {
            userDetails.setAuthorities(ROLE.getRoleSet(ROLE.ADMIN).stream().collect(Collectors.toList()));
        }
        else {
            userDetails.setAuthorities(ROLE.getRoleSet(ROLE.USER).stream().collect(Collectors.toList()));
        }

        return userDetails;
    }
}

