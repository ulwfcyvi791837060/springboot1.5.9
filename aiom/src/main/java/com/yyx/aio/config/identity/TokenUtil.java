package com.yyx.aio.config.identity;

import com.yyx.aio.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
public class TokenUtil {

    //private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;// 10 days Validity
    private static final long VALIDITY_TIME_MS =  2 * 60 * 60 * 1000; // 2 hours  validity
    private static final String AUTH_HEADER_NAME = "Authorization";

    private String secret="aiom";

    public Authentication verifyToken(HttpServletRequest request) {
      final String token = request.getHeader(AUTH_HEADER_NAME);

      if (token != null && !token.isEmpty()){
        final User user = parseUserFromToken(token.replace("Bearer","").trim());
        if (user != null) {
            return  new UserAuthentication(user);
        }
      }
      return null;

    }

    //Get User Info from the Token
    public User parseUserFromToken(String token){

        Claims claims = Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token)
            .getBody();

        User user = new User();
        user.setId(Long.parseLong(claims.get("userId").toString()));
        //user.setCustomerId((Integer)claims.get("customerId"));
        //user.setRole((String)claims.get("role"));
        user.setMobile((String) claims.get("mobile"));
        return user;
    }

    public String createTokenForUser(User user) {
      return Jwts.builder()
        .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_TIME_MS))
        .setSubject(user.getUserName())
        .claim("userId", user.getId())
        .claim("mobile", user.getMobile())
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
    }

}
