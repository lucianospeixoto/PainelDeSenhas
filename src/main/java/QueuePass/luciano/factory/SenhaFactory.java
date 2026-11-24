package QueuePass.luciano.factory;

import QueuePass.luciano.dto.CreatePassword;
import QueuePass.luciano.model.Enum.PasswordType;
import QueuePass.luciano.model.Senha;
import QueuePass.luciano.model.Enum.PasswordStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SenhaFactory {

    private final Map<PasswordType, SenhaCreator> creators;
    private final Map<PasswordType, AtomicLong> contadores = new EnumMap<>(PasswordType.class);

    public SenhaFactory(List<SenhaCreator> senhaCreators) {
        this.creators = senhaCreators.stream()
                .collect(Collectors.toMap(SenhaCreator::getTipo, Function.identity()));
        for (PasswordType tipo : PasswordType.values()) contadores.put(tipo, new AtomicLong(1));
    }

    public Senha criarSenha(PasswordType tipo, Long id) {
        SenhaCreator creator = creators.get(tipo);
        if (creator == null) {
            Senha senha = new Senha();
            senha.setId(id);
            senha.setCode(prefixo(tipo) + String.format("%03d", contadores.get(tipo).getAndIncrement()));
            senha.setPasswordType(tipo);
            senha.setPasswordStatus(PasswordStatus.AGUARDANDO);
            senha.setCreatedAt(LocalDateTime.now());
            return senha;
        }
        return creator.criar(id);
    }

    public Senha criarSenha(CreatePassword request, Long id) {
        return criarSenha(request.passwordType(), id);
    }

    public SenhaCreator getCreator(PasswordType tipo) {
        SenhaCreator creator = creators.get(tipo);
        if (creator == null) {
            throw new IllegalArgumentException("Tipo de senha não suportado: " + tipo);
        }
        return creator;
    }

    public void resetar() {
        creators.values().forEach(SenhaCreator::resetar);
        contadores.values().forEach(contador -> contador.set(1));
    }

    private String prefixo(PasswordType tipo) {
        return switch (tipo) {
            case PREFERENCIAL -> "P";
            case PRIORIDADE_80 -> "O";
            case AGENDADO -> "A";
            case URGENTE -> "U";
            default -> throw new IllegalArgumentException("Tipo de senha não suportado: " + tipo);
        };
    }
}
