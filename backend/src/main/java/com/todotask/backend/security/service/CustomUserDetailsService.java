package com.todotask.backend.security.service;

import com.todotask.backend.user.api.UserAuthInfo;
import com.todotask.backend.user.api.UserFacade;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserFacade userFacade;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) {
        UserAuthInfo authInfo;
        try {
            authInfo = userFacade.getAuthInfoByEmail(email);
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found: " + email);
        }

        return org.springframework.security.core.userdetails.User
            .withUsername(authInfo.email())
            .password(authInfo.password())
            .authorities(new SimpleGrantedAuthority("ROLE_" + authInfo.role()))
            .build();
    }
}
