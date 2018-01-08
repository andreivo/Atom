/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor.combofield.syslookup;

import java.util.List;

public interface SysIComboLookup {
    public Object getSelectedItem();
    public void setSelectedItem(Object selectedItem);
    public List<?> getListItems();
    public void setListItems(List<?> listItems);
    public Object getValueOf(String itemOf);
    public Object getItemExcludeList();
}
