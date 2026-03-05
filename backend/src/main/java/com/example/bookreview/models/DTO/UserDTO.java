package com.example.bookreview.models.DTO;

import lombok.*;
import java.util.List;
import java.time.LocalDate;
import com.example.bookreview.models.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO{
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private LocalDate createdAt;
    private Role role;
    private Boolean blocked;
    private List<RatingDTO> ratings;
    private List<CommentDTO> comments;
}