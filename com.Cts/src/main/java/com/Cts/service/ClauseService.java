package com.Cts.service;
import com.Cts.entity.Clause;
import com.Cts.repository.ClauseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClauseService {


    @Autowired
    private ClauseRepository clauseRepository;


    public Clause addClauses(Clause clause){
        return clauseRepository.save(clause);
    }

    public List<Clause> getAllClauses(){
        return clauseRepository.findAll();
    }


}
