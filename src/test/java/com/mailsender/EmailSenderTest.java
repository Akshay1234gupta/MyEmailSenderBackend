package com.mailsender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mailsender.helper.Message;
import com.mailsender.services.EmailService;

@SpringBootTest
public class EmailSenderTest {

    @Autowired
    private EmailService emailService;

    @Test
    void emailSendTest() {
        System.out.println("Sending simple email...");
        emailService.sendEmail("akshayguptaa444@gmail.com", "Email from Spring", "This is a test email from Spring.");
        System.out.println("Email sent successfully.");
    }

    @Test
    void sendHtmlInEmail() {
        String html = "<h1 style='color:red;border:1px solid red;'>Welcome to Learn Code</h1>";
        emailService.sendEmailWithHtml("akshayguptaa444@gmail.com", "HTML Email from Spring", html);
        System.out.println("HTML email sent successfully.");
    }

    @Test
    void sendEmailWithFile() {
        File file = new File("src/main/resources/static/images/review1.png");
        // Assertions.assertTrue(file.exists(), "File does not exist at the specified path!");
        
        emailService.sendEmailWithFile("akshayguptaa444@gmail.com", "Email with Attachment from Spring", 
            "This is a test email with file attachment.", file);
        
        System.out.println("Email with attachment sent successfully.");
    }

    @Test
    void sendEmailWithFileWithStream() {
        File file = new File("src/main/resources/static/images/review1.png");
        Assertions.assertTrue(file.exists(), "File does not exist at the specified path!");

        // 
        try {
            InputStream is=new FileInputStream(file);
            emailService.sendEmailWithFile("akshayguptaa444@gmail.com", "Email with Attachment from Input Spring", 
            "This is a test email with file attachment.", is);            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    // Receiving email
    @Test
    void getInbox() throws IOException {
        List<Message> inboxMessages = emailService.getInboxMessages();
        
        // Assertions.assertNotNull(inboxMessages, "Inbox messages list should not be null.");
        //  Assertions.assertTrue(inboxMessages.size() > 0, "Inbox should contain at least one message.");
        
         inboxMessages.forEach(message -> {
             System.out.println("Subject: " + message.getSubjects());
             System.out.println("Content: " + message.getContent());
             System.out.println("Attachments: " + message.getFiles());
             System.out.println("---------------");
         });

       
    }
}
