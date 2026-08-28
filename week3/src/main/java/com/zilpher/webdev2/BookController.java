package com.zilpher.webdev2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/books")
public class BookController {
    private final List<Book> books = new ArrayList<>();
    public BookController() {
        books.add(new Book(1L, "Clean Code", "Robert C. Martin"));
        books.add(new Book(2L, "Effective Java", "Joshua Bloch"));
        books.add(new Book(3L, "The Pragmatic Programmer", "Andrew Hunt"));
    }

    @GetMapping
    @ResponseBody
    public List<Book> getAllBooks(@RequestParam(required = false) String author) {
        if (author != null && !author.isBlank()) {
            return books.stream()
                    .filter(b -> b.getAuthor().equalsIgnoreCase(author))
                    .collect(Collectors.toList());
        }
        return books;
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> bookOpt = books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst();

        return bookOpt
                .map(book -> ResponseEntity.ok(book))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/view")
    public String getBookDetailView(@PathVariable Long id, Model model) {
        Optional<Book> bookOpt = books.stream()
            .filter(b -> b.getId().equals(id))
                .findFirst();

        if (bookOpt.isPresent()) {
            model.addAttribute("book", bookOpt.get());
            return "book-detail";
        }
        return "404"; 
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createBook(@ModelAttribute Book newBook) {
        if (newBook.getId() == null) {
            newBook.setId((long) (books.size() + 1));
        }
        books.add(newBook);
        return "redirect:/books";
    }
}