package com.example.course.models;

import lombok.Data;
import javax.persistence.*;


@Entity
@Table(name = "raiting")
@Data
public class Raiting
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int value;

    @ManyToOne
    @JoinColumn(name="company_id", nullable=false)
    private Company company;

    @OneToOne
    private User user;

    public Raiting() {
    }

    public Raiting(int value, Company company, User user) {
        this.value = value;
        this.company = company;
        this.user = user;
    }
}
