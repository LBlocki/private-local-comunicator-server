package com.lblocki.privatecommunicatorserver.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lblocki.privatecommunicatorserver.security.authentication.LoginAuthenticationToken;
import com.lblocki.privatecommunicatorserver.security.utils.SecurityUtils;
import com.lblocki.privatecommunicatorserver.usecase.JWTTokenService;
import com.lblocki.privatecommunicatorserver.web.dto.UserCredentialsDTO;
import com.lblocki.privatecommunicatorserver.web.request.UserLoginRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class UsernamePasswordLoginAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService jwtTokenService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(final HttpServletRequest httpServletRequest,
                                    final HttpServletResponse httpServletResponse,
                                    final FilterChain filterChain) {

        final UserLoginRequest userLoginRequest = validateUserLoginRequestAndReturnParsedObject(httpServletRequest);

        final Authentication authentication = authenticationManager.authenticate(
                new LoginAuthenticationToken(userLoginRequest.getUsername(), userLoginRequest.getPassword()));

        final String authenticationToken = jwtTokenService.generateTokenCode(authentication.getName());
        prepareHttpServletResponse(httpServletResponse, authenticationToken);

        log.debug("User logged in. Authentication token has been issued. Listing additional information:\nUsername: {}\nIp address: {}",
                authentication.getName(), httpServletRequest.getRemoteAddr());

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !SecurityUtils.LOGIN_HTTP_PATH.equals(request.getServletPath()) ||
                !request.getMethod().equals(HttpMethod.POST.toString());
    }

    private UserLoginRequest validateUserLoginRequestAndReturnParsedObject(@NonNull final HttpServletRequest httpServletRequest) {
        try {

            final UserLoginRequest loginRequest =
                    objectMapper.readValue(httpServletRequest.getInputStream(), UserLoginRequest.class);

            //todo walidacja długości itp dla warstwy domenowej
            Validate.notNull(loginRequest.getUsername());
            Validate.notBlank(loginRequest.getPassword());
            Validate.notBlank(loginRequest.getUsername());

            return loginRequest;

        } catch (Exception ex) {
            throw new IllegalArgumentException("Login request validation failed.", ex);
        }
    }

    private void prepareHttpServletResponse(@NonNull final HttpServletResponse httpServletResponse,
                                            @NonNull final String authenticationTokenCode) {

        val response = UserCredentialsDTO.builder()
                .authenticationToken(SecurityUtils.TOKEN_HEADER_PREFIX + authenticationTokenCode)
                .build();

        try {
            httpServletResponse.getWriter().write(objectMapper.writeValueAsString(response));
            httpServletResponse.setContentType("application/json");
        } catch (IOException ex) {
            log.debug("Failed to write http response during login.", ex);
        }
    }
}
