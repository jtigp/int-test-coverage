package com.jtigp.server.web;

import com.jtigp.server.persistence.model.Contact;
import com.jtigp.server.persistence.model.PhoneNumber;
import com.jtigp.server.persistence.repository.ContactRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class ContactServiceTest {
    private static PhoneNumber PHONE_1 = new PhoneNumber();
    private static Contact CONTACT_1 = new Contact();

    static {
        PHONE_1.setLabel("work");
        PHONE_1.setNumber("555-555-5555");
        CONTACT_1.setFirstName("Bobby");
        CONTACT_1.setLastName("Tables");
        CONTACT_1.addPhoneNumber(PHONE_1);
    }

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAll() {
        Mockito.when(contactRepository.findAll()).thenReturn(Collections.singleton(CONTACT_1));

        List<Contact> found = contactService.getAll(null, null);
        assertThat(found.size(), is(1));
        assertThat(found, hasItem(CONTACT_1));
    }

    @Test
    public void getAllByFirstName() {
        Mockito.when(contactRepository.findByFirstName("Bobby")).thenReturn(Collections.singletonList(CONTACT_1));

        List<Contact> found = contactService.getAll("Bobby", null);
        assertThat(found.size(), is(1));
        assertThat(found, hasItem(CONTACT_1));
    }

    @Test
    public void getAllByFirstNameDNE() {
        Mockito.when(contactRepository.findByFirstName("John")).thenReturn(Collections.emptyList());

        List<Contact> found = contactService.getAll("John", null);
        assertThat(found.size(), is(0));
    }

    @Test
    public void getAllByLastName() {
        Mockito.when(contactRepository.findByLastName("Tables")).thenReturn(Collections.singletonList(CONTACT_1));

        List<Contact> found = contactService.getAll(null, "Tables");
        assertThat(found.size(), is(1));
        assertThat(found, hasItem(CONTACT_1));
    }

    @Test
    public void getAllByLastNameDNE() {
        Mockito.when(contactRepository.findByLastName("Doe")).thenReturn(Collections.emptyList());

        List<Contact> found = contactService.getAll(null, "Doe");
        assertThat(found.size(), is(0));
    }

    @Test
    public void getAllByFirstAndLastName() {
        Mockito.when(contactRepository.findByFirstNameAndLastName("Bobby", "Tables")).thenReturn(Collections.singletonList(CONTACT_1));

        List<Contact> found = contactService.getAll("Bobby", "Tables");
        assertThat(found.size(), is(1));
        assertThat(found, hasItem(CONTACT_1));
    }

    @Test
    public void getById() {
        Mockito.when(contactRepository.findById(1)).thenReturn(CONTACT_1);

        Contact found = contactService.getById(1);
        assertThat(found, is(CONTACT_1));
    }

    @Test
    public void getByIdDNE() {
        try {
            Mockito.when(contactRepository.findById(1000)).thenReturn(null);

            contactService.getById(1000);
            fail("Should throw 404 if not found");
        } catch (NotFoundException e) {
        }
    }
}