package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Consulta;
import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.repository.HistoricoConsultas;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraReembolso {

    private static final int ESCALA_PADRAO = 2;
    private static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_UP;

    private final HistoricoConsultas historicoConsultas;

    public CalculadoraReembolso(HistoricoConsultas historicoConsultas) {
        this.historicoConsultas = historicoConsultas;
    }

    public CalculadoraReembolso() {
        this.historicoConsultas = null; // Para compatibilidade com testes antigos
    }

    public BigDecimal calcularReembolso(BigDecimal valorConsulta, BigDecimal percentualCobertura, Paciente paciente) {
        if (valorConsulta == null || percentualCobertura == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorReembolso = valorConsulta
                .multiply(percentualCobertura)
                .divide(new BigDecimal("100"), ESCALA_PADRAO, MODO_ARREDONDAMENTO);

        // Salvar no histórico se disponível
        if (historicoConsultas != null && paciente != null) {
            String nomePaciente = paciente.getNome() != null ? paciente.getNome() : "Paciente Anônimo";
            Consulta consulta = new Consulta(valorConsulta, percentualCobertura, valorReembolso, nomePaciente);
            historicoConsultas.adicionarConsulta(consulta);
        }

        return valorReembolso;
    }

    @Deprecated
    public BigDecimal calcularReembolso(BigDecimal valorConsulta, BigDecimal percentualCobertura) {
        return calcularReembolso(valorConsulta, percentualCobertura, new Paciente());
    }
}