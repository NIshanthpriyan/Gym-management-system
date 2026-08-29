package com.gym.serviceImpl;

import com.gym.dto.AuthResponse;
import com.gym.dto.LoginRequest;
import com.gym.dto.RegisterRequest;
import com.gym.entity.*;
import com.gym.exception.BadRequestException;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.*;
import com.gym.security.JwtTokenProvider;
import com.gym.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long memberOrTrainerId = null;
        if (roles.contains("ROLE_MEMBER")) {
            Member member = memberRepository.findByUserId(user.getId()).orElse(null);
            if (member != null) memberOrTrainerId = member.getId();
        } else if (roles.contains("ROLE_TRAINER")) {
            Trainer trainer = trainerRepository.findByUserId(user.getId()).orElse(null);
            if (trainer != null) memberOrTrainerId = trainer.getId();
        }

        return new AuthResponse(jwt, user.getId(), user.getUsername(), user.getEmail(), roles, memberOrTrainerId);
    }

    @Override
    @Transactional
    public void registerMember(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        // Create User
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setGender(registerRequest.getGender());
        user.setDob(registerRequest.getDob());
        user.setStatus("ACTIVE");

        Role memberRole = roleRepository.findByName("ROLE_MEMBER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        user.setRoles(Collections.singleton(memberRole));

        User savedUser = userRepository.save(user);

        // Create Member details
        Member member = new Member();
        member.setUser(savedUser);
        member.setJoinDate(LocalDate.now());
        member.setStatus("ACTIVE");

        if (registerRequest.getMembershipPlanId() != null) {
            MembershipPlan plan = planRepository.findById(registerRequest.getMembershipPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Membership Plan not found"));
            member.setMembershipPlan(plan);
            member.setMembershipExpiryDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
        } else {
            // Default 1 month expiry if no plan
            member.setMembershipExpiryDate(LocalDate.now().plusMonths(1));
        }



        member.setEmergencyContact(registerRequest.getEmergencyContact());
        member.setEmergencyPhone(registerRequest.getEmergencyPhone());

        memberRepository.save(member);
    }
}
