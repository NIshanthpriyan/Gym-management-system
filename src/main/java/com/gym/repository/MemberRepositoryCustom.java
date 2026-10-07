package com.gym.repository;

import com.gym.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {
    Page<Member> searchMembers(String query, Pageable pageable);
    Optional<Member> findByUserUsername(String username);
    List<Member> findByTrainerUserId(Long userId);
}
