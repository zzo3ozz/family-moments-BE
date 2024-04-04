package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.document.ChatDocument;
import com.spring.familymoments.domain.chat.model.ChatRoomInfo;
import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.chat.model.MessageTemplate;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatDocumentRepository chatDocumentRepository;
    private final RedisService redisService;
    private final UserFamilyRepository userFamilyRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // chat Document에 저장
    public MessageRes createChat(Long familyId, MessageReq messageReq) {
        ChatDocument chatDocument = ChatDocument.builder()
                .familyId(familyId)
                .sender(messageReq.getSender())
                .message(messageReq.getMessage())
                .sendedTime(LocalDateTime.now())
                .build();

        chatDocument = chatDocumentRepository.save(chatDocument);

        MessageRes messageRes = MessageRes.builder()
                .messageId(chatDocument.getId().toString())
                .familyId(chatDocument.getFamilyId())
                .sender(chatDocument.getSender())
                .message(chatDocument.getMessage())
                .sendedTime(chatDocument.getSendedTime())
                .build();

        return messageRes;
    }

    @Transactional(readOnly = true)
    public void sendAlarm(long familyId, MessageRes messageRes) {
        Set<String> unsubMembers = redisService.getMembers(ChatRedisPrefix.FAMILY_UNSUB.value + familyId);
        Set<String> offlineMembers = redisService.getMembers(ChatRedisPrefix.FAMILY_OFF.value + familyId);

        // Online-unsub 유저에게 알림 발송
        for(String uuid : unsubMembers) {
            User user = userFamilyRepository.findActiveUserByFamilyIdAndUuid(familyId, uuid).orElseThrow();
            MessageTemplate messageTemplate = new MessageTemplate(MessageTemplate.MessageType.NOTIFICATION, messageRes);

            simpMessagingTemplate.convertAndSend("/sub/notification." + user.getId(), messageTemplate);
        }
        // TODO: offline 유저에게 알림 발송
    }

    // 메세지 목록 조회 - 읽지 않은 메세지(마지막 접속 기록 기준)
    public List<MessageRes> getUnreadMessages(User user, Long familyId) {
        return null;
    }

    // 메세지 목록 조회 - messageId 이전 메세지
    public List<MessageRes> getPreviousMessages(User user, Long familyId, String messageId) {
        return null;
    }

    // 채팅방 목록 조회
    public List<ChatRoomInfo> getMyChatRooms(User user) {
        return null;
    }

    // 현재 채팅방 정보 조회 - 유저 정보, 채팅방 정보
    public ChatRoomInfo getChatRoomInfo(User user, Long familyId) {
        return null;
    }
}
