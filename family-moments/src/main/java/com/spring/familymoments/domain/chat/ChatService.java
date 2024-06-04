package com.spring.familymoments.domain.chat;

import com.spring.familymoments.config.BaseException;
import com.spring.familymoments.domain.chat.document.ChatDocument;
import com.spring.familymoments.domain.chat.model.ChatRoomInfo;
import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import com.spring.familymoments.domain.chat.model.MessageTemplate;
import com.spring.familymoments.domain.common.UserFamilyRepository;
import com.spring.familymoments.domain.common.entity.UserFamily;
import com.spring.familymoments.domain.family.entity.Family;
import com.spring.familymoments.domain.redis.RedisService;
import com.spring.familymoments.domain.user.entity.User;
import com.spring.familymoments.domain.user.model.ChatProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.spring.familymoments.config.BaseResponseStatus.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatDocumentRepository chatDocumentRepository;
    private final RedisService redisService;
    private final UserFamilyRepository userFamilyRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;

    private static final int MAXIMUM_MESSAGE = 300;
    private static final int MESSAGE_PAGE = 30;

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
    @Transactional(readOnly = true)
    public List<MessageRes> getUnreadMessages(User user, Long familyId) {
        UserFamily userFamily = userFamilyRepository.findActiveUserFamilyByFamilyIdAndUser(familyId, user)
                .orElseThrow(() -> new BaseException(minnie_FAMILY_INVALID_USER));

        LocalDateTime lastAccessedTime = userFamily.getLastAccessedTime();

        List<ChatDocument> chatDocuments = chatDocumentRepository
                .findByFamilyIdAndSendedTimeAfterOrderBySendedTimeDesc(familyId, lastAccessedTime, PageRequest.of(0, MAXIMUM_MESSAGE));

        List<MessageRes> messages = chatDocuments.stream()
                .map(message -> MessageRes.builder()
                        .messageId(message.getId().toString())
                        .sender(message.getSender())
                        .message(message.getMessage())
                        .sendedTime(message.getSendedTime())
                        .build())
                .collect(Collectors.toList());

        return messages;
    }

    // 메세지 목록 조회 - messageId 이전 메세지
    @Transactional(readOnly = true)
    public List<MessageRes> getPreviousMessages(User user, Long familyId, String messageId) {
        UserFamily userFamily = userFamilyRepository.findActiveUserFamilyByFamilyIdAndUser(familyId, user)
                .orElseThrow(() -> new BaseException(minnie_FAMILY_INVALID_USER));

        List<ChatDocument> chatDocuments = chatDocumentRepository.findByFamilyIdAndIdBeforeOrderByIdDesc(
                familyId, new ObjectId(messageId), PageRequest.of(0, MESSAGE_PAGE)
        );

        List<MessageRes> messages = chatDocuments.stream()
                .map(message -> MessageRes.builder()
                        .messageId(message.getId().toString())
                        .sender(message.getSender())
                        .message(message.getMessage())
                        .sendedTime(message.getSendedTime())
                        .build())
                .collect(Collectors.toList());

        return messages;
    }

    // 채팅방 목록 조회
    public List<ChatRoomInfo> getMyChatRooms(User user) {
        return null;
    }

    // 현재 채팅방 정보 조회 - 유저 정보, 채팅방 정보
    @Transactional(readOnly = true)
    public ChatRoomInfo getChatRoomInfo(User user, Long familyId) {
        UserFamily userFamily = userFamilyRepository.findActiveUserFamilyByFamilyIdAndUser(familyId, user)
                .orElseThrow(() -> new BaseException(minnie_FAMILY_INVALID_USER));

        List<User> members = userFamilyRepository.findActiveAllUserByFamilyId(familyId);

        List<ChatProfile> chatProfiles = members.stream()
                .map(member -> ChatProfile.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .profileImg(member.getProfileImg())
                        .build())
                .collect(Collectors.toList());

        return ChatRoomInfo.builder()
                .familyId(familyId)
                .familyName(userFamily.getFamilyId().getFamilyName())
                .members(chatProfiles)
                .build();
    }
}
