package com.mailsender.services.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.mailsender.helper.Message;
import com.mailsender.services.EmailService;

import jakarta.mail.BodyPart;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;

    private Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${mail.store.protocol}")
    private String protocol;

    @Value("${mail.imaps.host}")
    private String host;

    @Value("${mail.imaps.port}")
    private String port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(fromEmail); // use configured email
        mailSender.send(simpleMailMessage);
        logger.info("Email sent to {} with subject {}", to, subject);
    }

    @Override
    public void sendEmail(String[] to, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(fromEmail); // use configured email
        mailSender.send(simpleMailMessage);
        logger.info("Email sent to {} recipients with subject {}", to.length, subject);
    }

    @Override
    public void sendEmailWithHtml(String to, String subject, String htmlContent) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail); // use configured email
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
            logger.info("HTML Email sent to {} with subject {}", to, subject);

        } catch (MessagingException e) {

            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage());
            throw new EmailServiceException("Failed to send HTML email", e);
        }
    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, InputStream is) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail); // use configured email
            helper.setText(message, true);

             //Create temporary file
            //  File tempFile = Files.createTempFile("attachment", ".tmp").toFile();
            //  try (InputStream inputStream = is) {
            //      Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            //  }
             File file=new File("mailSenderApp/src/main/resources/static/images/review1.png");
             Files.copy(is, file.toPath(),StandardCopyOption.REPLACE_EXISTING);

            //FileSystemResource fileSystemResource = new FileSystemResource(tempFile);

            FileSystemResource fileSystemResource = new FileSystemResource(file);
             //helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);
            helper.addAttachment(fileSystemResource.getFilename(), file);


            mailSender.send(mimeMessage);
            logger.info("Email with attachment sent to {} with subject {}", to, subject);

        } catch (MessagingException | IOException e) {

            logger.error("Failed to send email with attachment to {}: {}", to, e.getMessage());
            throw new EmailServiceException("Failed to send email with attachment", e);
        }
    }

    @Override
    public void sendEmailWithFile(String to, String subject, String message, File file) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromEmail); // use configured email
            helper.setText(message, true);

            FileSystemResource fileSystemResource = new FileSystemResource(file);
            helper.addAttachment(fileSystemResource.getFilename(), fileSystemResource);

            mailSender.send(mimeMessage);
            logger.info("Email with file attachment sent to {} with subject {}", to, subject);

        } catch (MessagingException e) {
            logger.error("Failed to send email with file to {}: {}", to, e.getMessage());
             throw new EmailServiceException("Failed to send email with file", e);

        }
    }

    // @Override
    // public List<Message> getInboxMessages() {

    //     List<Message> messagesList = new ArrayList<>();
    //     Properties properties = new Properties();

    //     properties.setProperty("mail.store.protocol", protocol);
    //     properties.setProperty("mail.imap.host", host);
    //     properties.setProperty("mail.imap.port", port);

    //     Session session = Session.getDefaultInstance(properties);
    //     try (Store store = session.getStore()) {
    //         store.connect(username, password);

    //         Folder inbox = store.getFolder("INBOX");
    //         inbox.open(Folder.READ_ONLY);

    //         jakarta.mail.Message[] messages = inbox.getMessages();
    //         for (jakarta.mail.Message message : messages) {

    //             String content = getContentFromEmailMessage(message);
    //             List<String> files = getFilesFromEmailMessage(message);
    //             messagesList.add(Message.builder()
    //                     .subjects(message.getSubject())
    //                     .content(content)
    //                     .files(files)
    //                     .build());
    //         }
    //     } catch (MessagingException | IOException e) {
    //         logger.error("Error while fetching inbox messages: {}", e.getMessage());
    //     }
    //     return messagesList;
    // }

    // private List<String> getFilesFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {
    //     List<String> files = new ArrayList<>();
    //     if (message.isMimeType("multipart/*")) {
    //         Multipart content = (Multipart) message.getContent();
    //         for (int i = 0; i < content.getCount(); i++) {
    //             BodyPart bodyPart = content.getBodyPart(i);
    //             if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
    //                 File tempFile = Files.createTempFile("email-attachment", ".tmp").toFile();
    //                 try (InputStream inputStream = bodyPart.getInputStream()) {
    //                     Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    //                 }
    //                 files.add(tempFile.getAbsolutePath());
    //             }
    //         }
    //     }
    //     return files;
    // }

    // private String getContentFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {
    //     if (message.isMimeType("text/plain") || message.isMimeType("text/html")) {
    //         return (String) message.getContent();
    //     } else if (message.isMimeType("multipart/*")) {
    //         Multipart multipart = (Multipart) message.getContent();
    //         for (int i = 0; i < multipart.getCount(); i++) {
    //             BodyPart bodyPart = multipart.getBodyPart(i);
    //             if (bodyPart.isMimeType("text/plain")) {
    //                 return (String) bodyPart.getContent();
    //             }
    //         }
    //     }
    //     return null;
    // }

    // Custom exception class
     public class EmailServiceException extends RuntimeException {
         public EmailServiceException(String message, Throwable cause) {
             super(message, cause);
         }
     }

    @Override
    public List<Message> getInboxMessages() throws IOException {

        //code to receive email get all email
        Properties configuration=new Properties();

        configuration.setProperty("mail.store.protocol",protocol);
        configuration.setProperty("mail.imaps.host",host);
        configuration.setProperty("mail.imaps.port",port);

        Session session=Session.getDefaultInstance(configuration);

        List<Message> list=new ArrayList<>();


        try {
            Store store = session.getStore();

            store.connect(username,password);

            Folder inbox=store.getFolder("INBOX");

            inbox.open(Folder.READ_ONLY);

            jakarta.mail.Message[] messages=inbox.getMessages();

            // List<Message> list=new ArrayList<>();

            for(jakarta.mail.Message message:messages)
            {
               System.out.println(message.getSubject());

               String content=getContentFromEmailMessage(message);
               List<String> files=getFilesFromEmailMessage(message);
               System.out.println("-----------");

               list.add(Message.builder().subjects(message.getSubject()).content(content).files(files).build());
            }

            return list;



        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return list;


    }

    private List<String> getFilesFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {

        List<String> files=new ArrayList<>();

        if(message.isMimeType("multipart/*"))
        {
            Multipart content=(Multipart)message.getContent();


            for(int i=0;i<content.getCount();i++)
            {
                BodyPart bodyPart=content.getBodyPart(i);

                if(Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()))
                {
                    
                    InputStream inputStream=bodyPart.getInputStream();
                    File file=new File("src/main/resources/email/"+bodyPart.getFileName());

                    //save the file
                    Files.copy(inputStream,file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    files.add(file.getAbsolutePath());

                }

            }
        }
        return files;
        
    }

    private String getContentFromEmailMessage(jakarta.mail.Message message) throws MessagingException, IOException {

        if(message.isMimeType("text/plain") || message.isMimeType("text/html"))
        {
            String content=(String) message.getContent();
            return content;
        }


        else if(message.isMimeType("multipart/*"))
        {
            Multipart part=(Multipart) message.getContent();

            for(int i=0;i<part.getCount();i++)
            {
                BodyPart bodyPart=part.getBodyPart(i);

                if(bodyPart.isMimeType("text/plain"))
                {
                    return (String) bodyPart.getContent();
                }
            }
        }
        return null;
    }
}
