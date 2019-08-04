package com.study.pattern.jpaPatterns.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.study.pattern.jpaPatterns.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {

	Optional<BookCategory> findByName(String name);

//	@Modifying
//	@Query("DELETE BookCategory b WHERE b.name = ?1")
//	void deleteByCategoryId(String name);
}
