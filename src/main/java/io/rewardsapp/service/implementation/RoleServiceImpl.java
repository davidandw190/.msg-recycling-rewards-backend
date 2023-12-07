package io.rewardsapp.service.implementation;

import io.rewardsapp.domain.Role;
import io.rewardsapp.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    @Override
    public Role getRoleByUserId(Long id) {
        return null;
    }
}
