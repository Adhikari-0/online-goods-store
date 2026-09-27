package com.store.auth;

import com.store.security.CustomUserDetails;
import com.store.security.JwtService;
import com.store.security.dto.LoginRequest;
import com.store.security.dto.LoginResponse;
import com.store.security.dto.RegisterRequest;
import com.store.security.CustomUserDetailsService;
import com.store.user.UserService;
import com.store.user.dto.CreateUserRequest;
import com.store.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final UserService userService;

	public AuthenticationController(AuthenticationManager authenticationManager,
			CustomUserDetailsService userDetailsService, JwtService jwtService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

		// record login timestamp (optional)
		userService.recordLogin(user.getId());

		String access = jwtService.generateAccessToken(user);
		String refresh = jwtService.generateRefreshToken(user);

		return ResponseEntity.ok(new LoginResponse(access, refresh, "Bearer", 900_000L,
				new LoginResponse.UserSummary(user.getId(), user.getUsername(), null, // fullName — load from
						// UserService if you need it
						user.getRoles(), user.getPermissions())));
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
		UserResponse created = userService.create(
				new CreateUserRequest(request.email(), request.password(), request.fullName(), request.phone()));
		return ResponseEntity.ok(created);
	}

	@PostMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken) {
		if (!jwtService.isValid(refreshToken) || !"refresh".equals(jwtService.extractTokenType(refreshToken))) {
			return ResponseEntity.status(401).build();
		}

		String email = jwtService.extractEmail(refreshToken);
		CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

		String newAccess = jwtService.generateAccessToken(user);
		String newRefresh = jwtService.generateRefreshToken(user);

		return ResponseEntity.ok(
				new LoginResponse(newAccess, newRefresh, "Bearer", 900_000L, new LoginResponse.UserSummary(user.getId(),
						user.getUsername(), null, user.getRoles(), user.getPermissions())));
	}

	@GetMapping("/me")
	public ResponseEntity<LoginResponse.UserSummary> me(
			@org.springframework.security.core.annotation.AuthenticationPrincipal CustomUserDetails user) {
		if (user == null)
			return ResponseEntity.status(401).build();
		return ResponseEntity.ok(new LoginResponse.UserSummary(user.getId(), user.getUsername(), null, user.getRoles(),
				user.getPermissions()));
	}
}