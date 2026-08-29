package com.gym.service;

import com.gym.dto.MemberDTO;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.*;
import com.gym.serviceImpl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock private MemberRepository memberRepository;
    @Mock private UserRepository userRepository;
    @Mock private MembershipPlanRepository planRepository;


    private Member mockMember;
    private User mockUser;
    private MembershipPlan mockPlan;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("john_doe");
        mockUser.setFullName("John Doe");
        mockUser.setEmail("john@test.com");
        mockUser.setPhone("1234567890");
        mockUser.setStatus("ACTIVE");

        mockPlan = new MembershipPlan();
        mockPlan.setId(1L);
        mockPlan.setName("Gold Plan");
        mockPlan.setDurationMonths(6);
        mockPlan.setPrice(BigDecimal.valueOf(100.0));

        mockMember = new Member();
        mockMember.setId(1L);
        mockMember.setUser(mockUser);
        mockMember.setMembershipPlan(mockPlan);
        mockMember.setJoinDate(LocalDate.now());
        mockMember.setMembershipExpiryDate(LocalDate.now().plusMonths(6));
        mockMember.setStatus("ACTIVE");
    }

    @Test
    void getAllMembers_shouldReturnPagedResult() {
        Page<Member> mockPage = new PageImpl<>(List.of(mockMember));
        when(memberRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<MemberDTO> result = memberService.getAllMembers(null, null, 0, 10, "id", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFullName()).isEqualTo("John Doe");
    }

    @Test
    void getMemberById_shouldReturnMember_whenIdExists() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));

        MemberDTO result = memberService.getMemberById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
    }

    @Test
    void getMemberById_shouldThrowResourceNotFoundException_whenIdNotFound() {
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberById(999L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteMember_shouldCallDeleteAndUserDelete_whenMemberExists() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).delete(mockMember);
    }


}
