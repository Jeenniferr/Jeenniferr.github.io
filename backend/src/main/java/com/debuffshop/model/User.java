package com.example.debuffshop.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String twitchId;
    private String displayName;
    private String email;
    private int coins;
    private int packs;

    @OneToMany(mappedBy = "user")
    private List<Card> cards;

    public Long getId() { return id; }
    public String getTwitchId() { return twitchId; }
    public void setTwitchId(String twitchId) { this.twitchId = twitchId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }

    public int getPacks() { return packs; }
    public void setPacks(int packs) { this.packs = packs; }
}