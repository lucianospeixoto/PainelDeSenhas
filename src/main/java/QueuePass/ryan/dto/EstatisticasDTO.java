package QueuePass.ryan.dto;

import java.util.List;

public record EstatisticasDTO(
        long totalAguardando,
        long totalAtendidasHoje,
        long totalAtendimentos,
        double tempoMedioAtendimentoMinutos,
        double tempoMedioEsperaMinutos,
        GuicheAtendimentosDTO guicheMaisAtendimentos,
        List<GuicheAtendimentosDTO> atendimentosPorGuiche
) {
}