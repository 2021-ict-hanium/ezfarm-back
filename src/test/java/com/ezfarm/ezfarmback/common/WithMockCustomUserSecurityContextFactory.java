package com.ezfarm.ezfarmback.common;

import com.ezfarm.ezfarmback.security.UserPrincipal;
import com.ezfarm.ezfarmback.user.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = User.builder()
                .id(customUser.id())
                .name(customUser.name())
                .email(customUser.email())
                .password(customUser.password())
                .role(customUser.roles())
                .build();

        UserPrincipal principal = UserPrincipal.create(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, user.getPassword(), principal.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }
}
