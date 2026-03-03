package com.example.bookreview.services;

import com.example.bookreview.models.Book;
import com.example.bookreview.models.DTO.BookDTO;
import com.example.bookreview.repositories.BookRepository;
import com.example.bookreview.repositories.RatingRepository;
import com.example.bookreview.models.User;
import com.example.bookreview.models.DTO.UserDTO;
import com.example.bookreview.models.Rating;
import com.example.bookreview.models.DTO.CommentDTO;
import com.example.bookreview.models.DTO.BookDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;
    private final CommentService commentService;

    public List<BookDTO> getAllBooks(){
        return bookRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Book getBookEntityById(Long id){
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public BookDTO createBook(BookDTO dto){
        Book book = Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .description(dto.getDescription())
                .coverUrl(dto.getCoverUrl())
                .build();
        bookRepository.save(book);
        return mapToDTO(book);
    }

    public BookDetailsDTO getBookDetails(Long bookId, User currentUser, String sortBy) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        //average rating
        BigDecimal averageBD = ratingRepository.findAverageScoreByBook(book);
        Long ratingsCount = ratingRepository.countByBook(book);
        double avgRating = averageBD != null ? averageBD.setScale(2, RoundingMode.HALF_UP).doubleValue() : 0.0;
        Double userRating = null;
        if (currentUser != null) {
            userRating = ratingRepository.findByUserAndBook(currentUser, book)
                    .map(r -> r.getScore().doubleValue())
                    .orElse(null);
        }
        //first page of comments(10)
        Page<CommentDTO> commentsPage = commentService.getCommentsForBookPaged(book, currentUser, 0, 10, sortBy);
        return BookDetailsDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .coverUrl(book.getCoverUrl())
                .averageRating(avgRating)
                .ratingsCount(ratingsCount)
                .userRating(userRating)
                .comments(commentsPage.getContent())
                .currentPage(commentsPage.getNumber())
                .totalPages(commentsPage.getTotalPages())
                .totalComments(commentsPage.getTotalElements())
                .build();
    }

    public BookDTO getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToDTO(book);
    }

    public void deleteBook(Long id){
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.delete(book);
    }

    public BookDTO updateBook(Long id, BookDTO dto){
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setDescription(dto.getDescription());
        book.setCoverUrl(dto.getCoverUrl());
        bookRepository.save(book);
        return mapToDTO(book);
    }

    private BookDTO mapToDTO(Book book){
        BigDecimal averageBD = ratingRepository.findAverageScoreByBook(book);
        BigDecimal roundedAverage = averageBD != null ? averageBD.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        Long count = ratingRepository.countByBook(book);

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .coverUrl(book.getCoverUrl())
                .averageRating(roundedAverage.doubleValue()) // для DTO как Double
                .ratingsCount(count)
                .build();
    }
}