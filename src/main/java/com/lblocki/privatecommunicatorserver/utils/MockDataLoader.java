package com.lblocki.privatecommunicatorserver.utils;

import com.lblocki.privatecommunicatorserver.usecase.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MockDataLoader implements CommandLineRunner {

    private final UserManagementService userManagementService;

    @Override
    public void run(String... args) {
        loadAllData();
    }

    private void loadAllData() {
        userManagementService.registerUser("lukasz", "lukasz");
        userManagementService.registerUser("Stefan Czarnecki", "stefan");
        userManagementService.registerUser("Jan Sobieski", "sobieski");
    }
}
