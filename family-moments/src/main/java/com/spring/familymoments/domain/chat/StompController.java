package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.model.MessageReq;
import com.spring.familymoments.domain.chat.model.MessageRes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

@Controller
@MessageMapping("familyId")
@RequiredArgsConstructor
public class StompController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("{familyId}.send")
    public void handleSend(@DestinationVariable("familyId") long familyId, MessageReq messageReq) {
        MessageRes messageRes = MessageRes.builder()
                .type(MessageRes.MessageType.MESSAGE)
                .sender(messageReq.getSender())
                .message(messageReq.getMessage())
                .sendedTime(LocalDateTime.now())
                .build();
        simpMessagingTemplate.convertAndSend("/sub/" + familyId, messageRes);
    }
}
