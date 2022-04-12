package com.watchparty.server.model;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.sql.Time;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long roomId;
    private String roomTitle;
    private String roomDescription;
    private Time creationTime;
    private String password;
    private String owner;
    private String vanity;
    private boolean isChatDisabled;
    private boolean isSubRoom;
    private Time lastUpdatedTime;
    private String mediaPath;
    private JsonAnyFormatVisitor data;
}