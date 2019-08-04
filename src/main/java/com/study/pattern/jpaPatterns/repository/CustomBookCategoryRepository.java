package com.study.pattern.jpaPatterns.repository;

import com.study.pattern.jpaPatterns.entity.BookCategory;

public interface CustomBookCategoryRepository {
	
	void detach(BookCategory bookCategory);

}
