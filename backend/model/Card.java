package com.example.debuffshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cards")
public class Card {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_type_id")
    private CardType type;

    private LocalDateTime obtainedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CardType getType() { return type; }
    public void setType(CardType type) { this.type = type; }
    public LocalDateTime getObtainedAt() { return obtainedAt; }
}