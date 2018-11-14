/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.gui.helper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreeModel;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysProject;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;
import sysvap.core.data.SysTypeState;
import sysvap.core.events.SysEvent;
import sysvap.core.events.SysEventState;
import sysvap.core.events.SysValidationPlan;
import sysvap.core.simulation.SysSimulation;
import sysvap.gui.JPanelView;
import sysvap.gui.SysView;
import sysvap.gui.core.data.SysGUIMEF;
import sysvap.gui.core.data.SysGUIProject;
import sysvap.gui.core.data.SysGUIState;
import sysvap.gui.drawer.SysDrawer;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;
import sysvap.gui.helper.propertyeditor.SysReflection;
import sysvap.gui.helper.scriptdebugging.SysScriptDebuggingTableModel;
import sysvap.gui.helper.treeview.SysProjectTreeModel;
import sysvap.gui.helper.vartwatch.SysVarWatchTableModel;

public class SysActionsGUI {

    public static void setTitle(SysView form) {
        String nameProject = SysConstants.NAME_DEFAULT_PROJECT;
        if (SysGUIProject.getInstance().getNameProject() != null) {
            nameProject = SysGUIProject.getInstance().getNameProject();
        }

        if (SysGUIProject.getInstance().getChanged()) {
            nameProject = nameProject + " (*)";

        }

        form.getFrame().setTitle("Atom 1.1 - " + nameProject);
    }

    public static String getNameProject() {
        if (SysGUIProject.getInstance().getNameProject() == null) {
            return "";
        } else {
            return SysGUIProject.getInstance().getNameProject();
        }
    }

    public static void addNewTab(SysView form, JTabbedPane tbsPrincipal, String name) {
        if (name == null || name.equals("")) {
            name = "MEF" + SysGUIProject.getInstance().getNextIdMEF();
        }

        if (SysGUIProject.getInstance().getNextIdMEF() != 0) {
            SysGUIProject.getInstance().setChanged(true);
        }
        JPanelView paneVisu = new JPanelView(form, tbsPrincipal, name);
        paneVisu.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(paneVisu);
        scrollPane.setBorder(null);
        scrollPane.setFocusable(false);
        tbsPrincipal.add(name, scrollPane);
        paneVisu.requestFocus();

        setTitle(form);

        //Atualiza o treeview
        TreeModel model = form.getJtrProject().getModel();
        if (model != null) {
            SysProjectTreeModel treeModel = (SysProjectTreeModel) form.getJtrProject().getModel();
            treeModel.addChildMEF(paneVisu.getSysMEF());
        }
    }

    public static void delTab(SysView form, JTabbedPane tbsPrincipal) {
        if (tbsPrincipal.getSelectedIndex() >= 0) {
            SysGUIMEF sysGUIMEF = (SysGUIMEF) SysGUIProject.getInstance().getSelectedMEF();

            if (!SysGUIProject.getInstance().mefInUse(sysGUIMEF)) {
                int response = SysDialogs.getUserConfirmation("Do you confirm the deletion of the tab?");
                if (response == JOptionPane.YES_OPTION) {
                    //Atualiza o treeview
                    TreeModel model = form.getJtrProject().getModel();
                    if (model != null) {
                        SysProjectTreeModel treeModel = (SysProjectTreeModel) form.getJtrProject().getModel();
                        treeModel.delChildMEF((SysGUIMEF) SysGUIProject.getInstance().getSelectedMEF());
                    }

                    tbsPrincipal.remove(tbsPrincipal.getSelectedIndex());
                    SysGUIProject.getInstance().delMEF(SysGUIProject.getInstance().getSelectedMEF());
                }
            } else {
                SysDialogs.showAlert("MEF '" + sysGUIMEF.getName() + "' can't be deleted!");
            }
        }


        if (tbsPrincipal.getTabCount() == 0) {
            addNewTab(form, tbsPrincipal, null);
        }
    }

    public static void addState(SysView form, JPanelView jPanelView, int x, int y) {
        SysState state = SysGUIProject.getInstance().getSelectedMEF().addState(SysDrawerHelper.getCenterMouse(x), SysDrawerHelper.getCenterMouse(y));
        Dimension dimension = form.getFrame().getSize();
        SysDrawerHelper.setDimension(jPanelView, dimension, new Point(x, y));
        setTitle(form);
        addStateUpdateTreeView(form, state);
    }

    public static void deleteObjectsMEF(SysView form, JTabbedPane tbsPrincipal) {
        //Atualiza o treeview
        if (SysGUIProject.getInstance().getSelectedMEF().getSelectedState() != null) {
            TreeModel model = form.getJtrProject().getModel();
            if (model != null) {
                SysProjectTreeModel treeModel = (SysProjectTreeModel) form.getJtrProject().getModel();
                treeModel.delChildState((SysGUIMEF) SysGUIProject.getInstance().getSelectedMEF(), (SysGUIState) SysGUIProject.getInstance().getSelectedMEF().getSelectedState());
            }

        }

        tbsPrincipal.repaint();
        SysGUIProject.getInstance().getSelectedMEF().delObjectsMEF();
        tbsPrincipal.repaint();
    }

    public static void newProject(SysView form, JTabbedPane tbsPrincipal, boolean checkChange) {
        if (checkChange) {
            if (SysGUIProject.getInstance().getChanged()) {
                int response = SysDialogs.getUserConfirmation("Project was modified. Do you want to save it?");
                if (response == JOptionPane.YES_OPTION) {
                    SaveProject(form);
                }
            }
        }

        tbsPrincipal.removeAll();
        SysGUIProject.NewProject();
        setTitle(form);
        form.getJtrProject().setModel(new SysProjectTreeModel(SysGUIProject.getInstance().getMEFs()));
        addNewTab(form, tbsPrincipal, null);
        tbsPrincipal.repaint();
        cleanGUIProject(form);
    }

