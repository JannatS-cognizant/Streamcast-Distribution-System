package com.Cts.repository;
import com.Cts.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract,Long>
//<Contract,Long> second one "long" is datatype of the primary key
{
}
