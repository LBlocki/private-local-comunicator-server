package com.lblocki.privatecommunicatorserver.config;

import com.lblocki.privatecommunicatorserver.infrastructure.UserRepository;
import com.lblocki.privatecommunicatorserver.security.filter.ApplicationRestCorsFilter;
import com.lblocki.privatecommunicatorserver.security.filter.UsernamePasswordLoginAuthenticationFilter;
import com.lblocki.privatecommunicatorserver.security.provider.AccessTokenAuthenticationProvider;
import com.lblocki.privatecommunicatorserver.security.provider.UsernamePasswordLoginAuthenticationProvider;
import com.lblocki.privatecommunicatorserver.security.userdetails.DbUserDetailsService;
import com.lblocki.privatecommunicatorserver.security.utils.RestAccessDeniedHandler;
import com.lblocki.privatecommunicatorserver.security.utils.RestAuthenticationEntryPointHandler;
import com.lblocki.privatecommunicatorserver.security.utils.SecurityUtils;
import com.lblocki.privatecommunicatorserver.usecase.JWTTokenService;
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Configuration
@RequiredArgsConstructor
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final JWTTokenService jwtTokenService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(usernamePasswordLoginAuthenticationProvider())
                .authenticationProvider(accessTokenAuthenticationProvider());
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
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPointHandler())
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restAccessDeniedHandler())
                .and()
                .authorizeRequests()
                .mvcMatchers(HttpMethod.POST, SecurityUtils.REGISTRATION_HTTP_PATH).permitAll()
                .antMatchers(HttpMethod.GET, SecurityUtils.HTTP_UPGRADE_PATH).permitAll()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(applicationCorsFilter(), WebAsyncManagerIntegrationFilter.class)
                .addFilterBefore(usernamePasswordLoginAuthenticationFilter(), BasicAuthenticationFilter.class)
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
    protected RestAuthenticationEntryPointHandler restAuthenticationEntryPointHandler() {
        return new RestAuthenticationEntryPointHandler();
    }

    @Bean
    protected RestAccessDeniedHandler restAccessDeniedHandler() {
        return new RestAccessDeniedHandler();
    }

    @Bean
    protected AuthenticationProvider usernamePasswordLoginAuthenticationProvider() {
        return new UsernamePasswordLoginAuthenticationProvider(passwordEncoder(), userDetailsService());
    }

    @Bean
    protected AuthenticationProvider accessTokenAuthenticationProvider() {
        return new AccessTokenAuthenticationProvider(jwtTokenService, userDetailsService());
    }

    @Bean
    protected ApplicationRestCorsFilter applicationCorsFilter() {
        return new ApplicationRestCorsFilter();
    }

    @Bean
    protected UsernamePasswordLoginAuthenticationFilter usernamePasswordLoginAuthenticationFilter() throws Exception {
        return new UsernamePasswordLoginAuthenticationFilter(authenticationManager(), jwtTokenService);
    }

    @Bean
    protected CharacterEncodingFilter characterEncodingFilter() {
        val filter = new CharacterEncodingFilter();
        filter.setEncoding(StandardCharsets.UTF_8.name());
        filter.setForceEncoding(true);
        return filter;
    }
}
