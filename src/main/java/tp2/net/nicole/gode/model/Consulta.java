package tp2.net.nicole.gode.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consulta {
    private final BigDecimal valorConsulta;
    private final BigDecimal percentualCobertura;
    private final BigDecimal valorReembolso;
    private final String nomePaciente;
    private final LocalDateTime dataHora;

    public Consulta(BigDecimal valorConsulta, BigDecimal percentualCobertura,
                    BigDecimal valorReembolso, String nomePaciente) {
        this.valorConsulta = valorConsulta;
        this.percentualCobertura = percentualCobertura;
        this.valorReembolso = valorReembolso;
        this.nomePaciente = nomePaciente;
        this.dataHora = LocalDateTime.now();
    }

    public BigDecimal getValorConsulta() {
        return valorConsulta;
    }

    public BigDecimal getPercentualCobertura() {
        return percentualCobertura;
    }

    public BigDecimal getValorReembolso() {
        return valorReembolso;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}