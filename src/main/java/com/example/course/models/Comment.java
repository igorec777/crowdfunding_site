package com.example.course.models;

import lombok.Data;
import javax.persistence.*;


@Entity
@Table(name = "comments")
@Data
public class Comment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 5000)
    private String text;

    private String date;

    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
