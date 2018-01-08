/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.core.data;

import java.io.Serializable;
import java.util.List;
import sysvap.gui.helper.propertyeditor.combofield.syslookup.SysIComboLookup;

public class SysLookupMEF implements SysIComboLookup, Serializable {

    private Object selectedItem;
    private Object parentItem;
    private List<?> listItems;
    private SysProject sysProject;

    public SysLookupMEF(Object selectedItem, Object parentItem, List<?> listItems, SysProject sysProject) {
        this.selectedItem = selectedItem;
        this.listItems = listItems;
        this.parentItem = parentItem;
        this.sysProject = sysProject;
    }

    public SysProject getSysProject() {
        return sysProject;
    }

    public void setSysProject(SysProject sysProject) {
        this.sysProject = sysProject;
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(SysMEF selectedItem) {
        getSysProject().setChanged(true);
        this.selectedItem = selectedItem;
    }

    @Override
    public List<?> getListItems() {
        return listItems;
    }

    @Override
    public void setListItems(List<?> listItems) {
        this.listItems = listItems;
    }

    @Override
    public Object getValueOf(String itemOf) {
        for (Object item : listItems) {
            if (item.toString().equals(itemOf)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void setSelectedItem(Object selectedItem) {
        this.selectedItem = selectedItem;
    }

    public Object getParentItem() {
        return parentItem;
    }

    public void setParentItem(Object parentItem) {
        this.parentItem = parentItem;
    }

    @Override
    public Object getItemExcludeList() {
        return getParentItem();
    }
}
