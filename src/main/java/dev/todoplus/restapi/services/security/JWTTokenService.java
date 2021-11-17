package dev.todoplus.restapi.services.security;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.*;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.io.Encoders.BASE64;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
final class JWTTokenService implements Clock {
    private static final String DOT = ".";

    String issuer;
    int expirationSec;
    int clockSkewSec;
    String secretKey;

    JWTTokenService(@Value("${jwt.issuer:4camps}") final String issuer,
                    @Value("${jwt.expiration-sec:86400}") final int expirationSec,
                    @Value("${jwt.clock-skew-sec:300}") final int clockSkewSec,
                    @Value("${jwt.secret:unknown_secret}") final String secret) {
        this.issuer = requireNonNull(issuer);
        this.expirationSec = expirationSec;
        this.clockSkewSec = clockSkewSec;
        this.secretKey = BASE64.encode(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String permanent(final Map<String, String> attributes) {
        return newToken(attributes, 0);
    }

    public String expiring(final Map<String, String> attributes) {
        return newToken(attributes, expirationSec);
    }

    private String newToken(final Map<String, String> attributes, final int expiresInSec) {
        final Date now = new Date();
        final Claims claims = Jwts
                .claims()
                .setIssuer(issuer)
                .setIssuedAt(now);

        if (expiresInSec > 0) {
            final Date expiresAt = new Date(System.currentTimeMillis() + 1000L * expiresInSec);
            claims.setExpiration(expiresAt);
        }
        claims.putAll(attributes);

        return Jwts
                .builder()
                .setClaims(claims)
                .signWith(HS512, secretKey).compact();
    }

    public Map<String, String> verify(final String token) {
        final JwtParser parser = Jwts
                .parserBuilder()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .setSigningKey(secretKey)
                .build();
        return parseClaims(() -> parser.parseClaimsJws(token).getBody());
    }

    public Map<String, String> untrusted(final String token) {
        final JwtParser parser = Jwts
                .parserBuilder()
                .requireIssuer(issuer)
                .setClock(this)
                .setAllowedClockSkewSeconds(clockSkewSec)
                .build();

        final String withoutSignature = StringUtils.substringBeforeLast(token, DOT) + DOT;
        return parseClaims(() -> parser.parseClaimsJws(withoutSignature).getBody());
    }

    private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
        try {
            final Claims claims = toClaims.get();
            final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (final Map.Entry<String, Object> e : claims.entrySet()) {
                builder.put(e.getKey(), String.valueOf(e.getValue()));
            }
            return builder.build();
        } catch (final IllegalArgumentException | JwtException e) {
            return ImmutableMap.of();
        }
    }

    @Override
    public Date now() {
        return new Date();
    }
}
