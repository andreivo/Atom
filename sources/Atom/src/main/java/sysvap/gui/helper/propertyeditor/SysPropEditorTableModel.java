/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor;

import javax.swing.table.DefaultTableModel;

public class SysPropEditorTableModel extends DefaultTableModel {

    public SysPropEditorTableModel(Object[][] dataProperties, String[] columnNames) {
        super(dataProperties, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // se a coluna for a 2 não pode ser editada lembrando que 1 é a 0  
        if (column == 0) {
            return false;
        } else {
            String fieldValue = (String) super.getValueAt(row, 0);
            if (fieldValue.endsWith("*")) {
                return false;
            }
        }
        return super.isCellEditable(row, column);
    }

    @Override
    public Object getValueAt(int row, int column) {
        if (column == 0) {
            String fieldValue = (String) super.getValueAt(row, column);
            if (fieldValue.endsWith("*")) {
                fieldValue = fieldValue.substring(0, fieldValue.length() - 1);
            }
            return fieldValue;
        } else {
            return super.getValueAt(row, column);
        }                
    }
}
