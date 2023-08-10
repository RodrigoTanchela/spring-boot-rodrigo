package br.com.rodrigo.unitests.mockito.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import br.com.rodrigo.unittests.mapper.mocks.MockBook;

import br.com.rodrigo.model.Book;
import br.com.rodrigo.model.Person;
import br.com.rodrigo.repositories.BookRepository;
import br.com.rodrigo.services.BookServices;
import br.com.rodrigo.vo.v1.BookVO;
import br.excpetions.RequiredObjectIsNullException;

class BookServicesTest {

	private MockBook input;
	
	@InjectMocks
	private BookServices service;
	
	@Mock
	BookRepository repository;

	@BeforeEach
	void setUp() throws Exception {
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList();

		when(repository.findAll()).thenReturn(list);
		
		var books = service.findAll();
		assertNotNull(books);
		assertEquals(14, books.size());
		var bookOne = books.get(1);
		
		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());
		assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", bookOne.getAuthor());
		assertEquals("Title Test1", bookOne.getTitle());
		assertEquals(5.0, bookOne.getPrice());
		
		var bookFour = books.get(4);
		
		assertNotNull(bookFour);
		assertNotNull(bookFour.getKey());
		assertNotNull(bookFour.getLinks());
		assertTrue(bookFour.toString().contains("links: [</api/book/v1/4>;rel=\"self\"]"));
		assertEquals("Author Test4", bookFour.getAuthor());
		assertEquals("Title Test4", bookFour.getTitle());
		assertEquals(2.0, bookFour.getPrice());
		
		var bookSix = books.get(6);
		
		assertNotNull(bookSix);
		assertNotNull(bookSix.getKey());
		assertNotNull(bookSix.getLinks());
		assertTrue(bookSix.toString().contains("links: [</api/book/v1/6>;rel=\"self\"]"));
		assertEquals("Author Test6", bookSix.getAuthor());
		assertEquals("Title Test6", bookSix.getTitle());
		assertEquals(2.0, bookSix.getPrice());
	}

	@Test
	void testFindById() {
		Book book = input.mockEntity(1);
		book.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(book));
		
		var result = service.findById(1L);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		System.out.println(result.toString());
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		assertEquals(5.0, result.getPrice());
	}
	
	public Book mockEntity(Integer number) {
		Book book = new Book();
		assertEquals("Author Test1", book.getAuthor());
		assertEquals("Title Test1", book.getTitle());
		assertEquals(5.0, book.getPrice());
		book.setId(number.longValue());
		return book;
	}

	@Test
	void testCreate() {
		
		Book entity = input.mockEntity(1);
		Book persisted = entity;
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.Create(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		assertEquals(5.0, result.getPrice());
	}
	
	@Test
	void testCreateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.Create(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testUpdate() {
		Book entity = input.mockEntity(1);
		Book persisted = entity;
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1L);
		
		when(repository.findById(1L)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.Update(vo);
		
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author Test1", result.getAuthor());
		assertEquals("Title Test1", result.getTitle());
		assertEquals(5.0, result.getPrice());
	}
	
	@Test
	void testUpdateWithNullBook() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.Update(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testDelete() {
		Book book = input.mockEntity(1);
		book.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(book));
		
		service.Delete(1L);
	}

}
