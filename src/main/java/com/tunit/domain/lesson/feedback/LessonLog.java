package com.tunit.domain.lesson.feedback;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_log")
@Getter
@NoArgsConstructor
public class LessonLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_no")
    private Long logNo;

    @Column(name = "lesson_reservation_no", nullable = false, unique = true)
    private Long lessonReservationNo;

    @Column(name = "tutor_profile_no", nullable = false)
    private Long tutorProfileNo;

    @Column(name = "student_no", nullable = false)
    private Long studentNo;

    @Column(name = "progress_content", length = 2000, nullable = false)
    private String progressContent;

    @Column(name = "feedback", length = 2000)
    private String feedback;

    @Column(name = "student_question", length = 1000)
    private String studentQuestion;

    @Column(name = "questioned_at")
    private LocalDateTime questionedAt;

    @Column(name = "tutor_reply", length = 2000)
    private String tutorReply;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public LessonLog(Long logNo, Long lessonReservationNo, Long tutorProfileNo, Long studentNo,
                     String progressContent, String feedback,
                     String studentQuestion, LocalDateTime questionedAt,
                     String tutorReply, LocalDateTime repliedAt,
                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.logNo = logNo;
        this.lessonReservationNo = lessonReservationNo;
        this.tutorProfileNo = tutorProfileNo;
        this.studentNo = studentNo;
        this.progressContent = progressContent;
        this.feedback = feedback;
        this.studentQuestion = studentQuestion;
        this.questionedAt = questionedAt;
        this.tutorReply = tutorReply;
        this.repliedAt = repliedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(String progressContent, String feedback) {
        this.progressContent = progressContent;
        this.feedback = feedback;
    }

    public void registerQuestion(String question) {
        this.studentQuestion = question;
        this.questionedAt = LocalDateTime.now();
        this.tutorReply = null;
        this.repliedAt = null;
    }

    public void registerReply(String reply) {
        this.tutorReply = reply;
        this.repliedAt = LocalDateTime.now();
    }
}
