package org.example.contractservice.repository;
import org.example.contractservice.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract,Long>
//<Contract,Long> second one "long" is datatype of the primary key
{
}
