package QueuePass.luciano.model;

import QueuePass.luciano.model.Enum.PasswordStatus;
import QueuePass.luciano.model.Enum.PasswordType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Senha {
    private Long id;
    private String code;
    private String nomeCliente;
    private PasswordType passwordType;
    private PasswordStatus passwordStatus;
    private LocalDateTime createdAt;
    private LocalDateTime calledAt;
    private LocalDateTime endDate;
    private String guiche;
    private String nomeGuiche;
}