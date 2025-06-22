package br.com.thiagodotjpeg.controllers;

import br.com.thiagodotjpeg.controllers.docs.AuthControllerDocs;
import br.com.thiagodotjpeg.data.dto.security.AccountCredentialsDTO;
import br.com.thiagodotjpeg.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Endpoint")
public class AuthController implements AuthControllerDocs {
  @Autowired
  AuthService service;

  @PostMapping("/signIn")
  @Override
  public ResponseEntity<?> signIn(@RequestBody AccountCredentialsDTO credentials){
    if(credentialsIsInvalid(credentials)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");

    var token = service.signIn(credentials);
    if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
    return ResponseEntity.ok().body(token);
  }

  @PutMapping("/refresh/{username}")
  @Override
  public ResponseEntity<?> refreshToken(@PathVariable("username") String username, @RequestHeader("Authorization") String refreshToken) {
    if(parametersAreInvalid(username, refreshToken)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
    var token = service.refreshToken(username, refreshToken);
    if(token == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
    return token;
  }

  @PostMapping(value = "/createUser", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<AccountCredentialsDTO> createUser(@RequestBody AccountCredentialsDTO credentials){
    return ResponseEntity.ok(service.create(credentials));
  }

  private boolean parametersAreInvalid(String username, String refreshToken) {
    return StringUtils.isBlank(username) || StringUtils.isBlank(refreshToken);
  }

  private static boolean credentialsIsInvalid(AccountCredentialsDTO credentials) {
    return credentials == null || StringUtils.isBlank(credentials.getUsername()) || StringUtils.isBlank(credentials.getPassword());
  }

}
