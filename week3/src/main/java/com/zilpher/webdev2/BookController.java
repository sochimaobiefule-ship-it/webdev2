package com.zilpher.webdev2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/products")
public class BookController {
    private final List<Book> products = new ArrayList<>();
    
    public BookController() {
        Category tech = new Category("Technology");
        products.add(new Book(1L, "Clean Code", 45.00, tech));
        products.add(new Book(2L, "Effective Java", 50.00, tech));
        products.add(new Book(3L, "The Pragmatic Programmer", 40.00, tech));
    }

    @GetMapping
    public String getAllProducts(Model model) {
        model.addAttribute("products", products);
        return "products"; 
    }

    @GetMapping("/{id}")
    public String getProductDetailView(@PathVariable Long id, Model model) {
        Optional<Book> productOpt = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();

        if (productOpt.isPresent()) {
            model.addAttribute("product", productOpt.get());
            return "product-detail"; 
        }
        return "products"; 
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Book());
        return "product-form"; 
    }

    @PostMapping
    public String createProduct(@ModelAttribute("product") Book product, BindingResult result) {
        if (result.hasErrors()) {
            return "product-form"; 
        }

        product.setId((long) (products.size() + 1));
        if (product.getCategory() == null) {
            product.setCategory(new Category("General"));
        }
        products.add(product);
        return "redirect:/products"; 
    }
}