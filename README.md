# PainelDeSenhasFactory

Sistema de gerenciamento e chamada de senhas desenvolvido em Java com Spring Boot, utilizando o padrão de projeto **Factory Method** para a criação e gerenciamento de diferentes categorias de atendimento.

O projeto simula o funcionamento de um sistema de filas de atendimento hospitalar, permitindo trabalhar com três modalidades de senha: **Comum, Idoso e VIP**.

## Funcionalidades

- Geração de senhas para diferentes categorias de atendimento.
- Gerenciamento e chamada de senhas.
- Painel administrativo para controle dos atendimentos.
- Monitor de chamadas para visualização das senhas pelos clientes.
- Aplicação do padrão de projeto Factory Method.

## Tecnologias utilizadas

- Java 17
- Spring Boot
- Maven
- Factory Method e MVC Method

## Integrantes

- Luciano S. Peixoto
- Ryan Oliveira Moscardini
- Caue Vinicius
- Miguel de Paula Arantes
- Felipe de Oliveira Souza

## Acesso ao sistema

A aplicação está disponível online:

| Painel                | Endereço                                                                                             |
| --------------------- | ---------------------------------------------------------------------------------------------------- |
| Painel administrativo | [totemhospital.moscardini.com.br](https://totemhospital.moscardini.com.br)                           |
| Monitor de chamadas   | [totemhospital.moscardini.com.br/monitor.html](https://totemhospital.moscardini.com.br/monitor.html) |

## Como executar localmente

**Pré-requisitos:**

- Java 17 ou superior
- Git

**1. Clone o repositório:**

```
git clone <URL_DO_REPOSITORIO>
cd PainelDeSenhasFactory
```

**2. Execute a aplicação:**

Linux:

```
chmod +x mvnw
./mvnw spring-boot:run
```

Windows:

```
.\mvnw.cmd spring-boot:run
```

**3. Acesse a aplicação:**

```
http://localhost:8080 ou http://localhost:8081
```
