package tp2.net.nicole.gode.model;

import java.math.BigDecimal;

// Classe que simula um plano de saúde para testes
public class PlanoSaudeStub implements PlanoSaude {
    private final BigDecimal percentualCobertura;
    private final String nome;

    // Construtor que recebe o percentual e o nome do plano
    public PlanoSaudeStub(BigDecimal percentualCobertura, String nome) {
        this.percentualCobertura = percentualCobertura;
        this.nome = nome;
    }

    // Retorna o percentual de cobertura que foi definido
    @Override
    public BigDecimal getPercentualCobertura() {
        return percentualCobertura;
    }

    // Retorna o nome do plano
    @Override
    public String getNome() {
        return nome;
    }
}

// Plano básico com 50% de cobertura - útil para testes
class PlanoSaudeStubBasico implements PlanoSaude {
    // Sempre retorna 50% de cobertura
    @Override
    public BigDecimal getPercentualCobertura() {
        return new BigDecimal("50");
    }

    // Nome fixo do plano básico
    @Override
    public String getNome() {
        return "Plano Básico";
    }
}

// Plano premium com 80% de cobertura - para testar valores maiores
class PlanoSaudeStubPremium implements PlanoSaude {
    // Sempre retorna 80% de cobertura
    @Override
    public BigDecimal getPercentualCobertura() {
        return new BigDecimal("80");
    }

    // Nome fixo do plano premium
    @Override
    public String getNome() {
        return "Plano Premium";
    }
}