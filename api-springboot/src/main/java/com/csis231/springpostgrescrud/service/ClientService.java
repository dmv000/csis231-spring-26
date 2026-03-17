package com.csis231.springpostgrescrud.service;

import com.csis231.springpostgrescrud.dto.ClientDto;
import com.csis231.springpostgrescrud.entity.Client;
import com.csis231.springpostgrescrud.exeption.ResourceNotFoundException;
import com.csis231.springpostgrescrud.repository.ClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClientService {

    private ClientRepository clientRepository;

    public ClientDto createClient(ClientDto clientDto) {

        if (clientRepository.existsByEmail(clientDto.getEmail())) {
            throw new RuntimeException("Client already exists with email: " + clientDto.getEmail());
        }

        Client client = new Client();
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());

        Client savedClient = clientRepository.save(client);

        return new ClientDto(
                savedClient.getId(),
                savedClient.getFirstName(),
                savedClient.getLastName(),
                savedClient.getEmail(),
                savedClient.getPhone(),
                savedClient.getAddress()
        );
    }

    public ClientDto getClientById(Long id) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        return new ClientDto(
                client.getId(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getPhone(),
                client.getAddress()
        );
    }

    public List<ClientDto> getAllClients() {

        List<Client> clients = clientRepository.findAll();

        return clients.stream()
                .map(client -> new ClientDto(
                        client.getId(),
                        client.getFirstName(),
                        client.getLastName(),
                        client.getEmail(),
                        client.getPhone(),
                        client.getAddress()
                ))
                .collect(Collectors.toList());
    }

    public ClientDto updateClient(Long id, ClientDto clientDto) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setPhone(clientDto.getPhone());
        client.setAddress(clientDto.getAddress());

        Client updatedClient = clientRepository.save(client);

        return new ClientDto(
                updatedClient.getId(),
                updatedClient.getFirstName(),
                updatedClient.getLastName(),
                updatedClient.getEmail(),
                updatedClient.getPhone(),
                updatedClient.getAddress()
        );
    }

    public void deleteClient(Long id) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));

        clientRepository.delete(client);
    }
}