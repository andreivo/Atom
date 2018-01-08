/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor;

import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import javax.swing.AbstractAction;
import sysvap.core.data.SysLookupMEF;
import sysvap.core.data.SysProject;
import sysvap.gui.SysView;
import sysvap.gui.core.data.SysGUIMEF;
import sysvap.gui.core.data.SysGUIProject;
import sysvap.gui.helper.SysActionsGUI;
import sysvap.gui.helper.propertyeditor.combofield.enumtype.SysIComboFieldEnum;

public class SysPropEditorAction extends AbstractAction {

    private SysIPropEditor objExplore;
    private SysView form;

    public SysPropEditorAction(SysIPropEditor objExplore, SysView form) {
        this.objExplore = objExplore;
        this.form = form;
    }

    public SysView getForm() {
        return form;
    }

    public void setForm(SysView form) {
        this.form = form;
    }

    public SysIPropEditor getObjExplore() {
        return objExplore;
    }

    public void setObjExplore(SysIPropEditor objExplore) {
        this.objExplore = objExplore;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (getObjExplore() != null) {
                SysTableCellListener tcl = (SysTableCellListener) e.getSource();

                String name = tcl.getTable().getModel().getValueAt(tcl.getRow(), 0).toString();
                Field declaredField = SysReflection.getFieldByName(getObjExplore(), name);

                Object value;
                if (SysReflection.hasInterface(declaredField, SysIComboFieldEnum.class)) {
                    value = SysReflection.getEnumByValue(declaredField.getType(), (String) tcl.getNewValue());
                } else if (declaredField.getType() == SysLookupMEF.class) {
                    SysLookupMEF lok = (SysLookupMEF) SysReflection.getValueField(objExplore, declaredField);
                    value = lok.getValueOf((String) tcl.getNewValue());
                    SysReflection.setValueFieldForLookup(lok, (Object) value);
                } else if (declaredField.getType() == Integer.class) {
                    value = new Integer((String) tcl.getNewValue());
                } else {
                    value = tcl.getNewValue();
                }

                if (declaredField.getType() != SysLookupMEF.class) {
                    SysReflection.setValueField(getObjExplore(), declaredField, value);
                }

                SysGUIProject.getInstance().setChanged(true);
                SysActionsGUI.setTitle(form);

                if (objExplore.getClass() != SysProject.class) {
                    updateObjectsForm();
                }
            }
        } catch (Exception ex) {
            System.out.println("Error actionPerformed: " + ex.getMessage());
        }
    }

    public void updateObjectsForm() {
        if (getForm() != null) {
            getForm().getTbsPrincipal().repaint();
            SysActionsGUI.updateTree(getForm());

            if (objExplore.getClass() == SysGUIMEF.class) {
                getForm().getTbsPrincipal().setTitleAt(getForm().getTbsPrincipal().getSelectedIndex(), ((SysGUIMEF) objExplore).getName());
            }
        } else {
            System.out.println("UpdateObjectsForm form not found!");
        }

    }
}
