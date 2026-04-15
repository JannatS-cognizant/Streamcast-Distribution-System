package com.Cts.service;

import com.Cts.entity.Role;
import com.Cts.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // ✅ Create a new role
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // ✅ Fetch all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // ✅ Fetch role by ID
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    // ✅ Update role name
    public Role updateRole(Long id, Role updatedRole) {
        Role role = getRoleById(id);
        role.setName(updatedRole.getName());
        return roleRepository.save(role);
    }

    // ✅ Delete role
    public void deleteRole(Long id) {
        Role role = getRoleById(id);
        roleRepository.delete(role);
    }
}