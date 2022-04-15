package com.ultrasound.app;

import com.ultrasound.app.model.user.AppUser;
import com.ultrasound.app.model.user.ERole;
import com.ultrasound.app.model.user.Role;
import com.ultrasound.app.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
//		(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class AppApplication {

	// TODO delete for prod

//	@Autowired
//	PasswordEncoder encoder;


	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}
//}
	// TODO delete for prod

//	@Bean
//	CommandLineRunner run(AppUserService userService) {
//		return args -> {
//			Set<Role> adminRoles = new HashSet<>();
//			adminRoles.add(new Role(ERole.ROLE_USER));
//			adminRoles.add(new Role(ERole.ROLE_ADMIN));
//			Set<Role> roles = new HashSet<>();
//			roles.add(new Role(ERole.ROLE_USER));
//			userService.save(new AppUser("Admin", "admin", encoder.encode("admin123#@!"), "admin.email.com", adminRoles));
//			userService.save(new AppUser("Caleb Tracey", "caleb123", encoder.encode("123123"), "caleb.email.com", roles));
//
//		};
//
//	}
}


