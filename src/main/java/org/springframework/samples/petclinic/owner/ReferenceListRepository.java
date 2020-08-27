package org.springframework.samples.petclinic.owner;

import java.util.Set;

import org.springframework.data.cassandra.core.cql.ReactiveCqlTemplate;
import org.springframework.data.cassandra.core.cql.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

import reactor.core.publisher.Mono;

/**
 * Reference lists are grouped in a dedicated table not levering the
 * mapping object <=> data here.
 * 
 * CREATE TABLE IF NOT EXISTS petclinic_reference_lists (
 *   list_name text,
 *   values set<text>,
 *   PRIMARY KEY ((list_name))
 * );
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Component
public class ReferenceListRepository {
    
    /** Constants in the DB. */
    public static final String PET_TYPE      = "pet_type";
    public static final String VET_SPECIALTY = "vet_specialty";
    
    private CqlSession          cqlSession          = null;
    private ReactiveCqlTemplate reactiveCqlTemplate = null;
    
    private PreparedStatement   psReadList   = null;  
    private PreparedStatement   psUpsertList = null;  
    
    /**
     * Table create could be synchronous here.
     *
     * @param cqlT
     *      cql template
     * @param reactCqlT
     *       cql template reactive
     */
    public ReferenceListRepository(CqlSession cqlS, ReactiveCqlTemplate reactCqlT) {
        this.cqlSession          = cqlS;
        this.reactiveCqlTemplate = reactCqlT;
        
        // Create table if not exist
        cqlSession.execute(SchemaBuilder.createTable("petclinic_reference_lists")
                .ifNotExists()
                .withPartitionKey("list_name", DataTypes.TEXT)
                .withColumn("values", DataTypes.setOf(DataTypes.TEXT))
                .build());
        
        // Prepare once, execute multiple times
        psReadList = cqlSession.prepare(QueryBuilder.selectFrom("petclinic_reference_lists").column("values")
                    .whereColumn("list_name")
                    .isEqualTo(QueryBuilder.bindMarker())
                    .build());
        
        psUpsertList = cqlSession.prepare(QueryBuilder.insertInto("petclinic_reference_lists")
                .value("list_name", QueryBuilder.bindMarker())
                .value("values", QueryBuilder.bindMarker())
                .build());
    }
        
    public Mono<Boolean> saveList(String list, Set<String> values) {
        return reactiveCqlTemplate.execute(psUpsertList.bind(list, values));
    }
    
    public Mono<Set<String>> listPetType() {
        return findReferenceList(PET_TYPE);
    }
    
    public Mono<Set<String>>listVeretinianSpecialties() {
        return findReferenceList(VET_SPECIALTY);
    }
    
    protected Mono<Set<String>> findReferenceList(String listName) {
        return reactiveCqlTemplate.queryForObject(
                psReadList.bind(listName),                  // PreparedStatement + var = BoundStatement 
                new SingleColumnRowMapper<Set<String>>());  // values if present
    }

}
