package br.com.thiagodotjpeg.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class JwtTokenFilter extends GenericFilterBean {

  @Autowired
  private JwtTokenProvider tokenProvider;

  public JwtTokenFilter(JwtTokenProvider tokenProvider) {
    this.tokenProvider = tokenProvider;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {
    String token = tokenProvider.resolveToken((HttpServletRequest) request);
    if(StringUtils.isNotBlank(token) && tokenProvider.validateToken(token)) {
      Authentication auth = tokenProvider.getAuthentication(token);
      if(auth != null) {
        SecurityContextHolder.getContext().setAuthentication(auth);
      }
    }
    filter.doFilter(request, response);
  }
}
