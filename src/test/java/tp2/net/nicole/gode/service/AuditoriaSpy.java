package tp2.net.nicole.gode.service;

import java.util.ArrayList;
import java.util.List;

public class AuditoriaSpy implements Auditoria {
    private List<String> registrosAuditoria = new ArrayList<>();
    private int quantidadeChamadas = 0;
    private boolean foiChamado = false;

    @Override
    public void registrarConsulta(String detalhes) {
        foiChamado = true;
        quantidadeChamadas++;
        registrosAuditoria.add(detalhes);


        System.out.println("SPY - Auditoria registrada: " + detalhes);
    }


    public boolean foiChamado() {
        return foiChamado;
    }

    public int getQuantidadeChamadas() {
        return quantidadeChamadas;
    }

    public List<String> getRegistrosAuditoria() {
        return new ArrayList<>(registrosAuditoria);
    }

    public String getUltimoRegistro() {
        return registrosAuditoria.isEmpty() ? null : registrosAuditoria.get(registrosAuditoria.size() - 1);
    }

    public boolean contemRegistro(String detalhesEsperados) {
        return registrosAuditoria.stream().anyMatch(registro -> registro.contains(detalhesEsperados));
    }


    public void limpar() {
        foiChamado = false;
        quantidadeChamadas = 0;
        registrosAuditoria.clear();
    }
}