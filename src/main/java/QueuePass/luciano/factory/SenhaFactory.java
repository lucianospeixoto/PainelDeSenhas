package QueuePass.luciano.factory;

import QueuePass.luciano.dto.CreatePassword;
import QueuePass.luciano.model.Enum.PasswordType;
import QueuePass.luciano.model.Senha;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SenhaFactory {

    private final Map<PasswordType, SenhaCreator> creators;

    public SenhaFactory(List<SenhaCreator> senhaCreators) {
        this.creators = senhaCreators.stream()
                .collect(Collectors.toMap(SenhaCreator::getTipo, Function.identity()));
    }

    public Senha criarSenha(PasswordType tipo, Long id) {
        SenhaCreator creator = creators.get(tipo);
        if (creator == null) {
            throw new IllegalArgumentException("Tipo de senha não suportado: " + tipo);
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
    }
}
