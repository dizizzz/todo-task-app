package com.todotask.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTests {
    ApplicationModules modules = ApplicationModules.of(BackendApplication.class);

    @Test
    void verifyModularStructure() {
        modules.verify();
    }

    @Test
    void printModules() {
        modules.forEach(System.out::println);
    }
}
