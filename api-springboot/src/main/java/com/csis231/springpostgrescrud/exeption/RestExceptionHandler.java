package com.csis231.springpostgrescrud.exeption;

import com.csis231.springpostgrescrud.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleException(ResourceNotFoundException e) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.NOT_FOUND.value());
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setMessage("Invalid input. The ID must be a numeric value.");
        errorResponseDto.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(BadRequestException e) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
        errorResponseDto.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
    }


//    @ExceptionHandler
//    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
//        ErrorResponseDto errorResponseDto = new ErrorResponseDto();
//        errorResponseDto.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        errorResponseDto.setMessage(e.getMessage());
//        errorResponseDto.setTimestamp(System.currentTimeMillis());
//
//        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}
