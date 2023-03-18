package com.flw.moka.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("")
public class TestingController {

    @GetMapping("")
    public ResponseEntity<String> getStudent() {
        String message = "This is working";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

}
