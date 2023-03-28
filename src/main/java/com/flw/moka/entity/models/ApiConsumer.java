package com.flw.moka.entity.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiConsumer {

    private String username;
    private String password;

    public ApiConsumer(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
}
