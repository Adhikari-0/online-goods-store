package com.store.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthzService {

	/** True if the currently authenticated user is the user with the given id. */
	public boolean isSelf(Long userId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails me)) {
			return false;
		}
		return me.getId().equals(userId);
	}

	/** True if the current user has the given role. */
	public boolean hasRole(String roleName) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails me)) {
			return false;
		}
		return me.getRoles().contains(roleName);
	}

	/** True if the current user has the given permission. */
	public boolean hasPermission(String code) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails me)) {
			return false;
		}
		return me.getPermissions().contains(code);
	}
}