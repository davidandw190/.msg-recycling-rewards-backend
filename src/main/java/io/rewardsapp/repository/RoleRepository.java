package io.rewardsapp.repository;

import io.rewardsapp.domain.auth.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role> {
    T create(T data);

    Collection<Role> list();

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);

    void addRoleToUser(Long userId, String roleName);

    Role getRoleByUserId(Long userId);

    Role getRoleByUserEmail(Long email);

    void updateUserRole(Long userId, String roleName);

    boolean checkIfAdministrativeByUserId(Long id);
}
