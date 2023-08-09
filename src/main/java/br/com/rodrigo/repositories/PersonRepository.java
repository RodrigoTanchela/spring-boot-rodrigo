package br.com.rodrigo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import br.com.rodrigo.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {}
