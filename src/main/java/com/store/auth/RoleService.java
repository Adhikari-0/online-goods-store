package com.store.auth;

import com.store.auth.dto.RoleResponse;

import java.util.List;
import java.util.Set;

public interface RoleService {

    RoleResponse create(String name, String description, Set<String> permissionCodes);

    RoleResponse getById(Long id);

    RoleResponse getByName(String name);

    List<RoleResponse> listAll();

    RoleResponse updatePermissions(Long roleId, Set<String> permissionCodes);

    void delete(Long roleId);
}