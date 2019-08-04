package com.study.pattern.jpaPatterns.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import com.study.pattern.jpaPatterns.entity.Book;
import com.study.pattern.jpaPatterns.entity.BookCategory;

import lombok.extern.slf4j.Slf4j;

@DataJpaTest
@RunWith(SpringRunner.class)
@SqlGroup({
		@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = {
				"classpath:sql/dont-use-cascadeType-remove/before.sql" }),
		@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = {
				"classpath:sql/dont-use-cascadeType-remove/after.sql" }) })
@TestPropertySource(locations = { "classpath:application.properties" })
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
public class BookCategoryRepositoryTest {

	@Autowired
	public BookRepository bookRepository;

	@Autowired
	public BookCategoryRepository bookCategoryRepository;
	
	@Autowired
	public CustomBookCategoryRepository customBookCategoryRepository;

	@Test
	public void test_HappyPath() {
		assertThat(bookRepository).isNotNull();
		assertThat(bookCategoryRepository).isNotNull();

		Optional<BookCategory> autobiography = bookCategoryRepository.findByName("Autobiography");
		assertThat(autobiography).isNotEmpty();
		assertThat(autobiography.get().getBooks()).isNotNull();
		assertThat(autobiography.get().getBooks().size()).isEqualTo(4);
	}

	/**
	 * 
	 * 1.Don't use the cascadeType.REMOVE. 2.This creates a several issues in
	 * ***-To-Many Relations ships. 3.Use this for **-To-One Relation ship. 4.Don
	 * not use CascadeType Remove to remove many records from database will cause
	 * the performance issue. 5.It fires the too many queries to remove the data ,
	 * which can be done by single statement. 6.Remove the BookCategory and it will
	 * deletes the associated data from Books entity. 7.Get the data first and so
	 * enitity will available in persistent context.
	 * 
	 */

	@Test
	@Transactional
	public void test_delete_entries_using_delete_by_cascade_type_remove() {

		assertThat(bookRepository).isNotNull();
		assertThat(bookCategoryRepository).isNotNull();

		// Get the Entity first and place it into PersistenceContext,
		// Hence it will be managed by the life cycle of the PesistenceContext
		Optional<BookCategory> autobiography = bookCategoryRepository.findByName("Autobiography");
		assertThat(autobiography).isNotEmpty();

		bookCategoryRepository.delete(autobiography.get());
		assertThat(bookCategoryRepository.findByName("Autobiography")).isEmpty();
	}

	/**
	 * 1.This Object (autobiography) is available in persistence context. 2.This
	 * Object will be maintained by the Life cycle of persistence context. 3.When
	 * flush() or commit JPA synchronizes the data in Persistence context and
	 * Database 4.Hence we remove the all child entities in object of persistence
	 * context. 5.If orphanRemoval=true is specified the disconnected entity
	 * instance is automatically removed. 6.orphanRemoval means when we remove the
	 * relationship of entities JPA will remove from database when commit.
	 * 7.Hibernate automatically delete the child entity if I remove its association
	 * to the parent?
	 */
	@Test
	@Transactional
	public void test_remove_the_association_to_the_child_entity_from_parent_hibernate_delete_the_child_entity_automatically() {

		assertThat(bookRepository).isNotNull();
		assertThat(bookCategoryRepository).isNotNull();

		// Hence it will be managed by the life cycle of the PesistenceContext
		Optional<BookCategory> autobiography = bookCategoryRepository.findByName("Autobiography");
		assertThat(autobiography).isNotEmpty();

		assertThat(autobiography.get().getBooks().size()).isEqualTo(4);
		// When Removing the child entity hibernate will remove from the database
		autobiography.get().getBooks().remove(0);
		autobiography.get().getBooks().remove(0);
		autobiography.get().getBooks().remove(0);
		
		Optional<BookCategory> autobiography1 = bookCategoryRepository.findByName("Autobiography");
		assertThat(autobiography1).isNotEmpty();

		assertThat(autobiography1.get().getBooks().size()).isEqualTo(1);
	}

	@Test
	@Transactional
	public void test_jpa_way_of_adding_the_entity() {
		assertThat(bookRepository).isNotNull();
		assertThat(bookCategoryRepository).isNotNull();

	}
	
	
	/**
	 * 1.Here Entity we are updating into persistence Context
	 * 2.When Tx commits Persistence context will perform the dirty checking
	 *   and synchronizes the data into database.
	 * 3.Hence no need of delete operation to explicitly called.
	 * 
	 */
	@Transactional
	@Test
	public void update_entity_attributes_dirty_checking_with_out_calling_save() {
		
		Optional<BookCategory> autobiography = bookCategoryRepository.findByName("Autobiography");
		assertThat(autobiography).isNotEmpty();
		
		BookCategory bookCategory = autobiography.get();		
		Book book  = new Book("ISBN-100010","MY AUTOBIOGRAPHY");
		bookCategory.addBook(book);
		
		Optional<BookCategory> bookCategory2 = bookCategoryRepository.findByName("Autobiography");
		assertThat(bookCategory2).isNotEmpty();
		
		assertThat(bookCategory2.get().getBooks().size()).isEqualTo(5);
		bookCategory2.get().getBooks().forEach(b -> log.info("The Book : {}  ",b));

		log.info("*************************  [Removing the book] ********************************************");
		bookCategory.removeBook(book);
		assertThat(bookCategory2.get().getBooks().size()).isEqualTo(4);
		bookCategory2.get().getBooks().forEach(b -> log.info("The Book : {}  ",b));
	}
	
	@Transactional
	@Test
	public void detach_entity_from_persistent_context() {
		Optional<BookCategory> autobiography = bookCategoryRepository.findByName("Autobiography");
		assertThat(autobiography).isNotEmpty();
		customBookCategoryRepository.detach(autobiography.get());	
	}
}
