package com.gym.repository;

import com.gym.entity.Progress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProgressRepository extends MongoRepository<Progress, Long> {

    @Query(value = "{ 'member.$id': ?0 }", sort = "{ 'recordedDate': -1 }")
    List<Progress> findByMemberIdOrderByRecordedDateDesc(Long memberId);

    @Query(value = "{ 'member.$id': ?0 }", sort = "{ 'recordedDate': 1 }")
    List<Progress> findByMemberIdOrderByRecordedDateAsc(Long memberId);
}
