package org.springframework.samples.petclinic.owner;

import java.util.UUID;

import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Component;

/**
 * Implementing reactive cruds for Owners
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Component
public interface OwnerReactiveRepository extends ReactiveCassandraRepository<Owner, UUID> {}
