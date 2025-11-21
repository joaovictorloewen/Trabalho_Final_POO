import static org.junit.Assert.*;
import org.junit.Test;
import java.time.LocalDate;
import Modelo.*;

public class SaidaProdutoTest {

    @Test
    public void testTipoSaida() {
        SaidaProduto s = new SaidaProduto(LocalDate.now(), "P002", 3, 100.0, "Venda");
        assertEquals("Saida", s.getTipo());
        assertEquals("Venda", s.getMotivo());
    }

    @Test
    public void testToStringFormat() {
        SaidaProduto s = new SaidaProduto(LocalDate.now(), "P003", 2, 75.0, "Uso Interno");
        String csv = s.toString();
        assertTrue(csv.contains("Saida"));
        assertTrue(csv.contains("Uso Interno"));
    }
}
