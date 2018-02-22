package com.jtigp.server.persistence.repository;

import com.jtigp.server.persistence.model.Contact;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface ContactRepository extends CrudRepository<Contact, Integer> {

    List<Contact> findByFirstNameAndLastName(String firstName, String lastName);

    List<Contact> findByFirstName(String firstName);

    List<Contact> findByLastName(String lastName);

    Contact findById(int id);

    void deleteContactById(int id);

}