package Modelo;


/**
 * Interface para objetos que podem sofrer movimentações de estoque.
 */
public interface IMovimentavel {
    void adicionar(int quantidade);
    void remover(int quantidade);
}
