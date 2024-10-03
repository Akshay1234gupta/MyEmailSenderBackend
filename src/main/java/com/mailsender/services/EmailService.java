package com.mailsender.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.mailsender.helper.Message;

public interface EmailService {
    


    //send email to single person
    void sendEmail(String to,String subject,String message);

     //send email to multiple person
     void sendEmail(String []to,String subject,String message);

     //void sendEmailWithHtml

     void sendEmailWithHtml(String to,String subject,String htmlContent);

     //void send email with file
     void sendEmailWithFile(String to,String subject,String message,File file);


     void sendEmailWithFile(String to,String subject,String message,InputStream is);

     List<Message>getInboxMessages() throws IOException;

}
