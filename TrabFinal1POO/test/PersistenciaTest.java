import static org.junit.Assert.*;
import org.junit.Test;
import Persistencia.*;
import Modelo.*;
import java.util.List;

public class PersistenciaTest {

    @Test
    public void testSalvarECarregarProdutos() {
        Persistencia p = new Persistencia();
        Produto prod = new Produto("T100", "Cabo", Categoria.ACESSORIOS, 10.0, 50);
        List<Produto> lista = java.util.Arrays.asList(prod);
        p.salvarProdutos(lista);
        List<Produto> carregado = p.carregarProdutos();
        assertNotNull(carregado);
    }
}
