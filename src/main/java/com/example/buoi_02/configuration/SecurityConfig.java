package com.example.buoi_02.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration			//đã enable @EnableWebSecurity
@EnableWebSecurity		//optional có thể hoặc không
@EnableMethodSecurity	//phần quyền trên phương thức
public class SecurityConfig {

	private final String[] PUBLIC_ENDPOINT = { "/users", "/auth/token", "auth/introspect", 
			"auth/logout", "auth/refresh" };

//	@Value("${jwt.signerKey}")
//	private String signerKey;
	@Autowired
	private CustomJwtDecoder customJwtDecoder;
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    	httpSecurity.authorizeHttpRequests(
    			request -> request
    			.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT)	//xác định endpoint nào và cấu hình ra sao
    			.permitAll()		//-->cho phép truy cập mà không cần security
    			//muốn truy cập vào endpoint "/users" phải có Role là ADMIN
//    			.requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")	//cach 1
//    			.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name()) 	//cach 2
    			.anyRequest()		//--> các tất cả các request khác
    			.authenticated()	//--> phải yêu cầu authenticated()
    	);

    	//khi ta config oauth2ResourceServer thì ta muốn đky 1 provider manager,
    	//1 cái authentication provider để support cho jwt token
    	//nghĩ là khi ta thực hiện 1 request mà ta cung cấp được 1 token hợp lệ vào cái header authentication
    	//thì sẽ truy cập vào được các endpoint không đc permit
    	httpSecurity.oauth2ResourceServer(
    			oauth -> oauth.jwt(
    					jwtConfigurer -> 
    					jwtConfigurer.decoder(customJwtDecoder) //để thực hiện validate cần 1 jwtDecoder
//    								 .jwtAuthenticationConverter(jwtAuthenticationConverter())
//    								 .jwtAuthenticationConverter(new JwtAuthenticationConverter())
    					).authenticationEntryPoint(new JwtAuthenticationEntryPoint())
    	);

    	//spring security mặc định config sẽ bọc cái cấu hình csrf
    	//csrf sẽ bảo vệ endpoint khỏi những attach crop2
//    	httpSecurity.csrf(csrfConfigurer -> csrfConfigurer.disable());
    	httpSecurity.csrf(AbstractHttpConfigurer::disable);

        return httpSecurity.build();
    }
    
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
    	JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    	grantedAuthoritiesConverter.setAuthorityPrefix("");
    	
    	JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
    	authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    	
    	return authenticationConverter;
    }

    //decode cái jwtoken ta đưa vào hợp lệ hay không hợp lệ
    //chịu trách nhiệm cho việc verifyToken
//    @Bean
//    JwtDecoder jwtDecoder() {
//    	//key trong SecretKeySpec chính là signerKey
//    	//algorithm bên token dùng cái nào thì bên đây dùng y chang
//    	SecretKeySpec secretKey = new SecretKeySpec(signerKey.getBytes(), "HS512");
//    	return NimbusJwtDecoder
//    			.withSecretKey(secretKey)
//    			.macAlgorithm(MacAlgorithm.HS512)
//    			.build();
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder(10);
    }
}