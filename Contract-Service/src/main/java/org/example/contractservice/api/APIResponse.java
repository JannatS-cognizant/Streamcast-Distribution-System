package org.example.contractservice.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class APIResponse<C> {

    private String message;
    private C data;
    private boolean success;
}