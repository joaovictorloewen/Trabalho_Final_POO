package Modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Classe Produto
 * Representa um item do estoque com informações essenciais:
 * código, nome, categoria, preço unitário e quantidade disponível.
 */
public class Produto implements Serializable, IMovimentavel {

    private String codigo;
    private String nome;
    private Categoria categoria;
    private double precoUnitario;
    private int quantidade;
    private LocalDate dataCadastro;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // === Construtores ===
    public Produto() {}

    // Construtor completo com data
    public Produto(String codigo, String nome, Categoria categoria, double precoUnitario, int quantidade, LocalDate dataCadastro) {
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.precoUnitario = precoUnitario;
        this.quantidade = quantidade;
        this.dataCadastro = dataCadastro;
    }

    // Construtor antigo compatível (sem data, assume data atual)
    public Produto(String codigo, String nome, Categoria categoria, double precoUnitario, int quantidade) {
        this(codigo, nome, categoria, precoUnitario, quantidade, LocalDate.now());
    }

    // === Getters ===
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public Categoria getCategoria() { return categoria; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidade() { return quantidade; }
    public LocalDate getDataCadastro() { return dataCadastro; }

    // === Setters ===
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public void setNome(String nome) { this.nome = nome; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public void setPrecoUnitario(double precoUnitario) {
        if (precoUnitario < 0) throw new IllegalArgumentException("O preço não pode ser negativo.");
        this.precoUnitario = precoUnitario;
    }
    public void setQuantidade(int quantidade) {
        if (quantidade < 0) quantidade = 0;
        this.quantidade = quantidade;
    }
    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    // === Métodos auxiliares ===
    public void adicionar(int qtd) {
        if (qtd < 0) throw new IllegalArgumentException("Quantidade inválida.");
        this.quantidade += qtd;
    }

    public void remover(int qtd) {
        if (qtd < 0) throw new IllegalArgumentException("Quantidade inválida.");
        if (this.quantidade - qtd < 0) throw new IllegalArgumentException("Estoque insuficiente.");
        this.quantidade -= qtd;
    }

    public double getValorTotal() {
        return precoUnitario * quantidade;
    }

    // === Representação textual / CSV ===
    @Override
    public String toString() {
        // formato CSV: codigo;nome;categoria;preco;quantidade;data
        String dataStr = dataCadastro != null ? dataCadastro.format(FMT) : "";
        return String.join(";",
                codigo,
                nome,
                categoria != null ? categoria.name() : "INDEFINIDA",
                String.format("%.2f", precoUnitario).replace(",", "."),
                Integer.toString(quantidade),
                dataStr
        );
    }
}
