package Modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract 

/**
 * Classe base para movimentações de estoque (entrada e saída).
 */
class MovimentoEstoque {

    protected LocalDate data;
    protected String codigoProduto;
    protected int quantidade;
    protected double valorUnitario;
    protected String motivo;

    // FORMATO OFICIAL DO SISTEMA: dd/MM/yyyy
    protected static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public MovimentoEstoque(LocalDate data, String codigoProduto, int quantidade, double valorUnitario, String motivo) {
        this.data = data;
        this.codigoProduto = codigoProduto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.motivo = motivo;
    }

    public LocalDate getData() { return data; }
    public String getCodigoProduto() { return codigoProduto; }
    public int getQuantidade() { return quantidade; }
    public double getValorUnitario() { return valorUnitario; }
    public String getMotivo() { return motivo; }

    public abstract String getTipo(); // Entrada ou Saída

    @Override
    public String toString() {
        // CSV padrão do sistema:
        // data;tipo;codigo;quantidade;valorUnitario;motivo
        String valor = String.format("%.2f", valorUnitario).replace(",", ".");
        String m = motivo == null ? "" : motivo;

        return String.join(";",
                data.format(FMT),
                getTipo(),
                codigoProduto,
                Integer.toString(quantidade),
                valor,
                m
        );
    }
}
