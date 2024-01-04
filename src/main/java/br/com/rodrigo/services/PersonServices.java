package br.com.rodrigo.services;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import br.com.rodrigo.controller.PersonController;
import br.com.rodrigo.mapper.DozerMapper;
import br.com.rodrigo.model.Person;
import br.com.rodrigo.repositories.PersonRepository;
import br.com.rodrigo.vo.v1.PersonVO;
import br.excpetions.RequiredObjectIsNullException;
import br.excpetions.ResourceNotFoundExcpetion;
import jakarta.transaction.Transactional;

@Service
public class PersonServices {
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	private PersonRepository personRepository;
	
	@Autowired
	PagedResourcesAssembler<PersonVO> assembler;
	
	public PagedModel<EntityModel<PersonVO>> findAll(Pageable pegeable) {
		logger.info("Finding all people");
		var personPage = personRepository.findAll(pegeable);
		
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personVosPage.map(p->p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));		
		Link link = linkTo(methodOn(PersonController.class).findAll(pegeable.getPageNumber(), pegeable.getPageSize(), "asc")).withSelfRel();
		
		return assembler.toModel(personVosPage, link);
	}
	
	public PagedModel<EntityModel<PersonVO>> findPersonByName(String firstName, Pageable pegeable) {
		logger.info("Finding all people");
		var personPage = personRepository.findPersonByName(firstName, pegeable);
		
		var personVosPage = personPage.map(p -> DozerMapper.parseObject(p, PersonVO.class));
		personVosPage.map(p->p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));		
		Link link = linkTo(methodOn(PersonController.class).findAll(pegeable.getPageNumber(), pegeable.getPageSize(), "asc")).withSelfRel();
		
		return assembler.toModel(personVosPage, link);
	}

	public PersonVO findById(Long id) {
		logger.info("Finding one person!");
		
		var entity = personRepository.findById(id).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public PersonVO Create(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		logger.info("Creating one person!");
		var entity = DozerMapper.parseObject(person, Person.class);
		var vo = DozerMapper.parseObject(personRepository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}

	
	public PersonVO Update(PersonVO person) {
		if(person == null) throw new RequiredObjectIsNullException();
		logger.info("Update one person!");
		Person entity = personRepository.findById(person.getKey()).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = DozerMapper.parseObject(personRepository.save(entity), PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(vo.getKey())).withSelfRel());
		return vo;
	}
	
	@Transactional
	public PersonVO disablePerson(Long id) {
		
		logger.info("Disabling one person!");
		
		personRepository.disablePerson(id);
		
		var entity = personRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundExcpetion("No records found for this ID!"));
		var vo = DozerMapper.parseObject(entity, PersonVO.class);
		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
		return vo;
	}
	
	public void Delete(Long key) {
		logger.info("Deleting one person!");
	    var entity = personRepository.findById(key).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));	
	    personRepository.delete(entity);
	}
}
