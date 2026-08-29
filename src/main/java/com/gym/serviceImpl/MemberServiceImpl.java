package com.gym.serviceImpl;

import com.gym.dto.MemberDTO;
import com.gym.entity.*;
import com.gym.exception.ResourceNotFoundException;
import com.gym.repository.MemberRepository;
import com.gym.repository.MembershipPlanRepository;

import com.gym.repository.UserRepository;
import com.gym.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private com.gym.repository.TrainerRepository trainerRepository;

    @Override
    public Page<MemberDTO> getAllMembers(String query, String status, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Member> membersPage;
        if (query != null && !query.trim().isEmpty()) {
            membersPage = memberRepository.searchMembers(query, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            membersPage = memberRepository.findByStatus(status, pageable);
        } else {
            membersPage = memberRepository.findAll(pageable);
        }

        List<MemberDTO> dtoList = membersPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, membersPage.getTotalElements());
    }

    @Override
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        return convertToDTO(member);
    }

    @Override
    public MemberDTO getMemberByUserId(Long userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "userId", userId));
        return convertToDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO memberDTO) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));

        User user = member.getUser();
        user.setFullName(memberDTO.getFullName());
        user.setEmail(memberDTO.getEmail());
        user.setPhone(memberDTO.getPhone());
        user.setAddress(memberDTO.getAddress());
        user.setGender(memberDTO.getGender());
        user.setDob(memberDTO.getDob());
        if (memberDTO.getStatus() != null) {
            user.setStatus(memberDTO.getStatus());
            member.setStatus(memberDTO.getStatus());
        }
        if (memberDTO.getProfilePicture() != null) {
            user.setProfilePicture(memberDTO.getProfilePicture());
        }
        userRepository.save(user);

        if (memberDTO.getMembershipPlanId() != null) {
            MembershipPlan plan = planRepository.findById(memberDTO.getMembershipPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan", "id", memberDTO.getMembershipPlanId()));
            member.setMembershipPlan(plan);
            // Extends expiry from today if changing plan
            member.setMembershipExpiryDate(LocalDate.now().plusMonths(plan.getDurationMonths()));
        }

        if (memberDTO.getTrainerId() != null) {
            Trainer trainer = trainerRepository.findById(memberDTO.getTrainerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", memberDTO.getTrainerId()));
            member.setTrainer(trainer);
        } else {
            member.setTrainer(null);
        }

        if (memberDTO.getMembershipExpiryDate() != null) {
            member.setMembershipExpiryDate(memberDTO.getMembershipExpiryDate());
        }

        member.setEmergencyContact(memberDTO.getEmergencyContact());
        member.setEmergencyPhone(memberDTO.getEmergencyPhone());

        Member updatedMember = memberRepository.save(member);
        return convertToDTO(updatedMember);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", "id", id));
        memberRepository.delete(member); // Will cascade delete user
    }

    @Override
    public List<MemberDTO> getExpiringMemberships(int daysLimit) {
        LocalDate thresholdDate = LocalDate.now().plusDays(daysLimit);
        List<Member> expiringMembers = memberRepository.findExpiringMemberships(thresholdDate);
        return expiringMembers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    private MemberDTO convertToDTO(Member member) {
        MemberDTO dto = new MemberDTO();
        dto.setId(member.getId());
        dto.setJoinDate(member.getJoinDate());
        dto.setStatus(member.getStatus());
        dto.setMembershipExpiryDate(member.getMembershipExpiryDate());
        dto.setEmergencyContact(member.getEmergencyContact());
        dto.setEmergencyPhone(member.getEmergencyPhone());

        User user = member.getUser();
        if (user != null) {
            dto.setUserId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setFullName(user.getFullName());
            dto.setPhone(user.getPhone());
            dto.setAddress(user.getAddress());
            dto.setGender(user.getGender());
            dto.setDob(user.getDob());
            dto.setProfilePicture(user.getProfilePicture());
        }

        MembershipPlan plan = member.getMembershipPlan();
        if (plan != null) {
            dto.setMembershipPlanId(plan.getId());
            dto.setMembershipPlanName(plan.getName());
            dto.setMembershipPlanPrice(plan.getPrice().doubleValue());
        }

        Trainer trainer = member.getTrainer();
        if (trainer != null) {
            dto.setTrainerId(trainer.getId());
            if (trainer.getUser() != null) {
                dto.setTrainerName(trainer.getUser().getFullName());
            }
        }

        return dto;
    }

    @Override
    public List<MemberDTO> getMembersByTrainerId(Long trainerId) {
        return memberRepository.findByTrainerId(trainerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberDTO> getMembersByTrainerUserId(Long userId) {
        return memberRepository.findByTrainerUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}
