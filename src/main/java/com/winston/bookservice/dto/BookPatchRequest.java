package com.winston.bookservice.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record BookPatchRequest(
        @Pattern(regexp = ".*\\S.*", message = "must not be blank") @Size(max = 255) String title,
        @Pattern(regexp = ".*\\S.*", message = "must not be blank") @Size(max = 255) String author,
        @Pattern(regexp = ".*\\S.*", message = "must not be blank") @Size(max = 20) String isbn,
        @PastOrPresent LocalDate publishedDate) {
}
