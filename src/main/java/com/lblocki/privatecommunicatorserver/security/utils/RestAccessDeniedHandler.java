package com.lblocki.privatecommunicatorserver.security.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.debug("Authentication access point exception [}", accessDeniedException);

        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

        httpServletResponse.getWriter()
                .write("{" +
                        "\"timestamp\":\"" + LocalDateTime.now().toString() + "\"," +
                        "\"status\":\"" + HttpServletResponse.SC_FORBIDDEN + "\"," +
                        "\"error\":\"Forbidden from performing this action\"," +
                        "\"message\":\"" + "\"" +
                        "}");

    }
}
