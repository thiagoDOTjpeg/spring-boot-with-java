package br.com.thiagodotjpeg.services;

import br.com.thiagodotjpeg.data.dto.security.AccountCredentialsDTO;
import br.com.thiagodotjpeg.data.dto.security.TokenDTO;
import br.com.thiagodotjpeg.data.dto.v1.PersonDTO;
import br.com.thiagodotjpeg.exceptions.RequiredObjectIsNullException;
import br.com.thiagodotjpeg.models.Person;
import br.com.thiagodotjpeg.models.User;
import br.com.thiagodotjpeg.repositories.UserRepository;
import br.com.thiagodotjpeg.security.jwt.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static br.com.thiagodotjpeg.mapper.ObjectMapper.parseObject;

@Service
public class AuthService {

  private final Logger logger = LoggerFactory.getLogger(AuthService.class);

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtTokenProvider tokenProvider;
  @Autowired
  private UserRepository userRepository;

  public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials){
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword())
    );

    User user = userRepository.findByUsername(credentials.getUsername());
    if(user == null) {
      throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found");
    }

    TokenDTO token = tokenProvider.createAccessToken(credentials.getUsername(), user.getRoles());
    return ResponseEntity.ok(token);
  }

  public ResponseEntity<TokenDTO> refreshToken(String username, String refreshToken) {
    var user = userRepository.findByUsername(username);
    TokenDTO token;
    if(user != null) {
      token = tokenProvider.refreshToken(refreshToken);
    } else {
      throw new UsernameNotFoundException("Username " + username + " not found");
    }
    return ResponseEntity.ok(token);
  }

  public AccountCredentialsDTO create(AccountCredentialsDTO user) {

    if (user == null) throw new RequiredObjectIsNullException();

    logger.info("Creating one new user!");
    var entity = new User();
    entity.setUsername(user.getUsername());
    entity.setPassword(generateHashedPassword(user.getPassword()));
    entity.setFullname(user.getFullname());
    entity.setAccountNonExpired(true);
    entity.setAccountNonLocked(true);
    entity.setCredentialsNonExpired(true);
    entity.setEnabled(true);


    return parseObject(userRepository.save(entity), AccountCredentialsDTO.class);
  }

  private String generateHashedPassword(String password) {
    PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder(
            "", 8, 185000,
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put("pbkdf2", pbkdf2Encoder);
    DelegatingPasswordEncoder passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
    passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);

    return passwordEncoder.encode(password);
  }
}
