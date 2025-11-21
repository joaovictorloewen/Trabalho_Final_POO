package Modelo;

import java.time.LocalDate;


/**
 * Representa uma movimentação de saída de produtos do estoque.
 */
public class SaidaProduto extends MovimentoEstoque {

    public SaidaProduto(LocalDate data, String codigoProduto, int quantidade, double valorUnitario, String motivo) {
        super(data, codigoProduto, quantidade, valorUnitario, motivo);
    }

    @Override
    public String getTipo() {
        return "Saida";
    }
}
