package com.example.course.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "news")
@Getter
@Setter
@NoArgsConstructor
public class News
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String title;

    private String date;

    @Column(length = 5000)
    private String text;

    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private Company company;
}
