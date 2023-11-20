package com.Albumstore.album;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.Albumstore.album.model.UserDetailServiceImpl;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class WebSecurityConfig {
	@Autowired
	private UserDetailServiceImpl userDetailsService;
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
	}


	private static final AntPathRequestMatcher[] WHITE_LIST_URLS = {
		new AntPathRequestMatcher("/h2-console/**"),
		new AntPathRequestMatcher("/api/**"),
		new AntPathRequestMatcher("/albums/**"),
		new AntPathRequestMatcher("/album/{id}/songs"),
		new AntPathRequestMatcher("/album"),
		new AntPathRequestMatcher("/css/**") 
	};
		private static final AntPathRequestMatcher[] ADMIN_LIST_URLS = {
		new AntPathRequestMatcher("/album/**"),
		new AntPathRequestMatcher("/owner/**") 
	};
		 
		// secure configurations with lambda
		@Bean
	    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
	        http
	            .authorizeHttpRequests(authorizeRequest -> authorizeRequest
	                .requestMatchers(WHITE_LIST_URLS).permitAll()
	                .requestMatchers(ADMIN_LIST_URLS).hasAuthority("ADMIN")
	                .anyRequest().authenticated())
	            .headers(headers -> headers.frameOptions().disable())
	            .formLogin(formlogin -> formlogin
	                .defaultSuccessUrl("/album", true)
	                .permitAll())
	            .logout(logout -> logout.permitAll())
	            .csrf(csrf -> csrf
	            		.ignoringRequestMatchers("/api/**")); // Keep in mind to enable for production
	        		
	        return http.build();
	    }
	}

