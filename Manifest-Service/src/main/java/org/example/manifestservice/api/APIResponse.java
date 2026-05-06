package org.example.manifestservice.api;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class APIResponse<M> {
    private String message;
    private M data;
    private boolean success;
}
