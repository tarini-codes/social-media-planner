package com.example.social_media_planner.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String platform;
    private String caption;
    private LocalDateTime scheduledTime;
    private String status = "Pending";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users owner;

    public String getFormattedScheduledTime() {
        if (this.scheduledTime == null) {
            return "Not Scheduled";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm:ss");
        return this.scheduledTime.format(formatter);
    }
}