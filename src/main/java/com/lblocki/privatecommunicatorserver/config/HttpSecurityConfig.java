package com.lblocki.privatecommunicatorserver.config;

import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import com.lblocki.privatecommunicatorserver.security.filters.ApplicationHttpCorsFilter;
import com.lblocki.privatecommunicatorserver.security.provider.WebsocketAuthenticationProvider;
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
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(usernamePasswordLoginAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //todo FIX HTTPS AND CORS
        http
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("script-src 'self'")
                .and()
                .and()
                .csrf().disable() //Fix csrf
                .cors().disable() //Fix cors
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, SecurityUtils.REGISTRATION_HTTP_PATH).anonymous()
                .antMatchers(HttpMethod.GET, SecurityUtils.HTTP_UPGRADE_PATH).anonymous()
                .antMatchers(HttpMethod.GET, SecurityUtils.HTTP_LOGIN_IV_PATH).anonymous()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(applicationHttpCorsFilter(), WebAsyncManagerIntegrationFilter.class)
                .addFilterBefore(characterEncodingFilter(), WebAsyncManagerIntegrationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.NEVER));
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
    protected AuthenticationProvider usernamePasswordLoginAuthenticationProvider() {
        return new WebsocketAuthenticationProvider(passwordEncoder(), userDetailsService());
    }

    @Bean
    protected ApplicationHttpCorsFilter applicationHttpCorsFilter() {
        return new ApplicationHttpCorsFilter();
    }


    @Bean
    protected CharacterEncodingFilter characterEncodingFilter() {
        val filter = new CharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name());
        filter.setForceEncoding(true);
        return filter;
    }
}
