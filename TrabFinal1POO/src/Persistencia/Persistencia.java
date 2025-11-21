package Persistencia;

import Modelo.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * Persistencia - leitura e escrita simples em CSV para produtos e movimentos.
 * Arquivos: dados/produtos.csv  e  dados/movimentos.csv
 */
public class Persistencia {

    private static final String PASTA = "dados";
    private static final String PRODUTOS_ARQ = PASTA + File.separator + "produtos.csv";
    private static final String MOV_ARQ = PASTA + File.separator + "movimentos.csv";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Persistencia() {
        garantirPasta();
    }

    private void garantirPasta() {
        try {
            Files.createDirectories(Paths.get(PASTA));
            File f1 = new File(PRODUTOS_ARQ);
            if (!f1.exists()) f1.createNewFile();
            File f2 = new File(MOV_ARQ);
            if (!f2.exists()) f2.createNewFile();
        } catch (IOException e) {
            System.err.println("Erro ao inicializar persistência: " + e.getMessage());
        }
    }

    // -----------------------
    // Produtos
    // -----------------------
    public void salvarProdutos(List<Produto> produtos) {
        garantirPasta();
        try (BufferedWriter w = new BufferedWriter(new FileWriter(PRODUTOS_ARQ, false))) {
            for (Produto p : produtos) {
                w.write(p.toString());
                w.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar produtos: " + e.getMessage());
        }
    }

    public List<Produto> carregarProdutos() {
        garantirPasta();
        List<Produto> lista = new ArrayList<>();
        try (BufferedReader r = new BufferedReader(new FileReader(PRODUTOS_ARQ))) {
            String linha;
            while ((linha = r.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                String[] dados = linha.split(";", -1);
                if (dados.length < 5) continue;

                String codigo = dados[0];
                String nome = dados[1];
                Categoria cat = Categoria.fromString(dados[2]);
                double preco = parseDoubleSafe(dados[3]);
                int qtd = parseIntSafe(dados[4]);

                LocalDate dataCadastro = null;
                if (dados.length >= 6 && !dados[5].trim().isEmpty()) {
                    String d = dados[5].trim();
                    try {
                        dataCadastro = LocalDate.parse(d, FMT);
                    } catch (Exception e1) {
                        try { dataCadastro = LocalDate.parse(d); }
                        catch (Exception ignored) {}
                    }
                }

                if (dataCadastro == null) dataCadastro = LocalDate.now();

                lista.add(new Produto(codigo, nome, cat, preco, qtd, dataCadastro));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar produtos: " + e.getMessage());
        }
        return lista;
    }

    // -----------------------
    // Movimentos
    // -----------------------
    public void salvarMovimentos(List<MovimentoEstoque> movimentos) {
        garantirPasta();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(MOV_ARQ, false))) {
            for (MovimentoEstoque mov : movimentos) {
                String dataStr = mov.getData() == null ? "" : mov.getData().format(FMT);
                String valorStr = String.format(Locale.US, "%.2f", mov.getValorUnitario()).replace(".", ",");
                String motivo = mov.getMotivo() == null ? "" : mov.getMotivo();
                String linha = String.join(";", dataStr, mov.getTipo(), mov.getCodigoProduto(),
                        String.valueOf(mov.getQuantidade()), valorStr, motivo);
                bw.write(linha);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao salvar movimentos: " + e.getMessage());
        }
    }

    public List<MovimentoEstoque> carregarMovimentos() {
        garantirPasta();
        List<MovimentoEstoque> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(MOV_ARQ))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;

                String[] p = linha.split(";", -1);
                if (p.length < 5) continue;

                String dataStr = p[0].trim();
                String tipo = p[1].trim();
                String codigoProduto = p[2].trim();
                int quantidade = parseIntSafe(p[3]);
                double valorUnit = parseDoubleSafe(p[4]);
                String motivo = p.length > 5 ? p[5] : "";

                LocalDate data = null;
                if (!dataStr.isEmpty()) {
                    try {
                        data = LocalDate.parse(dataStr, FMT);
                    } catch (DateTimeParseException ex1) {
                        try { data = LocalDate.parse(dataStr); }
                        catch (DateTimeParseException ex2) { continue; }
                    }
                }

                MovimentoEstoque m;
                if (tipo.equalsIgnoreCase("Entrada")) {
                    m = new EntradaProduto(data, codigoProduto, quantidade, valorUnit);
                } else {
                    m = new SaidaProduto(data, codigoProduto, quantidade, valorUnit, motivo);
                }
                lista.add(m);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar movimentos: " + e.getMessage());
        }
        return lista;
    }

    // -----------------------
    // Utils de parsing
    // -----------------------
    private double parseDoubleSafe(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return 0.0;
            return Double.parseDouble(s.replace(",", ".").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseIntSafe(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return 0;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
