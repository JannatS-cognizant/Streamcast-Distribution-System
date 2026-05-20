package org.example.contractservice.service;
import io.github.resilience4j.retry.annotation.Retry;
import org.example.contractservice.client.ContentCatalogClient;
import org.example.contractservice.entity.Contract;
import org.example.contractservice.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContentCatalogClient client;

    @CircuitBreaker(name = "contractService" ,fallbackMethod = "fallbackContract")
    @Retry(name = "contractService")
    //fallback method
    public String getContractDetails(Long id){
        return "Fallback response:Contract service is down";
    }
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

    public Contract getContractById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found with id: " + id));
    }

    public Contract saveContract(Contract contract){
        Object title=client.getTitleById((int) contract.getTitleId());
        System.out.println(title);
        return contractRepository.save(contract);
    }

}
