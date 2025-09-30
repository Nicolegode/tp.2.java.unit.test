package tp2.net.nicole.gode.helper;

import tp2.net.nicole.gode.model.Consulta;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsultaTestHelper {

    public static Consulta criarConsultaPadrao() {
        return new Consulta(
                new BigDecimal("100.00"),    // valorConsulta
                new BigDecimal("80.00"),     // percentualCobertura
                new BigDecimal("80.00"),     // valorReembolso
                "Paciente Padrão",           // nomePaciente
                LocalDateTime.now()          // dataHora
        );
    }

    // Criar consulta com valores customizados
    public static Consulta criarConsulta(BigDecimal valorConsulta, BigDecimal percentualCobertura, String nomePaciente) {
        BigDecimal valorReembolso = valorConsulta.multiply(percentualCobertura).divide(new BigDecimal("100"));
        return new Consulta(
                valorConsulta,
                percentualCobertura,
                valorReembolso,
                nomePaciente,
                LocalDateTime.now()
        );
    }

    // Criar consulta para plano autorizado
    public static Consulta criarConsultaPlanoAutorizado() {
        return new Consulta(
                new BigDecimal("200.00"),
                new BigDecimal("70.00"),
                new BigDecimal("140.00"),
                "Paciente Autorizado",
                LocalDateTime.now()
        );
    }

    // Criar consulta para plano não autorizado
    public static Consulta criarConsultaPlanoDenegado() {
        return new Consulta(
                new BigDecimal("500.00"),
                new BigDecimal("50.00"),
                new BigDecimal("250.00"),
                "Paciente Negado",
                LocalDateTime.now()
        );
    }
}