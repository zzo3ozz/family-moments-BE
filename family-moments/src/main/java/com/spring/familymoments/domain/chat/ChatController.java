package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseResponse;
import com.spring.familymoments.domain.chat.model.ChatRoomInfo;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *
 * 챗 관련 http api controller
 *
 * */
@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * 메세지 내역 조회 API
     * [GET] /chats/{familyId}?
     * @return BaseResponse<List<MessageRes>>
     */
    @GetMapping("/{familyId}")
    @Operation(summary = "메세지 목록 조회", description = "마지막 접속 기록으로부터의 메세지 목록 조회 / messageId 이전 메세지 목록 조회 ")
    public BaseResponse<List<MessageRes>> getMessageList(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                         @PathVariable Long familyId,
                                                         @RequestParam(name = "messageId", required = false)String messageId) {
        if(messageId == null) {
            List<MessageRes> messages = chatService.getUnreadMessages(user, familyId);
            return new BaseResponse<>(messages);
        }

        return null;
    }

    /**
     * 현재 채팅방의 멤버 조회 API
     * [GET] /chats/{familyId}/info
     * @return BaseResponse<List<ChatProfile>>
     */
    @GetMapping("/{familyId}/info")
    @Operation(summary = "채팅방 정보 조회", description = "접속한 채팅방의 멤버 정보 조회")
    public BaseResponse<ChatRoomInfo> getChatProfile(@AuthenticationPrincipal @Parameter(hidden = true) User user,
                                                     @PathVariable Long familyId) {
        return null;
    }

    /**
     * 나의 채팅방 조회 API
     * [GET] /chats/chatRooms
     * @return BaseResponse<List<ChatRoomInfo>>
     */
    @GetMapping("/chatRooms")
    @Operation(summary = "전체 채팅 목록 조회", description = "나의 전체 채팅방 목록 조회")
    public BaseResponse<List<ChatRoomInfo>> getChatRooms(@AuthenticationPrincipal @Parameter(hidden = true) User user) {
        return null;
    }
}
