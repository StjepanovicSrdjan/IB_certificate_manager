package com.certificate_manager.certificate_manager.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.certificate_manager.certificate_manager.security.auth.RestAuthenticationEntryPoint;
import com.certificate_manager.certificate_manager.security.auth.TokenAuthenticationFilter;
import com.certificate_manager.certificate_manager.security.jwt.IJWTTokenService;
import com.certificate_manager.certificate_manager.security.jwt.TokenUtils;
import com.certificate_manager.certificate_manager.services.UserServiceImpl;

@Configuration
@EnableWebSecurity

@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {
	
	@Autowired
	private IJWTTokenService tokenService;

	@Bean
    public UserDetailsService userDetailsService() {
        return new UserServiceImpl();
    }
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
  	
  
 	@Bean
 	public DaoAuthenticationProvider authenticationProvider() {
 	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
 	  
 	    authProvider.setUserDetailsService(userDetailsService());
 	    authProvider.setPasswordEncoder(passwordEncoder());
 	 
 	    return authProvider;
 	}
 	

 	@Autowired
 	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
 
 	
 	@Bean
 	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
 	    return authConfig.getAuthenticationManager();
 	}
 	
	@Autowired
	private TokenUtils tokenUtils;
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

    	http.authorizeRequests()
			.requestMatchers("/api/user/login").permitAll()
			.requestMatchers("/api/user").permitAll()
			.requestMatchers("/api/user/send/verification/email/{email}").permitAll()
			.requestMatchers("/api/user/activate/{activationId}").permitAll()
			.requestMatchers("/api/user/reset/password/email/{email}").permitAll()
			.requestMatchers("/api/user/resetPassword").permitAll()
			.requestMatchers("api/certificate/validate-upload").permitAll()
			.requestMatchers("/api/user/rotatePassword").permitAll()
			.requestMatchers("/api/user/**").permitAll()
			.anyRequest().authenticated().and()
			.cors().and()
			.addFilterBefore(new TokenAuthenticationFilter(tokenUtils,  userDetailsService(), tokenService), BasicAuthenticationFilter.class);
		
		http.csrf().disable(); 

		http.headers().frameOptions().disable();
        http.authenticationProvider(authenticationProvider());
            
        return http.build();
    }
           
    // metoda u kojoj se definisu putanje za igorisanje autentifikacije
    @Bean           
    public WebSecurityCustomizer webSecurityCustomizer() {     
    	// Dozvoljena POST metoda na ruti /auth/login, za svaki drugi tip HTTP metode greska je 401 Unauthorized
    	return (web) -> web.ignoring().requestMatchers(HttpMethod.POST, "/api/user/login").requestMatchers(HttpMethod.POST, "/api/user");	
    }
}
