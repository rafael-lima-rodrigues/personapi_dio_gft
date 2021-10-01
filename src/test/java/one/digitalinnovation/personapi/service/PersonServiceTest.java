package one.digitalinnovation.personapi.service;

import one.digitalinnovation.personapi.dto.request.PersonDTO;
import one.digitalinnovation.personapi.dto.response.MessageResponseDTO;
import one.digitalinnovation.personapi.entity.Person;
import one.digitalinnovation.personapi.exceptions.PersonNotFoundException;
import one.digitalinnovation.personapi.repository.PersonMapper;
import one.digitalinnovation.personapi.repository.PersonRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static one.digitalinnovation.personapi.service.utils.PersonUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

    @Mock
    private PersonRespository personRespository;

    @Mock
    private PersonMapper personMapper;

    @InjectMocks
    private PersonService personService;

    @Test
    void testGivenPersonDTOThenReturnSavedMessage(){
        PersonDTO personDTO = createFakeDTO();
        Person expectedSavedPerson = createFakeEntity();
        when(personRespository.save(any(Person.class))).thenReturn(expectedSavedPerson);
        MessageResponseDTO expectedSuccessMessage = createExpectedMessageResponse(expectedSavedPerson.getId());
        MessageResponseDTO successMessage = personService.createPerson(personDTO);

        assertEquals(expectedSuccessMessage, successMessage);
    }

    @Test
    void testGivenValidPersonIdThenReturnThisPerson() throws PersonNotFoundException {
        PersonDTO expectedPersonDTO = findByIdFakeDTO();
        Person expectedSavedPerson = createFakeEntity();

        when(personRespository.findById(expectedSavedPerson.getId())).thenReturn(Optional.of(expectedSavedPerson));

        PersonDTO personDTO = personService.findById(expectedSavedPerson.getId());

        assertEquals(expectedPersonDTO, personDTO);

        assertEquals(expectedSavedPerson.getId(), personDTO.getId());
        assertEquals(expectedSavedPerson.getName(), personDTO.getName());
    }

    @Test
    void testGivenValidPersonIdThenDeleteThisPerson() throws PersonNotFoundException {
        var deletePersonId = 1L;
        Person expectedSavedPerson = createFakeEntity();

        when(personRespository.existsById(expectedSavedPerson.getId())).thenReturn((true));

        personService.delete(deletePersonId);
        verify(personRespository, times(1))
                .deleteById(deletePersonId);
    }

    @Test
    void testGivenInvalidPersonIdToDeleteReturnNotFound() throws PersonNotFoundException {
        var invalidPersonId = 1L;
        Person expectedSavedPerson = createFakeEntity();

        when(personRespository.existsById(expectedSavedPerson.getId())).thenReturn((false));

        assertThrows(PersonNotFoundException.class, () -> personService.delete(invalidPersonId));
    }

    private MessageResponseDTO createExpectedMessageResponse(Long id) {
        return MessageResponseDTO
                .builder()
                .message("Created person with ID: " + id)
                .build();
    }

}
