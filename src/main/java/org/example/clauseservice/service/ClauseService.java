package org.example.clauseservice.service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.clauseservice.entity.Clause;
import org.example.clauseservice.repository.ClauseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClauseService {


    @Autowired
    private ClauseRepository clauseRepository;


    @CircuitBreaker(name="ClauseService",fallbackMethod = "fallbackClause")
    //fallback method
    public String getClauseDetails(Long id) {
        return "Fallback response: Clause service is down";
    }
    public Clause addClauses(Clause clause){
        return clauseRepository.save(clause);
    }

    public List<Clause> getAllClauses(){
        return clauseRepository.findAll();
    }


}
