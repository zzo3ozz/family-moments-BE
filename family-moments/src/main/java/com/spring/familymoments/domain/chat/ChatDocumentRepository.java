package com.spring.familymoments.domain.chat;

import com.spring.familymoments.domain.chat.document.ChatDocument;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatDocumentRepository extends MongoRepository<ChatDocument, ObjectId> {
    List<ChatDocument> findByFamilyIdAndSendedTimeAfterOrderBySendedTimeDesc(
            Long familyId, LocalDateTime sendedTime, Pageable pageable);

    List<ChatDocument> findByFamilyIdAndIdBeforeOrderByIdDesc(Long familyId, ObjectId id, Pageable pageable);

    Long countByFamilyIdAndSendedTimeAfter(Long familyId, LocalDateTime sendedTime);

    ChatDocument findFirstByFamilyIdOrderByIdDesc(Long familyId);
}
