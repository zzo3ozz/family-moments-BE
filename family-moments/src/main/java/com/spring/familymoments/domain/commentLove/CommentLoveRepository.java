package com.spring.familymoments.domain.commentLove;

import com.spring.familymoments.domain.comment.entity.Comment;
import com.spring.familymoments.domain.commentLove.entity.CommentLove;
import com.spring.familymoments.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLoveRepository extends JpaRepository<CommentLove, Long> {
    Optional<CommentLove> findByCommentIdAndUserId(Comment comment, User user);

    boolean existsByCommentIdAndUserId(Comment comment, User user);
}
