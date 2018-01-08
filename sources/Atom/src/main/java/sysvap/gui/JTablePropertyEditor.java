/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui;

import java.awt.Color;
import java.awt.Component;
import java.lang.reflect.Field;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import sysvap.core.data.SysLookupMEF;
import sysvap.gui.helper.SysActionsGUI;
import sysvap.gui.helper.SysDialogs;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;
import sysvap.gui.helper.propertyeditor.SysPropEditorAction;
import sysvap.gui.helper.propertyeditor.SysPropEditorTableModel;
import sysvap.gui.helper.propertyeditor.SysReflection;
import sysvap.gui.helper.propertyeditor.SysTableCellColorChooseFormUpdate;
import sysvap.gui.helper.propertyeditor.SysTableCellListener;
import sysvap.gui.helper.propertyeditor.cellbutton.SysTableCellButton;
import sysvap.gui.helper.propertyeditor.combofield.enumtype.SysIComboFieldEnum;
import sysvap.gui.helper.propertyeditor.combofield.enumtype.SysTableCellEditorIComboFieldEnum;
import sysvap.gui.helper.propertyeditor.combofield.enumtype.SysTableCellRendererIComboFieldEnum;
import sysvap.gui.helper.propertyeditor.combofield.syslookup.SysIComboLookup;
import sysvap.gui.helper.propertyeditor.combofield.syslookup.SysTableCellEditorIComboFieldLookup;
import sysvap.gui.helper.propertyeditor.combofield.syslookup.SysTableCellRendererIComboFieldLookup;

/**
 *
 * @author Ivo
 */
public final class JTablePropertyEditor extends JTable {

    private SysIPropEditor objExplore;
    private SysView form;
    private SysTableCellListener tcl;
    private SysPropEditorAction action;

    /**
     * Creates new form JPanelPropertyEditor
     */
    public JTablePropertyEditor() {
        initComponents();

        //  Create the table with default data
        this.setShowGrid(false);
        this.setShowVerticalLines(true);
        this.setShowHorizontalLines(true);
        this.setDefaultRenderer(Object.class, getRender());
        this.getTableHeader().setReorderingAllowed(false);

        this.action = new SysPropEditorAction(getObjExplore(), getForm());
        this.tcl = new SysTableCellListener(this, action);

        cleanObject();
    }

    public void cleanObject() {
        Object[][] dataProperties = null;
        String[] columnNames = {"Type", "Value"};

        SysPropEditorTableModel model = new SysPropEditorTableModel(dataProperties, columnNames);
        this.setModel(model);
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
        SysActionsGUI.updateActionOnObject(getForm());
        getForm().setEnableTxtActionOnEnter(false);
        getForm().setTxtActionOnEnter(null);
        getForm().setEnableTxtActionOnExit(false);
        getForm().setTxtActionOnExit(null);
        getForm().setEnableTxtActionOnUnrecognizedEvent(false);
        getForm().setTxtActionOnUnrecognizedEvent(null);


        this.objExplore = objExplore;
        Object[][] dataProperties = null;
        String[] columnNames = {"Type", "Value"};

        if (objExplore != null) {
            List<String> fields = getObjExplore().getFieldsName();
            dataProperties = new Object[fields.size()][2];
            try {
                for (int i = 0; i < fields.size(); i++) {
                    String field = fields.get(i);
                    if (fields.get(i).endsWith("*")) {
                        field = field.substring(0, field.length() - 1);
                    }

                    Field fld = SysReflection.getFieldByName(objExplore, field);
                    if (fld != null) {
                        Object retObj = SysReflection.getValueField(getObjExplore(), fld);
                        dataProperties[i][0] = fields.get(i);
                        dataProperties[i][1] = retObj;
                    }
                }

                //Verifica se o objeto possui o campo action para obter valor
                Field field = SysReflection.getFieldByName(this.objExplore, "actionOnEnter");
                if (field != null) {
                    String valueField = (String) SysReflection.getValueField(this.objExplore, field);
                    getForm().setTxtActionOnEnter(valueField);
                    getForm().setEnableTxtActionOnEnter(true);
                }

                //Verifica se o objeto possui o campo action para obter valor
                field = SysReflection.getFieldByName(this.objExplore, "actionOnExit");
                if (field != null) {
                    String valueField = (String) SysReflection.getValueField(this.objExplore, field);
                    getForm().setTxtActionOnExit(valueField);
                    getForm().setEnableTxtActionOnExit(true);
                }
                
                //Verifica se o objeto possui o campo action para obter valor
                field = SysReflection.getFieldByName(this.objExplore, "actionOnUnrecognizedEvent");
                if (field != null) {
                    String valueField = (String) SysReflection.getValueField(this.objExplore, field);
                    getForm().setTxtActionOnUnrecognizedEvent(valueField);
                    getForm().setEnableTxtActionOnUnrecognizedEvent(true);
                }
                
                
            } catch (Exception ex) {
                SysDialogs.showError("Error setObjExplore: " + ex.getMessage());
            }
        }

        SysPropEditorTableModel model = new SysPropEditorTableModel(dataProperties, columnNames);
        this.setModel(model);

        this.action.setObjExplore(getObjExplore());
        this.action.setForm(getForm());
    }

