package com.example.bookreview.services;

import com.example.bookreview.models.Book;
import com.example.bookreview.models.Comment;
import com.example.bookreview.models.User;
import com.example.bookreview.models.DTO.CommentDTO;
import com.example.bookreview.repositories.CommentRepository;
import com.example.bookreview.repositories.CommentReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService{
    private final CommentRepository commentRepository;
    private final CommentReactionRepository reactionRepository;

    public CommentDTO addComment(User user, Book book, String content){
        if (user.isBlocked()){
            throw new RuntimeException("Blocked users cannot comment");
        }

        if (content == null || content.isBlank()){
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        Comment comment = Comment.builder()
                .user(user)
                .book(book)
                .content(content.trim())
                .build();

        Comment saved = commentRepository.save(comment);
        return mapToDTO(saved, user);
    }

    public Comment getById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new RuntimeException("Comment not found"));
    }

    public CommentDTO toDTO(Comment comment){
        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .username(comment.getUser().getUsername())
                .avatarUrl(comment.getUser().getAvatarUrl())
                .likesCount(comment.getReactions() == null ? 0 :
                        comment.getReactions().stream().filter(r -> r.getValue() == 1).count())
                .dislikesCount(comment.getReactions() == null ? 0 :
                        comment.getReactions().stream().filter(r -> r.getValue() == -1).count())
                .userReaction(null)
                .build();
    }

    public void deleteComment(Long commentId){
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(commentId);
    }

    public Page<CommentDTO> getCommentsForBookPaged(Book book, User currentUser, int page, int size, String sortBy){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdDate"), Sort.Order.desc("id")));
        Page<Comment> commentsPage = commentRepository.findByBook(book, pageable);
        Page<CommentDTO> dtoPage = commentsPage.map(comment -> mapToDTO(comment, currentUser));
        if ("top".equalsIgnoreCase(sortBy)){
            List<CommentDTO> sorted = dtoPage.getContent()
                    .stream()
                    .sorted((c1, c2) -> Long.compare(c2.getLikesCount(), c1.getLikesCount()))
                    .toList();
            dtoPage = new PageImpl<>(sorted, pageable, dtoPage.getTotalElements());
        }
        return dtoPage;
    }

    public CommentDTO mapToDTO(Comment comment, User currentUser){
        List<?> reactions = comment.getReactions() != null ? comment.getReactions() : Collections.emptyList();
        long likes = comment.getReactions() == null ? 0 : comment.getReactions().stream()
                        .filter(r -> r.getValue() == 1)
                        .count();

        long dislikes = comment.getReactions() == null ? 0 : comment.getReactions().stream()
                        .filter(r -> r.getValue() == -1)
                        .count();
        String userReaction = null;
        if (currentUser != null && comment.getReactions() != null){
            userReaction = comment.getReactions().stream()
                    .filter(r -> r.getUser().getId().equals(currentUser.getId()))
                    .findFirst()
                    .map(r -> r.getValue() == 1 ? "LIKE" : "DISLIKE")
                    .orElse(null);
        }

        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .username(comment.getUser().getUsername())
                .avatarUrl(comment.getUser().getAvatarUrl())
                .likesCount(likes)
                .dislikesCount(dislikes)
                .userReaction(userReaction)
                .build();
    }
}