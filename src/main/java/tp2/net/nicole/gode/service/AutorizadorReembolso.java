package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Paciente;
import java.math.BigDecimal;

public interface AutorizadorReembolso {
    boolean autorizarReembolso(Paciente paciente, BigDecimal valorConsulta);
}