package tp2.net.nicole.gode.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Consulta {
    private BigDecimal valorConsulta;
    private BigDecimal percentualCobertura;
    private BigDecimal valorReembolso;
    private String nomePaciente;
    private LocalDateTime dataHora; // NOVO CAMPO


    public Consulta(BigDecimal valorConsulta, BigDecimal percentualCobertura, BigDecimal valorReembolso, String nomePaciente) {
        this.valorConsulta = valorConsulta;
        this.percentualCobertura = percentualCobertura;
        this.valorReembolso = valorReembolso;
        this.nomePaciente = nomePaciente;
        this.dataHora = LocalDateTime.now();
    }


    public Consulta(BigDecimal valorConsulta, BigDecimal percentualCobertura, BigDecimal valorReembolso, String nomePaciente, LocalDateTime dataHora) {
        this.valorConsulta = valorConsulta;
        this.percentualCobertura = percentualCobertura;
        this.valorReembolso = valorReembolso;
        this.nomePaciente = nomePaciente;
        this.dataHora = dataHora;
    }

    // Getters existentes
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


    public void setValorConsulta(BigDecimal valorConsulta) {
        this.valorConsulta = valorConsulta;
    }

    public void setPercentualCobertura(BigDecimal percentualCobertura) {
        this.percentualCobertura = percentualCobertura;
    }

    public void setValorReembolso(BigDecimal valorReembolso) {
        this.valorReembolso = valorReembolso;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}