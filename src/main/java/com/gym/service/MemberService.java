package com.gym.service;

import com.gym.dto.MemberDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {
    Page<MemberDTO> getAllMembers(String query, String status, int page, int size, String sortBy, String sortDir);
    MemberDTO getMemberById(Long id);
    MemberDTO getMemberByUserId(Long userId);
    MemberDTO updateMember(Long id, MemberDTO memberDTO);
    void deleteMember(Long id);
    List<MemberDTO> getExpiringMemberships(int daysLimit);
    List<MemberDTO> getMembersByTrainerId(Long trainerId);
    List<MemberDTO> getMembersByTrainerUserId(Long userId);
}
