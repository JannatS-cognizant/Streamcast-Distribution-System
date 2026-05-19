package com.cts.identityauth.service.impl;

import com.cts.identityauth.entity.Role;
import com.cts.identityauth.exception.ResourceNotFoundException;
import com.cts.identityauth.repository.RoleRepository;
import com.cts.identityauth.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepo;

    public RoleServiceImpl(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public Role createRole(Role role) {
        return roleRepo.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
    }

    @Override
    public Role updateRole(Long id, Role roleData) {
        Role existing = getRoleById(id);
        existing.setName(roleData.getName());
        return roleRepo.save(existing);
    }

    @Override
    public void deleteRole(Long id) {
        roleRepo.delete(getRoleById(id));
    }
}