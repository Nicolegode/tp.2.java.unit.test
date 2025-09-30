package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.repository.HistoricoConsultasFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.math.BigDecimal;
import tp2.net.nicole.gode.model.PlanoSaude;

class CalculadoraReembolsoTest {

    private CalculadoraReembolso calculadora;
    private CalculadoraReembolso calculadoraComHistorico;
    private Paciente pacienteDummy;
    private HistoricoConsultasFake historicoFake;

    @BeforeEach
    void setUp() {
        // Criando as instâncias para os testes
        calculadora = new CalculadoraReembolso();
        historicoFake = new HistoricoConsultasFake();
        calculadoraComHistorico = new CalculadoraReembolso(historicoFake);
        pacienteDummy = new Paciente("João Silva");
    }

    // Primeiro teste - calculando reembolso básico
    @Test
    void deveCalcularReembolsoBasicoComPercentualFixo() {
        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        BigDecimal esperado = new BigDecimal("140.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Testando quando o valor da consulta é zero
    @Test
    void deveRetornarZeroQuandoValorConsultaForZero() {
        BigDecimal valorConsulta = BigDecimal.ZERO;
        BigDecimal percentualCobertura = new BigDecimal("50");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEqualsComMargem(BigDecimal.ZERO, resultado);
    }

    // Testando quando o percentual é zero
    @Test
    void deveRetornarZeroQuandoPercentualCoberturaForZero() {
        BigDecimal valorConsulta = new BigDecimal("100.00");
        BigDecimal percentualCobertura = BigDecimal.ZERO;

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEqualsComMargem(BigDecimal.ZERO, resultado);
    }

    // Testando cobertura total (100%)
    @Test
    void deveReembolsarValorTotalQuandoPercentualForCemPorcento() {
        BigDecimal valorConsulta = new BigDecimal("150.00");
        BigDecimal percentualCobertura = new BigDecimal("100");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEqualsComMargem(new BigDecimal("150.00"), resultado);
    }

    // Verificando se funciona com valores nulos
    @Test
    void deveRetornarZeroQuandoValoresForemNulos() {
        assertEqualsComMargem(BigDecimal.ZERO, calculadora.calcularReembolso(null, new BigDecimal("50"), pacienteDummy));
        assertEqualsComMargem(BigDecimal.ZERO, calculadora.calcularReembolso(new BigDecimal("100"), null, pacienteDummy));
        assertEqualsComMargem(BigDecimal.ZERO, calculadora.calcularReembolso(null, null, pacienteDummy));
    }

    // Teste para verificar se a classe funciona independentemente
    @Test
    void deveCalcularReembolsoSemDependenciasExternas() {
        CalculadoraReembolso calculadoraIsolada = new CalculadoraReembolso();
        BigDecimal valorConsulta = new BigDecimal("300.00");
        BigDecimal percentualCobertura = new BigDecimal("80");
        Paciente pacienteLocal = new Paciente("Teste Isolado");

        BigDecimal resultado = calculadoraIsolada.calcularReembolso(valorConsulta, percentualCobertura, pacienteLocal);

        BigDecimal esperado = new BigDecimal("240.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Verificando se aceita o objeto Paciente sem problemas
    @Test
    void deveAceitarPacienteDummySemAlterarComportamento() {
        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        BigDecimal esperado = new BigDecimal("140.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Testando com paciente que tem nome nulo
    @Test
    void deveAceitarPacienteComNomeNulo() {
        Paciente pacienteComNomeNulo = new Paciente(null);
        BigDecimal valorConsulta = new BigDecimal("100.00");
        BigDecimal percentualCobertura = new BigDecimal("50");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteComNomeNulo);

        BigDecimal esperado = new BigDecimal("50.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Testando com paciente vazio
    @Test
    void deveAceitarPacienteVazio() {
        Paciente pacienteVazio = new Paciente();
        BigDecimal valorConsulta = new BigDecimal("120.00");
        BigDecimal percentualCobertura = new BigDecimal("60");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteVazio);

        BigDecimal esperado = new BigDecimal("72.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Verificando se a consulta fica salva no histórico
    @Test
    void deveRegistrarConsultaNoHistorico() {
        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        calculadoraComHistorico.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEquals(1, historicoFake.listarConsultas().size());
        assertEquals("João Silva", historicoFake.listarConsultas().getFirst().getNomePaciente());
    }

    // Testando múltiplas consultas no histórico
    @Test
    void deveRegistrarMultiplasConsultasNoHistorico() {
        Paciente paciente1 = new Paciente("Maria Santos");
        Paciente paciente2 = new Paciente("Pedro Oliveira");

        calculadoraComHistorico.calcularReembolso(new BigDecimal("100.00"), new BigDecimal("50"), paciente1);
        calculadoraComHistorico.calcularReembolso(new BigDecimal("300.00"), new BigDecimal("80"), paciente2);

        assertEquals(2, historicoFake.listarConsultas().size());
    }

    // Verificando que funciona mesmo sem histórico configurado
    @Test
    void deveCalcularSemHistoricoQuandoNaoConfigurado() {
        BigDecimal valorConsulta = new BigDecimal("150.00");
        BigDecimal percentualCobertura = new BigDecimal("60");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        BigDecimal esperado = new BigDecimal("90.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Testando comportamento com paciente nulo
    @Test
    void deveRegistrarConsultaComPacienteNuloComoAnonimo() {
        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("50");

        calculadoraComHistorico.calcularReembolso(valorConsulta, percentualCobertura, null);

        BigDecimal esperado = new BigDecimal("100.00");
        BigDecimal resultado = calculadoraComHistorico.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);
        assertEqualsComMargem(esperado, resultado);
    }

    // Verificando se os valores salvos no histórico estão corretos
    @Test
    void deveRegistrarValoresCorretosNoHistorico() {
        BigDecimal valorConsulta = new BigDecimal("250.00");
        BigDecimal percentualCobertura = new BigDecimal("80");

        BigDecimal resultadoCalculado = calculadoraComHistorico.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEquals(1, historicoFake.listarConsultas().size());
        var consultaRegistrada = historicoFake.listarConsultas().getFirst();

        assertEqualsComMargem(valorConsulta, consultaRegistrada.getValorConsulta());
        assertEqualsComMargem(percentualCobertura, consultaRegistrada.getPercentualCobertura());
        assertEqualsComMargem(resultadoCalculado, consultaRegistrada.getValorReembolso());
        assertEquals("João Silva", consultaRegistrada.getNomePaciente());
    }

    // ETAPA 6: TESTES COM PLANOS DE SAÚDE (usando calcularReembolsoComPlano)

    // Testando com plano básico que tem 50% de cobertura
    @Test
    void deveCalcularReembolsoComPlanoBasico() {
        // Criando um plano básico que sempre retorna 50%
        PlanoSaude planoBasico = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("50");
            }

            @Override
            public String getNome() {
                return "Plano Básico";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal resultado = calculadora.calcularReembolsoComPlano(valorConsulta, planoBasico, pacienteDummy);

        BigDecimal esperado = new BigDecimal("100.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Testando com plano premium que tem cobertura maior
    @Test
    void deveCalcularReembolsoComPlanoPremium() {
        // Criando um plano premium que sempre retorna 80%
        PlanoSaude planoPremium = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("80");
            }

            @Override
            public String getNome() {
                return "Plano Premium";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal resultado = calculadora.calcularReembolsoComPlano(valorConsulta, planoPremium, pacienteDummy);

        BigDecimal esperado = new BigDecimal("160.00");
        assertEqualsComMargem(esperado, resultado);
    }

    // Comparando diferentes planos para ver se o cálculo muda
    @Test
    void deveCompararDiferentesPlanos() {
        // Plano com 50% de cobertura
        PlanoSaude plano50 = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("50");
            }

            @Override
            public String getNome() {
                return "Plano 50%";
            }
        };

        // Plano com 80% de cobertura
        PlanoSaude plano80 = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("80");
            }

            @Override
            public String getNome() {
                return "Plano 80%";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("300.00");

        BigDecimal resultado50 = calculadora.calcularReembolsoComPlano(valorConsulta, plano50, pacienteDummy);
        BigDecimal resultado80 = calculadora.calcularReembolsoComPlano(valorConsulta, plano80, pacienteDummy);

        assertEqualsComMargem(new BigDecimal("150.00"), resultado50);
        assertEqualsComMargem(new BigDecimal("240.00"), resultado80);
    }

    // Verificando o que acontece quando o plano é nulo
    @Test
    void deveRetornarZeroQuandoPlanoForNulo() {
        BigDecimal valorConsulta = new BigDecimal("100.00");

        BigDecimal resultado = calculadora.calcularReembolsoComPlano(valorConsulta, null, pacienteDummy);

        assertEqualsComMargem(BigDecimal.ZERO, resultado);
    }

    // Testando se a consulta com plano fica salva no histórico
    @Test
    void deveRegistrarConsultaComPlanoNoHistorico() {
        PlanoSaude planoTeste = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("70");
            }

            @Override
            public String getNome() {
                return "Plano Teste";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("200.00");
        calculadoraComHistorico.calcularReembolsoComPlano(valorConsulta, planoTeste, pacienteDummy);

        assertEquals(1, historicoFake.listarConsultas().size());
        var consultaRegistrada = historicoFake.listarConsultas().getFirst();

        assertEqualsComMargem(valorConsulta, consultaRegistrada.getValorConsulta());
        assertEqualsComMargem(new BigDecimal("70"), consultaRegistrada.getPercentualCobertura());
        assertEqualsComMargem(new BigDecimal("140.00"), consultaRegistrada.getValorReembolso());
        assertEquals("João Silva", consultaRegistrada.getNomePaciente());
    }

    // Testando planos com percentuais bem diferentes
    @Test
    void deveFuncionarComPlanosComPercentuaisVariados() {
        // Plano econômico com apenas 25%
        PlanoSaude plano25 = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("25");
            }

            @Override
            public String getNome() {
                return "Plano Econômico";
            }
        };

        // Plano completo com 100%
        PlanoSaude plano100 = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("100");
            }

            @Override
            public String getNome() {
                return "Plano Completo";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("400.00");

        BigDecimal resultado25 = calculadora.calcularReembolsoComPlano(valorConsulta, plano25, pacienteDummy);
        BigDecimal resultado100 = calculadora.calcularReembolsoComPlano(valorConsulta, plano100, pacienteDummy);

        assertEqualsComMargem(new BigDecimal("100.00"), resultado25);
        assertEqualsComMargem(new BigDecimal("400.00"), resultado100);
    }

    // Método auxiliar para comparar BigDecimal
    private void assertEqualsComMargem(BigDecimal esperado, BigDecimal atual) {
        assertEquals(0, esperado.compareTo(atual),
                "Valores devem ser iguais: esperado=" + esperado + ", atual=" + atual);
    }
}