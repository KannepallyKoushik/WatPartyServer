package com.watchparty.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PublishObject {
    private String roomTopicId;
    private String actionEvent;
    private String seekTo;
}
