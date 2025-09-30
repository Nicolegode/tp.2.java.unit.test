
package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Consulta;
import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.model.PlanoSaude;
import tp2.net.nicole.gode.repository.HistoricoConsultas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CalculadoraReembolso {
    private HistoricoConsultas historico;
    private Auditoria auditoria; // NOVA DEPENDÊNCIA

    // Construtor sem dependências
    public CalculadoraReembolso() {
        this.historico = null;
        this.auditoria = null;
    }

    // Construtor com histórico
    public CalculadoraReembolso(HistoricoConsultas historico) {
        this.historico = historico;
        this.auditoria = null;
    }

    // NOVO: Construtor com auditoria
    public CalculadoraReembolso(Auditoria auditoria) {
        this.historico = null;
        this.auditoria = auditoria;
    }

    // NOVO: Construtor com histórico e auditoria
    public CalculadoraReembolso(HistoricoConsultas historico, Auditoria auditoria) {
        this.historico = historico;
        this.auditoria = auditoria;
    }

    public BigDecimal calcularReembolso(BigDecimal valorConsulta, BigDecimal percentualCobertura, Paciente paciente) {
        if (valorConsulta == null || percentualCobertura == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal reembolso = valorConsulta.multiply(percentualCobertura).divide(new BigDecimal("100"));

        // Registrar no histórico se disponível
        if (historico != null) {
            String nomePaciente = (paciente != null) ? paciente.getNome() : "Anônimo";
            Consulta consulta = new Consulta(valorConsulta, percentualCobertura, reembolso, nomePaciente, LocalDateTime.now());
            historico.adicionarConsulta(consulta);
        }

        // NOVO: Registrar na auditoria se disponível
        if (auditoria != null) {
            String nomePaciente = (paciente != null) ? paciente.getNome() : "Anônimo";
            String detalhes = String.format("Consulta registrada para o paciente: %s - Valor: %s - Reembolso: %s",
                    nomePaciente, valorConsulta, reembolso);
            auditoria.registrarConsulta(detalhes);
        }

        return reembolso;
    }

    public BigDecimal calcularReembolsoComPlano(BigDecimal valorConsulta, PlanoSaude plano, Paciente paciente) {
        if (plano == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentualCobertura = plano.getPercentualCobertura();
        return calcularReembolso(valorConsulta, percentualCobertura, paciente);
    }
}