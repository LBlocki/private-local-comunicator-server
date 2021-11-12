package com.lblocki.privatecommunicatorserver.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class RestAuthenticationEntryPointHandler  implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException authenticationException) throws IOException {

        log.debug("Authentication entry point exception [}", authenticationException);

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        httpServletResponse.getWriter()
                .write("{" +
                        "\"timestamp\":\"" + LocalDateTime.now().toString() + "\"," +
                        "\"status\":\"" + HttpServletResponse.SC_UNAUTHORIZED + "\"," +
                        "\"error\":\"Unauthorized or invalid credentials\"," +
                        "\"message\":\"" + "\"" +
                        "}");
    }
}
