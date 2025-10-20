package com.example.debuffshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "card_types")
public class CardType {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String rarity;
    private String debuffText;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
    public String getDebuffText() { return debuffText; }
    public void setDebuffText(String debuffText) { this.debuffText = debuffText; }
}