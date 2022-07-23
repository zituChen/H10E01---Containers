package de.tum.in.ase.eist;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import de.tum.in.ase.eist.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.management.BadAttributeValueExpException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PersonServiceTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testAddPerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        personService.save(person);

        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDeletePerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        person = personRepository.save(person);

        personService.delete(person);

        assertTrue(personRepository.findAll().isEmpty());
    }

    @Test
    void testAddParent() {
        var father = new Person();
        father.setFirstName("Bryan");
        father.setLastName("Carlson");
        father.setBirthday(LocalDate.now());

        var child = new Person();
        child.setFirstName("Nelson");
        child.setLastName("Cakes.Jr");
        child.setBirthday(LocalDate.now());

        personService.save(father);
        personService.save(child);

        personService.addParent(child, father);

        assertEquals(2, personRepository.findAll().size());
        assertTrue(child.getParents().contains(father));

    }

    @Test
    void testAddThreeParents() {
        var kid = new Person();
        kid.setFirstName("Alex");
        kid.setLastName("King");
        kid.setBirthday(LocalDate.now());

        var parent1 = new Person();
        parent1.setFirstName("Sarah");
        parent1.setLastName("Bell");
        parent1.setBirthday(LocalDate.now());

        var parent2 = new Person();
        parent2.setFirstName("Noah");
        parent2.setLastName("Fury");
        parent2.setBirthday(LocalDate.now());

        var parent3 = new Person();
        parent3.setFirstName("James");
        parent3.setLastName("Hardly");
        parent3.setBirthday(LocalDate.now());

        personService.save(kid);
        personService.save(parent1);
        personService.save(parent2);
        personService.save(parent3);

        kid = personService.addParent(kid, parent1);
        kid = personService.addParent(kid, parent2);

        //Assertions

//        assertEquals(kid, personService.addParent(kid, parent1));
//        assertEquals(kid, personService.addParent(kid, parent2));
        Person kidNew = kid;
        assertThrows(ResponseStatusException.class, ()->personService.addParent(kidNew, parent3));
        assertEquals(4, personRepository.findAll().size());
        assertTrue(kid.getParents().contains(parent1));
        assertTrue(kid.getParents().contains(parent2));

    }
}
