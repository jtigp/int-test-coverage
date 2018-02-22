package com.jtigp.server.web;

import com.jtigp.server.persistence.model.Contact;
import com.jtigp.server.persistence.model.PhoneNumber;
import com.jtigp.server.persistence.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("contacts")
@Transactional
public class ContactService {
    private static String API_PREFIX = "/api/contacts/";

    @Autowired
    private ContactRepository contactRepository;

    @GET
    @Path("/")
    public List<Contact> getAll(@QueryParam("firstname") String firstname, @QueryParam("lastname") String lastname) {
        if (!StringUtils.isEmpty(firstname) && !StringUtils.isEmpty(lastname)) {
            return contactRepository.findByFirstNameAndLastName(firstname, lastname);
        } else if (!StringUtils.isEmpty(firstname)) {
            return contactRepository.findByFirstName(firstname);
        } else if (!StringUtils.isEmpty(lastname)) {
            return contactRepository.findByLastName(lastname);
        }

        Iterable<Contact> contacts = contactRepository.findAll();
        return StreamSupport.stream(contacts.spliterator(), false).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Contact getById(@PathParam("id") int id) {
        Contact contact = contactRepository.findById(id);
        if (contact == null) {
            throw new NotFoundException();
        }
        return contact;
    }

    @DELETE
    @Path("/{id}")
    public void deleteById(@PathParam("id") int id) {
        Contact contact = contactRepository.findById(id);
        if (contact == null) {
            throw new NotFoundException();
        }
        contactRepository.deleteContactById(id);
    }

    @POST
    @Path("/")
    public Response addContact(Contact contact) {
        List<PhoneNumber> phoneNumbers = contact.getPhoneNumbers();
        if (phoneNumbers != null && phoneNumbers.size() > 0) {
            throw new IllegalArgumentException("Must add phone numbers through the api");
        }
        Contact saved = contactRepository.save(contact);
        return Response.created(URI.create(API_PREFIX + saved.getId())).build();
    }

    @POST
    @Path("/{id}/phone-numbers")
    @Consumes("")
    public Response addPhoneNumber(@PathParam("id") int contactId, PhoneNumber phoneNumber) {
        Contact contact = contactRepository.findById(contactId);
        if (contact == null) {
            throw new NotFoundException();
        }
        contact.addPhoneNumber(phoneNumber);
        Contact saved = contactRepository.save(contact);
        List<PhoneNumber> phoneNumbers = saved.getPhoneNumbers();
        return Response.created(URI.create(API_PREFIX + saved.getId() + "/phone-numbers/" + phoneNumbers.get(phoneNumbers.size() - 1).getId
                ())).build();
    }

    @GET
    @Path("/{id}/phone-numbers/{phone-id}")
    public PhoneNumber getPhoneNumber(@PathParam("id") int contactId, @PathParam("phone-id") int phoneId) {
        Contact contact = contactRepository.findById(contactId);
        if (contact == null) {
            throw new NotFoundException();
        }
        Optional<PhoneNumber> phoneNumber = contact.getPhoneNumbers().stream().filter(ph -> ph.getId() == phoneId).findAny();
        return phoneNumber.orElseThrow(NotFoundException::new);
    }

    @DELETE
    @Path("/{id}/phone-numbers/{phone-id}")
    public void removePhoneNumber(@PathParam("id") int contactId, @PathParam("phone-id") int phoneId) {
        Contact contact = contactRepository.findById(contactId);
        if (contact == null) {
            throw new NotFoundException();
        }
        Optional<PhoneNumber> phoneNumber = contact.getPhoneNumbers().stream().filter(ph -> ph.getId() == phoneId).findAny();
        contact.removePhoneNumber(phoneNumber.orElseThrow(NotFoundException::new));
        contactRepository.save(contact);
    }

}
