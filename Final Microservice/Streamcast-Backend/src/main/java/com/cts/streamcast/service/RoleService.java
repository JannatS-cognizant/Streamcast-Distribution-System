package com.cts.streamcast.service;

import com.cts.streamcast.entity.Role;
import com.cts.streamcast.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepo;

    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    // CREATE ROLE
    public Role createRole(Role role) {
        return roleRepo.save(role);
    }

    // GET ALL ROLES
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }
    
    public Role getRoleById(Long id) {
        return roleRepo.findById(id).orElseThrow(() -> new RuntimeException("Role not found"));
    }

    // UPDATE ROLE
    public Role updateRole(Long id, Role roleData) {
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(roleData.getName());
        return roleRepo.save(role);
    }

    // DELETE ROLE
    public void deleteRole(Long id) {
        roleRepo.deleteById(id);
    }
}

