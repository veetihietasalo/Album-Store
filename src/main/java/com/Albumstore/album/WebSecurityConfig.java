package com.Albumstore.album;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.Albumstore.album.model.UserDetailServiceImpl;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebSecurityConfig {
	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
	http
		.authorizeHttpRequests( authorize -> authorize
				.requestMatchers(antMatcher("/resources/**")).permitAll()
				.requestMatchers(antMatcher("/album")).permitAll()
				.requestMatchers(antMatcher("/album/**")).hasAuthority("ADMIN")
				.anyRequest().authenticated()
		)
		.formLogin( formlogin -> formlogin
			.loginPage("/login")
			.defaultSuccessUrl("/album", true)
			.permitAll()
		)
		.logout( logout -> logout
			.permitAll()
		);
		return http.build();
	}
	@Autowired
	private UserDetailServiceImpl userDetailsService;
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(userDetailsService).passwordEncoder(new
	BCryptPasswordEncoder());
	}
}
