package com.example.booklibrary.controller;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Book Management", description = "Operations related to books in the library")
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @GetMapping
    @Operation(summary = "Get all books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get a book by ID")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get a book by ISBN")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable String isbn) {
        return bookService.getBookByIsbn(isbn)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/author/{author}")
    @Operation(summary = "Get books by author")
    public List<Book> getBooksByAuthor(@PathVariable String author) {
        return bookService.getBooksByAuthor(author);
    }
    
    @GetMapping("/title/{title}")
    @Operation(summary = "Get books by title")
    public List<Book> getBooksByTitle(@PathVariable String title) {
        return bookService.getBooksByTitle(title);
    }
    
    @GetMapping("/genre/{genre}")
    @Operation(summary = "Get books by genre")
    public List<Book> getBooksByGenre(@PathVariable String genre) {
        return bookService.getBooksByGenre(genre);
    }
    
    @GetMapping("/available")
    @Operation(summary = "Get all available books")
    public List<Book> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }
    
    @PostMapping
    @Operation(summary = "Create a new book")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a book")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle book availability")
    public ResponseEntity<Book> toggleAvailability(@PathVariable Long id) {
        try {
            Book updatedBook = bookService.toggleAvailability(id);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}