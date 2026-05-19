package org.example.contractservice.controller;
import org.example.contractservice.api.APIResponse;
import org.example.contractservice.dto.request.ContractRequestDTO;
import org.example.contractservice.dto.response.ContractResponseDTO;
import org.example.contractservice.entity.Contract;
import org.example.contractservice.mapper.request.ContractRequestMapper;
import org.example.contractservice.mapper.response.ContractResponseMapper;
import org.example.contractservice.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    // ✅ CREATE
    @PreAuthorize("hasAnyRole('ADMIN','RIGHTS_MANAGER')")
    @PostMapping
    public APIResponse<ContractResponseDTO> createContract(@RequestBody ContractRequestDTO dto) {

        Contract contract = ContractRequestMapper.toEntity(dto);
        Contract saved = contractService.saveContract(contract);
        ContractResponseDTO response = ContractResponseMapper.toDTO(saved);

        return new APIResponse<>("Contract created successfully", response, true);
    }

    // ✅ GET ALL
    @PreAuthorize("hasAnyRole('ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER')")
    @GetMapping
    public APIResponse<List<ContractResponseDTO>> getAllContracts() {

        List<ContractResponseDTO> list = contractService.getContract()
                .stream()
                .map(ContractResponseMapper::toDTO)
                .collect(Collectors.toList());

        return new APIResponse<>("Contracts fetched successfully", list, true);
    }

    // ✅ UPDATE
    @PreAuthorize("hasAnyRole('ADMIN','RIGHTS_MANAGER')")
    @PutMapping("/{id}")
    public APIResponse<ContractResponseDTO> updateContract(
            @PathVariable Long id,
            @RequestBody ContractRequestDTO dto) {

        Contract contract = ContractRequestMapper.toEntity(dto);
        Contract updated = contractService.updateContract(id, contract);
        ContractResponseDTO response = ContractResponseMapper.toDTO(updated);

        return new APIResponse<>("Contract updated successfully", response, true);
    }

    // ✅ DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public APIResponse<String> deleteContract(@PathVariable Long id) {

        contractService.deleteById(id);
        return new APIResponse<>("Contract deleted successfully", "Success", true);
    }
    //Get by Id
    @PreAuthorize("hasAnyRole('ADMIN','RIGHTS_MANAGER','SCHEDULER','COMPLIANCE_OFFICER')")
    @GetMapping("/{id}")
    public APIResponse<ContractResponseDTO> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return new APIResponse<>("Contract fetched successfully", ContractResponseMapper.toDTO(contract), true);
    }
}
