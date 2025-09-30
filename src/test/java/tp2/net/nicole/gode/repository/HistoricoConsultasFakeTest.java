package tp2.net.nicole.gode.repository;

import tp2.net.nicole.gode.model.Consulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;

class HistoricoConsultasFakeTest {

    private HistoricoConsultasFake historico;

    @BeforeEach
    void setUp() {
        historico = new HistoricoConsultasFake();
    }

    // ETAPA 5: TESTES DO HISTÓRICO FAKE

    @Test
    void deveAdicionarConsultaNoHistorico() {
        // Arrange
        Consulta consulta = new Consulta(
                new BigDecimal("200.00"),
                new BigDecimal("70"),
                new BigDecimal("140.00"),
                "João Silva"
        );

        // Act
        historico.adicionarConsulta(consulta);

        // Assert
        List<Consulta> consultas = historico.listarConsultas();
        assertEquals(1, consultas.size());
        assertEquals("João Silva", consultas.get(0).getNomePaciente());
    }

    @Test
    void deveListarTodasAsConsultas() {
        // Arrange
        Consulta consulta1 = new Consulta(new BigDecimal("100.00"), new BigDecimal("50"), new BigDecimal("50.00"), "Maria");
        Consulta consulta2 = new Consulta(new BigDecimal("300.00"), new BigDecimal("80"), new BigDecimal("240.00"), "Pedro");

        // Act
        historico.adicionarConsulta(consulta1);
        historico.adicionarConsulta(consulta2);

        // Assert
        List<Consulta> consultas = historico.listarConsultas();
        assertEquals(2, consultas.size());
    }

    @Test
    void deveBuscarConsultasPorPaciente() {
        // Arrange
        Consulta consulta1 = new Consulta(new BigDecimal("100.00"), new BigDecimal("50"), new BigDecimal("50.00"), "Maria");
        Consulta consulta2 = new Consulta(new BigDecimal("200.00"), new BigDecimal("70"), new BigDecimal("140.00"), "João");
        Consulta consulta3 = new Consulta(new BigDecimal("150.00"), new BigDecimal("60"), new BigDecimal("90.00"), "Maria");

        historico.adicionarConsulta(consulta1);
        historico.adicionarConsulta(consulta2);
        historico.adicionarConsulta(consulta3);

        // Act
        List<Consulta> consultasMaria = historico.buscarPorPaciente("Maria");

        // Assert
        assertEquals(2, consultasMaria.size());
        assertTrue(consultasMaria.stream().allMatch(c -> "Maria".equals(c.getNomePaciente())));
    }

    @Test
    void deveRetornarListaVaziaParaPacienteInexistente() {
        // Arrange
        Consulta consulta = new Consulta(new BigDecimal("100.00"), new BigDecimal("50"), new BigDecimal("50.00"), "João");
        historico.adicionarConsulta(consulta);

        // Act
        List<Consulta> resultado = historico.buscarPorPaciente("Maria");

        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void deveLimparHistoricoCompletamente() {
        // Arrange
        Consulta consulta1 = new Consulta(new BigDecimal("100.00"), new BigDecimal("50"), new BigDecimal("50.00"), "João");
        Consulta consulta2 = new Consulta(new BigDecimal("200.00"), new BigDecimal("70"), new BigDecimal("140.00"), "Maria");

        historico.adicionarConsulta(consulta1);
        historico.adicionarConsulta(consulta2);

        // Act
        historico.limparHistorico();

        // Assert
        assertTrue(historico.listarConsultas().isEmpty());
    }

    @Test
    void deveIgnorarConsultaNula() {
        // Act
        historico.adicionarConsulta(null);

        // Assert
        assertTrue(historico.listarConsultas().isEmpty());
    }

    @Test
    void deveBuscarPorPacienteIgnorandoCaseSensitive() {
        // Arrange
        Consulta consulta = new Consulta(new BigDecimal("100.00"), new BigDecimal("50"), new BigDecimal("50.00"), "João Silva");
        historico.adicionarConsulta(consulta);

        // Act
        List<Consulta> resultado1 = historico.buscarPorPaciente("joão silva");
        List<Consulta> resultado2 = historico.buscarPorPaciente("JOÃO SILVA");

        // Assert
        assertEquals(1, resultado1.size());
        assertEquals(1, resultado2.size());
    }
}