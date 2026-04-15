package com.Cts.service;
import com.Cts.entity.Contract;
import com.Cts.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    public Contract createContract(Contract contract){
        return contractRepository.save(contract);
    }

    public List<Contract> getContract() {
        return contractRepository.findAll();
    }

    public Contract updateContract(Long id, Contract contract) {

        Contract existing=contractRepository.findById(id).orElseThrow(()->new RuntimeException("Contract Not Found"));
        existing.setTitleId(contract.getTitleId());
        existing.setExclusivityFlag(contract.getExclusivityFlag());
        existing.setTermsSummary(contract.getTermsSummary());
        existing.setTerritoryListJson(contract.getTerritoryListJson());
        existing.setStartDate(contract.getStartDate());
        existing.setEndDate(contract.getEndDate());
        existing.setStatus(contract.getStatus());
        return contractRepository.save(existing);
    }

    public void deleteById(Long id) {
        contractRepository.deleteById(id);
    }

}
