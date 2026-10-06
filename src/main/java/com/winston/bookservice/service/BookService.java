package com.winston.bookservice.service;

import com.winston.bookservice.dto.BookPatchRequest;
import com.winston.bookservice.dto.BookRequest;
import com.winston.bookservice.dto.BookResponse;
import com.winston.bookservice.exception.BookNotFoundException;
import com.winston.bookservice.exception.DuplicateIsbnException;
import com.winston.bookservice.model.Book;
import com.winston.bookservice.repository.BookRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public BookResponse create(BookRequest req) {
        if (repository.existsByIsbn(req.isbn())) {
            throw new DuplicateIsbnException(req.isbn());
        }
        Book saved = repository.save(new Book(req.title(), req.author(), req.isbn(), req.publishedDate()));
        return BookResponse.from(saved);
    }

    public List<BookResponse> findAll() {
        return repository.findAll().stream().map(BookResponse::from).toList();
    }

    public BookResponse findById(Long id) {
        return BookResponse.from(getOrThrow(id));
    }

    @Transactional
    public BookResponse update(Long id, BookRequest req) {
        Book book = getOrThrow(id);
        if (repository.existsByIsbnAndIdNot(req.isbn(), id)) {
            throw new DuplicateIsbnException(req.isbn());
        }
        book.setTitle(req.title());
        book.setAuthor(req.author());
        book.setIsbn(req.isbn());
        book.setPublishedDate(req.publishedDate());
        return BookResponse.from(repository.save(book));
    }

    @Transactional
    public BookResponse patch(Long id, BookPatchRequest req) {
        Book book = getOrThrow(id);
        if (req.title() != null) book.setTitle(req.title());
        if (req.author() != null) book.setAuthor(req.author());
        if (req.isbn() != null) {
            if (repository.existsByIsbnAndIdNot(req.isbn(), id)) {
                throw new DuplicateIsbnException(req.isbn());
            }
            book.setIsbn(req.isbn());
        }
        if (req.publishedDate() != null) book.setPublishedDate(req.publishedDate());
        return BookResponse.from(repository.save(book));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(getOrThrow(id));
    }

    private Book getOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }
}
