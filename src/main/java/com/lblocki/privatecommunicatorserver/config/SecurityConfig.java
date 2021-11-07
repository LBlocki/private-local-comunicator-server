package com.lblocki.privatecommunicatorserver.config;

import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import com.lblocki.privatecommunicatorserver.security.filter.ApplicationCorsFilter;
import com.lblocki.privatecommunicatorserver.security.provider.UsernamePasswordAuthenticationProvider;
import com.lblocki.privatecommunicatorserver.security.userdetails.DbUserDetailsService;
import com.lblocki.privatecommunicatorserver.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(usernamePasswordAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'")
                .and()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(3)
                .and()
                .sessionFixation().newSession()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .logout()
                .logoutUrl(SecurityUtils.LOGOUT_HTTP_PATH)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, SecurityUtils.REGISTRATION_HTTP_PATH).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(applicationCorsFilter(), WebAsyncManagerIntegrationFilter.class)
                .addFilterBefore(characterEncodingFilter(), WebAsyncManagerIntegrationFilter.class);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        return new DbUserDetailsService(userRepository);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        val encodersMap = new HashMap<String, PasswordEncoder>();
        val defaultEncoderId = "argon2";
        encodersMap.put(defaultEncoderId, new Argon2PasswordEncoder());
        return new DelegatingPasswordEncoder(defaultEncoderId, encodersMap);
    }

    @Bean
    public AuthenticationProvider usernamePasswordAuthenticationProvider() {
        return new UsernamePasswordAuthenticationProvider(passwordEncoder(), userDetailsService());
    }

    @Bean
    public ApplicationCorsFilter applicationCorsFilter() {
        return new ApplicationCorsFilter();
    }

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        val filter = new CharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name());
        filter.setForceEncoding(true);
        return filter;
    }

}
