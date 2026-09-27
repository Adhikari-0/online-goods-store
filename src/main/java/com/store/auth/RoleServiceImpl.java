package com.store.auth;

import com.store.auth.dto.RoleResponse;
import com.store.common.exception.BusinessException;
import com.store.common.exception.DuplicateResourceException;
import com.store.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleServiceImpl(RoleRepository roleRepository,
                           PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public RoleResponse create(String name, String description, Set<String> permissionCodes) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new DuplicateResourceException("Role already exists: " + name);
        }

        Role role = new Role();
        role.setName(name.toUpperCase().trim());
        role.setDescription(description);
        role.setSystem(false);
        role.setPermissions(resolvePermissions(permissionCodes));

        return toResponse(roleRepository.save(role));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getById(Long id) {
        return toResponse(roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Role", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getByName(String name) {
        return toResponse(roleRepository.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + name)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> listAll() {
        return roleRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    public RoleResponse updatePermissions(Long roleId, Set<String> permissionCodes) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        role.setPermissions(resolvePermissions(permissionCodes));
        return toResponse(roleRepository.save(role));
    }

    @Override
    public void delete(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new ResourceNotFoundException("Role", roleId));

        if (role.isSystem()) {
            throw new BusinessException("System roles cannot be deleted: " + role.getName());
        }

        roleRepository.delete(role);
    }

    private Set<Permission> resolvePermissions(Set<String> codes) {
        if (codes == null || codes.isEmpty()) return new HashSet<>();
        Set<Permission> perms = new HashSet<>(permissionRepository.findAllByCodeIn(codes));
        if (perms.size() != codes.size()) {
            Set<String> found = perms.stream().map(Permission::getCode).collect(Collectors.toSet());
            codes.stream()
                .filter(c -> !found.contains(c))
                .findFirst()
                .ifPresent(c -> { throw new ResourceNotFoundException("Permission not found: " + c); });
        }
        return perms;
    }

    private RoleResponse toResponse(Role role) {
        return new RoleResponse(
            role.getId(),
            role.getName(),
            role.getDescription(),
            role.isSystem(),
            role.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet())
        );
    }
}