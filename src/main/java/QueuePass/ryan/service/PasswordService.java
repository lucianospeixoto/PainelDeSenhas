package QueuePass.ryan.service;

import QueuePass.ryan.dto.CreatePassword;
import QueuePass.ryan.dto.EstatisticasDTO;
import QueuePass.ryan.dto.GuicheAtendimentosDTO;
import QueuePass.ryan.dto.SenhaFilaDTO;
import QueuePass.ryan.factory.SenhaFactory;
import QueuePass.ryan.model.Enum.PasswordStatus;
import QueuePass.ryan.model.Enum.PasswordType;
import QueuePass.ryan.model.Guiche;
import QueuePass.ryan.model.Senha;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final SenhaFactory senhaFactory;

    private final GuicheService guicheService;

    private final AtomicLong idGenerator = new AtomicLong(0);

    private final List<Senha> senhas = new CopyOnWriteArrayList<>();

    private final List<Long> temposAtendimentoSegundos = new CopyOnWriteArrayList<>();

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public synchronized Senha criarSenha(CreatePassword request) {
        Long id = idGenerator.getAndIncrement();
        Senha senha = senhaFactory.criarSenha(request, id);
        if (request.nomeCliente() != null && !request.nomeCliente().isBlank()) {
            senha.setNomeCliente(request.nomeCliente().trim());
        }
        senhas.add(senha);
        notificarClientes("senha-criada");
        return senha;
    }

    private int getPrioridadePeso(Senha s) {
        return switch (s.getPasswordType()) {
            case IDOSO -> 0;
            case VIP -> 1;
            case COMUM -> 2;
        };
    }

    public synchronized Senha chamarProximaSenha(String numeroGuiche) {
        Guiche guiche = guicheService.buscar(numeroGuiche);

        Senha proxima = senhas.stream()
                .filter(s -> s.getPasswordStatus() == PasswordStatus.AGUARDANDO)
                .sorted(Comparator.comparing(this::getPrioridadePeso)
                        .thenComparing(Senha::getCreatedAt))
                .findFirst()
                .orElse(null);

        if (proxima != null) {
            proxima.setPasswordStatus(PasswordStatus.CHAMADA);
            proxima.setCalledAt(java.time.LocalDateTime.now());
            proxima.setGuiche(guiche.numero());
            proxima.setNomeGuiche(guiche.nome());
            notificarClientes("senha-chamada");
        }

        return proxima;
    }

    public synchronized Senha finalizarSenha(Long id) {
        Senha senha = buscarPorId(id);
        if (senha != null) {
            senha.setPasswordStatus(PasswordStatus.FINALIZADA);
            senha.setEndDate(java.time.LocalDateTime.now());

            if (senha.getCalledAt() != null) {
                long segundos = Duration.between(senha.getCalledAt(), senha.getEndDate()).getSeconds();
                temposAtendimentoSegundos.add(segundos);
            }

            notificarClientes("senha-finalizada");
        }
        return senha;
    }

    public synchronized Senha cancelarSenha(Long id) {
        Senha senha = buscarPorId(id);
        if (senha != null) {
            senha.setPasswordStatus(PasswordStatus.CANCELADA);
            senha.setEndDate(java.time.LocalDateTime.now());
            notificarClientes("senha-cancelada");
        }
        return senha;
    }

    public Senha buscarPorId(Long id) {
        return senhas.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<Senha> listarAguardando() {
        return senhas.stream()
                .filter(s -> s.getPasswordStatus() == PasswordStatus.AGUARDANDO)
                .sorted(Comparator.comparing(this::getPrioridadePeso)
                        .thenComparing(Senha::getCreatedAt))
                .collect(Collectors.toList());
    }

    public List<Senha> listarChamadas() {
        return senhas.stream()
                .filter(s -> s.getPasswordStatus() == PasswordStatus.CHAMADA)
                .sorted(Comparator.comparing(Senha::getCalledAt).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Senha> listarTodas() {
        return senhas.stream()
                .sorted(Comparator.comparing(Senha::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public synchronized void resetar() {
        senhas.clear();
        temposAtendimentoSegundos.clear();
        idGenerator.set(0);
        senhaFactory.resetar();
        notificarClientes("sistema-resetado");
    }

    public List<SenhaFilaDTO> listarFilaComEstimativa() {
        List<Senha> fila = listarAguardando();
        double tempoMedioAtendimento = calcularTempoMedioAtendimentoMinutos();

        List<SenhaFilaDTO> resultado = new ArrayList<>();
        for (int i = 0; i < fila.size(); i++) {
            long posicao = i + 1;
            double estimativa = posicao * tempoMedioAtendimento;
            resultado.add(new SenhaFilaDTO(fila.get(i), posicao, arredondar(estimativa)));
        }
        return resultado;
    }

    private double calcularTempoMedioAtendimentoMinutos() {
        if (temposAtendimentoSegundos.isEmpty()) {
            return 3.0;
        }
        double mediaSegundos = temposAtendimentoSegundos.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(180);
        return mediaSegundos / 60.0;
    }

    public EstatisticasDTO obterEstatisticas() {
        long aguardando = senhas.stream()
                .filter(s -> s.getPasswordStatus() == PasswordStatus.AGUARDANDO)
                .count();

        long atendidas = senhas.stream()
                .filter(s -> s.getPasswordStatus() == PasswordStatus.FINALIZADA)
                .count();

        double tempoMedioAtendimento = arredondar(calcularTempoMedioAtendimentoMinutos());
        double tempoMedioEspera = calcularTempoMedioEsperaReal();

        List<GuicheAtendimentosDTO> porGuiche = calcularAtendimentosPorGuiche();
        long totalAtendimentos = porGuiche.stream()
                .mapToLong(GuicheAtendimentosDTO::totalAtendimentos)
                .sum();
        GuicheAtendimentosDTO guicheMaisAtendimentos = porGuiche.stream()
                .filter(g -> g.totalAtendimentos() > 0)
                .findFirst()
                .orElse(null);

        return new EstatisticasDTO(aguardando, atendidas, totalAtendimentos, tempoMedioAtendimento,
                tempoMedioEspera, guicheMaisAtendimentos, porGuiche);
    }

    public List<Guiche> listarGuiches() {
        return guicheService.listar();
    }

    public synchronized Guiche adicionarGuiche(String numero, String nome) {
        Guiche guiche = guicheService.adicionar(numero, nome);
        notificarClientes("guiche-adicionado");
        return guiche;
    }

    // Atendimento = senha que foi chamada para um guichê (exceto as canceladas).
    // Lista ordenada do guichê que mais atendeu para o que menos atendeu.
    private List<GuicheAtendimentosDTO> calcularAtendimentosPorGuiche() {
        Map<String, Long> contagem = senhas.stream()
                .filter(s -> s.getGuiche() != null && s.getPasswordStatus() != PasswordStatus.CANCELADA)
                .collect(Collectors.groupingBy(Senha::getGuiche, Collectors.counting()));

        return guicheService.listar().stream()
                .map(g -> new GuicheAtendimentosDTO(g.numero(), g.nome(), contagem.getOrDefault(g.numero(), 0L)))
                .sorted(Comparator.comparingLong(GuicheAtendimentosDTO::totalAtendimentos).reversed()
                        .thenComparing(GuicheAtendimentosDTO::numero))
                .collect(Collectors.toList());
    }

    private double calcularTempoMedioEsperaReal() {
        List<Senha> chamadasOuFinalizadas = senhas.stream()
                .filter(s -> s.getCalledAt() != null)
                .collect(Collectors.toList());

        if (chamadasOuFinalizadas.isEmpty()) return 0.0;

        double mediaSegundos = chamadasOuFinalizadas.stream()
                .mapToLong(s -> Duration.between(s.getCreatedAt(), s.getCalledAt()).getSeconds())
                .average()
                .orElse(0);

        return arredondar(mediaSegundos / 60.0);
    }

    private double arredondar(double valor) {
        return Math.round(valor * 10.0) / 10.0;
    }

    public SseEmitter registrarEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((e) -> emitters.remove(emitter));
        try {
            // Evento inicial para o navegador receber a resposta e confirmar a conexão imediatamente
            emitter.send(SseEmitter.event().name("conectado").data("ok"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }
        return emitter;
    }

    private void notificarClientes(String evento) {
        List<SseEmitter> mortos = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(evento).data("update"));
            } catch (IOException e) {
                mortos.add(emitter);
            }
        }
        emitters.removeAll(mortos);
    }
}