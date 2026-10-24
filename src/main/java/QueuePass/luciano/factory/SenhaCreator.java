package QueuePass.luciano.factory;

import QueuePass.luciano.model.Enum.PasswordType;
import QueuePass.luciano.model.Senha;

public interface SenhaCreator {

    Senha criar(Long id);

    PasswordType getTipo();

    void resetar();
}
