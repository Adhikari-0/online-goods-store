package com.store.security;

import com.store.auth.*;
import com.store.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
public class AdminSeeder implements CommandLineRunner {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordEncoder passwordEncoder;

	public AdminSeeder(UserRepository userRepository, RoleRepository roleRepository,
			UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.userRoleRepository = userRoleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public void run(String... args) {
		// Seed roles if missing
		for (String name : Set.of("USER", "ADMIN", "SUPER_ADMIN", "VENDOR", "DELIVERY_AGENT")) {
			roleRepository.findByName(name).orElseGet(() -> {
				Role r = new Role();
				r.setName(name);
				r.setSystem(true);
				r.setDescription("System role");
				return roleRepository.save(r);
			});
		}

		// Seed super admin if missing
		String adminEmail = "admin@store.com";
		if (userRepository.findByEmail(adminEmail).isEmpty()) {
			User admin = new User();
			admin.setEmail(adminEmail);
			admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
			admin.setFullName("Super Admin");
			admin.setStatus(UserStatusEnum.ACTIVE);
			admin.setEmailVerified(true);
			userRepository.save(admin);

			Role superAdmin = roleRepository.findByName("SUPER_ADMIN").orElseThrow();
			UserRole ur = new UserRole();
			ur.setUser(admin);
			ur.setRole(superAdmin);
			ur.setGrantedBy("system");
			userRoleRepository.save(ur);
		}
	}
}