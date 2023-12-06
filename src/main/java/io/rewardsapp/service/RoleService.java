package io.rewardsapp.service;

import io.rewardsapp.domain.Role;

public interface RoleService {
    Role getRoleByUserId(Long id);
}
