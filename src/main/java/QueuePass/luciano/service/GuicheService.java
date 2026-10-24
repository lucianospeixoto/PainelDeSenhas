package QueuePass.luciano.service;

import QueuePass.luciano.model.Guiche;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class GuicheService {

    private final Map<String, Guiche> guiches = new ConcurrentSkipListMap<>();

    public GuicheService() {
        adicionar("01", "Atendimento Geral");
        adicionar("02", "Atendimento Geral");
        adicionar("03", "Preferencial");
        adicionar("04", "Caixa");
        adicionar("05", "Empresarial");
    }

    public synchronized Guiche adicionar(String numero, String nome) {
        String numeroNormalizado = normalizarNumero(numero);
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Informe o nome do guichê.");
        }
        if (guiches.containsKey(numeroNormalizado)) {
            throw new IllegalArgumentException("Guichê " + numeroNormalizado + " já existe.");
        }
        Guiche guiche = new Guiche(numeroNormalizado, nome.trim());
        guiches.put(numeroNormalizado, guiche);
        return guiche;
    }

    public Guiche buscar(String numero) {
        Guiche guiche = guiches.get(normalizarNumero(numero));
        if (guiche == null) {
            throw new IllegalArgumentException("Guichê não cadastrado: " + numero);
        }
        return guiche;
    }

    public List<Guiche> listar() {
        return new ArrayList<>(guiches.values());
    }

    private String normalizarNumero(String numero) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Informe o número do guichê.");
        }
        String valor = numero.trim();
        return valor.matches("\\d{1,9}") ? String.format("%02d", Integer.parseInt(valor)) : valor.toUpperCase();
    }
}
