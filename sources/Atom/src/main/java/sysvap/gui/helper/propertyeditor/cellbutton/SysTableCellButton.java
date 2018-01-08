/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 *          EMBRACE - Estudo e Monitoramento Brasileiro do Clima Espacial        
 *                               CTIS Tecnologia S/A                             
 * -----------------------------------------------------------------------------
 * Arquiteto de Software - André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * Analista de Sistemas  - Fernando de O. Pereira <fernando@dpi.inpe.br>        
 * Analista de Sistemas  - Rodolfo G. Lotte <rodolfo.lotte@inpe.br>             
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor.cellbutton;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class SysTableCellButton extends AbstractCellEditor
        implements TableCellRenderer, TableCellEditor, ActionListener {

    JButton editButton;
    Color color;
    SysITableCellButtonRepaint sysFormUpdate;

    public SysTableCellButton(SysITableCellButtonRepaint sysFormUpdate) {
        super();
        editButton = new JButton();
        editButton.setFocusPainted(false);
        editButton.addActionListener(this);
        this.sysFormUpdate = sysFormUpdate;
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JPanel renderPanel = new JPanel();
        if ((value != null) && (value.getClass() == Color.class)) {
            renderPanel.setBackground((Color) value);
        }
        return renderPanel;
    }

    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column) {
        color = (Color) value;
        editButton.setBackground(color);
        editButton.setForeground(color);
        return editButton;
    }

    public Object getCellEditorValue() {
        return color;
    }

    public void actionPerformed(ActionEvent e) {
        Color newColor = JColorChooser.showDialog(editButton, "Choose backgroud color", color);
        if (newColor != null) {
            color = newColor;
            editButton.setBackground(color);
            editButton.setForeground(color);
            sysFormUpdate.repaint(color);
        }
        fireEditingStopped();
    }
}
