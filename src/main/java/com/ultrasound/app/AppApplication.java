package com.ultrasound.app;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@RequiredArgsConstructor
public class AppApplication {

//	private final PasswordEncoder encoder;
	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	// TODO delete for prod
//	@Bean
//	CommandLineRunner run(AppUserService userService) {
//		return args -> {
//			Set<Role> adminRoles = new HashSet<>();
//			adminRoles.add(new Role(ERole.ROLE_USER));
//			adminRoles.add(new Role(ERole.ROLE_ADMIN));
//			Set<Role> roles = new HashSet<>();
//			roles.add(new Role(ERole.ROLE_USER));
//			userService.save(new AppUser("Admin", "admin", encoder.encode("test"), "admin.email.com", adminRoles));
//
//		};

//	}
}


