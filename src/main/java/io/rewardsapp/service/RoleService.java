package io.rewardsapp.service;

import io.rewardsapp.domain.Role;
import io.rewardsapp.dto.UserDTO;

import java.util.Collection;

public interface RoleService {
    Role getRoleByUserId(Long id);

    boolean checkIfIsAdministrative(Long userId);

    Collection<Role> getRoles();
}
