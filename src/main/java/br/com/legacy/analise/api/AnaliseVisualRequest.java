package br.com.legacy.analise.api;

import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class AnaliseVisualRequest {

    @Size(max = 500)
    String objetivo;
}
