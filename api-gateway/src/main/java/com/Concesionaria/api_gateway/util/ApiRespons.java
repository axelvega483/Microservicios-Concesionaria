package com.Concesionaria.api_gateway.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiRespons<T> {

    private String mensaje;
    private T data;
    private boolean exito;


    public static <T> ApiRespons<T> ok(String mensaje, T data) {
        return new ApiRespons<>(mensaje, data, true);
    }

    public static <T> ApiRespons<T> error(String mensaje) {
        return new ApiRespons<>(mensaje, null, false);
    }
}
