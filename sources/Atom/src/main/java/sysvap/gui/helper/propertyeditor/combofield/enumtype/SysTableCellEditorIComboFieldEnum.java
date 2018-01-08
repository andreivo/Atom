/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor.combofield.enumtype;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class SysTableCellEditorIComboFieldEnum extends DefaultCellEditor {

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

    public SysTableCellEditorIComboFieldEnum(Class<?> clazz, Object selected) {
        super(new JComboBox(getItems(clazz)));
        if (selected != null) {
            JComboBox combo = (JComboBox) getComponent();
            SysIComboFieldEnum comboFieldValue = (SysIComboFieldEnum) selected;
            combo.setSelectedItem(comboFieldValue.getValue());
        }
    }
}
