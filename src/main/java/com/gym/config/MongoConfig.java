package com.gym.config;

import com.gym.entity.*;
import com.gym.service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@Configuration
public class MongoConfig {

    @Autowired
    private SequenceGeneratorService sequenceGenerator;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new AbstractPlatformTransactionManager() {
            @Override
            protected Object doGetTransaction() {
                return new Object();
            }

            @Override
            protected void doBegin(Object transaction, TransactionDefinition definition) {
            }

            @Override
            protected void doCommit(DefaultTransactionStatus status) {
            }

            @Override
            protected void doRollback(DefaultTransactionStatus status) {
            }
        };
    }

    @Bean
    public AbstractMongoEventListener<Object> mongoModelListener() {
        return new AbstractMongoEventListener<Object>() {
            @Override
            public void onBeforeConvert(BeforeConvertEvent<Object> event) {
                Object source = event.getSource();

                if (source instanceof User user) {
                    if (user.getId() == null) {
                        user.setId(sequenceGenerator.generateSequence("users_sequence"));
                    }
                } else if (source instanceof Role role) {
                    if (role.getId() == null) {
                        role.setId((int) sequenceGenerator.generateSequence("roles_sequence"));
                    }
                } else if (source instanceof Member member) {
                    if (member.getId() == null) {
                        member.setId(sequenceGenerator.generateSequence("members_sequence"));
                    }
                } else if (source instanceof Trainer trainer) {
                    if (trainer.getId() == null) {
                        trainer.setId(sequenceGenerator.generateSequence("trainers_sequence"));
                    }
                } else if (source instanceof MembershipPlan plan) {
                    if (plan.getId() == null) {
                        plan.setId(sequenceGenerator.generateSequence("plans_sequence"));
                    }
                } else if (source instanceof GymClass gymClass) {
                    if (gymClass.getId() == null) {
                        gymClass.setId(sequenceGenerator.generateSequence("classes_sequence"));
                    }
                } else if (source instanceof ClassBooking booking) {
                    if (booking.getId() == null) {
                        booking.setId(sequenceGenerator.generateSequence("bookings_sequence"));
                    }
                } else if (source instanceof Payment payment) {
                    if (payment.getId() == null) {
                        payment.setId(sequenceGenerator.generateSequence("payments_sequence"));
                    }
                } else if (source instanceof Attendance attendance) {
                    if (attendance.getId() == null) {
                        attendance.setId(sequenceGenerator.generateSequence("attendance_sequence"));
                    }
                } else if (source instanceof Progress progress) {
                    if (progress.getId() == null) {
                        progress.setId(sequenceGenerator.generateSequence("progress_sequence"));
                    }
                } else if (source instanceof DietPlan dietPlan) {
                    if (dietPlan.getId() == null) {
                        dietPlan.setId(sequenceGenerator.generateSequence("diet_plans_sequence"));
                    }
                } else if (source instanceof DietMeal meal) {
                    if (meal.getId() == null) {
                        meal.setId(sequenceGenerator.generateSequence("diet_meals_sequence"));
                    }
                } else if (source instanceof WorkoutPlan workoutPlan) {
                    if (workoutPlan.getId() == null) {
                        workoutPlan.setId(sequenceGenerator.generateSequence("workout_plans_sequence"));
                    }
                } else if (source instanceof WorkoutExercise exercise) {
                    if (exercise.getId() == null) {
                        exercise.setId(sequenceGenerator.generateSequence("workout_exercises_sequence"));
                    }
                } else if (source instanceof ChatHistory chatHistory) {
                    if (chatHistory.getId() == null) {
                        chatHistory.setId(sequenceGenerator.generateSequence("chat_history_sequence"));
                    }
                }
            }
        };
    }
}
