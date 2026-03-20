package com.sensedia.consentapi.repository;

import com.sensedia.consentapi.domain.Consent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentRepository extends MongoRepository<Consent, String> {
        Optional<Consent> findByIdempotencyKey(String IdempotencyKey);
}
