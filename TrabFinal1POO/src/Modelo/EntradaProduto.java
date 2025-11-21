package Modelo;

import java.time.LocalDate;


/**
 * Representa uma movimentação de entrada de produtos no estoque.
 */
public class EntradaProduto extends MovimentoEstoque {

    public EntradaProduto(LocalDate data, String codigoProduto, int quantidade, double valorUnitario) {
        super(data, codigoProduto, quantidade, valorUnitario, "");
    }

    @Override
    public String getTipo() {
        return "Entrada";
    }
}
