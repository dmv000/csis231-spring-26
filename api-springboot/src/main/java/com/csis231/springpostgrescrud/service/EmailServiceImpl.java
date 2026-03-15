package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.EmailRequestDto;
import com.csis231.springpostgrescrud.exeption.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class EmailServiceImpl implements EmailService {
    private JavaMailSender mailSender;


@Override
public void sendEmails(EmailRequestDto emailRequestDto){
    if(emailRequestDto.getEmails() == null || emailRequestDto.getEmails().isEmpty()){
        throw new BadRequestException("No email recipients provided");
    }
    for (String email : emailRequestDto.getEmails()) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(emailRequestDto.getSubject());
        message.setText(emailRequestDto.getContent());

        mailSender.send(message);
        }
    }
}








