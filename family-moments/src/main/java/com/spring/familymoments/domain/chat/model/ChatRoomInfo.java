package com.spring.familymoments.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spring.familymoments.domain.user.model.ChatProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ChatRoomInfo {
    @NotNull
    Long familyId;
    @NotNull
    String familyName;
    String familyProfile;
    List<ChatProfile> members;
    @JsonIgnore
    Integer unreadMessages;
}