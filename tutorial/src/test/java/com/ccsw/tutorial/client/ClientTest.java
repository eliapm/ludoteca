package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {
    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    public void findAllShouldReturnAllClient() {
        List<Client> list = new ArrayList<>();
        list.add(mock(Client.class));

        when(clientRepository.findAll()).thenReturn(list);

        List<Client> clients = clientService.findAll();

        assertNotNull(clients);
        assertEquals(1, clients.size());
    }

    public static final String CLIENT_NAME = "CLIENT1";

    @Test
    public void saveNotExistsClientIdShouldInsert() {
        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);
        ArgumentCaptor<Client> client = ArgumentCaptor.forClass(Client.class);
        clientService.save(null, clientDto);
        verify(clientRepository).save(client.capture());
        assertEquals(CLIENT_NAME, client.getValue().getName());
    }

    public static final Long EXISTS_CLIENT_ID = 1L;

    @Test
    public void saveExistsClientIdShouldUpdate() {

        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.save(EXISTS_CLIENT_ID, clientDto);

        verify(clientRepository).save(client);
    }

    @Test
    public void deleteExistsClientIdShouldDelete() throws Exception {
        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        clientService.delete(EXISTS_CLIENT_ID);

        verify(clientRepository).deleteById(EXISTS_CLIENT_ID);
    }

    public static final Long NOT_EXISTS_CLIENT_ID = 0L;

    @Test
    public void getExistsClientIdShouldReturnClient() {
        Client client = mock(Client.class);
        when(client.getId()).thenReturn(EXISTS_CLIENT_ID);
        when(clientRepository.findById(EXISTS_CLIENT_ID)).thenReturn(Optional.of(client));

        Client clientResponse = clientService.get(EXISTS_CLIENT_ID);

        assertNotNull(clientResponse);
        assertEquals(EXISTS_CLIENT_ID, client.getId());
    }

    @Test
    public void getNotExistsClientIdShouldReturnNull() {
        when(clientRepository.findById(NOT_EXISTS_CLIENT_ID)).thenReturn(Optional.empty());

        Client client = clientService.get(NOT_EXISTS_CLIENT_ID);

        assertNull(client);
    }

    @Test
    public void saveAlreadyExistsClientNameShouldThrowException() {

        Long id = null;

        Client existingClient = new Client();
        existingClient.setId(1L);
        existingClient.setName(CLIENT_NAME);

        ClientDto newClientDto = new ClientDto();
        newClientDto.setName(existingClient.getName());

        when(clientRepository.findByName(CLIENT_NAME)).thenReturn(Optional.of(existingClient));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.save(id, newClientDto);
        });

        assertEquals("Ya existe un cliente con el nombre: " + CLIENT_NAME, exception.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }

    public static final String CLIENT_NAME_TO_UPDATE = "CLIENT2";

    @Test
    public void updateAlreadyExistsClientNameShouldThrowException() {
        Long existingClientId = 1L;
        Long updatingClientId = 2L;
        Client existingClient = new Client();
        existingClient.setId(existingClientId);
        existingClient.setName(CLIENT_NAME);

        ClientDto updatingClientDto = new ClientDto();
        updatingClientDto.setId(updatingClientId);
        updatingClientDto.setName(CLIENT_NAME_TO_UPDATE);

        Client updatingClient = new Client();
        updatingClient.setId(updatingClientId);
        updatingClient.setName(CLIENT_NAME_TO_UPDATE);

        when(clientRepository.findByName(CLIENT_NAME)).thenReturn(Optional.of(existingClient));

        updatingClientDto.setName(existingClient.getName());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.save(updatingClientId, updatingClientDto);
        });

        assertEquals("Ya existe un cliente con el nombre: " + CLIENT_NAME, exception.getMessage());
        verify(clientRepository, never()).save(any(Client.class));
    }
}
