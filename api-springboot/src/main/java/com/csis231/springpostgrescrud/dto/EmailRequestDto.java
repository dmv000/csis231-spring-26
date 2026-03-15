package com.csis231.springpostgrescrud.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class EmailRequestDto{
    private List<String> emails;
    private String subject;
    private String content;
}