    public DefaultTableCellRenderer getRender() {
        return new DefaultTableCellRenderer() {

            private Color whiteColor = new Color(255, 255, 255);
            private Color alternateColor = new Color(215, 240, 255);
            private Color selectedColor = new Color(51, 153, 255);

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean selected, boolean focused, int row,
                    int column) {

                super.getTableCellRendererComponent(table, value, selected,
                        focused, row, column);

                Color bg;
                if (!selected) {
                    bg = (column == 0 ? alternateColor : whiteColor);
                } else {
                    bg = selectedColor;
                }

                setBackground(bg);
                setForeground(selected ? Color.white : Color.black);

                if (value instanceof ImageIcon) {
                    setIcon((ImageIcon) value);
                    setText("");
                } else {
                    setIcon(null);
                }
                return this;
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {}
            },
            new String [] {

            }
        ));
        setName("Form"); // NOI18N
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });
    }// </editor-fold>//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        SysActionsGUI.KeyReleased(evt, getForm());
    }//GEN-LAST:event_formKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        try {
            if (column == 0) {
                return getDefaultEditor(String.class);
            } else {
                String fieldName = this.getModel().getValueAt(row, 0).toString();
                Field fld = SysReflection.getFieldByName(getObjExplore(), fieldName);
                //Verifica se deve criar combo para enumerados
                if (SysReflection.hasInterface(fld, SysIComboFieldEnum.class)) {
                    return new SysTableCellEditorIComboFieldEnum(fld.getType(), SysReflection.getValueField(objExplore, fld));
                } else if (SysReflection.hasInterface(fld, SysIComboLookup.class)) {
                    return new SysTableCellEditorIComboFieldLookup(SysReflection.getValueField(objExplore, fld));
                } else if (fld.getType() == Color.class) {
                    return new SysTableCellButton(new SysTableCellColorChooseFormUpdate(form));
                } else {
                    return getDefaultEditor(fld.getType());
                }

            }
        } catch (Exception ex) {
            System.out.println("Error getCellEditor: " + ex.getMessage());
        }
        return getDefaultEditor(String.class);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        try {
            if (column == 0) {
                return getDefaultRenderer(String.class);
            } else {
                String fieldName = this.getModel().getValueAt(row, 0).toString();
                Field fld = SysReflection.getFieldByName(getObjExplore(), fieldName);

                //Verifica se deve criar combo para enumerados
                if (SysReflection.hasInterface(fld, SysIComboFieldEnum.class)) {
                    return new SysTableCellRendererIComboFieldEnum(fld.getType());
                } else if (SysReflection.hasInterface(fld, SysIComboLookup.class)) {
                    return new SysTableCellRendererIComboFieldLookup((SysLookupMEF) SysReflection.getValueField(objExplore, fld));
                } else if (fld.getType() == Color.class) {
                    return new SysTableCellButton(new SysTableCellColorChooseFormUpdate(form));
                } else {
                    return getDefaultRenderer(fld.getType());
                }
            }

        } catch (Exception ex) {
            System.out.println("Error getCellRenderer: " + ex.getMessage());
        }
        return getDefaultRenderer(String.class);
    }
}
