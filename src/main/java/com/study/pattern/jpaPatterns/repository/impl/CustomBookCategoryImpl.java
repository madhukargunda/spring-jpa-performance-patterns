package com.study.pattern.jpaPatterns.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.study.pattern.jpaPatterns.entity.BookCategory;
import com.study.pattern.jpaPatterns.repository.CustomBookCategoryRepository;

@Repository
public class CustomBookCategoryImpl implements CustomBookCategoryRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void detach(BookCategory bookCategory) {
		entityManager.detach(bookCategory);
	}
}
