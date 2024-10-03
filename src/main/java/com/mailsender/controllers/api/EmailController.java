package com.mailsender.controllers.api;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mailsender.helper.CustomResponse;
import com.mailsender.helper.EmailRequest;
import com.mailsender.services.EmailService;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/email")
public class EmailController {

     private  EmailService emailService;


     
      public EmailController(EmailService emailService) {
                  this.emailService = emailService;
      }

    // send email

    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody EmailRequest request) {

        emailService.sendEmailWithHtml(request.getTo(), request.getSubject(), request.getMessage());
        return ResponseEntity.ok(
                CustomResponse.builder().message("Email Send Successfully !!").httpStatus(HttpStatus.OK).success(true)
                        .build());
    }

    @PostMapping("/send-with-file")
    public ResponseEntity<CustomResponse> sendWithFile(@RequestPart EmailRequest request,
            @RequestPart MultipartFile file) throws IOException {

        emailService.sendEmailWithFile(request.getTo(), request.getSubject(), request.getMessage(),
                file.getInputStream());

        return ResponseEntity.ok(
                CustomResponse.builder().message("Email Send Successfully !!").httpStatus(HttpStatus.OK).success(true)
                        .build());
    }

}
