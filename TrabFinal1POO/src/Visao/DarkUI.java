package Visao;

import javax.swing.*;
import java.awt.*;

/**
 * DarkUI
 * Tema escuro profissional baseado no Nimbus L&F, com correção de artefatos visuais
 * e aprimoramento de contraste para formulários e tabelas.
 */
public final class DarkUI {

    private DarkUI() {}

    public static void applyDarkNimbus() {
        Font base = new Font("Segoe UI", Font.PLAIN, 13);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        // Paleta refinada
        Color bg0 = new Color(0x111315);   // fundo geral
        Color bg1 = new Color(0x1A1D20);   // painéis e caixas
        Color bg2 = new Color(0x202427);   // campos e tabelas
        Color fg  = new Color(0xE6E6E6);   // texto principal
        Color fgDim = new Color(0xB0B6BC); // texto secundário
        Color acc = new Color(0x3D7EFF);   // azul principal
        Color accOK = new Color(0x2EB3A3); // verde/teal
        Color accWarn = new Color(0xE76666); // alerta

        // Remove os ícones de focus que criavam quadrados indesejados
        UIManager.put("TextField[Focused].backgroundPainter", null);
        UIManager.put("FormattedTextField[Focused].backgroundPainter", null);
        UIManager.put("PasswordField[Focused].backgroundPainter", null);
        UIManager.put("TextArea[Focused].backgroundPainter", null);
        UIManager.put("EditorPane[Focused].backgroundPainter", null);
        UIManager.put("TextPane[Focused].backgroundPainter", null);

        // Remove o contorno branco interno dos campos
        UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(0x2E3338), 1));
        UIManager.put("PasswordField.border", BorderFactory.createLineBorder(new Color(0x2E3338), 1));
        UIManager.put("FormattedTextField.border", BorderFactory.createLineBorder(new Color(0x2E3338), 1));

        // Paleta base Nimbus
        UIManager.put("control", bg1);
        UIManager.put("info", bg2);
        UIManager.put("nimbusBase", new Color(0x2B2F33));
        UIManager.put("nimbusBlueGrey", new Color(0x23282C));
        UIManager.put("nimbusLightBackground", bg1);
        UIManager.put("text", fg);
        UIManager.put("background", bg0);
        UIManager.put("nimbusFocus", acc);
        UIManager.put("nimbusSelectionBackground", acc);
        UIManager.put("nimbusSelectedText", Color.WHITE);

        // Componentes visuais
        UIManager.put("Panel.background", bg1);
        UIManager.put("OptionPane.background", bg1);
        UIManager.put("OptionPane.messageForeground", fg);
        UIManager.put("Label.foreground", fg);
        UIManager.put("Label.font", base);
        UIManager.put("Button.background", new Color(0x262A2F));
        UIManager.put("Button.foreground", fg);
        UIManager.put("Button.font", base);
        UIManager.put("Button.focus", acc);
        UIManager.put("CheckBox.foreground", fg);
        UIManager.put("RadioButton.foreground", fg);

        // Campos de texto
        UIManager.put("TextField.background", bg2);
        UIManager.put("TextField.foreground", fg);
        UIManager.put("TextField.caretForeground", fg);
        UIManager.put("TextField.inactiveForeground", fgDim);
        UIManager.put("PasswordField.background", bg2);
        UIManager.put("PasswordField.foreground", fg);
        UIManager.put("ComboBox.background", bg2);
        UIManager.put("ComboBox.foreground", fg);

        // Scroll e tabelas
        UIManager.put("ScrollPane.background", bg1);
        UIManager.put("Table.background", bg2);
        UIManager.put("Table.alternateRowColor", new Color(0x191D20));
        UIManager.put("Table.foreground", fg);
        UIManager.put("Table.gridColor", new Color(0x30363D));
        UIManager.put("Table.selectionBackground", acc);
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("TableHeader.background", new Color(0x20252A));
        UIManager.put("TableHeader.foreground", fg);
        UIManager.put("TableHeader.font", base.deriveFont(Font.BOLD));

        // Tooltips e tabs
        UIManager.put("TabbedPane.contentAreaColor", bg1);
        UIManager.put("TabbedPane.background", bg1);
        UIManager.put("TabbedPane.foreground", fg);
        UIManager.put("ToolTip.background", new Color(0x2B3036));
        UIManager.put("ToolTip.foreground", fg);
        UIManager.put("ToolTip.font", base);

        // ProgressBar
        UIManager.put("ProgressBar.background", new Color(0x1F2429));
        UIManager.put("ProgressBar.foreground", accOK);
        UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
        UIManager.put("ProgressBar.selectionForeground", Color.WHITE);

        // Frames e diálogos
        UIManager.put("Frame.background", bg0);
        UIManager.put("InternalFrame.activeTitleBackground", bg1);
        UIManager.put("InternalFrame.activeTitleForeground", fg);
        UIManager.put("InternalFrame.titleFont", base.deriveFont(Font.BOLD, 13f));

        // Barra de rolagem (scrollbar) mais suave
        UIManager.put("ScrollBar.thumb", new Color(0x353A3F));
        UIManager.put("ScrollBar.thumbDarkShadow", new Color(0x2B2F33));
        UIManager.put("ScrollBar.thumbHighlight", new Color(0x2B2F33));
        UIManager.put("ScrollBar.thumbShadow", new Color(0x2B2F33));
    }
}
