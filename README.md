# Totem Hospital Peixoto&Moscardini

Sistema web de gerenciamento de filas e chamadas para atendimento hospitalar. A aplicação usa Java, Spring Boot e o padrão Factory Method para gerar senhas por categoria.

## Telas

- **Recepção:** acompanha os pacientes aguardando, a chamada atual e os guichês em atendimento.
- **Totem do paciente:** coleta o nome, permite escolher a categoria e emite a senha.
- **Monitor de chamadas:** exibe a senha chamada e pode anunciá-la por voz.

## Categorias de atendimento

- **Normal:** atendimento geral.
- **Preferencial:** idosos, gestantes, pessoas com deficiência e pessoas autistas.
- **Prioridade 80+:** pessoas idosas com 80 anos ou mais.
- **Agendado:** check-in de consultas ou exames marcados.
- **Urgente:** prioridade máxima na fila. Em caso de emergência médica, procure a equipe do hospital imediatamente.

## Acesso online

| Tela | Endereço |
| --- | --- |
| Recepção | [totemhospital.moscardini.com.br](https://totemhospital.moscardini.com.br/) |
| Totem do paciente | [totemhospital.moscardini.com.br/totem.html](https://totemhospital.moscardini.com.br/totem.html) |
| Monitor de chamadas | [totemhospital.moscardini.com.br/monitor.html](https://totemhospital.moscardini.com.br/monitor.html) |

## Requisitos

- Java 17 ou superior.
- Git, para clonar o repositório.
- Maven Wrapper incluído no projeto; não é necessário instalar Maven separadamente.

## Executar localmente

Clone o repositório:

```bash
git clone https://github.com/lucianospeixoto/PainelDeSenhas.git
cd PainelDeSenhas
```

Inicie a aplicação:

**Windows (PowerShell):**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux ou macOS:**

```bash
./mvnw spring-boot:run
```

O servidor usa a porta **8081**. Acesse as telas localmente:

| Tela | Endereço local |
| --- | --- |
| Recepção | [http://localhost:8081/](http://localhost:8081/) |
| Totem do paciente | [http://localhost:8081/totem.html](http://localhost:8081/totem.html) |
| Monitor de chamadas | [http://localhost:8081/monitor.html](http://localhost:8081/monitor.html) |

Para compilar e executar os testes:

```powershell
.\mvnw.cmd clean package
```

## API principal

A API está sob `/api`. Entre os endpoints usados pelas telas:

- `POST /api/gerar-senha` — cria uma senha para a categoria selecionada.
- `GET /api/senhas/aguardando/detalhado` — lista a fila aguardando.
- `POST /api/chamar-senha` — chama a próxima senha para um guichê.
- `GET /api/eventos` — transmite atualizações em tempo real via Server-Sent Events.

## Tecnologias e padrões

- Java 17 e Spring Boot.
- Spring MVC e API REST.
- Maven Wrapper.
- Factory Method para criação de senhas por categoria.
- Server-Sent Events (SSE) para atualização dos painéis.

## Integrantes

- Luciano S. Peixoto
- Ryan Oliveira Moscardini
- Caue Vinicius
- Miguel de Paula Arantes
- Felipe de Oliveira Souza
