package com.spring.familymoments.domain.chat.model;

import com.spring.familymoments.domain.user.model.ChatProfile;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ChatRoomInfo {
    @NotNull
    Long familyId;
    @NotNull
    String familyName;
    String familyProfile;
    List<ChatProfile> members;
    Integer unreadMessages;
}