package com.todotask.backend.user.api;

import java.util.Set;

public interface UserFacade {
    UserInfo getById(Long id);

    Set<UserInfo> getByIds(Set<Long> ids);
    UserAuthInfo getAuthInfoByEmail(String email);
}
