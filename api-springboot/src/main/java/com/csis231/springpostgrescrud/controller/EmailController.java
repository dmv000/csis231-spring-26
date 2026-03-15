package com.csis231.springpostgrescrud.controller;

import com.csis231.springpostgrescrud.dto.EmailRequestDto;
import com.csis231.springpostgrescrud.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/emails")

public class EmailController {

    private EmailService emailService;
    @PostMapping("/send")
    public ResponseEntity<String> sendEmails(@RequestBody EmailRequestDto emailRequestDto){
    emailService.sendEmails(emailRequestDto);

    return new ResponseEntity<>("Emails sent succesfully", HttpStatus.OK);
    }
}




