import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import Controle.*;
import Modelo.*;

import java.util.List;

public class EstoqueControllerTest {

    private EstoqueController controller;

    @Before
    public void setUp() {
        controller = new EstoqueController();
    }

    @Test
    public void testCadastrarEListarProduto() {
        Produto p = new Produto("PX01", "Notebook", Categoria.ELETRONICOS, 3500.0, 2);
        controller.cadastrarProduto(p);
        List<Produto> produtos = controller.listarProdutos();
        assertTrue(produtos.stream().anyMatch(x -> x.getCodigo().equals("PX01")));
    }

    @Test
    public void testBuscarProdutoPorCodigo() {
        Produto p = new Produto("PX02", "Mouse", Categoria.PERIFERICOS, 100.0, 5);
        controller.cadastrarProduto(p);
        Produto encontrado = controller.buscarProdutoPorCodigo("PX02");
        assertNotNull(encontrado);
        assertEquals("Mouse", encontrado.getNome());
    }

    @Test
    public void testRegistrarEntradaSaida() {
        Produto p = new Produto("PX03", "Teclado", Categoria.PERIFERICOS, 200.0, 10);
        controller.cadastrarProduto(p);

        controller.registrarEntrada("PX03", 5, 200.0);
        assertEquals(15, controller.buscarProdutoPorCodigo("PX03").getQuantidade());

        controller.registrarSaida("PX03", 3, 200.0, "Venda");
        assertEquals(12, controller.buscarProdutoPorCodigo("PX03").getQuantidade());
    }
}
