package com.gym.repository;

import com.gym.entity.ChatHistory;
import com.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class ChatHistoryRepositoryImpl implements ChatHistoryRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<String> findDistinctUsernames() {
        return mongoTemplate.findDistinct(new Query(), "username", ChatHistory.class, String.class);
    }

    @Override
    public List<ChatHistory> findByUserUsernameOrderByTimestampAsc(String username) {
        User user = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), User.class);
        Criteria criteria;
        if (user != null) {
            criteria = new Criteria().orOperator(
                    Criteria.where("username").is(username),
                    Criteria.where("user.$id").is(user.getId())
            );
        } else {
            criteria = Criteria.where("username").is(username);
        }
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "timestamp"));
        return mongoTemplate.find(query, ChatHistory.class);
    }

    @Override
    public List<ChatHistory> findTop50ByUserUsernameOrderByTimestampDesc(String username) {
        User user = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), User.class);
        Criteria criteria;
        if (user != null) {
            criteria = new Criteria().orOperator(
                    Criteria.where("username").is(username),
                    Criteria.where("user.$id").is(user.getId())
            );
        } else {
            criteria = Criteria.where("username").is(username);
        }
        Query query = new Query(criteria).with(Sort.by(Sort.Direction.DESC, "timestamp")).limit(50);
        return mongoTemplate.find(query, ChatHistory.class);
    }

    @Override
    public void deleteByUserUsername(String username) {
        User user = mongoTemplate.findOne(new Query(Criteria.where("username").is(username)), User.class);
        Criteria criteria;
        if (user != null) {
            criteria = new Criteria().orOperator(
                    Criteria.where("username").is(username),
                    Criteria.where("user.$id").is(user.getId())
            );
        } else {
            criteria = Criteria.where("username").is(username);
        }
        mongoTemplate.remove(new Query(criteria), ChatHistory.class);
    }
}
