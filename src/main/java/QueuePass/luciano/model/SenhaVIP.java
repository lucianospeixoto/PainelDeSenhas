package QueuePass.luciano.model;

import QueuePass.luciano.model.Enum.PasswordStatus;
import QueuePass.luciano.model.Enum.PasswordType;

import java.time.LocalDateTime;

public class SenhaVIP extends Senha {
    public SenhaVIP(Long id, String code) {
        this.setId(id);
        this.setCode(code);
        this.setPasswordType(PasswordType.VIP);
        this.setPasswordStatus(PasswordStatus.AGUARDANDO);
        this.setCreatedAt(LocalDateTime.now());
    }
}
