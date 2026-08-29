package com.gym.service;

import com.gym.dto.AuthResponse;
import com.gym.dto.LoginRequest;
import com.gym.dto.RegisterRequest;
import com.gym.entity.*;
import com.gym.exception.BadRequestException;
import com.gym.repository.*;
import com.gym.security.JwtTokenProvider;
import com.gym.serviceImpl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private MembershipPlanRepository planRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider tokenProvider;

    private User mockUser;
    private Role memberRole;

    @BeforeEach
    void setUp() {
        memberRole = new Role();
        memberRole.setId(3);
        memberRole.setName("ROLE_MEMBER");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testmember");
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("hashed_password");
        mockUser.setFullName("Test Member");
        mockUser.setStatus("ACTIVE");
        mockUser.setRoles(java.util.Set.of(memberRole));
    }

    @Test
    void login_shouldReturnAuthResponse_whenCredentialsAreValid() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testmember");
        loginRequest.setPassword("password123");

        Authentication mockAuth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_MEMBER")))
            .when(mockAuth).getAuthorities();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(mockAuth);
        when(tokenProvider.generateToken(mockAuth)).thenReturn("mock.jwt.token");
        when(userRepository.findByUsername("testmember")).thenReturn(Optional.of(mockUser));

        Member mockMember = new Member();
        mockMember.setId(10L);
        when(memberRepository.findByUserId(1L)).thenReturn(Optional.of(mockMember));

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock.jwt.token");
        assertThat(response.getUsername()).isEqualTo("testmember");
        assertThat(response.getRoles()).contains("ROLE_MEMBER");
        assertThat(response.getMemberOrTrainerId()).isEqualTo(10L);
    }

    @Test
    void registerMember_shouldThrowBadRequest_whenUsernameAlreadyExists() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existinguser");
        req.setEmail("newemail@test.com");
        req.setPassword("password123");
        req.setFullName("Test User");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.registerMember(req))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Username is already taken");
    }

    @Test
    void registerMember_shouldThrowBadRequest_whenEmailAlreadyExists() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("duplicate@test.com");
        req.setPassword("password123");
        req.setFullName("Test User");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("duplicate@test.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> authService.registerMember(req))
            .isInstanceOf(BadRequestException.class)
            .hasMessageContaining("Email is already in use");
    }

    @Test
    void registerMember_shouldSucceed_whenValidDataProvided() {
        // Arrange
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newmember");
        req.setEmail("new@member.com");
        req.setPassword("pass123");
        req.setFullName("New Member");

        when(userRepository.existsByUsername("newmember")).thenReturn(false);
        when(userRepository.existsByEmail("new@member.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed_pass");
        when(roleRepository.findByName("ROLE_MEMBER")).thenReturn(Optional.of(memberRole));

        User savedUser = new User();
        savedUser.setId(99L);
        savedUser.setUsername("newmember");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Member savedMember = new Member();
        savedMember.setId(55L);
        when(memberRepository.save(any(Member.class))).thenReturn(savedMember);

        // Act - should not throw
        assertThatCode(() -> authService.registerMember(req)).doesNotThrowAnyException();

        // Verify save was called
        verify(userRepository, times(1)).save(any(User.class));
        verify(memberRepository, times(1)).save(any(Member.class));
    }
}
