package com.labo.anapath.auth;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class TwoFaIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("test_labo")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.baseline-on-migrate", () -> "true");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @LocalServerPort
    private int port;

    private static final String TEST_EMAIL = "2fa_test@labo.bj";
    private static final String TEST_PASSWORD = "password2fa";

    @BeforeEach
    void seedUser() {
        if (userRepository.findByEmail(TEST_EMAIL).isEmpty()) {
            User user = new User();
            user.setBranchId(UUID.randomUUID());
            user.setFirstname("2FA");
            user.setLastname("Test User");
            user.setEmail(TEST_EMAIL);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            user.setActive(true);
            userRepository.save(user);
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    private String loginAndGetToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> response = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().data().accessToken();
    }

    @Test
    @DisplayName("POST /auth/2fa/setup - should return QR code and secret for authenticated user")
    void setup_returnsQrCodeAndSecret() {
        String accessToken = loginAndGetToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<ApiResponse<TwoFaSetupResponse>> response = restTemplate.exchange(
                baseUrl() + "/2fa/setup",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        TwoFaSetupResponse data = response.getBody().data();
        assertThat(data).isNotNull();
        assertThat(data.secret()).isNotBlank();
        assertThat(data.qrCodeBase64()).isNotBlank();
    }

    @Test
    @DisplayName("Full 2FA flow: setup → verify → login requires challenge → challenge succeeds")
    void fullFlow_setup_verify_loginChallenge_success() {
        String accessToken = loginAndGetToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // 1. Setup — get secret
        ResponseEntity<ApiResponse<TwoFaSetupResponse>> setupResp = restTemplate.exchange(
                baseUrl() + "/2fa/setup",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {});
        assertThat(setupResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String secret = setupResp.getBody().data().secret();

        // 2. Verify — activate 2FA with live TOTP code
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        int totpCode = gAuth.getTotpPassword(secret);

        TwoFaCodeRequest verifyRequest = new TwoFaCodeRequest();
        verifyRequest.setCode(String.valueOf(totpCode));

        ResponseEntity<ApiResponse<Void>> verifyResp = restTemplate.exchange(
                baseUrl() + "/2fa/verify",
                HttpMethod.POST,
                new HttpEntity<>(verifyRequest, headers),
                new ParameterizedTypeReference<>() {});
        assertThat(verifyResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Login — should now require 2FA challenge
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> loginResp = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<>() {});

        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponse loginData = loginResp.getBody().data();
        assertThat(loginData.requires2fa()).isTrue();
        assertThat(loginData.tempToken()).isNotBlank();
        assertThat(loginData.accessToken()).isNull();

        // 4. Challenge — provide TOTP code + tempToken → get full JWT
        int challengeCode = gAuth.getTotpPassword(secret);

        TwoFactorVerifyRequest challengeRequest = new TwoFactorVerifyRequest();
        challengeRequest.setTempToken(loginData.tempToken());
        challengeRequest.setCode(String.valueOf(challengeCode));

        // AC-7: send User-Agent so lastLoginDevice can be computed
        HttpHeaders challengeHeaders = new HttpHeaders();
        challengeHeaders.set("User-Agent", "TestBrowser/1.0 Integration-Test");

        ResponseEntity<ApiResponse<LoginResponse>> challengeResp = restTemplate.exchange(
                baseUrl() + "/2fa/challenge",
                HttpMethod.POST,
                new HttpEntity<>(challengeRequest, challengeHeaders),
                new ParameterizedTypeReference<>() {});

        assertThat(challengeResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponse fullJwt = challengeResp.getBody().data();
        assertThat(fullJwt.accessToken()).isNotBlank();
        assertThat(fullJwt.refreshToken()).isNotBlank();
        assertThat(fullJwt.requires2fa()).isNull();

        // AC-7: lastLoginDevice doit être persisté après un challenge réussi
        User userAfterChallenge = userRepository.findByEmail(TEST_EMAIL).orElseThrow();
        assertThat(userAfterChallenge.getLastLoginDevice()).isNotBlank();

        // 5. Cleanup — disable 2FA to avoid polluting other tests
        int disableCode = gAuth.getTotpPassword(secret);
        TwoFaCodeRequest disableRequest = new TwoFaCodeRequest();
        disableRequest.setCode(String.valueOf(disableCode));

        HttpHeaders fullHeaders = new HttpHeaders();
        fullHeaders.setBearerAuth(fullJwt.accessToken());

        ResponseEntity<ApiResponse<Void>> disableResp = restTemplate.exchange(
                baseUrl() + "/2fa/disable",
                HttpMethod.POST,
                new HttpEntity<>(disableRequest, fullHeaders),
                new ParameterizedTypeReference<>() {});
        assertThat(disableResp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
