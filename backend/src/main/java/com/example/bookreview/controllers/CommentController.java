package com.example.bookreview.controllers;

import com.example.bookreview.models.Book;
import com.example.bookreview.models.DTO.CommentDTO;
import com.example.bookreview.models.DTO.CommentRequest;
import com.example.bookreview.models.User;
import com.example.bookreview.models.Comment;
import com.example.bookreview.security.SecurityUtils;
import com.example.bookreview.services.CommentService;
import com.example.bookreview.services.BookService;
import com.example.bookreview.services.CommentReactionService;
import com.example.bookreview.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController{
    private final CommentService commentService;
    private final UserService userService;
    private final BookService bookService;
    private final CommentReactionService reactionService;

    @GetMapping("/books/{bookId}/comments")
    public Page<CommentDTO> getComments(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "new") String sortBy){
        User currentUser = null;
        String email = SecurityUtils.getCurrentUserEmail();
        if (email != null) currentUser = userService.getByEmail(email);
        Book book = bookService.getBookEntityById(bookId);
        return commentService.getCommentsForBookPaged(book, currentUser, page, size, sortBy);
    }

    @PostMapping("/books/{bookId}/comments")
    public CommentDTO addComment(@PathVariable Long bookId, @RequestBody CommentRequest request){
        User user = userService.getByEmail(SecurityUtils.getCurrentUserEmail());
        Book book = bookService.getBookEntityById(bookId);
        return commentService.addComment(user, book, request.getContent());
    }

    @PostMapping("/comments/{commentId}/like")
    public CommentDTO likeComment(@PathVariable Long commentId){
        User user = userService.getByEmail(SecurityUtils.getCurrentUserEmail());
        Comment comment = commentService.getById(commentId);
        reactionService.reactToComment(user, comment, 1);
        return commentService.mapToDTO(comment, user);
    }

    @PostMapping("/comments/{commentId}/dislike")
    public CommentDTO dislikeComment(@PathVariable Long commentId){
        User user = userService.getByEmail(SecurityUtils.getCurrentUserEmail());
        Comment comment = commentService.getById(commentId);
        reactionService.reactToComment(user, comment, -1);
        return commentService.mapToDTO(comment, user);
    }
}