package io.rewardsapp.service;

import io.rewardsapp.domain.auth.Role;

import java.util.Collection;

public interface RoleService {
    Role getRoleByUserId(Long id);

    boolean checkIfIsAdministrative(Long userId);

    Collection<Role> getRoles();
}
