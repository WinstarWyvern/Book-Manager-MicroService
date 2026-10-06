package com.winston.bookservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.winston.bookservice.dto.BookPatchRequest;
import com.winston.bookservice.dto.BookRequest;
import com.winston.bookservice.dto.BookResponse;
import com.winston.bookservice.exception.BookNotFoundException;
import com.winston.bookservice.exception.DuplicateIsbnException;
import com.winston.bookservice.model.Book;
import com.winston.bookservice.repository.BookRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock BookRepository repository;
    @InjectMocks BookService service;

    @Test
    void createRejectsDuplicateIsbn() {
        var req = new BookRequest("T", "A", "111", LocalDate.of(2020, 1, 1));
        when(repository.existsByIsbn("111")).thenReturn(true);
        assertThrows(DuplicateIsbnException.class, () -> service.create(req));
        verify(repository, never()).save(any());
    }

    @Test
    void patchUpdatesOnlyProvidedFields() {
        Book book = new Book("Old", "Author", "111", LocalDate.of(2020, 1, 1));
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(book);

        BookResponse res = service.patch(1L, new BookPatchRequest("New", null, null, null));

        assertEquals("New", res.title());
        assertEquals("Author", res.author());
        assertEquals("111", res.isbn());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> service.findById(99L));
    }
}
