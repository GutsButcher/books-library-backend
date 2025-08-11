package com.example.booklibrary.service;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    public List<Book> getBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }
    
    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public List<Book> getBooksByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }
    
    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailable(true);
    }
    
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublicationDate(bookDetails.getPublicationDate());
        book.setGenre(bookDetails.getGenre());
        book.setDescription(bookDetails.getDescription());
        book.setAvailable(bookDetails.getAvailable());
        
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        bookRepository.delete(book);
    }
    
    public Book toggleAvailability(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        book.setAvailable(!book.getAvailable());
        return bookRepository.save(book);
    }
}