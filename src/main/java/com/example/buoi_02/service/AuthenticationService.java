package com.example.buoi_02.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.buoi_02.dto.request.AuthenticationRequest;
import com.example.buoi_02.dto.request.IntrospectRequest;
import com.example.buoi_02.dto.request.LogoutRequest;
import com.example.buoi_02.dto.request.RefreshRequest;
import com.example.buoi_02.dto.response.AuthenticationResponse;
import com.example.buoi_02.dto.response.IntrospectResponse;
import com.example.buoi_02.entity.InvalidatedToken;
import com.example.buoi_02.entity.User;
import com.example.buoi_02.exception.AppException;
import com.example.buoi_02.exception.ErrorCode;
import com.example.buoi_02.repository.InvalidatedTokenRepository;
import com.example.buoi_02.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final InvalidatedTokenRepository invalidatedTokenRepository;
	
	//chữ ký SIGNER_KEY rất quan trọng, bởi chỉ cần người khác biết được chữ ký này.
	//họ hoàn toàn có thể thay đổi nội dung payload.
	//Nếu họ không có chữ ký thì họ không thể nào sử đổi được
	@NonFinal
	@Value("${jwt.signerKey}")
	protected String SIGNER_KEY;
	
	@NonFinal
	@Value("${jwt.valid-duration}")
	protected long VALID_DURATION;
	
	@NonFinal
	@Value("${jwt.refreshable-duration}")
	protected long REFRESHABLE_DURATION;

	//verifie token
	public IntrospectResponse introspect(IntrospectRequest request)
			throws JOSEException, ParseException {
		//để verifier cần verifier và signedJWT

		var token = request.getToken();
		boolean invalid = true;
		try {
			verifyToken(token, false);
		} catch (AppException e){
			invalid = false;
		}
		
		//thời gian hết hạn phải SAU thời gian hiện tại
		return IntrospectResponse.builder()
				.valid(invalid)
				.build();
	}
	
	private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
		//do mã hoá bằng thuật toán SHA-512 nên phải verifier bằng thuật toán SHA
		JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

		SignedJWT signedJWT = SignedJWT.parse(token);
		//kiểm tra xem token này có hết hạn hay chưa
		//nếu isRefresh = true thì verify này dùng để refresh token
		//nếu false thì verify cho authencicate hoặc introspect
		Date expiryTime = (isRefresh)
				? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
						.plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
				: signedJWT.getJWTClaimsSet().getExpirationTime();

		var verified = signedJWT.verify(verifier); //verify trả về true hoặc false
		
		if (!(verified && expiryTime.after(new Date()))) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		
		if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}
		
		return signedJWT;
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		var user = userRepository.findByUserName(request.getUserName())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!authenticated) {
			throw new AppException(ErrorCode.UNAUTHENTICATED);
		}

		var token = generateToken(user);

		return AuthenticationResponse.builder()
				.token(token)
				.authenticated(true)
				.build();
	}

	private String generateToken(User user) {
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
				.subject(user.getUserName())
				.issuer("devteria.com")
				.issueTime(new Date())
				.expirationTime(new Date(
						Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
						))
				.jwtID(UUID.randomUUID().toString())
				.claim("scope", buildScope(user))
				.build();
		Payload payload = new Payload(jwtClaimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
			return jwsObject.serialize();
		} catch (JOSEException e) {
			System.err.println("Cannot create Token: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");
		if (!CollectionUtils.isEmpty(user.getRoles())) {
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if (!CollectionUtils.isEmpty(role.getPermissions())) {
					role.getPermissions().forEach(permission -> {
						stringJoiner.add(permission.getName());
					});
				}
			});
		}

		return stringJoiner.toString();
	}
	
	//Logout này chỉ tạm thời lưu các id token đã hết hạn vào Database
	//Nên vì vậy cần tìm hiểu thêm cronjob để xoá các id đó sau 1 khoảng thời gian
	//để database nó nhẹ đi
	public void logout(LogoutRequest request) throws JOSEException, ParseException {
		try {
			var signToken = verifyToken(request.getToken(), true);
			
			String jit = signToken.getJWTClaimsSet().getJWTID();
			Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
			
			InvalidatedToken invalidatedToken = InvalidatedToken.builder()
					.id(jit)
					.expiryTime(expiryTime)
					.build();
			
			invalidatedTokenRepository.save(invalidatedToken);
		} catch(AppException exception) {
			log.info("Token already expired");
		}
	}
	
	public AuthenticationResponse refreshToken(RefreshRequest request) throws JOSEException, ParseException {
		//kiem tra hieu luc cua token
		var signedJWT = verifyToken(request.getToken(), true);
		var jit = signedJWT.getJWTClaimsSet().getJWTID();
		var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		
		InvalidatedToken invalidatedToken = InvalidatedToken.builder()
				.id(jit)
				.expiryTime(expiryTime)
				.build();
		invalidatedTokenRepository.save(invalidatedToken);
		
		var userName = signedJWT.getJWTClaimsSet().getSubject();
		var user = userRepository.findByUserName(userName).orElseThrow(
			() -> new AppException(ErrorCode.UNAUTHENTICATED)
		);
		
		var token = generateToken(user);

		return AuthenticationResponse.builder()
				.token(token)
				.authenticated(true)
				.build();
	}
}
