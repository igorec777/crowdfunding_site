package com.example.course.models;

import com.example.course.helpers.DonateHelper;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "companies")
@Getter
@Setter
@NoArgsConstructor
public class Company
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 80)
    private String topic;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(length = 200)
    private String tags;

    @Column(length = 100)
    private String youtubeURL;

    private float currentSum;

    private float companyGoal;

    @Column(length = 50)
    private String expirationDate;

    private int totalRate;

    private int rateCount;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Bonus> bonuses;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Raiting> raitings;

    @OneToMany(mappedBy="company", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<News> news;
}
