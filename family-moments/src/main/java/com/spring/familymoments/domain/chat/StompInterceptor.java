package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.user.UserService;
import com.spring.familymoments.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {
    private static final String NOTIFICATION = "notification";

    private final SessionService sessionService;
    private final UserService userService; // TODO: 임시, 차후 삭제

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        StompCommand command = headerAccessor.getCommand();
        String destination = headerAccessor.getDestination();

        if(command.equals(StompCommand.SUBSCRIBE) && !destination.contains(NOTIFICATION)) {
            // 가족 채팅방 구독 시
            // TODO: Authentication

            // TODO: unsub -> sub으로 변경
        } else if (command.equals(StompCommand.UNSUBSCRIBE) && !destination.contains(NOTIFICATION)) {
            //가족 채팅방 구독 해제 시
            // TODO: Authentication

            // TODO : sub -> unsub으로 변경
        }

        return message;
    }
}
