package com.example.buoi_02.configuration;

import java.util.HashSet;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.buoi_02.entity.User;
import com.example.buoi_02.enums.Role;
import com.example.buoi_02.repository.UserRepository;

import lombok.RequiredArgsConstructor;

//class này tạo UserAdmin
@Configuration
@RequiredArgsConstructor
public class ApplicationInitConfig {

	private final PasswordEncoder passwordEncoder;

	@Bean
	ApplicationRunner applicationRunner(UserRepository userRepository) {
		return args -> {
			if (userRepository.findByUserName("admin").isEmpty()) {
				HashSet<String> roles = new HashSet<>();
				roles.add(Role.ADMIN.name());

				User user = User.builder()
						.userName("admin")
						.password(passwordEncoder.encode("admin"))
//						.roles(roles)
						.build();

				userRepository.save(user);
				System.err.println("Admin user default: admin, admin");
			}
		};

	}
}
