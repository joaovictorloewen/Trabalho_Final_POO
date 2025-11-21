import static org.junit.Assert.*;
import org.junit.Test;
import java.time.LocalDate;
import Modelo.*;

public class EntradaProdutoTest {

    @Test
    public void testTipoEntrada() {
        EntradaProduto e = new EntradaProduto(LocalDate.now(), "P001", 5, 150.0);
        assertEquals("Entrada", e.getTipo());
    }

    @Test
    public void testToStringFormat() {
        EntradaProduto e = new EntradaProduto(LocalDate.now(), "P010", 10, 25.0);
        String csv = e.toString();
        assertTrue(csv.contains("Entrada"));
        assertTrue(csv.contains("P010"));
    }
}
