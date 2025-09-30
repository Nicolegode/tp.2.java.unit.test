package tp2.net.nicole.gode.service;

import tp2.net.nicole.gode.model.Paciente;
import tp2.net.nicole.gode.repository.HistoricoConsultasFake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import tp2.net.nicole.gode.model.PlanoSaude;

@ExtendWith(MockitoExtension.class)
class CalculadoraReembolsoTest {

    private CalculadoraReembolso calculadora;
    private CalculadoraReembolso calculadoraComHistorico;
    private Paciente pacienteDummy;
    private HistoricoConsultasFake historicoFake;

    //  Auditoria
    private AuditoriaSpy auditoriaSpy;
    private CalculadoraReembolso calculadoraComAuditoria;
    private CalculadoraReembolso calculadoraCompleta;

    //  Autorização
    @Mock
    private AutorizadorReembolso autorizadorMock;
    private CalculadoraReembolso calculadoraComAutorizador;
    private CalculadoraReembolso calculadoraCompletaComAutorizador;

    @BeforeEach
    void setUp() {
        // Básico
        calculadora = new CalculadoraReembolso();
        historicoFake = new HistoricoConsultasFake();
        calculadoraComHistorico = new CalculadoraReembolso(historicoFake);
        pacienteDummy = new Paciente("João Silva");

        // Auditoria
        auditoriaSpy = new AuditoriaSpy();
        calculadoraComAuditoria = new CalculadoraReembolso(auditoriaSpy);
        calculadoraCompleta = new CalculadoraReembolso(historicoFake, auditoriaSpy);

        //  Novas instâncias com autorizador
        calculadoraComAutorizador = new CalculadoraReembolso(autorizadorMock);
        calculadoraCompletaComAutorizador = new CalculadoraReembolso(historicoFake, auditoriaSpy, autorizadorMock);
    }



    // Reembolso básico
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


