package com.study.pattern.jpaPatterns.entity;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "BOOK_CATEGORY")
@NoArgsConstructor
public class BookCategory {

	public BookCategory(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public BookCategory(Long id, String name, List<Book> books) {
		super();
		this.id = id;
		this.name = name;
		this.books = books.stream().collect(Collectors.toList());
		this.books.forEach(book -> {
			book.setBookCategory(this);
		});
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_category_generator")
	@SequenceGenerator(name = "book_category_generator", sequenceName = "book_category_generator")
	@Column(name = "ID", updatable = false, nullable = false)
	private Long id;

	@Column(name = "NAME", updatable = false, nullable = false)
	private String name;

	// Using orphanRemoval if you need to delete the associated entities only.
	@OneToMany(mappedBy = "bookCategory", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<Book> books;

	/**
	 * JPA Pattern to add the book entity PesistenceContext perform dirty checking
	 * and sync the database.
	 * 
	 * @param book
	 */

	public void addBook(Book book) {
		book.setBookCategory(this);
		this.books.add(book);
	}
	
	public void removeBook(Book book) {
		book.setBookCategory(null);
		books.remove(book);
	}

}
