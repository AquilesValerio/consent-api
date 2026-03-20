package com.sensedia.consentapi.controller;

import com.sensedia.consentapi.service.ConsentService;
import com.sensedia.consentapi.dto.ConsentRequestDTO;
import com.sensedia.consentapi.dto.ConsentResponseDTO;
import com.sensedia.consentapi.dto.ConsentUpdateDTO;
import com.sensedia.consentapi.service.IdempotentResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(value = "/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService service;

    @PostMapping
    public ResponseEntity<ConsentResponseDTO> create(@RequestHeader("x-idempotency-key") String idempotencyKey,
            @Valid @RequestBody ConsentRequestDTO requestDTO){

        IdempotentResult response = service.create(requestDTO,idempotencyKey);

        if(!response.created()){
            return ResponseEntity.ok().body(response.data());
        }

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(response.data().id()).toUri();
        return ResponseEntity.created(uri).body(response.data());
    }

    @GetMapping
    public ResponseEntity<Page<ConsentResponseDTO>> findAllPaged(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy
    ) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        Page<ConsentResponseDTO> list = service.findAllPaged(pageRequest);

        return ResponseEntity.ok().body(list);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Optional<ConsentResponseDTO>> findById(@PathVariable String id){
        var response = service.findById(id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ConsentResponseDTO> update(@PathVariable String id,
                                                     @Valid@RequestBody ConsentUpdateDTO request){
        var response = service.update(id, request);
        return ResponseEntity.ok().body(response);
    }

}
