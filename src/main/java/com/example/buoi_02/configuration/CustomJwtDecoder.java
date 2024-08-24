package com.example.buoi_02.configuration;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.buoi_02.dto.request.IntrospectRequest;
import com.example.buoi_02.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

@Component
public class CustomJwtDecoder implements JwtDecoder{

	@Value("${jwt.signerKey}")
	private String signerKey;
	@Autowired
	private AuthenticationService authenticationService;
	private NimbusJwtDecoder nimbusJwtDecoder = null;
	
	@Override
	public Jwt decode(String token) throws JwtException {
		try {
			//để check xem cái token có còn hiệu hay không, nếu không còn thì trả về exception
			var respone = authenticationService.introspect(IntrospectRequest.builder()
					.token(token)
					.build());
			if (!respone.isValid()) {
				throw new JwtException("Token invalid");
			}
		} catch (JOSEException | ParseException e) {
			throw new JwtException(e.getMessage());
		}
		
		//nếu token còn hiệu lực
		if (Objects.isNull(nimbusJwtDecoder)) {
			SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
			nimbusJwtDecoder = NimbusJwtDecoder		//thực hiện xác thực token và build jwt này theo yêu cầu spring security
					.withSecretKey(secretKeySpec)
					.macAlgorithm(MacAlgorithm.HS512)
					.build();
		}
		
		return nimbusJwtDecoder.decode(token);
	}

}
