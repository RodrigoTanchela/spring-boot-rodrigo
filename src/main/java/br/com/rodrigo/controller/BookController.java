package br.com.rodrigo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.rodrigo.services.BookServices;
import br.com.rodrigo.utils.MediaType;
import br.com.rodrigo.vo.v1.BookVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Books", description = "Endpoints for Managing books") 
public class BookController {
	
	@Autowired
	private BookServices service;
	
	@GetMapping(produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "Finds all Books", description = "Finds all Books", 
	tags = {"Books"}, 
	responses = {
			@ApiResponse(description = "Sucess", responseCode = "200", 
					content = {
							@Content(
									mediaType = "application/json",
									array     = @ArraySchema(schema = @Schema(implementation = BookVO.class))
									)
							}),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
			})
	public List<BookVO> findAll() {
		return service.findAll();
	}
	
	@GetMapping(value = "/{id}",
	        produces ={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "Finds a Book", description = "Finds a Book", 
	tags = {"Books"}, 
	responses = {
			@ApiResponse(description = "Sucess", responseCode = "200", 
					content = 
							@Content(schema = @Schema(implementation = BookVO.class))
									
			),
			@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
			})
	public BookVO findById(@PathVariable(value = "id") Long id) {
		return service.findById(id);
	}
	
	@PostMapping(consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
	        produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "add a Book", description = "add a Book", 
	tags = {"Books"}, 
	responses = {
			@ApiResponse(description = "Sucess", responseCode = "200", 
					content = 
							@Content(schema = @Schema(implementation = BookVO.class))
									
			),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
			})
	public BookVO create(@RequestBody BookVO book) {
		return service.Create(book);
	}
	
	@PutMapping( consumes = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML},
	        produces = {MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML})
	@Operation(summary = "alter a Book", description = "Alter a Book", 
	tags = {"Books"}, 
	responses = {
			@ApiResponse(description = "Sucess", responseCode = "200", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
			})
	public BookVO update(@RequestBody BookVO book) {
		return service.Update(book);
	}
	
	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Delete a Book", description = "Delete a Book", 
	tags = {"Books"}, 
	responses = {
			@ApiResponse(description = "Sucess", responseCode = "200", 
					content = 
							@Content(schema = @Schema(implementation = BookVO.class))
									
			),
			@ApiResponse(description = "No Content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Error", responseCode = "500", content = @Content)
			})
	public ResponseEntity<?> delete(@PathVariable(value = "id") Long id) {
		 service.Delete(id);
		 return ResponseEntity.noContent().build();
	}

}
