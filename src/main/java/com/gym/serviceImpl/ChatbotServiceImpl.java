package com.gym.serviceImpl;

import com.gym.dto.ChatHistoryDTO;
import com.gym.dto.ChatRequestDTO;
import com.gym.dto.ChatResponseDTO;
import com.gym.entity.*;
import com.gym.repository.*;
import com.gym.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipPlanRepository planRepository;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private GymClassRepository gymClassRepository;

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private DietPlanRepository dietPlanRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");

    @Override
    @Transactional
    public ChatResponseDTO processChatMessage(String username, ChatRequestDTO request) {
        String userMsg = request.getMessage() != null ? request.getMessage().trim() : "";
        String msgLower = userMsg.toLowerCase();
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "session-" + username;

        User user = userRepository.findByUsername(username).orElse(null);
        Member member = null;
        if (user != null) {
            member = memberRepository.findByUserId(user.getId()).orElse(null);
        }

        ChatResponseDTO response = new ChatResponseDTO();
        String intentCategory = "GENERAL";
        StringBuilder reply = new StringBuilder();
        List<String> suggestions = new ArrayList<>();
        Map<String, Object> accountSnippet = new HashMap<>();

        // 1. SAFETY FILTER: Medical conditions, severe injuries, drugs/steroids
        if (containsAny(msgLower, "sharp pain", "chest pain", "injury", "injured", "sprain", "broken bone",
                "fracture", "swollen", "dislocated", "dizziness", "fainted", "prescription", "steroid", "injection", "anabolic", "pills to take")) {
            response.setMedicalSafetyNotice(true);
            intentCategory = "SAFETY_MEDICAL";
            reply.append("⚠️ **Important Health & Medical Safety Notice:**\n\n");
            reply.append("I am an AI Gym Assistant designed for general fitness and workout guidance. **I cannot diagnose medical conditions, treat injuries, or prescribe medications.**\n\n");
            
            if (containsAny(msgLower, "chest pain", "dizziness", "fainted", "breathless")) {
                reply.append("🚨 **Immediate Action:** If you are experiencing chest pain, severe shortness of breath, or dizziness, please stop exercising immediately, sit down in a well-ventilated area, and seek emergency medical assistance or notify gym staff right away.\n\n");
            } else if (containsAny(msgLower, "pain", "injury", "injured", "sprain", "swollen")) {
                reply.append("🩺 **For Aches & Injuries:** Please discontinue any exercise that causes sharp pain. Follow the **R.I.C.E.** protocol (Rest, Ice, Compression, Elevation) as temporary relief and consult a licensed physician or physical therapist for a thorough examination.\n\n");
            } else if (containsAny(msgLower, "steroid", "injection", "anabolic", "pills")) {
                reply.append("🚫 **Safe Training Policy:** We strictly advise against non-prescribed pharmaceuticals or anabolic substances. Consistent training, natural protein-rich nutrition, and adequate sleep will yield safe and sustainable long-term gains.\n\n");
            }

            reply.append("If you need modified low-impact exercises once cleared by your doctor, feel free to ask!");
            suggestions.addAll(Arrays.asList("Low-impact cardio options", "Stretching & mobility routine", "Who is my trainer?", "Gym opening timings"));
        }
        // 2. ACCOUNT CONTEXT: Membership Status & Expiry
        else if (containsAny(msgLower, "my membership", "membership status", "expiry", "expire", "renew", "subscription", "plan expiry", "my plan")) {
            intentCategory = "ACCOUNT_MEMBERSHIP";
            if (member != null) {
                MembershipPlan plan = member.getMembershipPlan();
                LocalDate expiry = member.getMembershipExpiryDate();
                String status = member.getStatus() != null ? member.getStatus() : "ACTIVE";
                
                reply.append("📋 **Your Membership Overview:**\n\n");
                reply.append("• **Member Name:** ").append(user.getFullName()).append("\n");
                reply.append("• **Current Plan:** ").append(plan != null ? plan.getName() : "No Plan Assigned").append("\n");
                reply.append("• **Status:** `").append(status).append("`\n");
                
                if (expiry != null) {
                    long daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), expiry);
                    reply.append("• **Expiry Date:** ").append(expiry.format(DATE_FMT)).append("\n");
                    if (daysLeft > 0) {
                        reply.append("• **Time Remaining:** ").append(daysLeft).append(" days remaining.\n\n");
                        if (daysLeft <= 7) {
                            reply.append("⚠️ *Your plan is expiring soon! You can renew online under the Payments tab.*\n\n");
                        } else {
                            reply.append("✅ *Your membership is active and in good standing.*\n\n");
                        }
                    } else if (daysLeft == 0) {
                        reply.append("⚠️ *Your membership expires today!*\n\n");
                    } else {
                        reply.append("❌ *Your membership expired on ").append(expiry.format(DATE_FMT)).append(". Please renew to keep gym access active.*\n\n");
                    }
                    accountSnippet.put("expiryDate", expiry.toString());
                    accountSnippet.put("daysLeft", daysLeft);
                }
                accountSnippet.put("planName", plan != null ? plan.getName() : "N/A");
                accountSnippet.put("status", status);
            } else {
                reply.append("You are logged in as **").append(username).append("**. No active member profile was found associated with your account. Please contact the front desk or admin.");
            }
            suggestions.addAll(Arrays.asList("View all gym plans", "Check my attendance", "Who is my trainer?", "My workout plan"));
        }
        // 3. ACCOUNT CONTEXT: Attendance Information
        else if (containsAny(msgLower, "my attendance", "attendance", "how many days", "streak", "check in", "checkin", "gym visits", "attendance record")) {
            intentCategory = "ACCOUNT_ATTENDANCE";
            if (user != null) {
                LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
                LocalDate today = LocalDate.now();
                List<Attendance> monthlyAttendance = attendanceRepository.findByUserIdAndDateBetween(user.getId(), startOfMonth, today);
                Optional<Attendance> todayAtt = attendanceRepository.findByUserIdAndDate(user.getId(), today);

                reply.append("📊 **Your Attendance Record:**\n\n");
                reply.append("• **Visits This Month:** ").append(monthlyAttendance.size()).append(" days\n");
                
                if (todayAtt.isPresent()) {
                    Attendance att = todayAtt.get();
                    reply.append("• **Today's Status:** ✅ Checked In at ").append(att.getCheckInTime() != null ? att.getCheckInTime().format(TIME_FMT) : "Morning").append("\n");
                } else {
                    reply.append("• **Today's Status:** ⏳ Not checked in yet today. Ready for your workout?\n");
                }

                if (monthlyAttendance.size() >= 15) {
                    reply.append("\n🔥 **Fantastic consistency!** You are among our most dedicated members!");
                } else if (monthlyAttendance.size() >= 8) {
                    reply.append("\n💪 **Great momentum!** Keep showing up and pushing forward.");
                } else {
                    reply.append("\n🎯 *Tip: Aim for at least 3-4 gym sessions per week for steady progress!*");
                }

                accountSnippet.put("monthlyVisits", monthlyAttendance.size());
                accountSnippet.put("checkedInToday", todayAtt.isPresent());
            }
            suggestions.addAll(Arrays.asList("Show my workout plan", "Today's gym classes", "Membership status", "High protein meal ideas"));
        }
        // 4. ACCOUNT CONTEXT: Trainer Queries
        else if (containsAny(msgLower, "my trainer", "assigned trainer", "who is my trainer", "trainer info", "trainer contact", "trainer name")) {
            intentCategory = "ACCOUNT_TRAINER";
            if (member != null && member.getTrainer() != null) {
                Trainer tr = member.getTrainer();
                User trUser = tr.getUser();
                reply.append("🏋️‍♂️ **Your Assigned Personal Trainer:**\n\n");
                reply.append("• **Name:** ").append(trUser.getFullName()).append("\n");
                reply.append("• **Specialization:** ").append(tr.getSpecialization() != null ? tr.getSpecialization() : "General Fitness & Bodybuilding").append("\n");
                reply.append("• **Experience:** ").append(tr.getExperienceYears()).append(" Years\n");
                reply.append("• **Email:** ").append(trUser.getEmail()).append("\n");
                if (trUser.getPhone() != null && !trUser.getPhone().isEmpty()) {
                    reply.append("• **Phone:** ").append(trUser.getPhone()).append("\n");
                }
                reply.append("\n💡 You can request customized workout schedules and diet consultations with your trainer!");
                accountSnippet.put("trainerName", trUser.getFullName());
                accountSnippet.put("specialization", tr.getSpecialization());
            } else if (member != null) {
                reply.append("ℹ️ You currently do not have a personal trainer assigned to your account.\n\n");
                reply.append("Our certified trainers are available for specialized strength, weight loss, and athletic coaching. Would you like to see our trainer list?");
            } else {
                reply.append("Trainer information is available for registered members. Please contact gym administration.");
            }
            suggestions.addAll(Arrays.asList("List all gym trainers", "Show my workout plan", "Group classes schedule", "Membership status"));
        }
        // 5. GYM TRAINERS LIST
        else if (containsAny(msgLower, "all trainers", "list trainers", "gym trainers", "available trainers", "our trainers")) {
            intentCategory = "GYM_TRAINERS";
            List<Trainer> trainers = trainerRepository.findAll();
            if (!trainers.isEmpty()) {
                reply.append("🏅 **Our Certified Gym Trainers:**\n\n");
                for (Trainer t : trainers) {
                    if (t.getUser() != null) {
                        reply.append("• **").append(t.getUser().getFullName()).append("** — ")
                             .append(t.getSpecialization() != null ? t.getSpecialization() : "Fitness Specialist")
                             .append(" (").append(t.getExperienceYears()).append(" yrs exp)\n");
                    }
                }
                reply.append("\nFeel free to speak to any trainer on the gym floor for guidance!");
            } else {
                reply.append("No trainer profiles registered currently.");
            }
            suggestions.addAll(Arrays.asList("Who is my trainer?", "Group classes schedule", "Workout plan", "Gym timings"));
        }
        // 6. ACCOUNT CONTEXT: Workout Plan
        else if (containsAny(msgLower, "my workout", "my exercise", "my workout plan", "what is my workout", "workout today", "todays workout")) {
            intentCategory = "ACCOUNT_WORKOUT";
            if (member != null) {
                Optional<WorkoutPlan> planOpt = workoutPlanRepository.findFirstByMemberIdOrderByCreatedAtDesc(member.getId());
                if (planOpt.isPresent()) {
                    WorkoutPlan wp = planOpt.get();
                    reply.append("💪 **Your Current Workout Plan: ").append(wp.getPlanName()).append("**\n\n");
                    if (wp.getDescription() != null && !wp.getDescription().isEmpty()) {
                        reply.append("📝 *").append(wp.getDescription()).append("*\n\n");
                    }
                    if (wp.getExercises() != null && !wp.getExercises().isEmpty()) {
                        reply.append("**Assigned Exercises:**\n");
                        Map<String, List<WorkoutExercise>> grouped = wp.getExercises().stream()
                                .collect(Collectors.groupingBy(WorkoutExercise::getDayOfWeek));
                        for (Map.Entry<String, List<WorkoutExercise>> entry : grouped.entrySet()) {
                            reply.append("\n📅 **").append(entry.getKey()).append(":**\n");
                            for (WorkoutExercise ex : entry.getValue()) {
                                reply.append("  • ").append(ex.getExerciseName())
                                     .append(" (").append(ex.getSets()).append(" sets × ").append(ex.getReps()).append(" reps");
                                if (ex.getWeightLbs() != null && ex.getWeightLbs() > 0) {
                                    reply.append(" @ ").append(ex.getWeightLbs()).append(" lbs");
                                }
                                reply.append(")\n");
                            }
                        }
                    } else {
                        reply.append("No individual exercises listed yet. Your trainer will configure them shortly.");
                    }
                } else {
                    reply.append("You don't have a customized workout plan assigned yet.\n\n");
                    reply.append("Would you like a beginner full-body routine or push-pull-legs suggestion right now?");
                }
            } else {
                reply.append("Workout plans are linked to active member accounts. You can also explore our beginner workout guides in the sidebar!");
            }
            suggestions.addAll(Arrays.asList("Suggest 3-day workout split", "Push-Pull-Legs routine", "How to do bench press?", "Post-workout meal ideas"));
        }
        // 7. GYM CLASSES: Schedules & Group sessions
        else if (containsAny(msgLower, "class", "classes", "zumba", "yoga", "crossfit", "spinning", "aerobics", "group class", "schedule")) {
            intentCategory = "GYM_CLASSES";
            List<GymClass> upcomingClasses = gymClassRepository.findByScheduleTimeAfterOrderByScheduleTimeAsc(LocalDateTime.now());
            if (!upcomingClasses.isEmpty()) {
                reply.append("📅 **Upcoming Gym Group Classes:**\n\n");
                int count = 0;
                for (GymClass gc : upcomingClasses) {
                    if (count++ >= 5) break;
                    String trainerName = gc.getTrainer() != null && gc.getTrainer().getUser() != null ? gc.getTrainer().getUser().getFullName() : "Gym Staff";
                    reply.append("• **").append(gc.getClassName()).append("**\n")
                         .append("  ⏰ ").append(gc.getScheduleTime().format(DATE_FMT)).append(" at ").append(gc.getScheduleTime().format(TIME_FMT))
                         .append(" | 📍 Room: ").append(gc.getRoom() != null ? gc.getRoom() : "Studio A")
                         .append(" | 👤 Trainer: ").append(trainerName).append("\n\n");
                }
                reply.append("You can book slots directly in the **Group Classes** page!");
            } else {
                List<GymClass> allClasses = gymClassRepository.findAll();
                if (!allClasses.isEmpty()) {
                    reply.append("📅 **Our Gym Class Schedule:**\n\n");
                    for (GymClass gc : allClasses) {
                        reply.append("• **").append(gc.getClassName()).append("** — ")
                             .append(gc.getDescription() != null ? gc.getDescription() : "High energy workout")
                             .append(" (").append(gc.getRoom() != null ? gc.getRoom() : "Studio A").append(")\n");
                    }
                } else {
                    reply.append("🧘 **Popular Gym Classes Offered:**\n\n");
                    reply.append("• **Power Yoga:** Mon & Wed 7:00 AM - 8:00 AM\n");
                    reply.append("• **High-Intensity Zumba:** Tue & Thu 6:30 PM - 7:30 PM\n");
                    reply.append("• **CrossFit & Strength:** Fri & Sat 6:00 PM - 7:00 PM\n");
                    reply.append("• **Spinning & Cardio:** Mon, Wed, Fri 5:30 PM - 6:30 PM\n");
                }
            }
            suggestions.addAll(Arrays.asList("Book a class", "Who is my trainer?", "Gym opening timings", "Check my attendance"));
        }
        // 8. MEMBERSHIP PLANS & PRICING
        else if (containsAny(msgLower, "membership plan", "membership plans", "gym plan", "gym plans", "plan price", "pricing", "membership cost", "fee", "rates", "membership options", "how much is membership", "join cost")) {
            intentCategory = "GYM_PLANS";
            List<MembershipPlan> plans = planRepository.findByStatus("ACTIVE");
            reply.append("💳 **Available Membership Plans:**\n\n");
            if (!plans.isEmpty()) {
                for (MembershipPlan p : plans) {
                    reply.append("⭐ **").append(p.getName()).append("** — $").append(p.getPrice()).append(" / ").append(p.getDurationMonths()).append(" month(s)\n");
                    if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                        reply.append("   *").append(p.getDescription()).append("*\n");
                    }
                    reply.append("\n");
                }
            } else {
                reply.append("• **Monthly Basic:** $49.99/mo (Cardio & Weights access)\n");
                reply.append("• **Quarterly Standard:** $129.99/3-mo (Includes 2 group classes)\n");
                reply.append("• **Annual Premium:** $399.99/year (All-access pass + Personal Trainer consult)\n\n");
            }
            reply.append("Visit the **Payments** section to renew or upgrade your plan anytime!");
            suggestions.addAll(Arrays.asList("My membership status", "Gym opening timings", "What classes are available?", "Who is my trainer?"));
        }
        // 9. GYM TIMINGS, RULES & LOCATION
        else if (containsAny(msgLower, "timing", "timings", "hours", "open", "close", "time", "location", "address", "rules", "locker", "shower", "facilities")) {
            intentCategory = "GYM_FACILITIES";
            reply.append("⏰ **Gym Operating Hours & Facilities:**\n\n");
            reply.append("• **Monday – Friday:** 6:00 AM – 10:00 PM\n");
            reply.append("• **Saturday – Sunday:** 7:00 AM – 8:00 PM\n");
            reply.append("• **Public Holidays:** 8:00 AM – 4:00 PM\n\n");
            reply.append("🏢 **Amenities Included:**\n");
            reply.append("• Heavy weight training & cardio sections\n");
            reply.append("• Clean locker rooms & shower facilities\n");
            reply.append("• Free high-speed Wi-Fi & filtered hydration station\n");
            reply.append("• Dedicated stretching & mobility zone\n\n");
            reply.append("📌 **Etiquette Tip:** Please wipe down machines after use and re-rack your weights for fellow members!");
            suggestions.addAll(Arrays.asList("View membership plans", "Group classes schedule", "Who is my trainer?", "Suggest workout split"));
        }
        // 10. EXERCISE INSTRUCTIONS & FORM
        else if (containsAny(msgLower, "squat", "bench press", "deadlift", "pull up", "pullup", "push up", "pushup", "bicep curl", "plank", "overhead press", "shoulder press", "lunges", "lat pulldown", "barbell row")) {
            intentCategory = "EXERCISE_GUIDE";
            if (msgLower.contains("squat")) {
                reply.append("🏋️ **Barbell / Goblet Squats Technique:**\n\n");
                reply.append("1. **Setup:** Feet shoulder-width apart, toes pointed slightly outwards (~15-30°).\n");
                reply.append("2. **Descent:** Inhale, brace your core, send hips back and knees tracking in line with toes until thighs are at or below parallel.\n");
                reply.append("3. **Ascent:** Drive through mid-foot and heel, keeping chest upright. Exhale at the top.\n");
                reply.append("⚠️ **Common Mistake:** Letting knees cave inward or heels lifting off the floor.");
            } else if (msgLower.contains("bench press")) {
                reply.append("🏋️ **Barbell Bench Press Technique:**\n\n");
                reply.append("1. **Setup:** Lie flat on bench with eyes under barbell, plant feet firmly on the floor, retract shoulder blades together.\n");
                reply.append("2. **Descent:** Unrack bar, lower it steadily to mid-chest while keeping elbows at ~45-75° angle (avoid flaring).\n");
                reply.append("3. **Drive:** Press bar upward in a slight arc back over shoulders, locking out with control.\n");
                reply.append("⚠️ **Common Mistake:** Bouncing the bar off your sternum.");
            } else if (msgLower.contains("deadlift")) {
                reply.append("🏋️ **Conventional Deadlift Technique:**\n\n");
                reply.append("1. **Setup:** Stand with bar over mid-foot, feet hip-width. Grip bar outside shins.\n");
                reply.append("2. **Engagement:** Pull chest up, flatten your back, engage lats, take the slack out of the bar.\n");
                reply.append("3. **Lift:** Push the floor away with legs until bar passes knees, then thrust hips forward to lock out.\n");
                reply.append("⚠️ **Common Mistake:** Rounding lower back under heavy loads.");
            } else if (msgLower.contains("plank")) {
                reply.append("🧘 **Core Plank Technique:**\n\n");
                reply.append("1. Rest on forearms and toes, elbows under shoulders.\n");
                reply.append("2. Squeeze glutes, tighten abs, and maintain a straight line from head to heels.\n");
                reply.append("3. Hold for 30–60 seconds while maintaining steady rhythmic breathing.\n");
                reply.append("⚠️ **Common Mistake:** Sagging hips or arching your lower back.");
            } else {
                reply.append("💪 **General Exercise Execution Tips:**\n\n");
                reply.append("• **Warm Up First:** Perform 5-10 mins dynamic stretching before heavy sets.\n");
                reply.append("• **Mind-Muscle Connection:** Focus on squeezing the target muscle through full Range of Motion (ROM).\n");
                reply.append("• **Tempo Control:** 2 seconds lowering (eccentric), 1 second pause, 1 second explosive concentric.\n");
                reply.append("• **Progressive Overload:** Increase weight or reps gradually week-over-week.");
            }
            suggestions.addAll(Arrays.asList("Suggest 3-day workout split", "Push-Pull-Legs routine", "Pre-workout meal ideas", "Show my workout plan"));
        }
        // 11. WORKOUT SPLIT SUGGESTIONS (PPL, Full Body, Fat Loss, Muscle Building)
        else if (containsAny(msgLower, "split", "routine", "beginner workout", "fat loss routine", "muscle building", "hypertrophy", "push pull legs", "ppl", "full body", "workout suggestion", "workout program")) {
            intentCategory = "WORKOUT_SUGGESTION";
            if (containsAny(msgLower, "ppl", "push pull legs", "push-pull-legs")) {
                reply.append("🔥 **Push-Pull-Legs (PPL) 3 to 6-Day Split:**\n\n");
                reply.append("• **Day 1: PUSH (Chest, Shoulders, Triceps)**\n");
                reply.append("  - Incline Dumbbell Bench: 3 sets × 8-10 reps\n");
                reply.append("  - Overhead Dumbbell Press: 3 sets × 10-12 reps\n");
                reply.append("  - Cable Chest Flyes: 3 sets × 12-15 reps\n");
                reply.append("  - Triceps Rope Pushdown: 3 sets × 12 reps\n\n");
                reply.append("• **Day 2: PULL (Back, Biceps, Rear Delts)**\n");
                reply.append("  - Lat Pulldowns: 3 sets × 10-12 reps\n");
                reply.append("  - Seated Cable Row: 3 sets × 10-12 reps\n");
                reply.append("  - Dumbbell Hammer Curls: 3 sets × 12 reps\n");
                reply.append("  - Facepulls: 3 sets × 15 reps\n\n");
                reply.append("• **Day 3: LEGS & CORE**\n");
                reply.append("  - Goblet or Barbell Squats: 3 sets × 8-10 reps\n");
                reply.append("  - Romanian Deadlifts: 3 sets × 10 reps\n");
                reply.append("  - Leg Press / Walking Lunges: 3 sets × 12 reps\n");
                reply.append("  - Hanging Knee Raises / Planks: 3 sets to fatigue");
            } else if (containsAny(msgLower, "fat loss", "lose weight", "weight loss", "cutting", "cardio")) {
                reply.append("🏃 **Effective Fat Loss & Toning Routine:**\n\n");
                reply.append("• **Strength Component (3-4x/week):** Compound lifting preserves lean muscle mass and elevates resting metabolic rate.\n");
                reply.append("• **Cardio Component:**\n");
                reply.append("  - 20-30 mins Zone 2 moderate cardio (treadmill 12-3-30 or incline walking).\n");
                reply.append("  - 1-2 HIIT sessions weekly (sprints, rowing intervals).\n");
                reply.append("• **Caloric Deficit:** Consume 300-500 kcal below maintenance with 1.6-2.0g protein per kg of bodyweight.");
            } else {
                reply.append("🌟 **Recommended 3-Day Beginner Full-Body Routine:**\n\n");
                reply.append("• **Monday (Full Body A):**\n");
                reply.append("  - Goblet Squat: 3 × 10\n");
                reply.append("  - Push-ups / Bench Press: 3 × 10\n");
                reply.append("  - Lat Pulldown: 3 × 12\n");
                reply.append("  - Plank: 3 × 45s\n\n");
                reply.append("• **Wednesday (Full Body B):**\n");
                reply.append("  - Dumbbell Deadlift: 3 × 10\n");
                reply.append("  - Dumbbell Shoulder Press: 3 × 10\n");
                reply.append("  - Seated Cable Rows: 3 × 12\n");
                reply.append("  - Bodyweight Lunges: 3 × 12/leg\n\n");
                reply.append("• **Friday (Full Body C):**\n");
                reply.append("  - Leg Press: 3 × 12\n");
                reply.append("  - Incline Dumbbell Press: 3 × 10\n");
                reply.append("  - Dumbbell Bicep Curl & Triceps Extension Superset: 3 × 12\n");
                reply.append("  - 15 mins treadmill cool-down");
            }
            suggestions.addAll(Arrays.asList("High protein meal ideas", "How to calculate calories?", "Check my workout plan", "Who is my trainer?"));
        }
        // 12. NUTRITION, DIET & MEAL ADVICE
        else if (containsAny(msgLower, "diet", "diet plan", "diet plans", "date plan", "date plans", "meal plan", "meal plans", "nutrition", "protein", "calorie", "calories", "food", "meal", "macro", "macros", "creatine", "supplement", "weight loss diet", "vegetarian protein", "pre workout", "post workout")) {
            intentCategory = "NUTRITION_DIET";
            
            // Check if member has an assigned diet plan in DB
            Optional<DietPlan> dbDietPlan = member != null ? dietPlanRepository.findFirstByMemberIdOrderByCreatedAtDesc(member.getId()) : Optional.empty();

            if (dbDietPlan.isPresent()) {
                DietPlan dp = dbDietPlan.get();
                reply.append("🥗 **Your Personalized Diet Plan: ").append(dp.getPlanName()).append("**\n\n");
                reply.append("• **Target Calories:** ").append(dp.getCaloriesPerDay() != null ? dp.getCaloriesPerDay() : 2000).append(" kcal/day\n");
                reply.append("• **Target Protein:** ").append(dp.getProteinGrams() != null ? dp.getProteinGrams() : 120).append(" g\n");
                reply.append("• **Target Carbs:** ").append(dp.getCarbsGrams() != null ? dp.getCarbsGrams() : 220).append(" g | **Fats:** ").append(dp.getFatsGrams() != null ? dp.getFatsGrams() : 60).append(" g\n\n");
                if (dp.getNotes() != null && !dp.getNotes().isEmpty()) {
                    reply.append("📝 *Notes from Trainer:* ").append(dp.getNotes()).append("\n\n");
                }
                if (dp.getMeals() != null && !dp.getMeals().isEmpty()) {
                    reply.append("**Assigned Meals:**\n");
                    for (DietMeal meal : dp.getMeals()) {
                        reply.append("• **").append(meal.getMealType()).append(":** ")
                             .append(meal.getFoodItems());
                        if (meal.getCalories() != null && meal.getCalories() > 0) {
                            reply.append(" (~").append(meal.getCalories()).append(" kcal)");
                        }
                        reply.append("\n");
                    }
                }
            } else if (containsAny(msgLower, "vegetarian", "vegan", "veg protein", "plant protein")) {
                reply.append("🌱 **Top High-Protein Vegetarian / Vegan Sources:**\n\n");
                reply.append("• **Paneer / Cottage Cheese:** ~18g protein per 100g\n");
                reply.append("• **Tofu & Tempeh:** ~15-20g protein per 100g (complete amino acid profile)\n");
                reply.append("• **Greek Yogurt:** ~10-15g protein per 100g\n");
                reply.append("• **Lentils, Chickpeas & Kidney Beans:** ~15-18g protein per cooked cup\n");
                reply.append("• **Soya Chunks / Edamame:** ~50g protein per 100g dry soya\n");
                reply.append("• **Whey / Plant Protein Powder:** 24-27g protein per scoop");
            } else if (containsAny(msgLower, "pre workout", "pre-workout", "post workout", "post-workout")) {
                reply.append("🥗 **Optimal Workout Timing Fuel:**\n\n");
                reply.append("• **Pre-Workout (1-2 hrs before):**\n");
                reply.append("  - Complex carbs + moderate protein (e.g. Oatmeal with banana & peanut butter, or whole wheat toast with boiled eggs).\n");
                reply.append("  - Drink 300-500ml water for optimal cellular hydration.\n\n");
                reply.append("• **Post-Workout (Within 1 hr):**\n");
                reply.append("  - Fast-absorbing protein + carbs for muscle protein synthesis and glycogen replenishment (e.g. Whey protein shake with banana, or grilled chicken/paneer with rice).");
            } else {
                reply.append("🥗 **Healthy Diet & Meal Plan Guidelines:**\n\n");
                reply.append("• **Sample High-Protein Daily Meal Plan:**\n");
                reply.append("  - **Breakfast (8:00 AM):** 3-4 Eggs / Scrambled Tofu + Oats with berries & almonds (~30g protein)\n");
                reply.append("  - **Lunch (1:00 PM):** Grilled chicken breast or Paneer (150g) + Brown rice / Quinoa + Steamed broccoli (~35g protein)\n");
                reply.append("  - **Pre-Workout Snack (4:30 PM):** Banana with peanut butter on whole-wheat toast\n");
                reply.append("  - **Post-Workout Fuel (6:30 PM):** 1 scoop Whey / Plant protein shake (~25g protein)\n");
                reply.append("  - **Dinner (8:30 PM):** Fish or Lentils/Dal + Large green salad + 2 Rotis (~30g protein)\n\n");
                reply.append("• **Daily Protein Goal:** 1.6g – 2.0g per kg of bodyweight.\n");
                reply.append("• **Hydration:** Aim for 3 to 4 Liters of water daily.");
            }
            suggestions.addAll(Arrays.asList("High protein vegetarian meals", "Calculate my BMI", "Suggest 3-day workout split", "My membership status"));
        }
        // 13. GREETINGS & CASUAL TALK
        else if (containsAny(msgLower, "hello", "hi", "hey", "good morning", "good evening", "namaste", "vanakkam", "help", "who are you", "what can you do")) {
            intentCategory = "GREETING";
            String displayName = user != null ? user.getFullName() : "Athlete";
            reply.append("👋 **Hello ").append(displayName).append("! Welcome to your AI Gym Assistant.**\n\n");
            reply.append("I can help you with:\n");
            reply.append("• 📋 **Account Details:** Check your membership status, expiry date & payments\n");
            reply.append("• 📊 **Attendance:** View your gym visits and monthly streak\n");
            reply.append("• 🏋️ **Workouts & Exercises:** Form guides, custom splits (PPL, Full-body)\n");
            reply.append("• 🥗 **Diet & Nutrition:** Meal suggestions, protein targets & calorie guidance\n");
            reply.append("• 📅 **Gym Info:** Group class schedules, opening timings & trainer details\n\n");
            reply.append("What would you like to explore today?");
            suggestions.addAll(Arrays.asList("Check my membership status", "Show my attendance", "Who is my trainer?", "Suggest 3-day workout split", "High protein meal ideas", "Gym opening timings"));
        }
        // 14. MOTIVATIONAL / GENERAL FALLBACK
        else {
            intentCategory = "GENERAL_FALLBACK";
            reply.append("🤖 **AI Gym Assistant Response:**\n\n");
            reply.append("I understand you're asking about: *\"").append(userMsg).append("\"*.\n\n");
            reply.append("Here are some helpful things I can assist you with right away:\n");
            reply.append("1. **Your Account:** Check membership expiry, attendance, and assigned trainer.\n");
            reply.append("2. **Fitness Routine:** Ask for workout splits (Push-Pull-Legs, Fat Loss, Muscle Gain).\n");
            reply.append("3. **Nutrition:** Ask about daily protein needs, pre/post workout meals, and healthy recipes.\n");
            reply.append("4. **Gym Facilities:** Class schedules, gym rules, and opening timings.\n\n");
            reply.append("💡 *Try asking one of the quick suggestions below or rephrase your question!*");
            suggestions.addAll(Arrays.asList("What is my membership status?", "Show my workout plan", "How many days have I attended?", "High protein meal ideas", "Upcoming gym classes"));
        }

        response.setReply(reply.toString());
        response.setIntentCategory(intentCategory);
        response.setSuggestions(suggestions);
        response.setAccountSnippet(accountSnippet);

        // Save conversation history to Database
        if (user != null) {
            ChatHistory history = new ChatHistory(user, userMsg, reply.toString(), intentCategory, sessionId);
            chatHistoryRepository.save(history);
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatHistoryDTO> getUserChatHistory(String username) {
        return chatHistoryRepository.findByUserUsernameOrderByTimestampAsc(username).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearUserChatHistory(String username) {
        chatHistoryRepository.deleteByUserUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatHistoryDTO> getAllChatLogs() {
        return chatHistoryRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getChatbotStatistics() {
        Map<String, Object> stats = new HashMap<>();
        long totalMessages = chatHistoryRepository.count();
        List<String> distinctUsers = chatHistoryRepository.findDistinctUsernames();
        stats.put("totalMessages", totalMessages);
        stats.put("activeChatUsers", distinctUsers.size());
        stats.put("distinctUsernames", distinctUsers);
        return stats;
    }

    private ChatHistoryDTO mapToDTO(ChatHistory entity) {
        ChatHistoryDTO dto = new ChatHistoryDTO();
        dto.setId(entity.getId());
        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUsername(entity.getUser().getUsername());
            dto.setUserFullName(entity.getUser().getFullName());
        }
        dto.setUserMessage(entity.getUserMessage());
        dto.setBotResponse(entity.getBotResponse());
        dto.setIntentCategory(entity.getIntentCategory());
        dto.setSessionId(entity.getSessionId());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null) return false;
        for (String kw : keywords) {
            if (text.contains(kw.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
