package com.example.course.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

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

    @Column()
    private Long user_id;

    public Raiting() {
    }

    public Raiting(int value, Company company, Long user_id) {
        this.value = value;
        this.company = company;
        this.user_id = user_id;
    }
}
