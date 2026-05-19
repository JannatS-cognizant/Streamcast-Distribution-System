package org.example.clauseservice.repository;
import org.example.clauseservice.entity.Clause;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClauseRepository extends JpaRepository<Clause, Long> {
}
