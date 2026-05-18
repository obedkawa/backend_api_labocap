package com.labo.anapath.auth;

import com.labo.anapath.common.dto.ApiResponse;
import com.labo.anapath.user.User;
import com.labo.anapath.user.UserRepository;
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

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AuthIntegrationTest {

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

    private static final String TEST_EMAIL = "test@labo.bj";
    private static final String TEST_PASSWORD = "password123";
    private static final String INACTIVE_EMAIL = "inactive@labo.bj";

    @BeforeEach
    void seedUser() {
        if (userRepository.findByEmail(TEST_EMAIL).isEmpty()) {
            User user = new User();
            user.setBranchId(UUID.randomUUID());
            user.setFirstname("Test");
            user.setLastname("User");
            user.setEmail(TEST_EMAIL);
            user.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            user.setActive(true);
            userRepository.save(user);
        }
        if (userRepository.findByEmail(INACTIVE_EMAIL).isEmpty()) {
            User inactive = new User();
            inactive.setBranchId(UUID.randomUUID());
            inactive.setFirstname("Inactive");
            inactive.setLastname("User");
            inactive.setEmail(INACTIVE_EMAIL);
            inactive.setPassword(passwordEncoder.encode(TEST_PASSWORD));
            inactive.setActive(false);
            userRepository.save(inactive);
        }
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    @Test
    @DisplayName("POST /login - credentials valides → 200 + tokens")
    void loginSuccess_returnsTokens() {
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> response = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().success()).isTrue();

        LoginResponse data = response.getBody().data();
        assertThat(data).isNotNull();
        assertThat(data.accessToken()).isNotBlank();
        assertThat(data.refreshToken()).isNotBlank();
        assertThat(data.tokenType()).isEqualTo("Bearer");
        assertThat(data.expiresIn()).isGreaterThan(0);
        assertThat(data.user().email()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("POST /login - mauvais password → 401")
    void loginInvalid_returns401() {
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword("wrong-password");

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /logout puis requête avec même token → 401")
    void logout_thenTokenRejected() {
        // 1. Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> loginResp = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<>() {});

        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String accessToken = loginResp.getBody().data().accessToken();

        // 2. Logout avec le token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<String> logoutResp = restTemplate.exchange(
                baseUrl() + "/logout",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);
        assertThat(logoutResp.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 3. Re-appel /logout avec le même token → doit retourner 401
        ResponseEntity<String> rejectedResp = restTemplate.exchange(
                baseUrl() + "/logout",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);
        assertThat(rejectedResp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /refresh - refresh token valide → nouveaux tokens (AC8)")
    void refreshToken_valid_returnsNewTokens() {
        // 1. Login pour obtenir un refresh token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> loginResp = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<>() {});

        assertThat(loginResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        String originalRefreshToken = loginResp.getBody().data().refreshToken();
        String originalAccessToken  = loginResp.getBody().data().accessToken();
        assertThat(originalRefreshToken).isNotBlank();

        // 2. Utiliser le refresh token → nouveaux tokens
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(originalRefreshToken);

        ResponseEntity<ApiResponse<LoginResponse>> refreshResp = restTemplate.exchange(
                baseUrl() + "/refresh",
                HttpMethod.POST,
                new HttpEntity<>(refreshRequest),
                new ParameterizedTypeReference<>() {});

        assertThat(refreshResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponse newTokens = refreshResp.getBody().data();
        assertThat(newTokens.accessToken()).isNotBlank();
        assertThat(newTokens.refreshToken()).isNotBlank();
        assertThat(newTokens.accessToken()).isNotEqualTo(originalAccessToken);
    }

    @Test
    @DisplayName("POST /refresh - access token en guise de refresh → 401")
    void refreshToken_withAccessToken_returns401() {
        // Login pour avoir un access token
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> loginResp = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<>() {});

        String accessToken = loginResp.getBody().data().accessToken();

        // Envoyer l'access token à /refresh → doit être rejeté
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(accessToken);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/refresh", refreshRequest, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("POST /login - utilisateur inactif → 401 (AC2)")
    void login_inactiveUser_returns401() {
        LoginRequest request = new LoginRequest();
        request.setEmail(INACTIVE_EMAIL);
        request.setPassword(TEST_PASSWORD);

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/login", request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Refresh token comme Authorization header → 401 (sécurité)")
    void refreshToken_asAuthorizationHeader_returns401() {
        // Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<ApiResponse<LoginResponse>> loginResp = restTemplate.exchange(
                baseUrl() + "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<>() {});

        String refreshToken = loginResp.getBody().data().refreshToken();

        // Utiliser le refresh token comme Bearer sur un endpoint protégé
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(refreshToken);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/logout",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
