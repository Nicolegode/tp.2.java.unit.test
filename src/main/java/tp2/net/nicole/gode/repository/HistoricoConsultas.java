package tp2.net.nicole.gode.repository;

import tp2.net.nicole.gode.model.Consulta;
import java.util.List;

public interface HistoricoConsultas {
    void adicionarConsulta(Consulta consulta);
    List<Consulta> listarConsultas();
    List<Consulta> buscarPorPaciente(String nomePaciente);
    void limparHistorico();
}