package com.example.bookreview.controllers;

import com.example.bookreview.models.User;
import com.example.bookreview.models.DTO.UserDTO;
import com.example.bookreview.models.DTO.CommentDTO;
import com.example.bookreview.models.DTO.RatingDTO;
import com.example.bookreview.security.SecurityUtils;
import com.example.bookreview.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController{
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(401).build();
        UserDTO dto = userService.getCurrentUser(email);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(@RequestBody UserDTO request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userService.getByEmail(email);
        if (user.isBlocked()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Blocked users cannot update profile");
        }
        User updated = userService.updateProfile(email, request);
        UserDTO dto = userService.mapToDTO(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(){
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        userService.deleteUserByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/ratings")
    public ResponseEntity<List<RatingDTO>> getUserRatings() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.getUserRatings(email));
    }

    @GetMapping("/comments")
    public ResponseEntity<List<CommentDTO>> getUserComments() {
        String email = SecurityUtils.getCurrentUserEmail();
        return ResponseEntity.ok(userService.getUserComments(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO dto = userService.getPublicUserDTO(id);
        return ResponseEntity.ok(dto);
    }
}