package com.gym.repository;

import com.gym.entity.Member;
import com.gym.entity.Trainer;
import com.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Member> searchMembers(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            long total = mongoTemplate.count(new Query(), Member.class);
            List<Member> list = mongoTemplate.find(new Query().with(pageable), Member.class);
            return new PageImpl<>(list, pageable, total);
        }

        Pattern pattern = Pattern.compile(Pattern.quote(query.trim()), Pattern.CASE_INSENSITIVE);
        Query userQuery = new Query(new Criteria().orOperator(
                Criteria.where("fullName").regex(pattern),
                Criteria.where("email").regex(pattern),
                Criteria.where("phone").regex(pattern)
        ));
        List<User> matchingUsers = mongoTemplate.find(userQuery, User.class);
        if (matchingUsers.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Long> userIds = matchingUsers.stream().map(User::getId).toList();
        Query memberQuery = new Query(Criteria.where("user.$id").in(userIds));
        long total = mongoTemplate.count(memberQuery, Member.class);
        List<Member> members = mongoTemplate.find(memberQuery.with(pageable), Member.class);
        return new PageImpl<>(members, pageable, total);
    }

    @Override
    public Optional<Member> findByUserUsername(String username) {
        User user = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), User.class);
        if (user == null) {
            return Optional.empty();
        }
        Member member = mongoTemplate.findOne(new Query(Criteria.where("user.$id").is(user.getId())), Member.class);
        return Optional.ofNullable(member);
    }

    @Override
    public List<Member> findByTrainerUserId(Long userId) {
        Trainer trainer = mongoTemplate.findOne(new Query(Criteria.where("user.$id").is(userId)), Trainer.class);
        if (trainer == null) {
            return Collections.emptyList();
        }
        return mongoTemplate.find(new Query(Criteria.where("trainer.$id").is(trainer.getId())), Member.class);
    }
}
