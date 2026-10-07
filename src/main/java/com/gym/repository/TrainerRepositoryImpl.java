package com.gym.repository;

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

public class TrainerRepositoryImpl implements TrainerRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<Trainer> findByUserUsername(String username) {
        User user = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), User.class);
        if (user == null) {
            return Optional.empty();
        }
        Trainer trainer = mongoTemplate.findOne(new Query(Criteria.where("user.$id").is(user.getId())), Trainer.class);
        return Optional.ofNullable(trainer);
    }

    @Override
    public Page<Trainer> searchTrainers(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            long total = mongoTemplate.count(new Query(), Trainer.class);
            List<Trainer> list = mongoTemplate.find(new Query().with(pageable), Trainer.class);
            return new PageImpl<>(list, pageable, total);
        }

        Pattern pattern = Pattern.compile(Pattern.quote(query.trim()), Pattern.CASE_INSENSITIVE);
        Query userQuery = new Query(new Criteria().orOperator(
                Criteria.where("fullName").regex(pattern),
                Criteria.where("email").regex(pattern)
        ));
        List<User> matchingUsers = mongoTemplate.find(userQuery, User.class);
        List<Long> userIds = matchingUsers.stream().map(User::getId).toList();

        Criteria criteria = new Criteria().orOperator(
                Criteria.where("user.$id").in(userIds),
                Criteria.where("specialization").regex(pattern)
        );

        Query trainerQuery = new Query(criteria);
        long total = mongoTemplate.count(trainerQuery, Trainer.class);
        List<Trainer> trainers = mongoTemplate.find(trainerQuery.with(pageable), Trainer.class);
        return new PageImpl<>(trainers, pageable, total);
    }
}
