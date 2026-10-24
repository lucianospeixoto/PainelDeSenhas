package QueuePass.luciano.model;

import QueuePass.luciano.model.Enum.PasswordStatus;
import QueuePass.luciano.model.Enum.PasswordType;

import java.time.LocalDateTime;

public class SenhaComum extends Senha {
    public SenhaComum(Long id, String code) {
        this.setId(id);
        this.setCode(code);
        this.setPasswordType(PasswordType.COMUM);
        this.setPasswordStatus(PasswordStatus.AGUARDANDO);
        this.setCreatedAt(LocalDateTime.now());
    }
}
