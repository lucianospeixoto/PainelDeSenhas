package QueuePass.luciano;

import QueuePass.luciano.dto.CreatePassword;
import QueuePass.luciano.factory.SenhaCreator;
import QueuePass.luciano.factory.SenhaFactory;
import QueuePass.luciano.factory.SenhaComumCreator;
import QueuePass.luciano.factory.SenhaVIPCreator;
import QueuePass.luciano.factory.SenhaIdosoCreator;
import QueuePass.luciano.model.Enum.PasswordStatus;
import QueuePass.luciano.model.Enum.PasswordType;
import QueuePass.luciano.model.Senha;
import QueuePass.luciano.dto.EstatisticasDTO;
import QueuePass.luciano.service.GuicheService;
import QueuePass.luciano.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class PasswordServiceTest {

    private PasswordService service;

    @BeforeEach
    void setUp() {
        List<SenhaCreator> creators = List.of(new SenhaComumCreator(), new SenhaVIPCreator(), new SenhaIdosoCreator());
        SenhaFactory factory = new SenhaFactory(creators);
        service = new PasswordService(factory, new GuicheService());
    }

    @Test
    void deveCriarSenhasCorretamente() {
        Senha c = service.criarSenha(new CreatePassword(PasswordType.COMUM));
        Senha v = service.criarSenha(new CreatePassword(PasswordType.VIP));
        Senha i = service.criarSenha(new CreatePassword(PasswordType.IDOSO));

        assertThat(c.getCode()).isEqualTo("C001");
        assertThat(v.getCode()).isEqualTo("V001");
        assertThat(i.getCode()).isEqualTo("I001");
    }

    @Test
    void deveChamarProximaSenhaComPrioridadeCorreta() {
        // Idoso > VIP > Comum
        service.criarSenha(new CreatePassword(PasswordType.COMUM));
        service.criarSenha(new CreatePassword(PasswordType.IDOSO));
        service.criarSenha(new CreatePassword(PasswordType.VIP));

        Senha primeira = service.chamarProximaSenha("01");
        assertThat(primeira.getPasswordType()).isEqualTo(PasswordType.IDOSO);

        Senha segunda = service.chamarProximaSenha("02");
        assertThat(segunda.getPasswordType()).isEqualTo(PasswordType.VIP);

        Senha terceira = service.chamarProximaSenha("03");
        assertThat(terceira.getPasswordType()).isEqualTo(PasswordType.COMUM);
    }

    @Test
    void deveListarAguardandoComPrioridade() {
        service.criarSenha(new CreatePassword(PasswordType.COMUM));
        service.criarSenha(new CreatePassword(PasswordType.VIP));
        service.criarSenha(new CreatePassword(PasswordType.IDOSO));

        List<Senha> aguardando = service.listarAguardando();
        assertThat(aguardando.get(0).getPasswordType()).isEqualTo(PasswordType.IDOSO);
        assertThat(aguardando.get(1).getPasswordType()).isEqualTo(PasswordType.VIP);
        assertThat(aguardando.get(2).getPasswordType()).isEqualTo(PasswordType.COMUM);
    }

    @Test
    void deveRetornarNullSeFilaVaziaAoChamar() {
        Senha chamada = service.chamarProximaSenha("01");
        assertThat(chamada).isNull();
    }

    @Test
    void deveFinalizarSenha() {
        Senha criada = service.criarSenha(new CreatePassword(PasswordType.COMUM));
        service.chamarProximaSenha("01");

        Senha finalizada = service.finalizarSenha(criada.getId());
        assertThat(finalizada.getPasswordStatus()).isEqualTo(PasswordStatus.FINALIZADA);
        assertThat(finalizada.getEndDate()).isNotNull();
    }

    @Test
    void deveCancelarSenha() {
        Senha criada = service.criarSenha(new CreatePassword(PasswordType.COMUM));

        Senha cancelada = service.cancelarSenha(criada.getId());
        assertThat(cancelada.getPasswordStatus()).isEqualTo(PasswordStatus.CANCELADA);
    }

    @Test
    void deveGuardarNomeDaPessoaNaSenha() {
        Senha senha = service.criarSenha(new CreatePassword(PasswordType.COMUM, "  Maria Silva "));
        assertThat(senha.getNomeCliente()).isEqualTo("Maria Silva");
    }

    @Test
    void deveIdentificarGuicheAoChamar() {
        service.criarSenha(new CreatePassword(PasswordType.COMUM, "João"));
        Senha chamada = service.chamarProximaSenha("3");

        assertThat(chamada.getGuiche()).isEqualTo("03");
        assertThat(chamada.getNomeGuiche()).isEqualTo("Preferencial");
    }

    @Test
    void deveRecusarGuicheNaoCadastrado() {
        assertThatThrownBy(() -> service.chamarProximaSenha("99"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveContarAtendimentosEGuicheQueMaisTrabalhou() {
        for (int i = 0; i < 4; i++) {
            service.criarSenha(new CreatePassword(PasswordType.COMUM, "Pessoa " + i));
        }
        service.chamarProximaSenha("01");
        service.chamarProximaSenha("02");
        service.chamarProximaSenha("02");

        EstatisticasDTO stats = service.obterEstatisticas();
        assertThat(stats.totalAtendimentos()).isEqualTo(3);
        assertThat(stats.guicheMaisAtendimentos().numero()).isEqualTo("02");
        assertThat(stats.guicheMaisAtendimentos().totalAtendimentos()).isEqualTo(2);
        assertThat(stats.atendimentosPorGuiche()).hasSize(5);
    }

    @Test
    void naoDeveTerGuicheDestaqueSemAtendimentos() {
        EstatisticasDTO stats = service.obterEstatisticas();
        assertThat(stats.totalAtendimentos()).isZero();
        assertThat(stats.guicheMaisAtendimentos()).isNull();
    }

    @Test
    void deveAdicionarNovoGuiche() {
        service.adicionarGuiche("6", "Retirada de Documentos");
        assertThat(service.listarGuiches()).extracting(g -> g.numero()).contains("06");
        assertThatThrownBy(() -> service.adicionarGuiche("06", "Duplicado"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deveResetarSistemaCompleto() {
        service.criarSenha(new CreatePassword(PasswordType.COMUM));
        service.resetar();

        assertThat(service.listarTodas()).isEmpty();

        Senha nova = service.criarSenha(new CreatePassword(PasswordType.COMUM));
        assertThat(nova.getCode()).isEqualTo("C001");
        assertThat(nova.getId()).isEqualTo(0L);
    }
}
