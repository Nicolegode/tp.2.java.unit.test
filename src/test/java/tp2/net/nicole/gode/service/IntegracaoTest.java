package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.model.PlanoSaude;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IntegracaoTest {

    @Mock
    private AutorizadorReembolso autorizador;

    @Test
    void testeIntegracao() {
        // Mock sempre autoriza
        when(autorizador.autorizarReembolso(any(), any())).thenReturn(true);

        // Stub do plano
        PlanoSaude plano = new PlanoSaude() {
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("80");
            }
            public String getNome() {
                return "Teste";
            }
        };

        // Dummy paciente
        Paciente paciente = new Paciente("João");

        // Calculadora
        CalculadoraReembolso calc = new CalculadoraReembolso(autorizador);

        // Teste: R\$200 x 80% = R\$160, limitado a R\$150
        BigDecimal resultado = calc.calcularReembolsoComPlano(
                new BigDecimal("200"), plano, paciente);

        // Verificações
        assertEquals(new BigDecimal("150.00"), resultado);
        verify(autorizador).autorizarReembolso(paciente, new BigDecimal("200"));
    }
}