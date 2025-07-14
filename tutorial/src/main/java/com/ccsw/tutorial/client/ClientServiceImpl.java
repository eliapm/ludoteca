package com.ccsw.tutorial.client;

import com.ccsw.tutorial.client.model.Client;
import com.ccsw.tutorial.client.model.ClientDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public Client get(Long id) {
        return this.clientRepository.findById(id).orElse(null);

    }

    @Override
    public List<Client> findAll() {
        return (List<Client>) this.clientRepository.findAll();

    }

    @Override
    public void save(Long id, ClientDto dto) {
        Client client;

        if (id == null) {
            if (this.clientRepository.findByName(dto.getName()).isEmpty()) {
                client = new Client();
            } else {
                throw new IllegalArgumentException("Ya existe un cliente con el nombre: " + dto.getName());
            }
            //codigo para comprobar si ya hay algun cliente con ese mismo nombre
        } else {
            Optional<Client> clientHasThatName = this.clientRepository.findByName(dto.getName());
            if (clientHasThatName.isPresent() && !clientHasThatName.get().getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un cliente con el nombre: " + dto.getName());
            } else {
                client = this.get(id);

            }
        }

        client.setName(dto.getName());

        this.clientRepository.save(client);
    }

    @Override
    public void delete(Long id) throws Exception {
        if (this.get(id) == null) {
            throw new Exception("Not exists");
        }

        this.clientRepository.deleteById(id);
    }
}
