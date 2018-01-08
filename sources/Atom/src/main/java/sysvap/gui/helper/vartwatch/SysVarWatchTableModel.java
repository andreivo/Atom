/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.vartwatch;

import javax.swing.table.DefaultTableModel;

public class SysVarWatchTableModel extends DefaultTableModel {

    public SysVarWatchTableModel(Object[][] dataProperties, String[] columnNames) {
        super(dataProperties, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return (column==0);
    }

    public void addVariableRow(String var) {
        Object[] dataVar = new Object[2];
        dataVar[0] = var;
        dataVar[1] = null;        
        super.addRow(dataVar);
    }
}
