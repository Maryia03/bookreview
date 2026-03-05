package com.example.bookreview.services;

import com.example.bookreview.models.User;
import com.example.bookreview.models.DTO.UserDTO;
import com.example.bookreview.models.DTO.CommentDTO;
import com.example.bookreview.models.DTO.RatingDTO;
import com.example.bookreview.repositories.UserRepository;
import com.example.bookreview.repositories.CommentRepository;
import com.example.bookreview.repositories.RatingRepository;
import com.example.bookreview.services.CommentService;
import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService{
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    public User getByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserDTO getCurrentUser(String email){
        User user = getByEmail(email);
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .blocked(user.isBlocked())
                .build();
    }

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public User getById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void blockUser(Long userId){
        User user = getById(userId);
        user.setBlocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long userId){
        User user = getById(userId);
        user.setBlocked(false);
        userRepository.save(user);
    }

    public User updateProfile(String email, UserDTO request){
        User user = getByEmail(email);
        user.setUsername(request.getUsername());
        user.setAvatarUrl(request.getAvatarUrl());
        return userRepository.save(user);
    }

    private List<RatingDTO> getRatingsForUser(User user){
        return ratingRepository.findByUser(user)
                .stream()
                .map(r -> RatingDTO.builder()
                        .id(r.getId())
                        .value(r.getScore())
                        .username(user.getUsername())
                        .bookId(r.getBook().getId())
                        .bookTitle(r.getBook().getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private List<CommentDTO> getCommentsForUser(User user){
        return commentRepository.findByUser(user)
                .stream()
                .map(c -> {
                    long likes = c.getReactions().stream().filter(r -> r.getValue() == 1).count();
                    long dislikes = c.getReactions().stream().filter(r -> r.getValue() == -1).count();
                    return CommentDTO.builder()
                            .id(c.getId())
                            .content(c.getContent())
                            .createdDate(c.getCreatedDate())
                            .userId(c.getUser().getId())
                            .username(c.getUser().getUsername())
                            .avatarUrl(c.getUser().getAvatarUrl())
                            .likesCount(likes)
                            .dislikesCount(dislikes)
                            .userReaction(null)
                            .bookId(c.getBook().getId())
                            .bookTitle(c.getBook().getTitle())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .blocked(user.isBlocked())
                .ratings(getRatingsForUser(user))
                .comments(getCommentsForUser(user))
                .build();
    }

    public List<RatingDTO> getUserRatings(String email){
        User user = getByEmail(email);
        return getRatingsForUser(user);
    }

    public List<CommentDTO> getUserComments(String email){
        User user = getByEmail(email);
        return getCommentsForUser(user);
    }

    public UserDTO getPublicUserDTO(Long id){
        User user = getById(id);
        List<RatingDTO> ratings = ratingRepository.findByUser(user)
                .stream()
                .map(r -> RatingDTO.builder()
                        .id(r.getId())
                        .value(r.getScore())
                        .bookId(r.getBook().getId())
                        .bookTitle(r.getBook().getTitle())
                        .username(user.getUsername())
                        .build())
                .toList();
        List<CommentDTO> comments = commentRepository.findByUser(user)
                .stream()
                .map(c -> commentService.mapToDTO(c, null))
                .toList();

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .ratings(ratings)
                .comments(comments)
                .build();
    }
}