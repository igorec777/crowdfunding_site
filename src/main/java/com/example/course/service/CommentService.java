package com.example.course.service;

import com.example.course.models.Comment;
import com.example.course.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    public void save(Comment comment) {
        commentRepository.saveAndFlush(comment);
    }

    public List<Comment> findByCompanyId(Long id) {
        List<Comment> comments = commentRepository.findAll();

        comments.removeIf(com -> !com.getCompany().getId().equals(id));

        return comments;
    }
}
