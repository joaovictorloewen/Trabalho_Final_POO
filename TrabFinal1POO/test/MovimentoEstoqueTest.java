import static org.junit.Assert.*;
import org.junit.Test;
import java.time.LocalDate;
import Modelo.*;

public class MovimentoEstoqueTest {

    @Test
    public void testToStringFormat() {
        MovimentoEstoque m = new MovimentoEstoque(LocalDate.of(2024, 10, 1), "P100", 5, 30.0, "Teste") {
            @Override
            public String getTipo() {
                return "TesteTipo";
            }
        };
        String csv = m.toString();
        assertTrue(csv.contains("P100"));
        assertTrue(csv.contains("TesteTipo"));
    }
}
