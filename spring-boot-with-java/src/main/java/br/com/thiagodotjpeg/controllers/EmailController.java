package br.com.thiagodotjpeg.controllers;

import br.com.thiagodotjpeg.controllers.docs.EmailControllerDocs;
import br.com.thiagodotjpeg.data.dto.request.EmailRequestDTO;
import br.com.thiagodotjpeg.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController implements EmailControllerDocs {

  @Autowired
  private EmailService service;

  @PostMapping
  @Override
  public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
    service.sendSimpleEmail(emailRequestDTO);
    return new ResponseEntity<>("e-Mail sent Successfully", HttpStatus.OK);
  }

  @PostMapping(value = "/withAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Override
  public ResponseEntity<String> sendEmailWithAttachment(@RequestParam("emailRequest") String emailRequest, @RequestParam("attachment") MultipartFile attachment) {
    service.sendEmailWithAttachment(emailRequest, attachment);
    return new ResponseEntity<>("e-Mail with attachment send successfully!", HttpStatus.OK);
  }
}
