package QueuePass.luciano.dto;

import QueuePass.luciano.model.Enum.PasswordType;

public record CreatePassword(
        PasswordType passwordType,
        String nomeCliente
) {
    public CreatePassword(PasswordType passwordType) {
        this(passwordType, null);
    }
}
