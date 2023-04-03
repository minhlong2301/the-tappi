package vn.project.nfc.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.project.nfc.sercurity.impl.UserDetailsImpl;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
    private final long jwtExpiration = 604800000L;

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    public String createJwtToken(UserDetailsImpl userDetails) {
        log.info("jwtsecret " + jwtSecret);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration);
        String encoding = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, encoding)
                .compact();
    }

    public String getEmailFromJwtToken(String token) {
        String encoding = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        String email = Jwts.parser().setSigningKey(encoding).parseClaimsJws(token).getBody().getSubject();
        if (Objects.nonNull(email)) {
            return email;
        }
        return "";
    }

    public boolean validateJwtToken(String token) {
        try {
            String encoding = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
            Jwts.parser().setSigningKey(encoding).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("Token hết hạn, " + e);
        } catch (UnsupportedJwtException e) {
            logger.error("Token này không được hỗ trợ, " + e);
        } catch (MalformedJwtException e) {
            logger.error("Token không đúng định dạng, " + e);
        } catch (SignatureException e) {
            logger.error("Token không xác thực được, " + e);
        } catch (IllegalArgumentException e) {
            logger.error("Token có kí tự trống không hợp lê, " + e);
        }
        return true;
    }


}
