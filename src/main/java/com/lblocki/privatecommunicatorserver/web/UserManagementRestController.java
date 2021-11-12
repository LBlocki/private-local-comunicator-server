package com.lblocki.privatecommunicatorserver.web;

import com.lblocki.privatecommunicatorserver.security.utils.SecurityUtils;
import com.lblocki.privatecommunicatorserver.usecase.UserManagementService;
import com.lblocki.privatecommunicatorserver.utils.UserAlreadyExistException;
import com.lblocki.privatecommunicatorserver.web.request.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Stream;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserManagementRestController {

    private final UserManagementService userManagementService;

    @PostMapping(SecurityUtils.REGISTRATION_HTTP_PATH)
    public void registerUser(@RequestBody final UserRegisterRequest request) {

        try {
            Stream.of(request)
                    .filter(this::validateRegisterRequest)
                    .findFirst().
                    orElseThrow(() -> new IllegalArgumentException("Register request validation failed"));

            log.debug("Registering new user " + request.getUsername());
            userManagementService.registerUser(request.getUsername(), request.getPassword());

        } catch (IllegalArgumentException ex) {
            log.debug("Error. Invalid request to register user", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request");
        } catch (UserAlreadyExistException ex) {
            log.debug("Error. User already exists exception while registering user", ex);
            throw new ResponseStatusException(HttpStatus.GONE, "User already exists");
        } catch (Exception ex) {
            log.debug("Error. Unexpected exception has occurred", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error. Please contact the administrators");
        }
    }

    private boolean validateRegisterRequest(final UserRegisterRequest request) {

        try {

            Validate.notNull(request);
            Validate.notBlank(request.getUsername());
            Validate.notBlank(request.getPassword());

            return true;

        } catch (NullPointerException | IllegalArgumentException ex) {
            log.debug("Failed to validate register request.", ex);
            return false;
        }
    }
}
