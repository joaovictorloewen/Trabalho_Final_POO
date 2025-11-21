import static org.junit.Assert.*;
import org.junit.Test;
import Modelo.*;

public class ProdutoTest {

    @Test
    public void testAdicionar() {
        Produto p = new Produto("P001", "Mouse", Categoria.PERIFERICOS, 100.0, 10);
        p.adicionar(5);
        assertEquals(15, p.getQuantidade());
    }

    @Test
    public void testRemover() {
        Produto p = new Produto("P002", "Teclado", Categoria.PERIFERICOS, 80.0, 10);
        p.remover(4);
        assertEquals(6, p.getQuantidade());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoverMaisQueEstoque() {
        Produto p = new Produto("P003", "Monitor", Categoria.PERIFERICOS, 400.0, 5);
        p.remover(10); // deve lançar exceção
    }

    @Test
    public void testValorTotal() {
        Produto p = new Produto("P004", "SSD", Categoria.COMPONENTES, 200.0, 3);
        assertEquals(600.0, p.getValorTotal(), 0.001);
    }

    @Test
    public void testToStringFormat() {
        Produto p = new Produto("P005", "Cabo HDMI", Categoria.ACESSORIOS, 50.0, 2);
        String csv = p.toString();
        assertTrue(csv.contains("P005"));
        assertTrue(csv.contains("ACESSORIOS"));
    }
}
