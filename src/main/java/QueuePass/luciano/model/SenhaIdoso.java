package QueuePass.luciano.model;

import QueuePass.luciano.model.Enum.PasswordStatus;
import QueuePass.luciano.model.Enum.PasswordType;

import java.time.LocalDateTime;

public class SenhaIdoso extends Senha {
    public SenhaIdoso(Long id, String code) {
        this.setId(id);
        this.setCode(code);
        this.setPasswordType(PasswordType.IDOSO);
        this.setPasswordStatus(PasswordStatus.AGUARDANDO);
        this.setCreatedAt(LocalDateTime.now());
    }
}
