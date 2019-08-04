package com.study.pattern.jpaPatterns.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity(name = "BOOKS")
@NoArgsConstructor
@ToString(exclude = "bookCategory")
public class Book {

	public Book(String isbn, String bookName) {
		super();
		this.isbn = isbn;
		this.bookName = bookName;
	}

	public Book(String isbn, String bookName, BookCategory bookCategory) {
		super();
		this.isbn = isbn;
		this.bookName = bookName;
		this.bookCategory = bookCategory;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_id_generator")
	@SequenceGenerator(name = "book_id_generator", sequenceName = "book_id_generator")
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;

	@Column(name = "ISBN", updatable = false, nullable = false)
	private String isbn;

	@Column(name = "BOOK_NAME", updatable = false, nullable = false)
	private String bookName;

	@ManyToOne
	@JoinColumn(name = "BOOK_CATEGORY_ID")
	private BookCategory bookCategory;

	public void remove(BookCategory category) {
		category.removeBook(this);
		this.setBookCategory(null);
	}

}
