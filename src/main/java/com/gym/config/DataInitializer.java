package com.gym.config;

import com.gym.entity.*;
import com.gym.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MembershipPlanRepository planRepository;



    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Seed Roles
        Role adminRole = seedRole("ROLE_ADMIN");
        Role memberRole = seedRole("ROLE_MEMBER");

        // 2. Seed Membership Plans
        MembershipPlan basicPlan = seedPlan("Monthly Basic", 1, BigDecimal.valueOf(49.99), "Access to all cardio equipment and weights.");
        seedPlan("Quarterly Standard", 3, BigDecimal.valueOf(129.99), "Access to all equipment plus 2 group sessions/month.");
        seedPlan("Annual Premium", 12, BigDecimal.valueOf(399.99), "Unrestricted access, personal trainer consult, and pool.");

        // 3. Seed Admin User
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@fitness.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setFullName("Administrator");
            admin.setPhone("9876543210");
            admin.setAddress("123 Main St, New York");
            admin.setGender("MALE");
            admin.setDob(LocalDate.of(1990, 1, 1));
            admin.setStatus("ACTIVE");
            admin.setRoles(Collections.singleton(adminRole));
            userRepository.save(admin);
        }

        // 4. Seed Trainer User & Profile
        Role trainerRole = seedRole("ROLE_TRAINER");
        if (!userRepository.existsByUsername("johndoe")) {
            User trainerUser = new User();
            trainerUser.setUsername("johndoe");
            trainerUser.setEmail("johndoe@fitness.com");
            trainerUser.setPassword(passwordEncoder.encode("trainer123"));
            trainerUser.setFullName("John Doe");
            trainerUser.setPhone("9876543211");
            trainerUser.setAddress("456 Park Ave, New York");
            trainerUser.setGender("MALE");
            trainerUser.setDob(LocalDate.of(1988, 5, 15));
            trainerUser.setStatus("ACTIVE");
            trainerUser.setRoles(Collections.singleton(trainerRole));

            User savedUser = userRepository.save(trainerUser);

            Trainer trainer = new Trainer();
            trainer.setUser(savedUser);
            trainer.setSpecialization("Strength & Conditioning");
            trainer.setExperienceYears(5);
            trainer.setSalary(BigDecimal.valueOf(3500.00));
            trainer.setStatus("ACTIVE");
            trainerRepository.save(trainer);
        }

        // 5. Seed Member User & Profile
        if (!userRepository.existsByUsername("alice_smith")) {
            User memberUser = new User();
            memberUser.setUsername("alice_smith");
            memberUser.setEmail("alice@member.com");
            memberUser.setPassword(passwordEncoder.encode("member123"));
            memberUser.setFullName("Alice Smith");
            memberUser.setPhone("9876543212");
            memberUser.setAddress("789 Road St, Texas");
            memberUser.setGender("FEMALE");
            memberUser.setDob(LocalDate.of(1995, 10, 20));
            memberUser.setStatus("ACTIVE");
            memberUser.setRoles(Collections.singleton(memberRole));
            
            User savedUser = userRepository.save(memberUser);

            Member member = new Member();
            member.setUser(savedUser);
            member.setJoinDate(LocalDate.now().minusDays(15));
            member.setStatus("ACTIVE");
            member.setMembershipPlan(basicPlan);
            member.setMembershipExpiryDate(LocalDate.now().plusMonths(1));

            Trainer defaultTrainer = trainerRepository.findByUserUsername("johndoe").orElse(null);
            member.setTrainer(defaultTrainer);

            member.setEmergencyContact("Bob Smith");
            member.setEmergencyPhone("9876543219");
            memberRepository.save(member);
        }
    }

    private Role seedRole(String roleName) {
        return roleRepository.findByName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleName);
            return roleRepository.save(role);
        });
    }

    private MembershipPlan seedPlan(String name, int duration, BigDecimal price, String desc) {
        return planRepository.findByStatus("ACTIVE").stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> {
                    MembershipPlan plan = new MembershipPlan();
                    plan.setName(name);
                    plan.setDurationMonths(duration);
                    plan.setPrice(price);
                    plan.setDescription(desc);
                    plan.setStatus("ACTIVE");
                    return planRepository.save(plan);
                });
    }
}
