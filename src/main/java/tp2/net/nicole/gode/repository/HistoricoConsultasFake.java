package tp2.net.nicole.gode.repository;

import tp2.net.nicole.gode.model.Consulta;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricoConsultasFake implements HistoricoConsultas {
    private final List<Consulta> consultas = new ArrayList<>();

    @Override
    public void adicionarConsulta(Consulta consulta) {
        if (consulta != null) {
            consultas.add(consulta);
        }
    }

    @Override
    public List<Consulta> listarConsultas() {
        return new ArrayList<>(consultas);
    }

    @Override
    public List<Consulta> buscarPorPaciente(String nomePaciente) {
        if (nomePaciente == null || nomePaciente.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return consultas.stream()
                .filter(consulta -> nomePaciente.equalsIgnoreCase(consulta.getNomePaciente()))
                .collect(Collectors.toList());
    }

    @Override
    public void limparHistorico() {
        consultas.clear();
    }
}