package com.example.bookreview.controllers;

import com.example.bookreview.models.DTO.BookDTO;
import com.example.bookreview.models.DTO.BookDetailsDTO;
import com.example.bookreview.services.BookService;
import com.example.bookreview.repositories.UserRepository;
import com.example.bookreview.security.SecurityUtils;
import com.example.bookreview.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController{
    private final BookService bookService;
    private final UserRepository userRepository;

    //GET /api/books
    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    //GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDTO> getBookDetails(@PathVariable Long id, @RequestParam(defaultValue = "new") String sortBy){
        String email = SecurityUtils.getCurrentUserEmail();
        User currentUser = null;
        if (email != null){
            currentUser = userRepository.findByEmail(email).orElse(null);
        }
        BookDetailsDTO dto = bookService.getBookDetails(id, currentUser, sortBy);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(@RequestParam String query){
        return ResponseEntity.ok(bookService.searchBooks(query));
    }
}