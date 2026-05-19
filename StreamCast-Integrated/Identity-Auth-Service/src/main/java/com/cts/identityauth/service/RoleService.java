package com.cts.identityauth.service;

import com.cts.identityauth.entity.Role;
import java.util.List;

public interface RoleService {
    Role createRole(Role role);
    List<Role> getAllRoles();
    Role getRoleById(Long id);
    Role updateRole(Long id, Role roleData);
    void deleteRole(Long id);
}