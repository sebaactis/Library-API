package com.library.administration.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    private Author author;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private LocalDate published;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishList> wishListEntries = new ArrayList<>();

    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }
}
