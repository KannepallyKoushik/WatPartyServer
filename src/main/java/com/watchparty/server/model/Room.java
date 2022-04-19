package com.watchparty.server.model;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long roomId;
    private String roomTitle;
    private LocalDateTime creationTime;
    private boolean isChatDisabled;
    private LocalDateTime lastUpdatedTime;
    private String mediaPath;
    private String roomTopicId;

    public Room(String roomTitle, String mediaPath) {
        this.setRoomTitle(roomTitle);
        this.setMediaPath(mediaPath);
        this.setCreationTime(LocalDateTime.now());
        this.setLastUpdatedTime(LocalDateTime.now());
        this.setMediaPath(mediaPath);
        this.setRoomTopicId("roomtopic"+UUID.randomUUID().toString());
    }
}