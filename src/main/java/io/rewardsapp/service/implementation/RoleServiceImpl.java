package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.auth.Role;
import io.rewardsapp.repository.RoleRepository;
import io.rewardsapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository<Role> roleRoleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return roleRoleRepository.getRoleByUserId(id);
    }

    @Override
    public boolean checkIfIsAdministrative(Long userId) {
        return roleRoleRepository.checkIfAdministrativeByUserId(userId);
    }

    @Override
    public Collection<Role> getRoles() {
        return roleRoleRepository.list();
    }
}
