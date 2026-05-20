package org.example.clauseservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

public class Contract {
    @Entity
    @Table(name = "contracts")
    public class ContractClient {

        @Id
        private Long id;
    }
}
