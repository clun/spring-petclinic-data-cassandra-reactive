package org.springframework.samples.petclinic.conf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.samples.petclinic.owner.ReferenceListRepository;
import org.springframework.samples.petclinic.vet.Vet;
import org.springframework.samples.petclinic.vet.VetReactiveRepository;
import org.springframework.stereotype.Component;

import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

/**
 * After application is (Spring Data would have created the tables if needed)
 * we want to insert some values. We can use a listern on {@link ApplicationReadyEvent}.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Component
public class CassandraDataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    /** Logger for the class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDataInitializer.class);
    
    /** Injecting through contructor. */
    private final VetReactiveRepository   vetRepository;
    private final ReferenceListRepository refRepository;
    private final ReactiveCqlTemplate     reactiveCqlTemplate;

    public CassandraDataInitializer(VetReactiveRepository repository, 
            ReferenceListRepository refListrepo, ReactiveCqlTemplate cqlR) {
        this.vetRepository = repository;
        this.refRepository = refListrepo;
        this.reactiveCqlTemplate = cqlR;
    }
    
    /** {@inheritDoc} */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("Populating data sample and indexes");
        /* 
         * After owner table creation to search on 'lastname'
         * BUT: lastname is NOT part of the primay key
         * SO: we could use ALLOW FILTERING (nooooooo = I don't know what I am doing)  
         * SO: we create a secondary index
         */
        reactiveCqlTemplate.execute(SchemaBuilder.createIndex("petclinic_idx_ownername")
                .ifNotExists()
                .onTable("petclinic_owner")
                .andColumn("last_name")
                .build()).subscribe();
        LOGGER.info("+ Index '{}' has been created", "petclinic_idx_ownername");
        reactiveCqlTemplate.execute(SchemaBuilder.createIndex("petclinic_idx_vetname")
                .ifNotExists()
                .onTable("petclinic_vet")
                .andColumn("last_name")
                .build()).subscribe();
        LOGGER.info("+ Index '{}' has been created", "petclinic_idx_vetname");
        
        refRepository.saveList(ReferenceListRepository.VET_SPECIALTY, new HashSet<>(
                        Arrays.asList("dentistry", "radiology", "surgery")))
                     .subscribe();
        refRepository.saveList(ReferenceListRepository.PET_TYPE, new HashSet<>(
                        Arrays.asList("bird", "cat", "dog", "hamster", "lizard", "snake")))
                     .subscribe();
        
        vetRepository.save(new Vet(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "James", "Carter", new HashSet<String>())).subscribe();
        vetRepository.save(new Vet(UUID.fromString("22222222-2222-2222-2222-222222222222"), 
                "Helen", "Leary", new HashSet<String>(Arrays.asList("radiology")))).subscribe();
        vetRepository.save(new Vet(UUID.fromString("33333333-3333-3333-3333-333333333333"), 
                "Linda", "Douglas", new HashSet<String>(Arrays.asList("dentistry", "surgery")))).subscribe();
        vetRepository.save(new Vet(UUID.fromString("44444444-4444-4444-4444-444444444444"), 
                "Rafael", "Ortega", new HashSet<String>(Arrays.asList("surgery")))).subscribe();
        vetRepository.save(new Vet(UUID.fromString("55555555-5555-5555-5555-555555555555"),
                "Henry", "Stevens", new HashSet<String>(Arrays.asList("radiology")))).subscribe();
        vetRepository.save(new Vet(UUID.fromString("66666666-6666-6666-6666-666666666666"),
                "Sharon", "Jenkins", new HashSet<String>())).subscribe();
        LOGGER.info("+ Tables have been populated.");
        LOGGER.info("[OK] Database is ready.");
    }

}