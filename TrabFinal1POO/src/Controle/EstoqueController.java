package Controle;

import Modelo.*;
import Persistencia.Persistencia;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Controlador principal do estoque. Faz a ponte entre a camada de visão e as classes de modelo.
 */
public class EstoqueController {

    private final Persistencia persistencia;
    private final List<Produto> produtos;
    private final List<MovimentoEstoque> movimentos;

    public EstoqueController() {
        this.persistencia = new Persistencia();
        this.produtos = new ArrayList<>(persistencia.carregarProdutos());
        this.movimentos = new ArrayList<>(persistencia.carregarMovimentos());
    }

    // ==============================
    //        PRODUTOS
    // ==============================

    public List<Produto> listarProdutos() {
        return new ArrayList<>(produtos);
    }

    public List<Produto> getProdutos() {
        return produtos; // devolve lista real
    }

    public void cadastrarProduto(Produto p) {
        if (p == null) throw new IllegalArgumentException("Produto inválido.");
        for (Produto existente : produtos) {
            if (existente.getCodigo().equalsIgnoreCase(p.getCodigo())) {
                throw new IllegalArgumentException("Já existe um produto com este código.");
            }
        }
        produtos.add(p);
        persistencia.salvarProdutos(produtos);
    }

    public Produto buscarProdutoPorCodigo(String codigo) {
        for (Produto p : produtos) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null;
    }

    // **NOVO MÉTODO**
    public Produto buscarProdutoPorNome(String nome) {
        for (Produto p : produtos) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return p;
            }
        }
        return null;
    }

    // ==============================
    //       MOVIMENTAÇÕES
    // ==============================

    public List<MovimentoEstoque> getMovimentos() {
        return movimentos; // devolve lista real
    }

    public void registrarEntrada(String codigoProduto, int quantidade, double valorUnitario) {
        Produto p = buscarProdutoPorCodigo(codigoProduto);
        if (p == null) {
            JOptionPane.showMessageDialog(null, "Produto não encontrado!");
            return;
        }

        p.adicionar(quantidade);
        p.setPrecoUnitario(valorUnitario);

        MovimentoEstoque entrada = new EntradaProduto(LocalDate.now(), codigoProduto, quantidade, valorUnitario);
        movimentos.add(entrada);

        persistencia.salvarProdutos(produtos);
        persistencia.salvarMovimentos(movimentos);
    }

    public void registrarSaida(String codigoProduto, int quantidade, double valorUnitario, String motivo) {
        Produto p = buscarProdutoPorCodigo(codigoProduto);
        if (p == null) {
            JOptionPane.showMessageDialog(null, "Produto não encontrado!");
            return;
        }

        if (quantidade > p.getQuantidade()) {
            JOptionPane.showMessageDialog(null, "Quantidade insuficiente em estoque!");
            return;
        }

        p.remover(quantidade);

        MovimentoEstoque saida = new SaidaProduto(LocalDate.now(), codigoProduto, quantidade, valorUnitario, motivo);
        movimentos.add(saida);

        persistencia.salvarProdutos(produtos);
        persistencia.salvarMovimentos(movimentos);
    }

    // **NOVO MÉTODO** para adicionar movimentação diretamente
    public void adicionarMovimento(MovimentoEstoque mov) {
        if (mov == null) return;
        movimentos.add(mov);
        persistencia.salvarMovimentos(movimentos);
    }

    // Atualizar movimentação existente
    public void atualizarMovimento(int index, MovimentoEstoque mov) {
        movimentos.set(index, mov);
        persistencia.salvarMovimentos(movimentos);
    }


    /**
     * Substitui toda a lista de movimentos em memória
     * e persiste o novo conteúdo no arquivo de movimentos.
     *
     * @param novosMovimentos nova lista de movimentos já recalculada
     */
    public void substituirMovimentos(java.util.List<MovimentoEstoque> novosMovimentos) {
        movimentos.clear();
        if (novosMovimentos != null) {
            movimentos.addAll(novosMovimentos);
        }
        persistencia.salvarMovimentos(movimentos);
    }

    // ==============================
    //         UTILITÁRIOS
    // ==============================

    public double calcularValorTotalEstoque() {
        return produtos.stream()
                .mapToDouble(p -> p.getPrecoUnitario() * p.getQuantidade())
                .sum();
    }

    public int calcularTotalItens() {
        return produtos.stream()
                .mapToInt(Produto::getQuantidade)
                .sum();
    }

    public void salvarTudo() {
        persistencia.salvarProdutos(produtos);
        persistencia.salvarMovimentos(movimentos);
    }

    public boolean removerProduto(String codigo) {
        Produto p = buscarProdutoPorCodigo(codigo);
        if (p != null) {
            produtos.remove(p);
            persistencia.salvarProdutos(produtos);
            return true;
        }
        return false;
    }

    public void atualizarPreco(String codigo, double novoPreco) {
        Produto p = buscarProdutoPorCodigo(codigo);
        if (p != null) {
            p.setPrecoUnitario(novoPreco);
            persistencia.salvarProdutos(produtos);
        }
    }

    public void atualizarProduto(Produto atualizado) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getCodigo().equalsIgnoreCase(atualizado.getCodigo())) {
                produtos.set(i, atualizado);
                persistencia.salvarProdutos(produtos);
                return;
            }
        }
    }
}
