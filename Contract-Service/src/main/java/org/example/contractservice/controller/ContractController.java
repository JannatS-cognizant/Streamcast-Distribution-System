package org.example.contractservice.controller;
import org.example.contractservice.api.APIResponse;
import org.example.contractservice.dto.request.ContractRequestDTO;
import org.example.contractservice.dto.response.ContractResponseDTO;
import org.example.contractservice.entity.Contract;
import org.example.contractservice.mapper.request.ContractRequestMapper;
import org.example.contractservice.mapper.response.ContractResponseMapper;
import org.example.contractservice.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    // ✅ CREATE
    @PostMapping
    public APIResponse<ContractResponseDTO> createContract(@RequestBody ContractRequestDTO dto) {

        Contract contract = ContractRequestMapper.toEntity(dto);
        Contract saved = contractService.createContract(contract);
        ContractResponseDTO response = ContractResponseMapper.toDTO(saved);

        return new APIResponse<>("Contract created successfully", response, true);
    }

    // ✅ GET ALL
    @GetMapping
    public APIResponse<List<ContractResponseDTO>> getAllContracts() {

        List<ContractResponseDTO> list = contractService.getContract()
                .stream()
                .map(ContractResponseMapper::toDTO)
                .collect(Collectors.toList());

        return new APIResponse<>("Contracts fetched successfully", list, true);
    }

    // ✅ UPDATE
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
    @DeleteMapping("/{id}")
    public APIResponse<String> deleteContract(@PathVariable Long id) {

        contractService.deleteById(id);
        return new APIResponse<>("Contract deleted successfully", "Success", true);
    }
}