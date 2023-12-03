package io.rewardsapp.repository.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository<Role> {
    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list() {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {

    }

    @Override
    public Role getRoleByUserId(Long userId) {
        return null;
    }

    @Override
    public Role getRoleByUserEmail(Long email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
