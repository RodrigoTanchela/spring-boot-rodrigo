package br.com.rodrigo.services;


import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Service;

import br.com.rodrigo.controller.PersonController;
import br.com.rodrigo.mapper.DozerMapper;
import br.com.rodrigo.model.Person;
import br.com.rodrigo.repositories.PersonRepository;
import br.com.rodrigo.vo.v1.PersonVO;
import br.excpetions.RequiredObjectIsNullException;
import br.excpetions.ResourceNotFoundExcpetion;

@Service
public class PersonServices {
	private Logger logger = Logger.getLogger(PersonServices.class.getName());
	
	@Autowired
	private PersonRepository personRepository;
	
	public List<PersonVO> findAll() {
		logger.info("Finding all people");
		var persons =  DozerMapper.parseListObject(personRepository.findAll(), PersonVO.class);
		persons.stream().forEach(p -> p.add(linkTo(methodOn(PersonController.class).findById(p.getKey())).withSelfRel()));
		return persons;
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
	
	public void Delete(Long key) {
		logger.info("Deleting one person!");
	    var entity = personRepository.findById(key).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));	
	    personRepository.delete(entity);
	}
}
