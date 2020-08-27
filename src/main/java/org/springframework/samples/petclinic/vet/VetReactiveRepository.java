package org.springframework.samples.petclinic.vet;

import java.util.UUID;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Component;

/**
 * Implementing reactive cruds for Vets.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Component
public interface VetReactiveRepository extends ReactiveCassandraRepository<Vet, UUID> {
    
}
