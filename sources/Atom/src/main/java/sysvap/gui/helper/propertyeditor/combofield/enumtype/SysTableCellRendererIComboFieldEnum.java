/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor.combofield.enumtype;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import sysvap.gui.helper.propertyeditor.SysReflection;

public class SysTableCellRendererIComboFieldEnum extends JComboBox implements TableCellRenderer {

    private static String[] getItems(Class<?> clazz) {
        Object[] enumArray = clazz.getEnumConstants();
        String[] result = new String[enumArray.length];
        for (int i = 0; i < enumArray.length; i++) {
            Object fld = enumArray[i];
            SysIComboFieldEnum comboFieldValue = (SysIComboFieldEnum) fld;
            result[i] = comboFieldValue.getValue();
        }
        return result;
    }

    public SysTableCellRendererIComboFieldEnum(Class<?> clazz) {
        super(getItems(clazz));
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
        } else if (SysReflection.hasInterface(value.getClass(), SysIComboFieldEnum.class)) {
            setSelectedItem(SysIComboFieldEnum.class.cast(value).getValue());
        }
        return this;
    }
}
