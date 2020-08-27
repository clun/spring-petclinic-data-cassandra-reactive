package org.springframework.samples.petclinic.owner;

import java.util.UUID;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple JavaBean domain object representing an owner.s
 */
@Data 
@AllArgsConstructor
@NoArgsConstructor
@Table("petclinic_owner")
public class Owner {

    @PrimaryKey
    @NotEmpty
    private UUID id;
    
    @Column("first_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String firstName;

    @Column("last_name")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String lastName;

    @Column("address")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String address;

    @Column("city")
    @CassandraType(type = CassandraType.Name.TEXT)
    private String city;
    
    @Column("telephone")
    @Digits(fraction = 0, integer = 10)
    @CassandraType(type = CassandraType.Name.TEXT)
    private String telephone;
    
    public Owner(String uid) {
        this.id = UUID.fromString(uid);
    }

}