    public static void SaveProject(SysView form) {
        if (SysGUIProject.getInstance().getNameProject() == null) {
            SaveAsProject(form);
        } else {
            try {
                updateActionOnObject(form);
                SysGUIProject.SaveToFile(SysGUIProject.getInstance(), SysGUIProject.getInstance().getNameProject(), SysGUIProject.getInstance().getPathProject());
                if (form != null) {
                    setTitle(form);
                }
            } catch (Exception ex) {
                SysDialogs.showError("Erro to save project: " + ex.getMessage());
            }
        }
    }

    public static void SaveAsProject(SysView form) {
        JFileChooser c = new JFileChooser();
        FileFilter filter1 = new SysExtensionFileFilter("SysVap", new String[]{"VAP"});
        c.setFileFilter(filter1);
        int rVal = c.showSaveDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                updateActionOnObject(form);
                String fileName = c.getSelectedFile().getName();
                String filePath = c.getSelectedFile().getAbsolutePath().toString();
                if (!fileName.toLowerCase().endsWith(".vap")) {
                    fileName = fileName + ".vap";
                    filePath = filePath + ".vap";
                }

                File file = new File(filePath);
                if (file.exists()) {
                    int response = SysDialogs.getUserConfirmation("The project already exists. Do you want to overwrite?");
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                SysGUIProject.SaveToFile(SysGUIProject.getInstance(), fileName, filePath);
                if (form != null) {
                    setTitle(form);
                    form.getJtrProject().setModel(new SysProjectTreeModel(SysGUIProject.getInstance().getMEFs()));
                }
            } catch (Exception ex) {
                SysDialogs.showError("Erro to save project: " + ex.getMessage());
            }
        }
    }

    public static void OpenProject(SysView form, JTabbedPane tbsPrincipal) throws HeadlessException {
        if (SysGUIProject.getInstance().getChanged()) {
            int response = SysDialogs.getUserConfirmation("Project was modified. Do you want to save it?");
            if (response == JOptionPane.YES_OPTION) {
                SaveProject(form);
            }
        }

        JFileChooser c = new JFileChooser(".");
        FileFilter filter1 = new SysExtensionFileFilter("SysVap", new String[]{"VAP"});
        c.setFileFilter(filter1);
        int rVal = c.showOpenDialog(form.getFrame());
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                tbsPrincipal.removeAll();
                SysGUIProject sysProjectLFFile = (SysGUIProject) SysGUIProject.LoadFromFile(SysGUIProject.getInstance(), c.getSelectedFile().getAbsoluteFile().toString());
                SysGUIProject.setInstance(sysProjectLFFile);
                sysProjectLFFile.setPathProject(c.getSelectedFile().getAbsoluteFile().toString());

                for (SysMEF sysMEF : SysGUIProject.getInstance().getMEFs()) {
                    if (sysMEF != null) {
                        JPanelView paneVisu = new JPanelView(form, tbsPrincipal, (SysGUIMEF) sysMEF);
                        JScrollPane scrollPane = new JScrollPane(paneVisu);
                        tbsPrincipal.add(sysMEF.getName(), scrollPane);
                    }
                }

                setTitle(form);
                form.getBtnSelectMouse().setSelected(true);
                SysGUIProject.getInstance().setSelectedTool(SysTool.TOOL_MOUSE);
                tbsPrincipal.repaint();
            } catch (Exception ex) {
                SysDialogs.showError("Invalid file: " + ex.getMessage());
            }
        }

        form.getJtrProject().setModel(new SysProjectTreeModel(SysGUIProject.getInstance().getMEFs()));
        cleanGUIProject(form);
    }

    public static void copyState() {
        SysGUIState sysState = (SysGUIState) SysGUIProject.getInstance().getSelectedMEF().getSelectedState();
        if (sysState != null) {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection stringState = new StringSelection(sysState.serializeToString());
                clipboard.setContents(stringState, null);
            } catch (Exception ex) {
            }
        }
    }

    public static void pasteState(JTabbedPane tbsPrincipal, SysView form) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(form);
        if ((contents != null) && (contents.isDataFlavorSupported(DataFlavor.stringFlavor))) {
            try {
                String stringState = (String) contents.getTransferData(DataFlavor.stringFlavor);
                SysGUIState sysState = SysGUIState.deserializeFromString(stringState);

                SysGUIProject.getInstance().getSelectedMEF().addState(sysState);
                tbsPrincipal.repaint();

                addStateUpdateTreeView(form, sysState);
            } catch (Exception ex) {
            }
        }
    }

    public static void moveStateToRight(int step, JTabbedPane tbsPrincipal) {
        if (SysGUIProject.getInstance().getSelectedMEF().getSelectedState() != null) {
            int x = SysGUIProject.getInstance().getSelectedMEF().getSelectedState().getX();
            x = x + step;
            SysGUIProject.getInstance().getSelectedMEF().getSelectedState().setX(x);
            tbsPrincipal.repaint();
        }
    }

    public static void moveStateToLeft(int step, JTabbedPane tbsPrincipal) {
        if (SysGUIProject.getInstance().getSelectedMEF().getSelectedState() != null) {
            int x = SysGUIProject.getInstance().getSelectedMEF().getSelectedState().getX();
            x = x - step;
            if (x < 0) {
                x = 0;
            }
            SysGUIProject.getInstance().getSelectedMEF().getSelectedState().setX(x);
            tbsPrincipal.repaint();
        }
    }

    public static void moveStateToUp(int step, JTabbedPane tbsPrincipal) {
        if (SysGUIProject.getInstance().getSelectedMEF().getSelectedState() != null) {
            int y = SysGUIProject.getInstance().getSelectedMEF().getSelectedState().getY();
            y = y - step;
            if (y < 0) {
                y = 0;
            }
            SysGUIProject.getInstance().getSelectedMEF().getSelectedState().setY(y);
            tbsPrincipal.repaint();
        }
    }

    public static void moveStateToDown(int step, JTabbedPane tbsPrincipal) {
        if (SysGUIProject.getInstance().getSelectedMEF().getSelectedState() != null) {
            int y = SysGUIProject.getInstance().getSelectedMEF().getSelectedState().getY();
            y = y + step;
            SysGUIProject.getInstance().getSelectedMEF().getSelectedState().setY(y);
            tbsPrincipal.repaint();
        }
    }

    public static void selectNextState(JTabbedPane tbsPrincipal) {
        SysGUIProject.getInstance().getSelectedMEF().setSelectedNextObject();
        tbsPrincipal.repaint();
    }

    public static void selectPreviusState(JTabbedPane tbsPrincipal) {
        SysGUIProject.getInstance().getSelectedMEF().setSelectedPreviusObject();
        tbsPrincipal.repaint();
    }

    private static void addStateUpdateTreeView(SysView form, SysState state) {
        //Atualiza o treeview
        TreeModel model = form.getJtrProject().getModel();
        if (model != null) {
            SysProjectTreeModel treeModel = (SysProjectTreeModel) form.getJtrProject().getModel();
            treeModel.addChildState((SysGUIMEF) SysGUIProject.getInstance().getSelectedMEF(), (SysGUIState) state);
        }
    }

    public static void treeValueChanged(SysView form, JTabbedPane tbsPrincipal) {

        Object selected = form.getJtrProject().getLastSelectedPathComponent();

        if ((selected instanceof String) && (SysGUIProject.getInstance().toString().equals((String) selected))) {
            //projeto
            form.getjPropertyEditor().setObjExplore(SysGUIProject.getInstance());
        } else if (selected instanceof SysMEF) {
            SysMEF sysMEF = (SysMEF) selected;
            int indexOfTab = tbsPrincipal.indexOfTab(sysMEF.getName());
            tbsPrincipal.setSelectedIndex(indexOfTab);
            form.getjPropertyEditor().setObjExplore(sysMEF);
        } else if (selected instanceof SysState) {
            SysState sysState = (SysState) selected;
            sysState.setChecked(true);
            int indexOfTab = tbsPrincipal.indexOfTab(sysState.getParentSysMEF().getName());
            tbsPrincipal.setSelectedIndex(indexOfTab);
            tbsPrincipal.repaint();
        }
    }

    public static void ExportAsProject(SysView form) {
        JFileChooser c = new JFileChooser();
        FileFilter filter1 = new SysExtensionFileFilter("Export SysVap", new String[]{"EVAP"});
        c.setFileFilter(filter1);
        int rVal = c.showSaveDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                updateActionOnObject(form);
                String fileName = c.getSelectedFile().getName();
                String filePath = c.getSelectedFile().getAbsolutePath().toString();
                if (!fileName.toLowerCase().endsWith(".evap")) {
                    fileName = fileName + ".evap";
                    filePath = filePath + ".evap";
                }

                File file = new File(filePath);
                if (file.exists()) {
                    int response = SysDialogs.getUserConfirmation("The export project already exists. Do you want to overwrite?");
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                SysProject exportProject = SysTransportSim.sysGUIProjectToSysProject(SysGUIProject.getInstance());
                SysProject.SaveToFile(exportProject, fileName, filePath);

                if (form != null) {
                    setTitle(form);
                    form.getJtrProject().setModel(new SysProjectTreeModel(SysGUIProject.getInstance().getMEFs()));
                }
            } catch (Exception ex) {
                SysDialogs.showError("Erro to export project: " + ex.getMessage());
            }
        }
    }

    public static void updateActionOnObject(SysView form) {
        if (form != null) {
            SysIPropEditor objExplore = form.getjPropertyEditor().getObjExplore();

            if (objExplore != null) {
                Field field = SysReflection.getFieldByName(objExplore, "actionOnEnter");
                if (field != null) {
                    try {
                        SysReflection.setValueField(objExplore, field, form.getTxtActionOnEnter());
                    } catch (Exception ex) {
                        SysDialogs.showError("Error update action: " + ex.getMessage());
                    }
                }

                field = SysReflection.getFieldByName(objExplore, "actionOnExit");
                if (field != null) {
                    try {
                        SysReflection.setValueField(objExplore, field, form.getTxtActionOnExit());
                    } catch (Exception ex) {
                        SysDialogs.showError("Error update action: " + ex.getMessage());
                    }
                }

                field = SysReflection.getFieldByName(objExplore, "actionOnUnrecognizedEvent");
                if (field != null) {
                    try {
                        SysReflection.setValueField(objExplore, field, form.getTxtActionOnUnrecognizedEvent());
                    } catch (Exception ex) {
                        SysDialogs.showError("Error update action: " + ex.getMessage());
                    }
                }
            }
        }
    }

    public static void execSimulation(SysView form) {
        SysGUIMEF sysMEF = (SysGUIMEF) SysGUIProject.getInstance().getMainMEF().getSelectedItem();
        if (sysMEF.getSysStates().size() > 0) {
            updateActionOnObject(form);

            int rVal = JFileChooser.APPROVE_OPTION;

            SysScriptDebuggingTableModel model = (SysScriptDebuggingTableModel) form.getJtbScriptDebugging().getModel();
            if (model.getRowCount() == 0) {
                if (form.getTxtScript().getText().equals("")) {
                    rVal = loadScript(form);
                    if (rVal == JFileChooser.APPROVE_OPTION) {
                        showDebugPanel(form);
                    }
                }
            }

            if (rVal == JFileChooser.APPROVE_OPTION) {
                debugSimulation(form);
                debugContinueSimulation(form);
            }
        }
    }

    public static void debugSimulation(SysView form) {
        SysGUIMEF sysMEF = (SysGUIMEF) SysGUIProject.getInstance().getMainMEF().getSelectedItem();
        if (sysMEF.getSysStates().size() > 0) {
            updateActionOnObject(form);
            try {
                if (form.getJtbScriptDebugging().getRowCount() > 0) {
                    form.getJtbScriptDebugging().setRowSelectionInterval(0, 0);
                }

                form.getTbsOutput().setSelectedIndex(0);
                SysProject exportProject = SysTransportSim.sysGUIProjectToSysProject(SysGUIProject.getInstance());
                SysSimulation simulation = new SysSimulation(exportProject);
                form.setSysSimulation(simulation);
                showDebugPanel(form);
                form.getTxtScriptEvent().setText("");
                form.getTxtScriptEvent().requestFocus();
                System.out.println("");
                System.out.println(SysConstants.OUT_ALERT + "Started debug simulation - " + getNameProject());
                System.out.println(SysConstants.OUT_ALERT + "--------------------------------------------------");

                simulation.setSysSimListener(new SysSimulationListener(form));

                if (simulation.init()) {
                    form.startDebug();
                } else {
                    form.getSysSimulation().release();
                    form.setSysSimulation(null);
                }
            } catch (Exception ex) {
                form.stopDebug();
                SysDialogs.showError("Error on simulation: " + ex.getMessage());
            }

        }
    }

    public static void debugAddSendEvent(SysView form) {
        if (form.getSysSimulation() != null) {
            if (!form.getTxtScriptEvent().getText().equals("")) {
                SysEvent sysEvent = debugAddEvent(form);
                int index = form.getJtbScriptDebugging().getSelectedRow();
                sendGUIEvent(form, sysEvent, index, false, false);
            }
        }
    }

    public static SysEvent debugAddEvent(SysView form) {
        if (!form.getTxtScriptEvent().getText().equals("")) {
            Integer delay = null;
            if (!form.getTxtScriptDelay().getText().equals("")) {
                delay = Integer.parseInt(form.getTxtScriptDelay().getText());
            }

            Integer rate = null;
            if (!form.getTxtScriptRate().getText().equals("")) {
                rate = Integer.parseInt(form.getTxtScriptRate().getText());
            }

            SysEvent sysEvent = new SysEvent(form.getTxtScriptEvent().getText(), delay, rate);
            SysScriptDebuggingTableModel model = (SysScriptDebuggingTableModel) form.getJtbScriptDebugging().getModel();
            model.addEventRow(sysEvent);

            form.getTxtScriptEvent().setText("");
            form.getTxtScriptEvent().requestFocus();
            return sysEvent;
        }
        return null;
    }

    public static void debugDeleteEvent(SysView form) {
        if (form.getJtbScriptDebugging().getSelectedRow() >= 0) {
            if (SysDialogs.getUserConfirmation("Are you sure you want to delete event?") == JOptionPane.YES_OPTION) {
                SysScriptDebuggingTableModel model = (SysScriptDebuggingTableModel) form.getJtbScriptDebugging().getModel();
                model.removeRow(form.getJtbScriptDebugging().getSelectedRow());
            }
        }
    }

    public static void debugStopSimulation(SysView form, boolean showConfirm) {
        if (form.getSysSimulation() != null) {
            if (form.isInDebug()) {
                if (showConfirm) {
                    if (SysDialogs.getUserConfirmation("Are you sure you want to stop simulation?") != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                form.getSysSimulation().release();
                form.setSysSimulation(null);
                form.stopDebug();
                System.out.println("");
                System.out.println(SysConstants.OUT_ALERT + "Finished debug simulation - " + getNameProject());
                System.out.println(SysConstants.OUT_ALERT + "--------------------------------------------------");
            }
        }
    }

    private static void sendGUIEvent(SysView form, SysEvent sysEvent, int index, boolean stepAllItens, boolean stopOnfinish) {
        //Inicia a simulação
        int delay = 0;
        Integer rateDelay = form.getSysSimulation().getSysProject().getRateDelay();

        if (sysEvent.getRateDelay() != null) {
            rateDelay = sysEvent.getRateDelay();
        }

        if (sysEvent.getMilisecDelay() != null) {
            delay = sysEvent.getMilisecDelay();
            if (rateDelay != null) {
                delay = delay / rateDelay;
            }
        }

        Timer sendEventScheduler = new Timer();
        SysGUISendEvent sysSendEvent = new SysGUISendEvent(form, sysEvent, index, stepAllItens, stopOnfinish);
        sendEventScheduler.schedule(sysSendEvent, delay);

    }

    public static void debugStepSimulation(SysView form, boolean stepAllItens) {
        if (form.getSysSimulation() != null) {
            if (form.getJtbScriptDebugging().getRowCount() > 0) {
                int index = form.getJtbScriptDebugging().getSelectedRow();
                if (form.getJtbScriptDebugging().getRowCount() > index) {
                    if (index == -1) {
                        index = 0;
                    }

                    String event = null;
                    Integer delay = null;
                    Integer rate = null;
                    if (form.getJtbScriptDebugging().getModel().getValueAt(index, 0) != null) {
                        event = (String) form.getJtbScriptDebugging().getModel().getValueAt(index, 0);
                    }


                    Object obj = form.getJtbScriptDebugging().getModel().getValueAt(index, 1);
                    if (obj != null) {
                        if (obj.getClass() == Integer.class) {
                            delay = (Integer) obj;
                        } else {
                            if (!((String) obj).equals("")) {
                                delay = Integer.parseInt((String) obj);
                            }
                        }
                    }


                    obj = form.getJtbScriptDebugging().getModel().getValueAt(index, 2);
                    if (obj != null) {
                        if (obj.getClass() == Integer.class) {
                            rate = (Integer) obj;
                        } else {
                            if (!((String) obj).equals("")) {
                                rate = Integer.parseInt((String) obj);
                            }
                        }
                    }

                    SysEvent sysEvent = new SysEvent(event, delay, rate);
                    sendGUIEvent(form, sysEvent, index, stepAllItens, true);
                }
            }
        }
    }

    public static int loadScript(SysView form) {
        JFileChooser c = new JFileChooser(".");
        FileFilter filter1 = new SysExtensionFileFilter("SisVAP - Plan (*.xml)", new String[]{"XML"});
        c.setFileFilter(filter1);
        int rVal = c.showOpenDialog(form.getFrame());
        if (rVal == JFileChooser.APPROVE_OPTION) {
            try {
                form.getTxtScript().setText(c.getSelectedFile().getAbsoluteFile().toString());
                SysValidationPlan sysValidationPlan = SysValidationPlan.LoadFromXML(c.getSelectedFile().getAbsoluteFile().toString());
                form.getJtbScriptDebugging().setObjScriptDebugging(sysValidationPlan);

                setStateTypeOnProject(form, sysValidationPlan);

                form.getTxtScriptEvent().setText("");
                form.getTxtScriptEvent().requestFocus();
            } catch (Exception ex) {
                SysDialogs.showError("Error on load script: " + ex.getMessage());
            }
        }
        return rVal;
    }

    public static void setStateTypeOnProject(SysView form, SysValidationPlan sysValidationPlan) {
        SysMEF sysMef = (SysMEF) SysGUIProject.getInstance().getMainMEF().getSelectedItem();
        if (sysMef != null) {
            if (sysValidationPlan.getMainMefInitialState() != null) {
                setStateInitalOnMEF(form, sysMef, sysValidationPlan.getMainMefInitialState());
            }
        }

        if (sysValidationPlan.getMefInitial() != null) {
            for (SysEventState itemEventState : sysValidationPlan.getMefInitial()) {
                sysMef = (SysMEF) SysGUIProject.getInstance().getMEFByName(itemEventState.getMef());
                if (sysMef != null) {
                    setStateInitalOnMEF(form, sysMef, itemEventState);
                }
            }
        }

        sysMef = (SysMEF) SysGUIProject.getInstance().getMainMEF().getSelectedItem();
        if (sysMef != null) {
            if (sysValidationPlan.getMainMefFinalState() != null) {
                setStateFinalOnMEF(form, sysMef, sysValidationPlan.getMainMefFinalState());
            }
        }

        if (sysValidationPlan.getMefFinal().size() > 0) {
            for (SysEventState itemEventState : sysValidationPlan.getMefFinal()) {
                if (itemEventState != null) {
                    sysMef = (SysMEF) SysGUIProject.getInstance().getMEFByName(itemEventState.getMef());
                    if (sysMef != null) {
                        setStateFinalOnMEF(form, sysMef, itemEventState);
                    }
                }
            }
        }
    }

    public static void setStateInitalOnMEF(SysView form, SysMEF selectedMEF, SysEventState sysEventState) {
        SysState initialState = selectedMEF.getStateByName(sysEventState.getState());
        if (initialState != null) {
            initialState.setType(SysTypeState.INITIAL);

            SysMEF subMEF01 = (SysMEF) initialState.getSub_MEF01().getSelectedItem();
            if (subMEF01 != null) {
                if (sysEventState.getSubState01() != null) {
                    setStateInitalOnMEF(form, subMEF01, sysEventState.getSubState01());
                }
            }

            SysMEF subMEF02 = (SysMEF) initialState.getSub_MEF02().getSelectedItem();
            if (subMEF02 != null) {
                if (sysEventState.getSubState02() != null) {
                    setStateInitalOnMEF(form, subMEF02, sysEventState.getSubState02());
                }
            }

            form.getTbsPrincipal().repaint();
        }
    }

    public static void setStateFinalOnMEF(SysView form, SysMEF selectedMEF, SysEventState sysEventState) {
        SysState finalState = selectedMEF.getStateByName(sysEventState.getState());
        if (finalState != null) {
            if (finalState.getType() == SysTypeState.INITIAL) {
                finalState.setType(SysTypeState.INITIAL_FINAL);
            } else {
                finalState.setType(SysTypeState.FINAL);
            }

            SysMEF subMEF01 = (SysMEF) finalState.getSub_MEF01().getSelectedItem();
            if (subMEF01 != null) {
                if (sysEventState.getSubState01() != null) {
                    setStateFinalOnMEF(form, subMEF01, sysEventState.getSubState01());
                }
            }

            SysMEF subMEF02 = (SysMEF) finalState.getSub_MEF02().getSelectedItem();
            if (subMEF02 != null) {
                if (sysEventState.getSubState02() != null) {
                    setStateFinalOnMEF(form, subMEF01, sysEventState.getSubState02());
                }
            }

            form.getTbsPrincipal().repaint();
        }
    }

    public static void saveScript(SysView form) {
        SysValidationPlan modelSysValPlan = form.getJtbScriptDebugging().getModelSysValPlan();

        if (modelSysValPlan != null) {
            JFileChooser c = new JFileChooser();
            FileFilter filter1 = new SysExtensionFileFilter("SisVAP - Plan (*.xml)", new String[]{"XML"});
            c.setFileFilter(filter1);
            int rVal = c.showSaveDialog(null);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                String filePath = c.getSelectedFile().getAbsolutePath().toString();
                if (!filePath.toLowerCase().endsWith(".xml")) {
                    filePath = filePath + ".xml";
                }

                File file = new File(filePath);
                if (file.exists()) {
                    int response = SysDialogs.getUserConfirmation("The export project already exists. Do you want to overwrite?");
                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                try {
                    modelSysValPlan.SaveToXML(filePath);
                    form.getTxtScriptEvent().setText("");
                    form.getTxtScriptEvent().requestFocus();
                } catch (Exception ex) {
                    SysDialogs.showError("Error on save script: " + ex.getMessage());
                }
            }
        }
    }

    public static void cleanGUIProject(SysView form) {
        form.getTxtScript().setText("");
        if (form.getJtbScriptDebugging() != null) {
            form.getJtbScriptDebugging().cleanObjValidationPlan();
        }
        form.stopDebug();
        if (form.getjPropertyEditor() != null) {
            form.getjPropertyEditor().cleanObject();
        }
    }

    public static void showDebugPanel(SysView form) {
        form.getJspOutput().getLeftComponent().setVisible(true);
        form.getJspOutput().setDividerSize(5);
        form.getJspOutput().setDividerLocation(300);
        form.getJspScriptDebugging().setDividerLocation(150);
    }

    public static void hideDebugPanel(SysView form) {
        form.getJspOutput().getLeftComponent().setVisible(false);
        form.getJspOutput().setDividerSize(0);
    }

    public static void updateTree(SysView form) {
        TreeModel model = form.getJtrProject().getModel();
        if (model != null) {
            SysProjectTreeModel treeModel = (SysProjectTreeModel) form.getJtrProject().getModel();
            treeModel.fireTreeStructureChanged();
        }
    }

    public static void KeyReleased(KeyEvent evt, SysView form) throws HeadlessException {
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_L:
                if (evt.isControlDown()) {
                    form.getTxtOutput().setText(null);
                }
                break;
            case KeyEvent.VK_S:
                if (evt.isControlDown() && evt.isShiftDown()) {
                    SysActionsGUI.SaveAsProject(form);
                } else if (evt.isControlDown()) {
                    SysActionsGUI.SaveProject(form);
                }
                break;
            case KeyEvent.VK_O:
                if (evt.isControlDown()) {
                    SysActionsGUI.OpenProject(form, form.getTbsPrincipal());
                }
                break;
            case KeyEvent.VK_N:
                if (evt.isControlDown()) {
                    SysActionsGUI.newProject(form, form.getTbsPrincipal(), true);
                }
                break;
            case KeyEvent.VK_F5:
                if (evt.isShiftDown()) {
                    SysActionsGUI.debugStopSimulation(form, true);
                } else if (form.isInDebug()) {
                    SysActionsGUI.debugContinueSimulation(form);
                } else if (evt.isControlDown()) {
                    SysActionsGUI.debugSimulation(form);
                } else {
                    SysActionsGUI.execSimulation(form);
                }
                break;
            case KeyEvent.VK_F8:
                if (form.isInDebug()) {
                    SysActionsGUI.debugStepSimulation(form, false);
                }
                break;
        }
    }

    public static void debugContinueSimulation(SysView form) {
        debugStepSimulation(form, true);
    }

    public static void debugClearEvent(SysView form) {
        if (form.getJtbScriptDebugging().getRowCount() > 0) {
            if (SysDialogs.getUserConfirmation("Are you sure you want to delete all events?") == JOptionPane.YES_OPTION) {
                SysScriptDebuggingTableModel model = (SysScriptDebuggingTableModel) form.getJtbScriptDebugging().getModel();
                int iTotal = form.getJtbScriptDebugging().getRowCount() - 1;
                for (int i = iTotal; i >= 0; i--) {
                    model.removeRow(i);
                }
            }
        }
    }

    public static String watchAddVar(SysView form) {
        String variable = SysDialogs.getUserString("Enter with variable:");
        if ((variable != null) && (!variable.equals(""))) {
            SysVarWatchTableModel model = (SysVarWatchTableModel) form.getJtbVarWatch().getModel();
            model.addVariableRow(variable);
        }
        return variable;

    }

    public static void watchDelVar(SysView form) {
        if (form.getJtbVarWatch().getSelectedRow() >= 0) {
            if (SysDialogs.getUserConfirmation("Are you sure you want to delete variable?") == JOptionPane.YES_OPTION) {
                SysVarWatchTableModel model = (SysVarWatchTableModel) form.getJtbVarWatch().getModel();
                model.removeRow(form.getJtbVarWatch().getSelectedRow());
            }
        }
    }

    public static void watchClearVar(SysView form) {
        if (form.getJtbVarWatch().getRowCount() > 0) {
            if (SysDialogs.getUserConfirmation("Are you sure you want to delete all variables?") == JOptionPane.YES_OPTION) {
                SysVarWatchTableModel model = (SysVarWatchTableModel) form.getJtbVarWatch().getModel();
                int iTotal = form.getJtbVarWatch().getRowCount() - 1;
                for (int i = iTotal; i >= 0; i--) {
                    model.removeRow(i);
                }
            }
        }
    }

    public static void updateVarsWatch(SysView form) {
        if (form.isInDebug() && form.getSysSimulation() != null) {
            if (form.getJtbVarWatch().getRowCount() > 0) {
                SysVarWatchTableModel model = (SysVarWatchTableModel) form.getJtbVarWatch().getModel();
                int iTotal = form.getJtbVarWatch().getRowCount() - 1;
                for (int i = iTotal; i >= 0; i--) {
                    String var = (String) model.getValueAt(i, 0);
                    String value = getValueVar(form, var);
                    model.setValueAt(value, i, 1);
                }
            }
        }
    }

    public static String getValueVar(SysView form, String variable) {
        if (form.isInDebug() && form.getSysSimulation() != null) {
            return form.getSysSimulation().getVarValueFromLUA(variable);
        }
        return null;
    }

    public static void resetVarsWatch(SysView form) {
        if (form.getJtbVarWatch().getRowCount() > 0) {
            SysVarWatchTableModel model = (SysVarWatchTableModel) form.getJtbVarWatch().getModel();
            int iTotal = form.getJtbVarWatch().getRowCount() - 1;
            for (int i = iTotal; i >= 0; i--) {
                model.setValueAt("", i, 1);
            }
        }
    }

    public static void showLUAInf() {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine e = sem.getEngineByExtension(".lua");
        ScriptEngineFactory f = e.getFactory();

        System.out.println("Script language name   : " + f.getLanguageName().toUpperCase());
        System.out.println("Script language version: " + f.getLanguageVersion());
        //System.out.println("Script engine name     : " + f.getEngineName());
        //System.out.println("Script engine version  : " + f.getEngineVersion());
    }

    public static void exportMefToJpeg(SysView form) {
        JFileChooser c = new JFileChooser();
        FileFilter filter1 = new SysExtensionFileFilter("Export image (*.png)", new String[]{"PNG"});
        c.setFileFilter(filter1);
        int rVal = c.showSaveDialog(null);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            String filePath = c.getSelectedFile().getAbsolutePath().toString();
            if (!filePath.toLowerCase().endsWith(".png")) {
                filePath = filePath + ".png";
            }

            File file = new File(filePath);
            if (file.exists()) {
                int response = SysDialogs.getUserConfirmation("The image already exists. Do you want to overwrite?");
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            Component tbComp = form.getTbsPrincipal().getComponent(form.getTbsPrincipal().getSelectedIndex());


            if (tbComp.getClass() == JScrollPane.class) {
                JPanelView pnView = (JPanelView) ((JScrollPane) tbComp).getViewport().getView();

                exportMefToJpeg(pnView, form, filePath);
            }
        }
    }

    public static void exportMefToJpeg(JPanelView panel, SysView form, String filePath) {
        try {
            int width = 0;
            int height = 0;
            for (SysState state : panel.getSysMEF().getSysStates()) {
                if (state.getX() > width) {
                    width = state.getX();
                }
                if (state.getY() > height) {
                    height = state.getY();
                }
            }

            BufferedImage imagem = new BufferedImage(width + SysConstants.BALL_DIAMETER + 50, height + SysConstants.BALL_DIAMETER + 50, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = imagem.createGraphics();

            //define a cor de fundo da imagem
            graphics.setColor(panel.getBackground());
            graphics.fillRect(0, 0, width + SysConstants.BALL_DIAMETER + 50, height + SysConstants.BALL_DIAMETER + 50);

            //Desenha a imagem
            SysDrawer.paintStates(panel, graphics, form);
            SysDrawer.paintTransitions(panel, graphics);

            Font font = new Font("Arial", Font.BOLD, 12);  // cria a fonte para escrever a frase
            graphics.setFont(font);  // estabelece a fonte que será usada a partir daqui.
            String texto = "Atom 1.1 - " + panel.getSysMEF().getName();
            graphics.drawString(texto, 5, 14);
            Rectangle2D bounds = graphics.getFontMetrics().getStringBounds(texto, graphics);
            bounds.setRect(bounds.getX() - 1, bounds.getY() + 11, bounds.getWidth() + 16, bounds.getHeight() + 5);
            graphics.draw(bounds);
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth() - 2, bounds.getHeight() + 2);
            graphics.draw(bounds);
            graphics.dispose();
            ImageIO.write(imagem, "png", new File(filePath));
        } catch (IOException e) {
            System.err.println("Error save image!");
        }
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String padCenter(String s, int size) {
        return padCenter(s, size, " ");
    }

    public static String padCenter(String s, int size, String pad) {
        if (pad == null) {
            throw new NullPointerException("pad cannot be null");
        }
        if (pad.length() <= 0) {
            throw new IllegalArgumentException("pad cannot be empty");
        }
        if (s == null || size <= s.length()) {
            return s;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }

    public static List<SysState> getAllStateOfEvent(SysState sysState, String event) {
        List<SysState> result = new ArrayList<SysState>();
        for (SysTransition transition : sysState.getSysTransitionOUT()) {
            for (SysEventTransition eventTransition : transition.getEvents()) {
                if (eventTransition.getEvent().equals(event)) {
                    result.add(transition.getNextState());
                }
            }
        }
        return result;
    }

    public static void showFormalTable() {
        SysGUIMEF sysMainMEF = (SysGUIMEF) SysGUIProject.getInstance().getSelectedMEF();

        if (sysMainMEF.getSysStates().size() > 0) {

            System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");
            System.out.println(SysConstants.OUT_ALERT + padCenter("Mathematical model", 76));
            System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");
            System.out.println("A finite state machine is a quintuple <I, Q, Q0, Z, F, O, Y>.");
            System.out.println("I  :is the input alphabet (a finite, non-empty set of symbols).");
            System.out.println("Q  :is a finite, non-empty set of states.");
            System.out.println("Q0 :is an initial state, an element of Q.");
            System.out.println("Z  :is the state-transition function: Z: Q x I = Q.");
            System.out.println("    In a nondeterministic finite automaton it would be Z: Q x I = P(Q).");
            System.out.println("F  :is the set of final states, a (possibly empty) subset of Q.");
            System.out.println("O  :is the output alphabet.");
            System.out.println("Y  :is the output function:");
            System.out.println("    Moore: Y: Q = O;");
            System.out.println("    Mealy: Y: Q x I = O.");
            System.out.println("-----------------------------------------------------------------------------");

            for (SysMEF sysMEF : SysGUIProject.getInstance().getMEFs()) {
                System.out.println("");
                if (sysMEF.equals(sysMainMEF)) {
                    System.out.println(SysConstants.OUT_ALERT + sysMEF.getName() + " - Main MEF");
                } else {
                    System.out.println(SysConstants.OUT_ALERT + sysMEF.getName());
                }
                System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");

                List<String> listIN = new ArrayList<String>();
                List<String> listState = new ArrayList<String>();
                for (SysState itemState : sysMEF.getSysStates()) {
                    if (!listState.contains(itemState.getName())) {
                        listState.add(itemState.getName());
                    }

                    for (SysTransition itemTran : itemState.getSysTransitionOUT()) {
                        for (SysEventTransition itemEvent : itemTran.getEvents()) {
                            if (!listIN.contains(itemEvent.getEvent())) {
                                listIN.add(itemEvent.getEvent());
                            }
                        }
                    }
                }


                ////////////
                String alfIN = null;
                Collections.sort(listIN, Collator.getInstance());
                for (String item : listIN) {
                    if (alfIN != null) {
                        alfIN = alfIN + ", " + item;
                    } else {
                        alfIN = item;
                    }
                }

                alfIN = "I = {" + alfIN + "}\n";
                System.out.println(alfIN);


                /////////////////
                String states = null;
                Collections.sort(listState, Collator.getInstance());
                for (String item : listState) {
                    if (states != null) {
                        states = states + ", " + item;
                    } else {
                        states = item;
                    }
                }

                states = "Q = {" + states + "}\n";
                System.out.println(states);

                ///////////////
                String stateIni = "Q0 = {";
                if (sysMEF.getInitialState() != null) {
                    stateIni = stateIni + sysMEF.getInitialState().getName();
                }

                stateIni = stateIni + "}\n";
                System.out.println(stateIni);

                /////////////
                List<String> listFunction = new ArrayList<String>();
                for (SysState itemState : sysMEF.getSysStates()) {
                    for (SysTransition itemTran : itemState.getSysTransitionOUT()) {
                        for (SysEventTransition itemEvent : itemTran.getEvents()) {
                            List<SysState> listSysState = getAllStateOfEvent(itemState, itemEvent.getEvent());

                            String sState = "";
                            for (SysState item : listSysState) {
                                if (!sState.equals("")) {
                                    sState = sState + ", " + item.getName();
                                } else {
                                    sState = item.getName();
                                }
                            }

                            if (listSysState.size() > 1) {
                                sState = "P(" + sState + ")";
                            }

                            listFunction.add(padRight("Z(" + itemState.getName() + "," + itemEvent.getEvent() + ")", 15) + " = " + sState);
                        }
                    }
                }

                List<String> listFuncOther = new ArrayList<String>();
                for (String item : listFunction) {
                    if (!listFuncOther.contains(item)) {
                        listFuncOther.add(item);
                    }
                }

                for (String item : listFuncOther) {
                    System.out.println(item);
                }

                ///////////
                List<String> listFinalState = new ArrayList<String>();
                for (SysState item : sysMEF.getFinalState()) {
                    listFinalState.add(item.getName());
                }

                Collections.sort(listFinalState, Collator.getInstance());
                String stateFinal = "";
                for (String item : listFinalState) {
                    if (!stateFinal.equals("")) {
                        stateFinal = stateFinal + ", " + item;
                    } else {
                        stateFinal = item;
                    }
                }
                System.out.println("\nF = {" + stateFinal + "}");

                ///////////
                List<String> listOutput = new ArrayList<String>();
                for (SysState itemState : sysMEF.getSysStates()) {
                    if (itemState.getOutputLabel() != null && itemState.getOutputLabel().length() > 0) {
                        if (!listOutput.contains(itemState.getOutputLabel())) {
                            listOutput.add(itemState.getOutputLabel());
                        }
                    }

                    for (SysTransition itemTransition : itemState.getSysTransitionOUT()) {
                        for (SysEventTransition itemEvent : itemTransition.getEvents()) {
                            if (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0) {
                                if (!listOutput.contains(itemEvent.getOutputLabel())) {
                                    listOutput.add(itemEvent.getOutputLabel());
                                }
                            }
                        }
                    }
                }

                Collections.sort(listOutput, Collator.getInstance());
                String output = "";
                for (String item : listOutput) {
                    if (!output.equals("")) {
                        output = output + ", " + item;
                    } else {
                        output = item;
                    }
                }
                System.out.println("\nO = {" + output + "}\n");

                /////////////
                List<String> listOutputFunction = new ArrayList<String>();
                for (SysState itemState : sysMEF.getSysStates()) {
                    if (itemState.getOutputLabel() != null && itemState.getOutputLabel().length() > 0) {
                        listOutputFunction.add(padRight("Y(" + itemState.getName() + ")", 15) + " = " + itemState.getOutputLabel());
                    }
                    for (SysTransition itemTran : itemState.getSysTransitionOUT()) {
                        for (SysEventTransition itemEvent : itemTran.getEvents()) {
                            if (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0) {
                                listOutputFunction.add(padRight("Y(" + itemState.getName() + "," + itemEvent.getEvent() + ")", 15) + " = " + itemEvent.getOutputLabel());
                            }
                        }
                    }
                }

                List<String> listOutputFuncOther = new ArrayList<String>();
                for (String item : listOutputFunction) {
                    if (!listOutputFuncOther.contains(item)) {
                        listOutputFuncOther.add(item);
                    }
                }

                for (String item : listOutputFuncOther) {
                    System.out.println(item);
                }





            }
            System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");
        }
    }

    public static void showBibTex() {
        System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");
        System.out.println(SysConstants.OUT_ALERT + padCenter("HOW TO CITE IN YOUR PAPERS (BibTex):", 76));
        System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");
        System.out.println("");
        System.out.println(SysConstants.OUT_ALERT + "    In Portuguese:");
        System.out.println("        @MASTERTHESIS {ivoandre;2013,");
        System.out.println("            author = \"Ivo, André A. S.;\",");
        System.out.println("            title  = \"Uma ferramenta para avaliação de planos de voo de satélites usando modelos de estados\",");
        System.out.println("            school = \"Instituto Nacional de Pesquisas Espaciais (INPE)\",");
        System.out.println("            year   = \"2013\",");
        System.out.println("            type   = \"Dissertação (Mestrado em Engenharia e Gerenciamento de Sistemas Espaciais)\"");
        System.out.println("            url    = \"http://urlib.net/rep/8JMKD3MGP7W/3FB2RJE?ibiurl.language=pt-BR\"");
        System.out.println("        }");
        System.out.println(SysConstants.OUT_ALERT + "    In English:");
        System.out.println("        @MASTERTHESIS {ivoandre;2013,");
        System.out.println("            author = \"Ivo, André A. S.;\",");
        System.out.println("            title  = \"A tool for evaluating satellite flight plans using state models\",");
        System.out.println("            school = \"National Institute of Space Research (INPE)\",");
        System.out.println("            year   = \"2013\",");
        System.out.println("            type   = \"Master Thesis (MSc in Engineering and Management of Space Systems)\"");
        System.out.println("            url    = \"http://urlib.net/rep/8JMKD3MGP7W/3FB2RJE?ibiurl.language=pt-BR\"");
        System.out.println("        }");
        System.out.println(SysConstants.OUT_ALERT + "-----------------------------------------------------------------------------");
        System.out.println("");
    }
}
