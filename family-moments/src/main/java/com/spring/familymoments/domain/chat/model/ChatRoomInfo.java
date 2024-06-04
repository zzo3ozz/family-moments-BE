package com.spring.familymoments.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.familymoments.domain.user.model.ChatProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
    String lastMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ChatProfile> members;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    int unreadMessages;
}