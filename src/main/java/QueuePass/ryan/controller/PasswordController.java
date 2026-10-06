package QueuePass.ryan.controller;

import QueuePass.ryan.dto.CallPasswordRequest;
import QueuePass.ryan.dto.CreateGuicheRequest;
import QueuePass.ryan.dto.CreatePassword;
import QueuePass.ryan.dto.EstatisticasDTO;
import QueuePass.ryan.dto.SenhaFilaDTO;
import QueuePass.ryan.model.Guiche;
import QueuePass.ryan.model.Senha;
import QueuePass.ryan.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@CrossOrigin(origins = "*")
public class PasswordController {

    private final PasswordService passwordService;

    @PostMapping("/gerar-senha")
    public ResponseEntity<Senha> criarSenha(@RequestBody CreatePassword createPassword) {
        if (createPassword.nomeCliente() == null || createPassword.nomeCliente().isBlank()) {
            throw new IllegalArgumentException("Informe o nome da pessoa para gerar a senha.");
        }
        Senha senha = passwordService.criarSenha(createPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(senha);
    }

    @PostMapping("/chamar-senha")
    public ResponseEntity<Senha> chamarProxima(@RequestBody CallPasswordRequest request) {
        Senha senha = passwordService.chamarProximaSenha(request.guiche());
        if (senha == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(senha);
    }

    @PutMapping("/senhas/{id}/finalizar")
    public ResponseEntity<Senha> finalizar(@PathVariable Long id) {
        Senha senha = passwordService.finalizarSenha(id);
        if (senha == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(senha);
    }

    @PutMapping("/senhas/{id}/cancelar")
    public ResponseEntity<Senha> cancelar(@PathVariable Long id) {
        Senha senha = passwordService.cancelarSenha(id);
        if (senha == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(senha);
    }

    @GetMapping("/senhas/aguardando")
    public List<Senha> listarAguardando() {
        return passwordService.listarAguardando();
    }

    @GetMapping("/senhas/aguardando/detalhado")
    public List<SenhaFilaDTO> listarFilaComEstimativa() {
        return passwordService.listarFilaComEstimativa();
    }

    @GetMapping("/senhas/chamadas")
    public List<Senha> listarChamadas() {
        return passwordService.listarChamadas();
    }

    @GetMapping("/senhas")
    public List<Senha> listarTodas() {
        return passwordService.listarTodas();
    }

    @GetMapping("/estatisticas")
    public EstatisticasDTO obterEstatisticas() {
        return passwordService.obterEstatisticas();
    }

    @GetMapping("/guiches")
    public List<Guiche> listarGuiches() {
        return passwordService.listarGuiches();
    }

    @PostMapping("/guiches")
    public ResponseEntity<Guiche> adicionarGuiche(@RequestBody CreateGuicheRequest request) {
        Guiche guiche = passwordService.adicionarGuiche(request.numero(), request.nome());
        return ResponseEntity.status(HttpStatus.CREATED).body(guiche);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarErroValidacao(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
    }

    @PostMapping("/senhas/resetar")
    public ResponseEntity<Void> resetar() {
        passwordService.resetar();
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/eventos", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter eventos() {
        return passwordService.registrarEmitter();
    }
}