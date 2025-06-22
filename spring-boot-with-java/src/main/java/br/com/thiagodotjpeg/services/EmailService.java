package br.com.thiagodotjpeg.services;

import br.com.thiagodotjpeg.config.EmailConfig;
import br.com.thiagodotjpeg.data.dto.request.EmailRequestDTO;
import br.com.thiagodotjpeg.mail.EmailSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class EmailService {
  @Autowired
  private EmailSender emailSender;

  @Autowired
  private EmailConfig emailConfigs;

  public void sendSimpleEmail(EmailRequestDTO emailRequest) {
    emailSender
            .to(emailRequest.getTo())
            .withSubject(emailRequest.getSubject())
            .withMessage(emailRequest.getSubject())
            .send(emailConfigs);
  }

  public void sendEmailWithAttachment(String emailRequestJson, MultipartFile attachment) {
    File tempFile = null;
    try {
      EmailRequestDTO emailRequest = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
      tempFile = File.createTempFile("attachment", attachment.getOriginalFilename());
      attachment.transferTo(tempFile);

      emailSender
              .to(emailRequest.getTo())
              .withSubject(emailRequest.getSubject())
              .withMessage(emailRequest.getSubject())
              .attach(tempFile.getAbsolutePath())
              .send(emailConfigs);
    } catch (IOException e) {
      throw new RuntimeException("Error processing the request", e);
    } finally {
      if (tempFile != null && tempFile.exists()) tempFile.delete();
    }
  }


}
