package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Consulta;
import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.model.PlanoSaude;
import tp2.net.nicole.gode.repository.HistoricoConsultas;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class CalculadoraReembolso {

    // ETAPA 11: Constante para o teto máximo de reembolso
    private static final BigDecimal TETO_MAXIMO_REEMBOLSO = new BigDecimal("150.00");

    private HistoricoConsultas historico;
    private Auditoria auditoria;
    private AutorizadorReembolso autorizador;

    public CalculadoraReembolso() {
        this.historico = null;
        this.auditoria = null;
        this.autorizador = null;
    }

    public CalculadoraReembolso(HistoricoConsultas historico) {
        this.historico = historico;
        this.auditoria = null;
        this.autorizador = null;
    }

    public CalculadoraReembolso(Auditoria auditoria) {
        this.historico = null;
        this.auditoria = auditoria;
        this.autorizador = null;
    }

    public CalculadoraReembolso(HistoricoConsultas historico, Auditoria auditoria) {
        this.historico = historico;
        this.auditoria = auditoria;
        this.autorizador = null;
    }

    public CalculadoraReembolso(AutorizadorReembolso autorizador) {
        this.historico = null;
        this.auditoria = null;
        this.autorizador = autorizador;
    }

    public CalculadoraReembolso(HistoricoConsultas historico, Auditoria auditoria, AutorizadorReembolso autorizador) {
        this.historico = historico;
        this.auditoria = auditoria;
        this.autorizador = autorizador;
    }

    // ETAPA 11: Método público para obter o teto máximo (útil para testes)
    public static BigDecimal getTetoMaximoReembolso() {
        return TETO_MAXIMO_REEMBOLSO;
    }

    public BigDecimal calcularReembolso(BigDecimal valorConsulta, BigDecimal percentualCobertura, Paciente paciente) {
        if (valorConsulta == null || percentualCobertura == null) {
            return BigDecimal.ZERO;
        }

        if (autorizador != null) {
            boolean autorizado = autorizador.autorizarReembolso(paciente, valorConsulta);
            if (!autorizado) {
                String nomePaciente = (paciente != null) ? paciente.getNome() : "Anônimo";
                throw new ReembolsoNaoAutorizadoException("Reembolso não autorizado para o paciente: " + nomePaciente);
            }
        }

        // Calcular reembolso com melhor precisão
        BigDecimal reembolso = valorConsulta.multiply(percentualCobertura)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // ETAPA 11: Aplicar teto máximo
        reembolso = aplicarTetoMaximo(reembolso);

        if (historico != null) {
            String nomePaciente = (paciente != null) ? paciente.getNome() : "Anônimo";
            Consulta consulta = new Consulta(valorConsulta, percentualCobertura, reembolso, nomePaciente, LocalDateTime.now());
            historico.adicionarConsulta(consulta);
        }

        if (auditoria != null) {
            String nomePaciente = (paciente != null) ? paciente.getNome() : "Anônimo";
            String detalhes = String.format("Consulta autorizada e registrada para o paciente: %s - Valor: %.2f - Reembolso: %.2f",
                    nomePaciente, valorConsulta, reembolso);
            // ✅ CORRIGIDO: Método correto da interface
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

    // ETAPA 11: Método privado para aplicar o teto máximo
    private BigDecimal aplicarTetoMaximo(BigDecimal valorReembolso) {
        if (valorReembolso == null) {
            return BigDecimal.ZERO;
        }

        // Se o valor calculado for maior que o teto, retorna o teto
        if (valorReembolso.compareTo(TETO_MAXIMO_REEMBOLSO) > 0) {
            return TETO_MAXIMO_REEMBOLSO;
        }

        // Caso contrário, retorna o valor calculado
        return valorReembolso;
    }
}