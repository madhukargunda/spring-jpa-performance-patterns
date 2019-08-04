package com.study.pattern.jpaPatterns.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.study.pattern.jpaPatterns.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
