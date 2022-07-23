package de.tum.in.ase.eist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PersonIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonRepository personRepository;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void testAddPerson() throws Exception {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        var response = this.mvc.perform(
                post("/persons")
                        .content(objectMapper.writeValueAsString(person))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDeletePerson() throws Exception {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        person = personRepository.save(person);

        var response = this.mvc.perform(
                delete("/persons/" + person.getId())
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        assertTrue(personRepository.findAll().isEmpty());
    }


    @Test
    void testAddParent() throws Exception {

        var father = new Person();
        father.setFirstName("Bryan");
        father.setLastName("Carlson");
        father.setBirthday(LocalDate.now());

        var child = new Person();
        child.setFirstName("Nelson");
        child.setLastName("Cakes.Jr");
        child.setBirthday(LocalDate.now());

        father = personRepository.save(father);
        child = personRepository.save(child);

        var response = this.mvc.perform(
                put("/persons/" +child.getId()+ "/parents")
                        .content(objectMapper.writeValueAsString(father))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(2, personRepository.findAll().size());
        //assertTrue(child.getParents().contains(father));
       // personRepository.findWithParentsAndChildrenById(child.getId());
        //assertTrue(personRepository.findWithChildrenById(child.getId()).get().getParents().contains(father));

    }

    @Test
    void testAddThreeParents() throws Exception{

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

        personRepository.save(kid);
        personRepository.save(parent1);
        personRepository.save(parent2);
        personRepository.save(parent3);

        var response = this.mvc.perform(
                put("/persons/" +kid.getId()+ "/parents")
                        .content(objectMapper.writeValueAsString(parent1))
                        .content(objectMapper.writeValueAsString(parent2))
                        .contentType("application/json")
        ).andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(4, personRepository.findAll().size());



    }
}
