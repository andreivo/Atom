/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor.combofield.syslookup;

import java.awt.Component;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import sysvap.gui.helper.propertyeditor.SysReflection;

public class SysTableCellRendererIComboFieldLookup extends JComboBox implements TableCellRenderer {

    private static String[] getItems(List<?> listItems) {
        String[] result = new String[listItems.size() + 1];
        result[0] = "None";
        for (int i = 0; i < listItems.size(); i++) {
            result[i + 1] = listItems.get(i).toString();
        }
        return result;
    }

    public SysTableCellRendererIComboFieldLookup(Object sysLookup) {
        super(getItems(((SysIComboLookup) sysLookup).getListItems()));
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        if (value.getClass() == String.class) {
            setSelectedItem(value);
        } else if (SysReflection.hasInterface(value.getClass(), SysIComboLookup.class)) {
            if (SysIComboLookup.class.cast(value).getSelectedItem() != null) {
                setSelectedItem(SysIComboLookup.class.cast(value).getSelectedItem().toString());
            }
        }
        return this;
    }
}
