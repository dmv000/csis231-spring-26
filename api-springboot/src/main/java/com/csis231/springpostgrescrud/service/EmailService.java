package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.EmailRequestDto;

public interface EmailService {

    void sendEmails(EmailRequestDto emailRequestDto);

}