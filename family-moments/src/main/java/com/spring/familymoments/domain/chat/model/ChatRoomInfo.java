package com.spring.familymoments.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spring.familymoments.domain.user.model.ChatProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "채팅방 정보")
public class ChatRoomInfo {
    @Schema(description = "가족 식별 정보", example = "1")
    @NotNull
    Long familyId;
    @Schema(description = "가족명", example = "민니네 가족")
    @NotNull
    String familyName;
    @Schema(description = "가족 프로필 사진", example = "https://profileImg.url.com")
    String familyProfile;
    @Schema(description = "채팅방의 마지막 메세지", example = "오늘 저녁 뭐야?")
    String lastMessage;
    @Schema(description = "채팅방 참여 멤버 정보")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ChatProfile> members;
    @Schema(description = "읽지 않은 메세지 수, MAX=300", example = "300")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    int unreadMessages;
}