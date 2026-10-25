package com.Albumstore.album;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.Albumstore.album.model.UserDetailServiceImpl;

@Configuration
public class WebSecurityConfig {
	@Autowired
	private UserDetailServiceImpl userDetailsService;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}

	// REST API (/api/albums and the Spring Data REST resources under /api).
	// Any logged-in user can read, only ADMIN can write. HTTP Basic, no session:
	// the login cookie from the web pages is never used here, so a cross-site
	// request can't ride on it and CSRF tokens aren't needed on this chain.
	@Bean
	@Order(1)
	public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
		http
			.securityMatcher(antMatcher("/api/**"))
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(antMatcher(HttpMethod.GET, "/api/**")).authenticated()
				.anyRequest().hasAuthority("ADMIN"))
			.httpBasic(Customizer.withDefaults())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.csrf(csrf -> csrf.disable());
		return http.build();
	}

	// Web pages. The album list and the song lists are open for read-only
	// browsing on purpose. Everything else under /album needs ADMIN. CSRF is on.
	@Bean
	@Order(2)
	public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(authorize -> authorize
				.requestMatchers(antMatcher("/css/**"), antMatcher("/error")).permitAll()
				.requestMatchers(antMatcher(HttpMethod.GET, "/album"),
						antMatcher(HttpMethod.GET, "/album/{id}/songs")).permitAll()
				.requestMatchers(antMatcher("/album/**")).hasAuthority("ADMIN")
				.anyRequest().authenticated())
			.formLogin(formlogin -> formlogin
				.defaultSuccessUrl("/album", true)
				.permitAll())
			.logout(logout -> logout.permitAll());
		return http.build();
	}
}
