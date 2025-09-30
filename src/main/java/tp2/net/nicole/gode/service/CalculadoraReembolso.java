package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Consulta;
import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.model.PlanoSaude;
import tp2.net.nicole.gode.repository.HistoricoConsultas;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculadoraReembolso {

    private final HistoricoConsultas historico;

    // Construtor sem histórico (para compatibilidade com testes antigos)
    public CalculadoraReembolso() {
        this.historico = null;
    }

    // Construtor com histórico
    public CalculadoraReembolso(HistoricoConsultas historico) {
        this.historico = historico;
    }

    // Método com percentual direto
    public BigDecimal calcularReembolso(BigDecimal valorConsulta, BigDecimal percentualCobertura, Paciente paciente) {
        if (valorConsulta == null || percentualCobertura == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal valorReembolso = valorConsulta
                .multiply(percentualCobertura)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Registra no histórico se disponível
        if (historico != null && paciente != null) {
            Consulta consulta = new Consulta(valorConsulta, percentualCobertura, valorReembolso, paciente.getNome());
            historico.adicionarConsulta(consulta);
        }

        return valorReembolso;
    }

    // Método com plano de saúde - NOME DIFERENTE para evitar ambiguidade
    public BigDecimal calcularReembolsoComPlano(BigDecimal valorConsulta, PlanoSaude plano, Paciente paciente) {
        if (valorConsulta == null || plano == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentualCobertura = plano.getPercentualCobertura();
        BigDecimal valorReembolso = valorConsulta
                .multiply(percentualCobertura)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Registra no histórico se disponível
        if (historico != null && paciente != null) {
            Consulta consulta = new Consulta(valorConsulta, percentualCobertura, valorReembolso, paciente.getNome());
            historico.adicionarConsulta(consulta);
        }

        return valorReembolso;
    }
}