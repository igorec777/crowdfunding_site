package com.example.course.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "users", uniqueConstraints =
        {
                @UniqueConstraint(columnNames = "username")
        })
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(length = 60, unique = true)
    private String email;

    @Column(length = 80)
    private String password;

    @Column(length = 60)
    private String firstname;

    @Column(length = 60)
    private String lastname;

    @Column(length = 50)
    private String registerDate;

    @Column(length = 20, nullable = false)
    private boolean isActive;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SecureToken secureToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Company> companies;

    @ManyToMany
    @JoinTable(name = "user_favouriteCompany", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id"))
    private Set<Company> favoriteCompanies;

    @ManyToMany
    @JoinTable(name = "user_backed_company", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "company_id"))
    private Set<Company> backedCompanies;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Raiting> raiting;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade =
                    {
                            CascadeType.DETACH,
                            CascadeType.MERGE,
                            CascadeType.REFRESH,
                            CascadeType.PERSIST
                    },
            targetEntity = Bonus.class)
    @JoinTable(name = "user_bonus",
            inverseJoinColumns = @JoinColumn(name = "bonus_id",
                    nullable = false,
                    updatable = false),
            joinColumns = @JoinColumn(name = "user_id",
                    nullable = false,
                    updatable = false),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private Set<Bonus> bonuses = new HashSet<>();

    public User() {
    }

    public User(String username, String email, String password, String firstname, String lastname, String registerDate,
                boolean isActive, Set<Company> companies, Set<Comment> comments) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.registerDate = registerDate;
        this.isActive = isActive;
        this.companies = companies;
        this.comments = comments;
    }
}
