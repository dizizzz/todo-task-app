@org.springframework.modulith.ApplicationModule(
    allowedDependencies = {"user::api", "core::exceptions", "core::security"}
)
package com.todotask.backend.task;