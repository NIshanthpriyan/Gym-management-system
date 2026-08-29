-- CREATE DATABASE
CREATE DATABASE IF NOT EXISTS gym_management_db;
USE gym_management_db;

-- -----------------------------------------------------
-- Table `roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `roles` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(100) NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(15) DEFAULT NULL,
  `address` TEXT DEFAULT NULL,
  `gender` VARCHAR(10) DEFAULT NULL,
  `dob` DATE DEFAULT NULL,
  `profile_picture` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'ACTIVE',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user_username (username),
  INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `user_roles`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` BIGINT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `membership_plans`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `membership_plans` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(50) NOT NULL UNIQUE,
  `duration_months` INT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `trainers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `trainers` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL UNIQUE,
  `specialization` VARCHAR(100) DEFAULT NULL,
  `experience_years` INT DEFAULT 0,
  `salary` DECIMAL(10,2) DEFAULT 0.00,
  `status` VARCHAR(20) DEFAULT 'ACTIVE',
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `members`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `members` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL UNIQUE,
  `membership_plan_id` BIGINT DEFAULT NULL,
  `trainer_id` BIGINT DEFAULT NULL,
  `join_date` DATE NOT NULL,
  `status` VARCHAR(20) DEFAULT 'ACTIVE',
  `membership_expiry_date` DATE DEFAULT NULL,
  `emergency_contact` VARCHAR(100) DEFAULT NULL,
  `emergency_phone` VARCHAR(15) DEFAULT NULL,
  INDEX idx_member_expiry (membership_expiry_date),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`membership_plan_id`) REFERENCES `membership_plans` (`id`) ON DELETE SET NULL,
  FOREIGN KEY (`trainer_id`) REFERENCES `trainers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `attendance`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `attendance` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `date` DATE NOT NULL,
  `check_in_time` TIME DEFAULT NULL,
  `check_out_time` TIME DEFAULT NULL,
  `status` VARCHAR(20) DEFAULT 'PRESENT',
  INDEX idx_attendance_date (date),
  UNIQUE KEY `user_date_uniq` (`user_id`, `date`),
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `payments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `payments` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL,
  `membership_plan_id` BIGINT DEFAULT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `payment_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `payment_method` VARCHAR(30) DEFAULT 'CASH',
  `transaction_id` VARCHAR(100) DEFAULT NULL UNIQUE,
  `status` VARCHAR(20) DEFAULT 'COMPLETED',
  `pdf_receipt_path` VARCHAR(255) DEFAULT NULL,
  INDEX idx_payment_date (payment_date),
  FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`membership_plan_id`) REFERENCES `membership_plans` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `workout_plans`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workout_plans` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL,
  `trainer_id` BIGINT DEFAULT NULL,
  `plan_name` VARCHAR(100) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `duration_weeks` INT DEFAULT 4,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`trainer_id`) REFERENCES `trainers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `workout_exercises`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `workout_exercises` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `workout_plan_id` BIGINT NOT NULL,
  `day_of_week` VARCHAR(15) NOT NULL,
  `exercise_name` VARCHAR(100) NOT NULL,
  `sets` INT NOT NULL DEFAULT 3,
  `reps` INT NOT NULL DEFAULT 10,
  `weight_lbs` INT DEFAULT 0,
  `notes` TEXT DEFAULT NULL,
  FOREIGN KEY (`workout_plan_id`) REFERENCES `workout_plans` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- Table `progress`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `progress` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL,
  `recorded_date` DATE NOT NULL,
  `weight` DECIMAL(5,2) DEFAULT NULL,
  `height` DECIMAL(5,2) DEFAULT NULL,
  `body_fat_percentage` DECIMAL(5,2) DEFAULT NULL,
  `muscle_mass` DECIMAL(5,2) DEFAULT NULL,
  `chest` DECIMAL(5,2) DEFAULT NULL,
  `waist` DECIMAL(5,2) DEFAULT NULL,
  `hips` DECIMAL(5,2) DEFAULT NULL,
  `bmi` DECIMAL(5,2) DEFAULT NULL,
  `notes` TEXT DEFAULT NULL,
  FOREIGN KEY (`member_id`) REFERENCES `members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- SEED DATA
-- -----------------------------------------------------

-- Insert Roles
INSERT INTO `roles` (`id`, `name`) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_TRAINER'),
(3, 'ROLE_MEMBER')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert Sample Users
-- Password hash corresponds to 'admin123' (for admin), 'trainer123' (for trainer), 'member123' (for member) using BCrypt
INSERT INTO `users` (`id`, `username`, `email`, `password`, `full_name`, `phone`, `address`, `gender`, `dob`, `status`) VALUES
(1, 'admin', 'admin@fitness.com', '$2a$10$w09aFwQx0bE7/lU4o3rB/.R5ZcSpZ7kXnNzeC8.Vd1sL.9M9c34uO', 'Administrator', '9876543210', '123 Main St, New York', 'MALE', '1990-01-01', 'ACTIVE'),
(2, 'johndoe', 'johndoe@trainer.com', '$2a$10$T1q5qS9eEw2b28F.jUuPqu743.mUuR6lJd83jUu931S1sL.9Mc34u', 'John Doe (Trainer)', '9876543211', '456 Gym Ave, California', 'MALE', '1985-05-15', 'ACTIVE'),
(3, 'alice_smith', 'alice@member.com', '$2a$10$e79eS9eEw2b28F.jUuPqu743.mUuR6lJd83jUu931S1sL.9Mc34u', 'Alice Smith', '9876543212', '789 Road St, Texas', 'FEMALE', '1995-10-20', 'ACTIVE')
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- Map Roles
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 2),
(3, 3)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Insert Membership Plans
INSERT INTO `membership_plans` (`id`, `name`, `duration_months`, `price`, `description`, `status`) VALUES
(1, 'Monthly Basic', 1, 49.99, 'Access to all cardio equipment and weights.', 'ACTIVE'),
(2, 'Quarterly Standard', 3, 129.99, 'Access to all equipment plus 2 group sessions/month.', 'ACTIVE'),
(3, 'Annual Premium', 12, 399.99, 'Unrestricted access, personal trainer consult, and pool.', 'ACTIVE')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Insert Trainer Details
INSERT INTO `trainers` (`id`, `user_id`, `specialization`, `experience_years`, `salary`, `status`) VALUES
(1, 2, 'Bodybuilding & Nutrition', 8, 3500.00, 'ACTIVE')
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Insert Member Details
INSERT INTO `members` (`id`, `user_id`, `membership_plan_id`, `trainer_id`, `join_date`, `status`, `membership_expiry_date`, `emergency_contact`, `emergency_phone`) VALUES
(1, 3, 1, 1, '2026-07-01', 'ACTIVE', '2026-08-01', 'Bob Smith', '9876543219')
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Insert Sample Attendance
INSERT INTO `attendance` (`user_id`, `date`, `check_in_time`, `check_out_time`, `status`) VALUES
(3, '2026-07-15', '08:00:00', '09:30:00', 'PRESENT'),
(3, '2026-07-16', '08:15:00', '09:45:00', 'PRESENT'),
(3, '2026-07-17', '08:05:00', '09:20:00', 'PRESENT'),
(2, '2026-07-15', '07:30:00', '16:30:00', 'PRESENT'),
(2, '2026-07-16', '07:30:00', '16:30:00', 'PRESENT')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- Insert Sample Payments
INSERT INTO `payments` (`id`, `member_id`, `membership_plan_id`, `amount`, `payment_date`, `payment_method`, `transaction_id`, `status`) VALUES
(1, 1, 1, 49.99, '2026-07-01 10:00:00', 'CREDIT_CARD', 'TXN748291048', 'COMPLETED')
ON DUPLICATE KEY UPDATE transaction_id=VALUES(transaction_id);

-- Insert Workout Plan
INSERT INTO `workout_plans` (`id`, `member_id`, `trainer_id`, `plan_name`, `description`, `duration_weeks`) VALUES
(1, 1, 1, 'Beginner Fat Loss', 'Cardio-focused strength regime for toning and fat loss.', 4)
ON DUPLICATE KEY UPDATE plan_name=VALUES(plan_name);

-- Insert Exercises
INSERT INTO `workout_exercises` (`workout_plan_id`, `day_of_week`, `exercise_name`, `sets`, `reps`, `weight_lbs`, `notes`) VALUES
(1, 'Monday', 'Treadmill Run', 1, 1, 0, '20 minutes moderate intensity'),
(1, 'Monday', 'Dumbbell Goblet Squats', 3, 12, 25, 'Focus on form, 60s rest'),
(1, 'Monday', 'Pushups', 3, 10, 0, 'Incline if needed'),
(1, 'Wednesday', 'Stationary Cycling', 1, 1, 0, '15 minutes interval training'),
(1, 'Wednesday', 'Lat Pulldowns', 3, 12, 70, 'Keep shoulders down'),
(1, 'Wednesday', 'Dumbbell Romanian Deadlifts', 3, 10, 30, 'Squeeze glutes at the top')
ON DUPLICATE KEY UPDATE exercise_name=VALUES(exercise_name);

-- Insert Progress Tracking
INSERT INTO `progress` (`member_id`, `recorded_date`, `weight`, `height`, `body_fat_percentage`, `muscle_mass`, `chest`, `waist`, `hips`, `bmi`, `notes`) VALUES
(1, '2026-07-01', 70.5, 1.68, 28.5, 23.2, 92.0, 80.0, 98.0, 24.98, 'Initial assessment'),
(1, '2026-07-15', 69.2, 1.68, 27.2, 23.6, 91.0, 78.5, 96.5, 24.52, 'Weight loss observed, muscle mass increased')
ON DUPLICATE KEY UPDATE notes=VALUES(notes);
