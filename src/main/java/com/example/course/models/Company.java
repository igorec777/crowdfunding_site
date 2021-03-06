package com.example.course.models;

import lombok.*;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.SortableField;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "companies")
@Indexed
@Getter
@Setter
@NoArgsConstructor
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(name = "name")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Field(name = "topic")
    @SortableField
    @Column(nullable = false, length = 80)
    private String topic;

    @Field(name = "description")
    @Column(nullable = false, length = 3000)
    private String description;

    @Field(name = "tags")
    @Column(length = 200)
    private String tags;

    @Column(length = 100)
    private String youtubeURL;

    private float currentSum;

    private float companyGoal;

    private String expirationDate;

    @Transient
    private float averageRate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "favoriteCompanies")
    private Set<User> likedUsers;

    @ManyToMany(mappedBy = "backedCompanies")
    private Set<User> backers;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Bonus> bonuses;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Raiting> raitings;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<News> news;
}
