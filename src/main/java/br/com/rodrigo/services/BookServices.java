package br.com.rodrigo.services;


import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.rodrigo.controller.BookController;
import br.com.rodrigo.mapper.DozerMapper;
import br.com.rodrigo.model.Book;
import br.com.rodrigo.repositories.BookRepository;
import br.com.rodrigo.vo.v1.BookVO;
import br.excpetions.RequiredObjectIsNullException;
import br.excpetions.ResourceNotFoundExcpetion;

@Service
public class BookServices {
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	private BookRepository bookRepository;
	
	public List<BookVO> findAll() {
		logger.info("Finding all people");
		var books =  DozerMapper.parseListObject(bookRepository.findAll(), BookVO.class);
		books.stream().forEach(p -> p.add(linkTo(methodOn(BookController.class).findById(p.getKey())).withSelfRel()));
		return books;
	}

	public BookVO findById(Long id) {
		logger.info("Finding one book!");
		
		var entity = bookRepository.findById(id).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));
		var vo = DozerMapper.parseObject(entity, BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public BookVO Create(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		logger.info("Creating one book!");
		var entity = DozerMapper.parseObject(book, Book.class);
		var vo = DozerMapper.parseObject(bookRepository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	
	public BookVO Update(BookVO book) {
		if(book == null) throw new RequiredObjectIsNullException();
		logger.info("Update one book!");
		Book entity = bookRepository.findById(book.getKey()).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));
		entity.setAuthor(book.getAuthor());
		entity.setLaunchDate(book.getLaunchDate());
		entity.setPrice(book.getPrice());
		entity.setTitle(book.getTitle());
		
		var vo = DozerMapper.parseObject(bookRepository.save(entity), BookVO.class);
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	public void Delete(Long key) {
		logger.info("Deleting one book!");
	    var entity = bookRepository.findById(key).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));	
	    bookRepository.delete(entity);
	}
}
