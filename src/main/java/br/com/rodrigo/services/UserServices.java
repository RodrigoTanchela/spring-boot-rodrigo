package br.com.rodrigo.services;


import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.rodrigo.repositories.UserRepository;

@Service
public class UserServices implements UserDetailsService {
	private Logger logger = Logger.getLogger(UserServices.class.getName());
	
	@Autowired
	private UserRepository userRepository;
	
	public UserServices(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("Finding one user by name "+ username + "!");
		var user = userRepository.findByUsername(username);
		if(user != null) {
			return user;
		} else {
			throw new UsernameNotFoundException("Username "+username+ "not found!");
		}
	}
	
//	public PersonVO findById(Long id) {
//		logger.info("Finding one!");
//		
//		var entity = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundExcpetion("No records Found for this Id"));
//		var vo = DozerMapper.parseObject(entity, PersonVO.class);
//		vo.add(linkTo(methodOn(PersonController.class).findById(id)).withSelfRel());
//		return vo;
//	}

	
	
}
