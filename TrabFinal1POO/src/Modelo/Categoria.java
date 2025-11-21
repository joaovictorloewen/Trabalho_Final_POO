package Modelo;


/**
 * Enumeração das categorias possíveis de produtos.
 */
public enum Categoria {
    COMPONENTES,
    PERIFERICOS,
    ACESSORIOS,
    ELETRONICOS, // 👈 adicionado para evitar o erro no EstoqueController
    OUTROS;

    public static Categoria fromString(String s) {
        if (s == null) return OUTROS;
        // Normaliza acentuação e letras
        s = s.trim()
             .toUpperCase()
             .replace("Ç", "C")
             .replace("Á", "A")
             .replace("Ã", "A")
             .replace("É", "E")
             .replace("Ê", "E")
             .replace("Í", "I")
             .replace("Ó", "O")
             .replace("Ô", "O")
             .replace("Ú", "U");

        switch (s) {
            case "COMPONENTES":
            case "COMPONENTES DE HARDWARE":
            case "HARDWARE":
                return COMPONENTES;
            case "PERIFERICOS":
            case "PERIFÉRICOS":
                return PERIFERICOS;
            case "ACESSORIOS":
            case "ACESSÓRIOS":
                return ACESSORIOS;
            case "ELETRONICOS":
            case "ELETRÔNICOS":
            case "ELETRONICO":
            case "ELETRÔNICO":
                return ELETRONICOS; // 👈 agora é reconhecido
            default:
                return OUTROS;
        }
    }
}
