package com.example.booklibrary.service;

import com.example.booklibrary.model.Book;
import com.example.booklibrary.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = new Book("Test Title", "Test Author", "1234567890");
        testBook.setId(1L);
        testBook.setGenre("Fiction");
        testBook.setAvailable(true);
    }

    @Test
    void getAllBooks() {
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(1, result.size());
        assertEquals("Test Title", result.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void getBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));

        Optional<Book> result = bookService.getBookById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Title", result.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void createBook() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.createBook(testBook);

        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook() {
        Book updatedBook = new Book("Updated Title", "Updated Author", "0987654321");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.updateBook(1L, updatedBook);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void deleteBook() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(any(Book.class));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(any(Book.class));
    }

    @Test
    void toggleAvailability() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        Book result = bookService.toggleAvailability(1L);

        assertNotNull(result);
        assertFalse(result.getAvailable());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(any(Book.class));
    }
}