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
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysProject;
import sysvap.core.data.SysState;
import sysvap.core.events.SysEvent;
import sysvap.core.events.SysEventState;
import sysvap.core.events.SysValidationPlan;
import sysvap.core.simulation.SysSimulateHelper;
import sysvap.gui.core.data.SysGUIProject;
import sysvap.gui.helper.SysActionsGUI;
import sysvap.gui.helper.scriptdebugging.SysScriptDebuggingTableModel;

/**
 *
 * @author Ivo
 */
public final class JTableScriptDebugging extends JTable {

    private SysValidationPlan objValidationPlan;
    private SysView form;

    /**
     * Creates new form JPanelPropertyEditor
     */
    public JTableScriptDebugging() {
        initComponents();

        //  Create the table with default data
        this.setShowGrid(false);
        this.setShowVerticalLines(true);
        this.setShowHorizontalLines(true);
        this.setDefaultRenderer(Object.class, getRender());
        this.getTableHeader().setReorderingAllowed(false);

        cleanObjValidationPlan();
    }

    public SysView getForm() {
        return form;
    }

    public void setForm(SysView form) {
        this.form = form;
    }

    public SysValidationPlan getObjValidationPlan() {
        return objValidationPlan;
    }

    public SysValidationPlan cleanObjValidationPlan() {

        SysMEF sysMEF = (SysMEF) SysGUIProject.getInstance().getMainMEF().getSelectedItem();
        SysState sysState = SysSimulateHelper.getInitialState(sysMEF);

        SysEventState initialState = null;
        if (sysState != null) {
            initialState = new SysEventState(sysState.getName(), null, null);
        }
        objValidationPlan = new SysValidationPlan(initialState, null, null);
        setObjScriptDebugging(objValidationPlan);

        return objValidationPlan;
    }

    public void setObjScriptDebugging(SysValidationPlan objValidationPlan) {
        this.objValidationPlan = objValidationPlan;
        Object[][] dataProperties = null;
        String[] columnNames = {"Event", "Delay", "Rate"};

        if (objValidationPlan != null) {
            List<SysEvent> sysEvents = getObjValidationPlan().getPlan();
            if (sysEvents != null) {
                dataProperties = new Object[sysEvents.size()][3];

                for (int i = 0; i < sysEvents.size(); i++) {
                    SysEvent sysEvent = sysEvents.get(i);
                    dataProperties[i][0] = sysEvent.getEvent();
                    dataProperties[i][1] = sysEvent.getMilisecDelay();
                    dataProperties[i][2] = sysEvent.getRateDelay();
                }
            }
        }

        SysScriptDebuggingTableModel model = new SysScriptDebuggingTableModel(dataProperties, columnNames);
        this.setModel(model);
    }

    public SysEventState getStateInital(SysMEF selectedMEF, boolean isMainMEF) {
        SysState initialState = selectedMEF.getInitialState();
        SysEventState sysEventState = null;
        if (initialState != null) {
            sysEventState = new SysEventState(initialState.getName(), null, null);
            if (!isMainMEF) {
                sysEventState.setMef(selectedMEF.getName());
            }


            SysMEF subMEF01 = (SysMEF) initialState.getSub_MEF01().getSelectedItem();
            if (subMEF01 != null) {
                sysEventState.setSubState01(getStateInital(subMEF01, false));
            }

            SysMEF subMEF02 = (SysMEF) initialState.getSub_MEF02().getSelectedItem();
            if (subMEF02 != null) {
                sysEventState.setSubState02(getStateInital(subMEF02, false));
            }
        }
        return sysEventState;
    }

    public SysEventState getStateFinal(SysMEF selectedMEF, boolean isMainMEF) {
        List<SysState> finalStateList = selectedMEF.getFinalState();

        SysState finalState = null;

        //Obtem o primeiro estado final da lista.
        if (finalStateList.size() > 0) {
            finalState = finalStateList.get(0);
        }

        SysEventState sysEventState = null;
        if (finalState != null) {
            sysEventState = new SysEventState(finalState.getName(), null, null);
            if (!isMainMEF) {
                sysEventState.setMef(selectedMEF.getName());
            }

            SysMEF subMEF01 = (SysMEF) finalState.getSub_MEF01().getSelectedItem();
            if (subMEF01 != null) {
                sysEventState.setSubState01(getStateInital(subMEF01, false));
            }

            SysMEF subMEF02 = (SysMEF) finalState.getSub_MEF02().getSelectedItem();
            if (subMEF02 != null) {
                sysEventState.setSubState02(getStateInital(subMEF02, false));
            }
        }
        return sysEventState;
    }

    public SysValidationPlan getModelSysValPlan() {
        SysScriptDebuggingTableModel model = (SysScriptDebuggingTableModel) getModel();
        SysValidationPlan sysVa = model.getValidationPlan();

        //Setando o estado inicial da mef principal
        SysMEF sysMef = (SysMEF) SysGUIProject.getInstance().getMainMEF().getSelectedItem();
        //if (objValidationPlan.getMainMefInitialState() != null) {
        //    sysVa.setMainMefInitialState(objValidationPlan.getMainMefInitialState());
        //} else {
            if (sysMef != null) {
                sysVa.setMainMefInitialState(getStateInital(sysMef, true));
            }
        //}

        //Setando o estado final da mef principal
        //if (objValidationPlan.getMainMefFinalState() != null) {
        //    sysVa.setMainMefFinalState(objValidationPlan.getMainMefFinalState());
        //} else {
            if (sysMef != null) {
                sysVa.setMainMefFinalState(getStateFinal(sysMef, true));
            }
        //}

        //Setando o estado inicial das outras mefs
        //if (objValidationPlan.getMefInitial() != null) {
        //    sysVa.setMefInitial(objValidationPlan.getMefInitial());
        //} else {
            sysVa.setMefInitial(new ArrayList<SysEventState>());
            for (SysMEF itemMEF : SysGUIProject.getInstance().getMEFs()) {
                sysVa.getMefInitial().add(getStateInital(itemMEF, false));
            }
        //}
        
        //Setando o estado final das outras mefs
        //if (objValidationPlan.getMefFinal() != null) {
        //    sysVa.setMefFinal(objValidationPlan.getMefFinal());
        //} else {
            sysVa.setMefFinal(new ArrayList<SysEventState>());
            for (SysMEF itemMEF : SysGUIProject.getInstance().getMEFs()) {
                sysVa.getMefFinal().add(getStateFinal(itemMEF, false));
            }
        //}

        return sysVa;
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
            switch (column) {
                case 0:
                    return getDefaultEditor(String.class);
                case 1:
                    return getDefaultEditor(Integer.class);
                case 2:
                    return getDefaultEditor(Integer.class);
            }


        } catch (Exception ex) {
            System.out.println("Error getCellEditor: " + ex.getMessage());
        }
        return getDefaultEditor(String.class);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        try {
            switch (column) {
                case 0:
                    return getDefaultRenderer(String.class);
                case 1:
                    return getDefaultRenderer(Integer.class);
                case 2:
                    return getDefaultRenderer(Integer.class);
            }
        } catch (Exception ex) {
            System.out.println("Error getCellRenderer: " + ex.getMessage());
        }
        return getDefaultRenderer(String.class);
    }
}
