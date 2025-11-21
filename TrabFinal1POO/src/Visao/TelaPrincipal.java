package Visao;

import Controle.EstoqueController;
import Modelo.*;
import Persistencia.Persistencia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * TelaPrincipal
 * Interface principal do sistema de estoque.
 * Contém as abas: Dashboard, Produtos, Movimentações e Configurações.
 */
public class TelaPrincipal {
    private final EstoqueController controller = new EstoqueController();

    private static final DateTimeFormatter FMT_PT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TelaPrincipal::new);
    }

    public TelaPrincipal() {
        try { DarkUI.applyDarkNimbus(); } catch (Exception ignored) { }

        JFrame frame = new JFrame("Sistema de Estoque - Painel Profissional");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1150, 720);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebar.setBackground(new Color(0x1A1D20));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        String[] menus = {"Dashboard", "Produtos", "Movimentações", "Configurações"};
        CardLayout card = new CardLayout();
        JPanel main = new JPanel(card);
        main.setBackground(new Color(0x121416));

        for (String menu : menus) {
            JButton btn = createSidebarButton(menu);
            sidebar.add(btn);

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(25, 25, 25, 25));
            panel.setOpaque(false);

            JLabel title = new JLabel(menu, SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            title.setForeground(Color.WHITE);
            panel.add(title, BorderLayout.NORTH);

            switch (menu) {

                case "Dashboard" -> panel.add(buildDashboardPanel(), BorderLayout.CENTER);

                case "Produtos" -> {
                    String[] cols = {"Código", "Nome", "Categoria", "Preço", "Quantidade", "Data Cadastro"};
                    DefaultTableModel model = new DefaultTableModel(cols, 0);
                    JTable table = new JTable(model);
                    table.setFillsViewportHeight(true);
                    table.setRowHeight(28);
                    table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
                    atualizarTabela(model);

                    JScrollPane scroll = new JScrollPane(table);
                    scroll.getViewport().setBackground(new Color(0x181B1E));
                    scroll.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                    panel.add(scroll, BorderLayout.CENTER);

                    // === FILTRO POR INTERVALO DE DATAS COM MÁSCARA ===
                    JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                    filtroPanel.setOpaque(false);

                    final JFormattedTextField txtDataInicio;
                    final JFormattedTextField txtDataFim;

                    MaskFormatter mask = null;
                    try {
                        mask = new MaskFormatter("##/##/####");
                        mask.setPlaceholderCharacter('_');
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }

                    if (mask != null) {
                        txtDataInicio = new JFormattedTextField(mask);
                        txtDataFim = new JFormattedTextField(mask);
                    } else {
                        txtDataInicio = new JFormattedTextField();
                        txtDataFim = new JFormattedTextField();
                    }

                    txtDataInicio.setColumns(10);
                    txtDataFim.setColumns(10);

                    JButton btnFiltrar = createPrimaryButton("Filtrar por Data");
                    JButton btnLimparFiltro = createSecondaryButton("Limpar Filtro");

                    filtroPanel.add(new JLabel("Data Inicial (dd/MM/yyyy):"));
                    filtroPanel.add(txtDataInicio);
                    filtroPanel.add(new JLabel("Data Final (dd/MM/yyyy):"));
                    filtroPanel.add(txtDataFim);
                    filtroPanel.add(btnFiltrar);
                    filtroPanel.add(btnLimparFiltro);
                    panel.add(filtroPanel, BorderLayout.NORTH);

                    btnFiltrar.addActionListener(e -> {
                        String inicioStr = txtDataInicio.getText().trim();
                        String fimStr = txtDataFim.getText().trim();

                        if (inicioStr.contains("_") || fimStr.contains("_") || inicioStr.isEmpty() || fimStr.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Datas inválidas! Use o formato dd/MM/yyyy.");
                            return;
                        }

                        try {
                            LocalDate dataInicio = LocalDate.parse(inicioStr, FMT_PT);
                            LocalDate dataFim = LocalDate.parse(fimStr, FMT_PT);

                            if (dataFim.isBefore(dataInicio)) {
                                JOptionPane.showMessageDialog(null, "A data final não pode ser anterior à data inicial.");
                                return;
                            }

                            model.setRowCount(0);
                            for (Produto p : controller.listarProdutos()) {
                                if (!p.getDataCadastro().isBefore(dataInicio) && !p.getDataCadastro().isAfter(dataFim)) {
                                    model.addRow(new Object[]{
                                        p.getCodigo(),
                                        p.getNome(),
                                        p.getCategoria(),
                                        String.format("R$ %.2f", p.getPrecoUnitario()).replace('.', ','),
                                        p.getQuantidade(),
                                        p.getDataCadastro().format(FMT_PT)
                                    });
                                }
                            }
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(null, "Datas inválidas! Use o formato dd/MM/yyyy.");
                        }
                    });

                    btnLimparFiltro.addActionListener(e -> {
                        txtDataInicio.setValue(null);
                        txtDataFim.setValue(null);
                        atualizarTabela(model);
                    });

                    // === BOTOES ===
                    JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
                    botoes.setOpaque(false);
                    JButton add = createPrimaryButton("Cadastrar Produto");
                    JButton editar = createPrimaryButton("Editar Produto");
                    JButton entrada = createSecondaryButton("Registrar Entrada");
                    JButton saida = createSecondaryButton("Registrar Saída");

                    add.addActionListener(e -> cadastrarProduto(model));
                    editar.addActionListener(e -> editarProduto(table, model));
                    entrada.addActionListener(e -> registrarEntrada(model));
                    saida.addActionListener(e -> registrarSaida(model));

                    botoes.add(add);
                    botoes.add(editar);
                    botoes.add(entrada);
                    botoes.add(saida);
                    panel.add(botoes, BorderLayout.SOUTH);
                }






                case "Movimentações" -> {
                    String[] colunas = {"Data", "Tipo", "Produto", "Quantidade", "Valor Unitário", "Valor Total", "Motivo"};
                    DefaultTableModel movModel = new DefaultTableModel(colunas, 0);
                    JTable tabelaMov = new JTable(movModel);
                    tabelaMov.setFillsViewportHeight(true);
                    tabelaMov.setRowHeight(28);
                    tabelaMov.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    tabelaMov.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
                    tabelaMov.getTableHeader().setReorderingAllowed(false);

                    JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                    filtroPanel.setOpaque(false);

                    final JFormattedTextField[] dataInicial = new JFormattedTextField[1];
                    final JFormattedTextField[] dataFinal = new JFormattedTextField[1];
                    try {
                        MaskFormatter mask = new MaskFormatter("##/##/####");
                        mask.setPlaceholderCharacter('_');
                        dataInicial[0] = new JFormattedTextField(mask);
                        dataFinal[0] = new JFormattedTextField(mask);
                    } catch (ParseException ex) {
                        dataInicial[0] = new JFormattedTextField();
                        dataFinal[0] = new JFormattedTextField();
                    }
                    dataInicial[0].setColumns(10);
                    dataFinal[0].setColumns(10);

                    filtroPanel.add(new JLabel("Data Inicial (dd/MM/yyyy):"));
                    filtroPanel.add(dataInicial[0]);
                    filtroPanel.add(new JLabel("Data Final (dd/MM/yyyy):"));
                    filtroPanel.add(dataFinal[0]);

                    JButton buscar = createPrimaryButton("Buscar");
                    JButton limpar = createSecondaryButton("Limpar Filtro");
                    filtroPanel.add(buscar);
                    filtroPanel.add(limpar);

                    buscar.addActionListener(e -> {
                        try {
                            String diText = dataInicial[0].getText().trim();
                            String dfText = dataFinal[0].getText().trim();

                            if (diText.isEmpty() || diText.contains("_")) diText = "";
                            if (dfText.isEmpty() || dfText.contains("_")) dfText = "";

                            LocalDate di = diText.isEmpty() ? null : LocalDate.parse(diText, FMT_PT);
                            LocalDate df = dfText.isEmpty() ? null : LocalDate.parse(dfText, FMT_PT);

                            movModel.setRowCount(0);
                            List<MovimentoEstoque> movs = controller.getMovimentos();
                            for (MovimentoEstoque mov : movs) {
                                LocalDate dataMov = parseDataMovimento(mov.getData());
                                if (dataMov == null) continue;

                                boolean dentro = (di == null || !dataMov.isBefore(di)) && (df == null || !dataMov.isAfter(df));
                                if (dentro) {
                                    Produto p = controller.buscarProdutoPorCodigo(mov.getCodigoProduto());
                                    String nomeProduto = (p != null ? p.getNome() : "Produto não encontrado");
                                    movModel.addRow(new Object[]{
                                            formatDataParaExibir(mov.getData()), mov.getTipo(), nomeProduto, mov.getQuantidade(),
                                            String.format("R$ %.2f", mov.getValorUnitario()),
                                            String.format("R$ %.2f", mov.getValorUnitario() * mov.getQuantidade()),
                                            mov.getMotivo() == null || mov.getMotivo().isEmpty() ? "-" : mov.getMotivo()
                                    });
                                }
                            }
                        } catch (DateTimeParseException ex) {
                            JOptionPane.showMessageDialog(null, "Formato de data inválido. Use dd/MM/yyyy.");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Erro no filtro: " + ex.getMessage());
                        }
                    });

                    limpar.addActionListener(e -> {
                        dataInicial[0].setValue(null);
                        dataFinal[0].setValue(null);
                        atualizarTabelaMovimentacoes(movModel);
                    });

                    panel.add(filtroPanel, BorderLayout.NORTH);
                    atualizarTabelaMovimentacoes(movModel);

                    JScrollPane sp = new JScrollPane(tabelaMov);
                    sp.getViewport().setBackground(new Color(0x181B1E));
                    sp.setBorder(BorderFactory.createLineBorder(new Color(0x3D7EFF), 1));
                    panel.add(sp, BorderLayout.CENTER);

                    JButton atualizar = createPrimaryButton("Atualizar Movimentações");
                    JButton editar = createPrimaryButton("Editar Movimentação");

                    atualizar.addActionListener(e -> atualizarTabelaMovimentacoes(movModel));
                    editar.addActionListener(e -> editarMovimentacao(tabelaMov, movModel));

                    JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
                    botoes.setOpaque(false);
                    botoes.add(atualizar);
                    botoes.add(editar);
                    panel.add(botoes, BorderLayout.SOUTH);
                }

                case "Configurações" -> {
                    JPanel configPanel = new JPanel(new BorderLayout(15, 15));
                    configPanel.setOpaque(false);

                    JTextArea txt = new JTextArea(
                            "Versão 1.0\n" +
                                    "Desenvolvido por João Victor Loewen, Lucas Shimazaki Batistti e Pedro Luna Renan.\n\n" +
                                    "Sistema de controle de estoque com interface Swing e armazenamento em arquivos CSV.\n" +
                                    "Tema visual: Dark Nimbus Moderno.");
                    txt.setEditable(false);
                    txt.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    txt.setForeground(Color.WHITE);
                    txt.setBackground(new Color(0x181B1E));
                    configPanel.add(txt, BorderLayout.CENTER);

                    JButton limpar = createDangerButton("Limpar Arquivos de Dados");
                    JButton recarregar = createPrimaryButton("Recarregar Sistema");

                    limpar.addActionListener(e -> limparArquivosDeDados());
                    recarregar.addActionListener(e -> recarregarSistema(frame));

                    JPanel botoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
                    botoes.setOpaque(false);
                    botoes.add(limpar);
                    botoes.add(recarregar);
                    configPanel.add(botoes, BorderLayout.SOUTH);

                    panel.add(configPanel, BorderLayout.CENTER);
                }
            }

            main.add(panel, menu);
            btn.addActionListener(e -> card.show(main, menu));
        }

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, main);
        split.setDividerLocation(240);
        split.setDividerSize(3);
        split.setBorder(null);
        frame.add(split, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // === DASHBOARD ===
    private JPanel buildDashboardPanel() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 25, 25));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(30, 80, 30, 80));
        try {
            List<Produto> produtos = controller.getProdutos();
            List<MovimentoEstoque> movs = controller.getMovimentos();

            int totalProdutos = produtos.size();
            int totalItens = produtos.stream().mapToInt(Produto::getQuantidade).sum();
            double valorTotal = produtos.stream().mapToDouble(p -> p.getPrecoUnitario() * p.getQuantidade()).sum();
            int totalMovs = movs.size();

            grid.add(createInfoCard("Produtos", String.valueOf(totalProdutos), new Color(0x3D7EFF)));
            grid.add(createInfoCard("Itens em Estoque", String.valueOf(totalItens), new Color(0x2EB3A3)));
            grid.add(createInfoCard("Valor Total", String.format("R$ %.2f", valorTotal), new Color(0xE76666)));
            grid.add(createInfoCard("Movimentações", String.valueOf(totalMovs), new Color(0xF0A500)));
        } catch (Exception e) {
            grid.add(new JLabel("Erro ao carregar resumo."));
        }
        return grid;
    }

    private JPanel createInfoCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(0x1E2327));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accent, 2, true),
                new EmptyBorder(20, 18, 20, 18)
        ));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(accent);

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(Color.WHITE);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);
        return card;
    }

    // === BOTÕES ===
    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(0x2B2F33));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(0x3D7EFF)); }
            @Override public void mouseExited(MouseEvent e) { btn.setBackground(new Color(0x2B2F33)); }
        });
        return btn;
    }

    private JButton createPrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x2B2F33));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0x3D7EFF)); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(new Color(0x2B2F33)); }
        });
        return b;
    }

    private JButton createSecondaryButton(String text) {
        JButton b = createPrimaryButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return b;
    }

    private JButton createDangerButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0xAA3333));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0xCC4444)); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(new Color(0xAA3333)); }
        });
        return b;
    }

    // === FUNÇÕES DE PRODUTOS/MOVIMENTAÇÕES ===
    private void atualizarTabela(DefaultTableModel model) {
        model.setRowCount(0);
        List<Produto> produtos = controller.listarProdutos();
        for (Produto p : produtos) {
            model.addRow(new Object[]{
                    p.getCodigo(),
                    p.getNome(),
                    p.getCategoria(),
                    String.format("R$ %.2f", p.getPrecoUnitario()).replace('.', ','),
                    p.getQuantidade(),
                    p.getDataCadastro().format(FMT_PT)
            });
        }
    }

    private void atualizarTabelaMovimentacoes(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<MovimentoEstoque> movs = controller.getMovimentos();
            for (MovimentoEstoque mov : movs) {
                Produto p = controller.buscarProdutoPorCodigo(mov.getCodigoProduto());
                String nomeProduto = (p != null ? p.getNome() : "Produto não encontrado");
                model.addRow(new Object[]{
                        formatDataParaExibir(mov.getData()), mov.getTipo(), nomeProduto, mov.getQuantidade(),
                        String.format("R$ %.2f", mov.getValorUnitario()),
                        String.format("R$ %.2f", mov.getValorUnitario() * mov.getQuantidade()),
                        mov.getMotivo() == null || mov.getMotivo().isEmpty() ? "-" : mov.getMotivo()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar movimentações: " + e.getMessage());
        }
    }

    private void cadastrarProduto(DefaultTableModel model) {
        try {
            JTextField codigoField = new JTextField();
            JTextField nomeField = new JTextField();
            JComboBox<Categoria> categoriaBox = new JComboBox<>(Categoria.values());
            JTextField precoField = new JTextField();
            JTextField qtdField = new JTextField();
            JFormattedTextField dataField;

            try {
                MaskFormatter mask = new MaskFormatter("##/##/####");
                mask.setPlaceholderCharacter('_');
                dataField = new JFormattedTextField(mask);
            } catch (ParseException ex) {
                dataField = new JFormattedTextField();
            }
            dataField.setColumns(10);
            dataField.setText(LocalDate.now().format(FMT_PT));

            JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
            painel.add(new JLabel("Código:"));
            painel.add(codigoField);
            painel.add(new JLabel("Nome:"));
            painel.add(nomeField);
            painel.add(new JLabel("Categoria:"));
            painel.add(categoriaBox);
            painel.add(new JLabel("Preço unitário:"));
            painel.add(precoField);
            painel.add(new JLabel("Quantidade inicial:"));
            painel.add(qtdField);
            painel.add(new JLabel("Data de cadastro (dd/MM/yyyy):"));
            painel.add(dataField);

            int opcao = JOptionPane.showConfirmDialog(null, painel, "Cadastrar Novo Produto",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcao == JOptionPane.OK_OPTION) {
                String codigo = codigoField.getText().trim();
                String nome = nomeField.getText().trim();
                Categoria categoria = (Categoria) categoriaBox.getSelectedItem();
                double preco = Double.parseDouble(precoField.getText().replace(",", "."));
                int quantidade = Integer.parseInt(qtdField.getText());

                LocalDate dataCadastro;
                try {
                    dataCadastro = LocalDate.parse(dataField.getText().trim(), FMT_PT);
                } catch (DateTimeParseException ex) {
                    dataCadastro = LocalDate.now();
                }

                Produto novoProduto = new Produto(codigo, nome, categoria, preco, quantidade, dataCadastro);
                controller.cadastrarProduto(novoProduto);
                atualizarTabela(model);
                JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar produto: " + ex.getMessage());
        }
    }


    private void editarProduto(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Selecione um produto para editar.");
            return;
        }

        String codigo = model.getValueAt(selectedRow, 0).toString();
        Produto p = controller.buscarProdutoPorCodigo(codigo);
        if (p == null) return;

        JTextField nomeField = new JTextField(p.getNome());
        JComboBox<Categoria> catBox = new JComboBox<>(Categoria.values());
        catBox.setSelectedItem(p.getCategoria());
        JTextField precoField = new JTextField(String.valueOf(p.getPrecoUnitario()));
        JTextField qtdField = new JTextField(String.valueOf(p.getQuantidade()));

        // Campo para editar a data de cadastro
        JFormattedTextField dataField;
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            dataField = new JFormattedTextField(mask);
        } catch (ParseException ex) {
            dataField = new JFormattedTextField();
        }
        dataField.setColumns(10);
        dataField.setText(p.getDataCadastro().format(FMT_PT));

        JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
        painel.add(new JLabel("Nome:"));
        painel.add(nomeField);
        painel.add(new JLabel("Categoria:"));
        painel.add(catBox);
        painel.add(new JLabel("Preço unitário:"));
        painel.add(precoField);
        painel.add(new JLabel("Quantidade:"));
        painel.add(qtdField);
        painel.add(new JLabel("Data de Cadastro (dd/MM/yyyy):"));
        painel.add(dataField);

        int opcao = JOptionPane.showConfirmDialog(null, painel,
                "Editar Produto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                p.setNome(nomeField.getText().trim());
                p.setCategoria((Categoria) catBox.getSelectedItem());
                p.setPrecoUnitario(Double.parseDouble(precoField.getText().replace(",", ".")));
                p.setQuantidade(Integer.parseInt(qtdField.getText()));

                // Atualiza a data de cadastro
                try {
                    LocalDate novaData = LocalDate.parse(dataField.getText().trim(), FMT_PT);
                    p.setDataCadastro(novaData);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Data inválida! Mantendo a data anterior.");
                }

                controller.atualizarProduto(p);
                atualizarTabela(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao editar produto: " + ex.getMessage());
            }
        }
    }


    private void registrarEntrada(DefaultTableModel model) {
        try {
            List<Produto> produtos = controller.listarProdutos();
            if (produtos.isEmpty()) { JOptionPane.showMessageDialog(null, "Não há produtos cadastrados."); return; }

            JComboBox<String> prodBox = new JComboBox<>();
            for (Produto p : produtos) prodBox.addItem(p.getNome());
            JTextField qtdField = new JTextField();
            JTextField valorField = new JTextField();

            JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
            painel.add(new JLabel("Produto:"));
            painel.add(prodBox);
            painel.add(new JLabel("Quantidade:"));
            painel.add(qtdField);
            painel.add(new JLabel("Valor unitário:"));
            painel.add(valorField);

            int opcao = JOptionPane.showConfirmDialog(null, painel, "Registrar Entrada",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcao == JOptionPane.OK_OPTION) {
                String nome = (String) prodBox.getSelectedItem();
                Produto p = controller.buscarProdutoPorNome(nome);
                if (p == null) return;

                int qtd = Integer.parseInt(qtdField.getText());
                double valor = Double.parseDouble(valorField.getText().replace(",", "."));

                p.adicionar(qtd);
                controller.atualizarProduto(p);
                controller.adicionarMovimento(new EntradaProduto(LocalDate.now(), p.getCodigo(), qtd, valor));
                atualizarTabela(model);
                JOptionPane.showMessageDialog(null, "Entrada registrada com sucesso!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao registrar entrada: " + ex.getMessage());
        }
    }

    private void registrarSaida(DefaultTableModel model) {
        try {
            List<Produto> produtos = controller.listarProdutos();
            if (produtos.isEmpty()) { JOptionPane.showMessageDialog(null, "Não há produtos cadastrados."); return; }

            JComboBox<String> prodBox = new JComboBox<>();
            for (Produto p : produtos) prodBox.addItem(p.getNome());
            JTextField qtdField = new JTextField();
            JTextField valorField = new JTextField();
            JComboBox<String> motivoBox = new JComboBox<>(new String[]{"Venda", "Uso Interno", "Devolução ao Fornecedor", "Produto Danificado", "Outro"});

            JPanel painel = new JPanel(new GridLayout(0, 2, 8, 8));
            painel.add(new JLabel("Produto:"));
            painel.add(prodBox);
            painel.add(new JLabel("Quantidade:"));
            painel.add(qtdField);
            painel.add(new JLabel("Valor unitário:"));
            painel.add(valorField);
            painel.add(new JLabel("Motivo:"));
            painel.add(motivoBox);

            int opcao = JOptionPane.showConfirmDialog(null, painel, "Registrar Saída",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcao == JOptionPane.OK_OPTION) {
                String nome = (String) prodBox.getSelectedItem();
                Produto p = controller.buscarProdutoPorNome(nome);
                if (p == null) return;

                int qtd = Integer.parseInt(qtdField.getText());
                double valor = Double.parseDouble(valorField.getText().replace(",", "."));
                String motivo = (String) motivoBox.getSelectedItem();

                if (qtd > p.getQuantidade()) { JOptionPane.showMessageDialog(null, "Quantidade insuficiente!"); return; }

                p.remover(qtd);
                controller.atualizarProduto(p);
                controller.adicionarMovimento(new SaidaProduto(LocalDate.now(), p.getCodigo(), qtd, valor, motivo));
                atualizarTabela(model);
                JOptionPane.showMessageDialog(null, "Saída registrada com sucesso!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao registrar saída: " + ex.getMessage());
        }
    }

    private LocalDate parseDataMovimento(Object dataObj) {
        if (dataObj == null) return null;
        String s = dataObj.toString();
        try { return LocalDate.parse(s); } catch (DateTimeParseException ignored) { }
        try { return LocalDate.parse(s, FMT_PT); } catch (DateTimeParseException ignored) { }
        return null;
    }

    private String formatDataParaExibir(Object dataObj) {
        LocalDate ld = parseDataMovimento(dataObj);
        return (ld != null) ? ld.format(FMT_PT) : dataObj == null ? "" : dataObj.toString();
    }

    private void limparArquivosDeDados() {
        try {
            new File("produtos.csv").delete();
            new File("movimentos.csv").delete();
            JOptionPane.showMessageDialog(null, "Arquivos de dados limpos com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao limpar arquivos: " + e.getMessage());
        }
    }

    private void recarregarSistema(JFrame frame) {
        frame.dispose();
        new TelaPrincipal();
    }

    // === EDIÇÃO DE MOVIMENTAÇÃO ===
    private void editarMovimentacao(JTable tabelaMov, DefaultTableModel movModel) {
        int selectedRow = tabelaMov.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Selecione uma movimentação para editar.");
            return;
        }

        String dataAtualStr = tabelaMov.getValueAt(selectedRow, 0).toString();
        LocalDate dataOriginal = LocalDate.parse(dataAtualStr, FMT_PT);
        String tipo = tabelaMov.getValueAt(selectedRow, 1).toString();
        String produtoNome = tabelaMov.getValueAt(selectedRow, 2).toString();
        int qtdAtual = Integer.parseInt(tabelaMov.getValueAt(selectedRow, 3).toString());
        String valorUnitarioStr = tabelaMov.getValueAt(selectedRow, 4).toString().replace("R$", "").replace(",", ".").trim();
        double valorUnitario = Double.parseDouble(valorUnitarioStr);
        String motivoAtual = tabelaMov.getValueAt(selectedRow, 6).toString();

        JFormattedTextField dataField;
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            dataField = new JFormattedTextField(mask);
        } catch (ParseException ex) {
            dataField = new JFormattedTextField();
        }
        dataField.setColumns(10);
        dataField.setText(dataAtualStr);

        JTextField qtdField = new JTextField(String.valueOf(qtdAtual));
        JComboBox<String> motivoBox = new JComboBox<>(new String[]{"Venda", "Uso Interno", "Devolução ao Fornecedor", "Produto Danificado", "Outro"});
        motivoBox.setSelectedItem(motivoAtual.equals("-") ? "Venda" : motivoAtual);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Produto:"));
        form.add(new JLabel(produtoNome));
        form.add(new JLabel("Tipo:"));
        form.add(new JLabel(tipo));
        form.add(new JLabel("Data (dd/MM/yyyy):"));
        form.add(dataField);
        form.add(new JLabel("Quantidade:"));
        form.add(qtdField);
        if (tipo.equalsIgnoreCase("Saida")) {
            form.add(new JLabel("Motivo:"));
            form.add(motivoBox);
        }

        int opt = JOptionPane.showConfirmDialog(null, form, "Editar Movimentação", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                LocalDate novaData = LocalDate.parse(dataField.getText().trim(), FMT_PT);
                int novaQtd = Integer.parseInt(qtdField.getText().trim());
                String novoMotivo = tipo.equalsIgnoreCase("Saida") ? (String) motivoBox.getSelectedItem() : "-";

                Produto p = controller.buscarProdutoPorNome(produtoNome);
                if (p == null) {
                    JOptionPane.showMessageDialog(null, "Produto não encontrado!");
                    return;
                }

                if (tipo.equalsIgnoreCase("Entrada")) {
                    p.remover(qtdAtual);
                    p.adicionar(novaQtd);
                } else {
                    p.adicionar(qtdAtual);
                    p.remover(novaQtd);
                }
                controller.atualizarProduto(p);
                atualizarMovimentacaoArquivo(dataOriginal, produtoNome, tipo, novaData, novaQtd, valorUnitario, novoMotivo);
                atualizarTabelaMovimentacoes(movModel);
                JOptionPane.showMessageDialog(null, "Movimentação atualizada com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao editar movimentação: " + ex.getMessage());
            }
        }
    }

    private void atualizarMovimentacaoArquivo(LocalDate dataOriginal, String produtoNome, String tipo,
                                              LocalDate novaData, int novaQtd, double valorUnit, String novoMotivo) {
        try {
            List<MovimentoEstoque> movs = controller.getMovimentos();
            List<MovimentoEstoque> novos = new ArrayList<>();

            for (MovimentoEstoque m : movs) {
                Produto p = controller.buscarProdutoPorCodigo(m.getCodigoProduto());
                String nome = (p != null) ? p.getNome() : "";

                if (nome.equals(produtoNome) && m.getTipo().equalsIgnoreCase(tipo)
                        && m.getData() != null && m.getData().isEqual(dataOriginal)) {
                    if (tipo.equalsIgnoreCase("Entrada")) {
                        novos.add(new EntradaProduto(novaData, m.getCodigoProduto(), novaQtd, valorUnit));
                    } else {
                        novos.add(new SaidaProduto(novaData, m.getCodigoProduto(), novaQtd, valorUnit, novoMotivo));
                    }
                } else {
                    novos.add(m);
                }
            }

            Persistencia persist = new Persistencia();
            persist.salvarMovimentos(novos);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar arquivo: " + e.getMessage());
        }
    }
}
