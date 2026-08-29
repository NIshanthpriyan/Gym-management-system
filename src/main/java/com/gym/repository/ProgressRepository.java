package com.gym.repository;

import com.gym.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByMemberIdOrderByRecordedDateDesc(Long memberId);
    List<Progress> findByMemberIdOrderByRecordedDateAsc(Long memberId);
}
