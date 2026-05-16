package com.codigo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.WebFilter;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;



import javax.crypto.SecretKey;
//le danos de estatus configuration porque este se convirtio en un contrato osea en un bean recuerda.
@Configuration
public class JwtGatewayFilter {

    //pasamos a crer sus atributos
    @Value("${key.signature}")
    private String keySignature;

    @Bean
    public WebFilter jwtFilter(){
        return (exchange, chain) -> {
            String path = exchange.getRequest().getURI().getPath();

            if(path.startsWith("/apis/codigo/api/authentication/v1/")){
                return chain.filter(exchange);
            }
            if(path.startsWith("/apis/codigo/api/reniec")){
                String autHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if(autHeader == null || !autHeader.startsWith("Bearer")){
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                String token = autHeader.substring(7);

                try{
                    Claims claims = Jwts.parser()
                            .verifyWith(getSigninKey())
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    Object rolesObject = claims.get("roles");

                    if(rolesObject == null){
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                    String roles = rolesObject.toString();
                    // ✅ Si NO tiene ADMIN ni ROLE_ADMIN → bloquear
                    if(!roles.contains("ADMIN") && !roles.contains("ROLE_ADMIN")){
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }

                    return chain.filter(exchange);
                }catch (Exception e){
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

            }
            return chain.filter(exchange);
        };
    }
    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(keySignature);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
