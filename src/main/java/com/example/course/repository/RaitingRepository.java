package com.example.course.repository;

import com.example.course.models.Raiting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RaitingRepository extends JpaRepository<Raiting, Long>
{
}
