package com.store.auth;

import com.store.auth.dto.AssignRoleRequest;
import java.util.Set;

public interface AuthService {

    void assignRole(AssignRoleRequest request);

    void revokeRole(Long userId, String roleName, Long organizationId);

    Set<String> getRolesForUser(Long userId);

    Set<String> getPermissionsForUser(Long userId);

    boolean userHasRole(Long userId, String roleName);

    boolean userHasPermission(Long userId, String permissionCode);
}