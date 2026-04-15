package com.Cts.controller;

import com.Cts.entity.Role;
import com.Cts.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    // CREATE ROLE
    @PostMapping
    public Role createRole(@RequestBody Role role) {
        return service.createRole(role);
    }

    // GET ALL ROLES
    @GetMapping
    public List<Role> getRoles() {
        return service.getAllRoles();
    }
    
    @GetMapping("/{id}")
    public Role getRoleById(@PathVariable Long id) {
        return service.getRoleById(id);
    }


    // UPDATE ROLE
    @PutMapping("/{id}")
    public Role updateRole(@PathVariable Long id, @RequestBody Role role) {
        return service.updateRole(id, role);
    }

    // DELETE ROLE
    @DeleteMapping("/{id}")
    public String deleteRole(@PathVariable Long id) {
        service.deleteRole(id);
        return "Role deleted successfully";
    }
}
