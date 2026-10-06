package com.winston.bookservice.dto;

import com.winston.bookservice.model.Book;
import java.time.LocalDate;

public record BookResponse(Long id, String title, String author, String isbn, LocalDate publishedDate) {

    public static BookResponse from(Book b) {
        return new BookResponse(b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getPublishedDate());
    }
}
