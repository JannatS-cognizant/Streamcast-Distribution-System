package com.cts.complianceservice.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse<T> {
    private String status;
    private String message;
    private T data;

    public static <T> APIResponse<T> success(String message, T data) {
        return new APIResponse<>("success", message, data);
    }

    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>("error", message, null);
    }
}