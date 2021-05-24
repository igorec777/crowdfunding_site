package com.example.course.models;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "bonuses")
@Data
public class Bonus
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String name;

    @Column(length = 800)
    private String description;

    private float price;

    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade =
                    {
                            CascadeType.DETACH,
                            CascadeType.MERGE,
                            CascadeType.REFRESH,
                            CascadeType.PERSIST
                    },
            targetEntity = User.class)
    @JoinTable(name = "user_bonus",
            joinColumns = @JoinColumn(name = "bonus_id",
                    nullable = false,
                    updatable = false),
            inverseJoinColumns = @JoinColumn(name = "user_id",
                    nullable = false,
                    updatable = false),
            foreignKey = @ForeignKey(ConstraintMode.CONSTRAINT),
            inverseForeignKey = @ForeignKey(ConstraintMode.CONSTRAINT))
    private  Set<User> users = new HashSet<>();
}
