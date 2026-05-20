package org.example.clauseservice;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "Contract-Service")
public interface ContractClient {

}
