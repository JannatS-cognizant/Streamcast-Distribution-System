package com.Cts.api;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class APIResponse<T> {

          private String message;
          private T data;
          private boolean success;
    }
