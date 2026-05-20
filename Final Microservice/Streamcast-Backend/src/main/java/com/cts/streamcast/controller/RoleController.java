package com.cts.streamcast.controller;

import com.cts.streamcast.entity.Role;
import com.cts.streamcast.service.RoleService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return service.createRole(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Role> getRoles() {
        return service.getAllRoles();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return service.getRoleById(id);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role) {
        return service.updateRole(id, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        service.deleteRole(id);
        return "Role deleted successfully";
    }
}
