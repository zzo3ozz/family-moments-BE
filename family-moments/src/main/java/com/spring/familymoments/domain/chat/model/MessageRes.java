package com.spring.familymoments.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Schema(description = "메세지 수신 결과")
public class MessageRes {
    @Schema(description = "메세지 식별 번호", example = "660eae160f07a759edbe46a7")
    @NotNull
    private String messageId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long familyId;
    @Schema(description = "발신자 id", example = "temp123456")
    @NotNull
    private String sender;
    @Schema(description = "메세지", example = "오늘 저녁 뭐임?")
    @NotNull
    private String message;
    @Schema(description = "발신 시각", example = "2024-04-04T22:41:42.457")
    @NotNull
    private LocalDateTime sendedTime;
}
