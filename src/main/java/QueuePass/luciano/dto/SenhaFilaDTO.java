package QueuePass.luciano.dto;

import QueuePass.luciano.model.Senha;

public record SenhaFilaDTO(
        Senha senha,
        long posicaoNaFila,
        double tempoEsperaEstimadoMinutos
) {
}