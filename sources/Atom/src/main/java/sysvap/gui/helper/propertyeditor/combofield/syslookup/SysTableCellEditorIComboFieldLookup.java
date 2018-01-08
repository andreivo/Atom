/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor.combofield.syslookup;

import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

public class SysTableCellEditorIComboFieldLookup extends DefaultCellEditor {

    private static String[] getItems(SysIComboLookup sysIComboLookup) {
        int offSet = 1;
        if (sysIComboLookup.getItemExcludeList() != null) {
            offSet = 0;
        }

        List<?> listItems = sysIComboLookup.getListItems();
        String[] result = new String[listItems.size() + offSet];
        result[0] = "None";

        Iterator<?> iterator = listItems.iterator();
        int i = 1;
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next != sysIComboLookup.getItemExcludeList()) {                
                result[i] = next.toString();
                i++;
            }
        }
        return result;
    }

    public SysTableCellEditorIComboFieldLookup(Object sysLookup) {
        super(new JComboBox(getItems((SysIComboLookup) sysLookup)));
        SysIComboLookup sysCb = (SysIComboLookup) sysLookup;
        if (sysCb.getSelectedItem() != null) {
            JComboBox combo = (JComboBox) getComponent();
            combo.setSelectedItem(sysCb.getSelectedItem().toString());
        }
    }
}