    // Testando com plano básico que tem 50% de cobertura
    @Test
    void deveCalcularReembolsoComPlanoBasico() {
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



    // Teste básico: verificar se a auditoria é chamada
    @Test
    void deveRegistrarConsultaNaAuditoria() {
        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        calculadoraComAuditoria.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertTrue(auditoriaSpy.foiChamado(), "Auditoria deveria ter sido chamada");
        assertEquals(1, auditoriaSpy.getQuantidadeChamadas(), "Auditoria deveria ter sido chamada exatamente 1 vez");
    }

    // Teste verificando o conteúdo do registro de auditoria
    @Test
    void deveRegistrarDetalhesCorretosNaAuditoria() {
        BigDecimal valorConsulta = new BigDecimal("300.00");
        BigDecimal percentualCobertura = new BigDecimal("80");

        calculadoraComAuditoria.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        String ultimoRegistro = auditoriaSpy.getUltimoRegistro();
        assertTrue(ultimoRegistro.contains("João Silva"), "Registro deve conter o nome do paciente");
        assertTrue(ultimoRegistro.contains("300.00"), "Registro deve conter o valor da consulta");
        assertTrue(ultimoRegistro.contains("240.00"), "Registro deve conter o valor do reembolso");
    }

    // Teste com múltiplas consultas
    @Test
    void deveRegistrarMultiplasConsultasNaAuditoria() {
        Paciente paciente1 = new Paciente("Maria Santos");
        Paciente paciente2 = new Paciente("Pedro Oliveira");

        calculadoraComAuditoria.calcularReembolso(new BigDecimal("100.00"), new BigDecimal("50"), paciente1);
        calculadoraComAuditoria.calcularReembolso(new BigDecimal("200.00"), new BigDecimal("70"), paciente2);

        assertEquals(2, auditoriaSpy.getQuantidadeChamadas(), "Auditoria deveria ter sido chamada 2 vezes");
        assertTrue(auditoriaSpy.contemRegistro("Maria Santos"), "Deve conter registro da Maria");
        assertTrue(auditoriaSpy.contemRegistro("Pedro Oliveira"), "Deve conter registro do Pedro");
    }

    // Teste com paciente nulo
    @Test
    void deveRegistrarAuditoriaComPacienteAnonimo() {
        BigDecimal valorConsulta = new BigDecimal("150.00");
        BigDecimal percentualCobertura = new BigDecimal("60");

        calculadoraComAuditoria.calcularReembolso(valorConsulta, percentualCobertura, null);

        assertTrue(auditoriaSpy.foiChamado(), "Auditoria deveria ter sido chamada");
        assertTrue(auditoriaSpy.contemRegistro("Anônimo"), "Deve registrar paciente como Anônimo");
    }

    // Teste com calculadora sem auditoria (não deve chamar)
    @Test
    void naoDeveRegistrarAuditoriaQuandoNaoConfigurada() {
        BigDecimal valorConsulta = new BigDecimal("100.00");
        BigDecimal percentualCobertura = new BigDecimal("50");

        calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertFalse(auditoriaSpy.foiChamado(), "Auditoria não deveria ter sido chamada");
        assertEquals(0, auditoriaSpy.getQuantidadeChamadas(), "Nenhuma chamada deveria ter sido feita");
    }

    // Teste com histórico E auditoria funcionando juntos
    @Test
    void deveFuncionarComHistoricoEAuditoriaSimultaneamente() {
        BigDecimal valorConsulta = new BigDecimal("250.00");
        BigDecimal percentualCobertura = new BigDecimal("75");

        calculadoraCompleta.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEquals(1, historicoFake.listarConsultas().size(), "Deve ter 1 consulta no histórico");
        assertTrue(auditoriaSpy.foiChamado(), "Auditoria deveria ter sido chamada");
        assertTrue(auditoriaSpy.contemRegistro("João Silva"), "Auditoria deve conter o nome do paciente");
    }

    // Teste com plano de saúde e auditoria
    @Test
    void deveRegistrarAuditoriaComPlanoSaude() {
        PlanoSaude planoTeste = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("80");
            }

            @Override
            public String getNome() {
                return "Plano Premium";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("400.00");
        calculadoraComAuditoria.calcularReembolsoComPlano(valorConsulta, planoTeste, pacienteDummy);

        assertTrue(auditoriaSpy.foiChamado(), "Auditoria deveria ter sido chamada");
        assertTrue(auditoriaSpy.contemRegistro("João Silva"), "Deve conter o nome do paciente");
        assertTrue(auditoriaSpy.contemRegistro("400.00"), "Deve conter o valor da consulta");
        assertTrue(auditoriaSpy.contemRegistro("320.00"), "Deve conter o valor do reembolso (80% de 400)");
    }



    // Teste básico: autorização aprovada
    @Test
    void deveCalcularReembolsoQuandoAutorizado() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class))).thenReturn(true);

        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        BigDecimal resultado = calculadoraComAutorizador.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        BigDecimal esperado = new BigDecimal("140.00");
        assertEqualsComMargem(esperado, resultado);

        verify(autorizadorMock, times(1)).autorizarReembolso(pacienteDummy, valorConsulta);
    }

    // Teste principal: autorização negada deve lançar exceção
    @Test
    void deveLancarExcecaoQuandoReembolsoNaoAutorizado() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class))).thenReturn(false);

        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        ReembolsoNaoAutorizadoException exception = assertThrows(
                ReembolsoNaoAutorizadoException.class,
                () -> calculadoraComAutorizador.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy)
        );

        assertTrue(exception.getMessage().contains("João Silva"));
        assertTrue(exception.getMessage().contains("não autorizado"));

        verify(autorizadorMock, times(1)).autorizarReembolso(pacienteDummy, valorConsulta);
    }

    // Teste com paciente nulo e autorização negada
    @Test
    void deveLancarExcecaoComPacienteNuloQuandoNaoAutorizado() {
        when(autorizadorMock.autorizarReembolso(isNull(), any(BigDecimal.class))).thenReturn(false);

        BigDecimal valorConsulta = new BigDecimal("150.00");
        BigDecimal percentualCobertura = new BigDecimal("60");

        ReembolsoNaoAutorizadoException exception = assertThrows(
                ReembolsoNaoAutorizadoException.class,
                () -> calculadoraComAutorizador.calcularReembolso(valorConsulta, percentualCobertura, null)
        );

        assertTrue(exception.getMessage().contains("Anônimo"));
        verify(autorizadorMock, times(1)).autorizarReembolso(null, valorConsulta);
    }

    // Teste com diferentes valores de consulta
    @Test
    void deveAutorizarComDiferentesValores() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class)))
                .thenAnswer(invocation -> {
                    BigDecimal valor = invocation.getArgument(1);
                    return valor.compareTo(new BigDecimal("300.00")) <= 0;
                });

        BigDecimal valorBaixo = new BigDecimal("250.00");
        BigDecimal resultado1 = calculadoraComAutorizador.calcularReembolso(valorBaixo, new BigDecimal("50"), pacienteDummy);
        assertEqualsComMargem(new BigDecimal("125.00"), resultado1);

        BigDecimal valorAlto = new BigDecimal("400.00");
        assertThrows(ReembolsoNaoAutorizadoException.class,
                () -> calculadoraComAutorizador.calcularReembolso(valorAlto, new BigDecimal("50"), pacienteDummy));

        verify(autorizadorMock, times(2)).autorizarReembolso(eq(pacienteDummy), any(BigDecimal.class));
    }

    // Teste com plano de saúde e autorização
    @Test
    void deveAutorizarReembolsoComPlanoSaude() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class))).thenReturn(true);

        PlanoSaude planoTeste = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("80");
            }

            @Override
            public String getNome() {
                return "Plano Premium";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("300.00");
        BigDecimal resultado = calculadoraComAutorizador.calcularReembolsoComPlano(valorConsulta, planoTeste, pacienteDummy);

        assertEqualsComMargem(new BigDecimal("240.00"), resultado);
        verify(autorizadorMock, times(1)).autorizarReembolso(pacienteDummy, valorConsulta);
    }

    // Teste com plano de saúde e autorização negada
    @Test
    void deveLancarExcecaoComPlanoQuandoNaoAutorizado() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class))).thenReturn(false);

        PlanoSaude planoTeste = new PlanoSaude() {
            @Override
            public BigDecimal getPercentualCobertura() {
                return new BigDecimal("60");
            }

            @Override
            public String getNome() {
                return "Plano Básico";
            }
        };

        BigDecimal valorConsulta = new BigDecimal("200.00");

        assertThrows(ReembolsoNaoAutorizadoException.class,
                () -> calculadoraComAutorizador.calcularReembolsoComPlano(valorConsulta, planoTeste, pacienteDummy));

        verify(autorizadorMock, times(1)).autorizarReembolso(pacienteDummy, valorConsulta);
    }


    @Test
    void deveFuncionarNormalmenteSemAutorizador() {
        BigDecimal valorConsulta = new BigDecimal("200.00");
        BigDecimal percentualCobertura = new BigDecimal("70");

        BigDecimal resultado = calculadora.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEqualsComMargem(new BigDecimal("140.00"), resultado);
        verifyNoInteractions(autorizadorMock);
    }

    // Teste completo
    @Test
    void deveFuncionarComTodasAsDependencias() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class))).thenReturn(true);

        BigDecimal valorConsulta = new BigDecimal("350.00");
        BigDecimal percentualCobertura = new BigDecimal("75");

        BigDecimal resultado = calculadoraCompletaComAutorizador.calcularReembolso(valorConsulta, percentualCobertura, pacienteDummy);

        assertEqualsComMargem(new BigDecimal("262.50"), resultado);
        assertEquals(1, historicoFake.listarConsultas().size());
        assertTrue(auditoriaSpy.foiChamado());
        assertTrue(auditoriaSpy.contemRegistro("João Silva"));
        verify(autorizadorMock, times(1)).autorizarReembolso(pacienteDummy, valorConsulta);
    }

    // Teste de múltiplas autorizações
    @Test
    void deveProcessarMultiplasAutorizacoes() {
        when(autorizadorMock.autorizarReembolso(any(Paciente.class), any(BigDecimal.class)))
                .thenReturn(true)
                .thenReturn(false);

        Paciente paciente1 = new Paciente("Maria Santos");
        Paciente paciente2 = new Paciente("Pedro Oliveira");

        BigDecimal resultado1 = calculadoraComAutorizador.calcularReembolso(
                new BigDecimal("100.00"), new BigDecimal("50"), paciente1);
        assertEqualsComMargem(new BigDecimal("50.00"), resultado1);

        assertThrows(ReembolsoNaoAutorizadoException.class,
                () -> calculadoraComAutorizador.calcularReembolso(
                        new BigDecimal("200.00"), new BigDecimal("60"), paciente2));

        verify(autorizadorMock, times(2)).autorizarReembolso(any(Paciente.class), any(BigDecimal.class));
    }


    private void assertEqualsComMargem(BigDecimal esperado, BigDecimal atual) {
        assertEquals(0, esperado.compareTo(atual),
                "Valores devem ser iguais: esperado=" + esperado + ", atual=" + atual);
    }
}