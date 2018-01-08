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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.Timer;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.TaskMonitor;
import sysvap.SysApp;
import sysvap.core.simulation.SysSimulation;
import sysvap.gui.core.data.SysGUIProject;
import sysvap.gui.helper.SysActionsGUI;
import sysvap.gui.helper.SysConstants;
import sysvap.gui.helper.SysDialogs;
import sysvap.gui.helper.SysGUIConsoleOutput;
import sysvap.gui.helper.SysTool;

/**
 * The application's main frame.
 */
public final class SysView extends FrameView {

    private JTablePropertyEditor jPropertyEditor;
    private final JTableScriptDebugging jtbScriptDebugging;
    private SysSimulation sysSimulation;
    private boolean inDebug;
    private final JTableVarWatch jtbVarWatch;

    public SysView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        jtrProject.setModel(null);
        SysActionsGUI.newProject(this, tbsPrincipal, false);
        this.getFrame().addWindowListener(new ApplicationCloseEvent());
        tbsPrincipal.setFocusable(false);

        jPropertyEditor = new JTablePropertyEditor();
        scrollProperty.setViewportView(jPropertyEditor);
        jPropertyEditor.setForm(this);
        setEnableTxtActionOnEnter(false);
        SysGUIConsoleOutput.redirectOutput(txtOutput);

        jtbScriptDebugging = new JTableScriptDebugging();
        scrollScriptDebugging.setViewportView(jtbScriptDebugging);

        jtbVarWatch = new JTableVarWatch();
        scrollVarWatch.setViewportView(jtbVarWatch);

        this.inDebug = false;
        configureButtonsDebuger();

        System.out.println(SysConstants.OUT_ALERT + "Atom SysVAP 1.0 - 2013");
        System.out.println("----------------------------------------------------");
        System.out.println("Author                 : André Ivo");
        System.out.println("Email                  : andre.ivo@gmail.com");
        System.out.println("");
        System.out.println("----------------------------------------------------");
        SysActionsGUI.showLUAInf();
        System.out.println("----------------------------------------------------");
        SysActionsGUI.showBibTex();

        SysActionsGUI.hideDebugPanel(this);

        URL caminhoImagem = this.getClass().getClassLoader().getResource("sysvap/gui/resources/icon.png");
        Image iconeTitulo = Toolkit.getDefaultToolkit().getImage(caminhoImagem);
        this.getFrame().setIconImage(iconeTitulo);
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = SysApp.getApplication().getMainFrame();
            aboutBox = new SysAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        SysApp.getApplication().show(aboutBox);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        tbPrincipal = new javax.swing.JToolBar();
        btnNewProject = new javax.swing.JButton();
        btnOpenProject = new javax.swing.JButton();
        btnSaveProject = new javax.swing.JButton();
        btnSaveAsProject = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnCopy = new javax.swing.JButton();
        btnPaste = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        btnSelectMouse = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnAddTab = new javax.swing.JButton();
        btnDelTab = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        btnAddState = new javax.swing.JToggleButton();
        btnAddTransition = new javax.swing.JToggleButton();
        btnDelete = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        btnExportImage = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        btnSimulation = new javax.swing.JButton();
        btnDebugerSimulation = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        btnContinueSimulation = new javax.swing.JButton();
        btnStopSimulation = new javax.swing.JButton();
        btnStepSimulation = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtrProject = new javax.swing.JTree();
        scrollProperty = new javax.swing.JScrollPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        tbsPrincipal = new javax.swing.JTabbedPane();
        tbsOutput = new javax.swing.JTabbedPane();
        jspOutput = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jspScriptDebugging = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        btnOpenScript = new javax.swing.JButton();
        txtScript = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtScriptEvent = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtScriptDelay = new javax.swing.JTextField();
        btnCleanEventScript = new javax.swing.JButton();
        btnAddEventScript = new javax.swing.JButton();
        btnAddEventScript1 = new javax.swing.JButton();
        btnOpenScript1 = new javax.swing.JButton();
        scrollScriptDebugging = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtScriptRate = new javax.swing.JTextField();
        btnDelEventScript = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        btnDelVarWatch = new javax.swing.JButton();
        btnAddVarWatch = new javax.swing.JButton();
        btnCleanWatch = new javax.swing.JButton();
        btnResetWatch = new javax.swing.JButton();
        btnUpdateWatch = new javax.swing.JButton();
        scrollVarWatch = new javax.swing.JScrollPane();
        tbsActionEvent = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtActionOnEnter = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtActionOnExit = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtActionOnUnrecognized = new javax.swing.JTextArea();
        mbPrincipal = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        buttonGroup1 = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        tbPrincipal.setFloatable(false);
        tbPrincipal.setRollover(true);
        tbPrincipal.setName("tbPrincipal"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(SysView.class);
        btnNewProject.setIcon(resourceMap.getIcon("btnNewProject.icon")); // NOI18N
        btnNewProject.setToolTipText(resourceMap.getString("btnNewProject.toolTipText")); // NOI18N
        btnNewProject.setFocusable(false);
        btnNewProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNewProject.setName("btnNewProject"); // NOI18N
        btnNewProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnNewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewProjectActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnNewProject);

        btnOpenProject.setIcon(resourceMap.getIcon("btnOpenProject.icon")); // NOI18N
        btnOpenProject.setText(resourceMap.getString("btnOpenProject.text")); // NOI18N
        btnOpenProject.setToolTipText(resourceMap.getString("btnOpenProject.toolTipText")); // NOI18N
        btnOpenProject.setFocusable(false);
        btnOpenProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnOpenProject.setName("btnOpenProject"); // NOI18N
        btnOpenProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnOpenProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenProjectActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnOpenProject);

        btnSaveProject.setIcon(resourceMap.getIcon("btnSaveProject.icon")); // NOI18N
        btnSaveProject.setText(resourceMap.getString("btnSaveProject.text")); // NOI18N
        btnSaveProject.setToolTipText(resourceMap.getString("btnSaveProject.toolTipText")); // NOI18N
        btnSaveProject.setFocusable(false);
        btnSaveProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveProject.setName("btnSaveProject"); // NOI18N
        btnSaveProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveProjectActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnSaveProject);

        btnSaveAsProject.setIcon(resourceMap.getIcon("btnSaveAsProject.icon")); // NOI18N
        btnSaveAsProject.setText(resourceMap.getString("btnSaveAsProject.text")); // NOI18N
        btnSaveAsProject.setToolTipText(resourceMap.getString("btnSaveAsProject.toolTipText")); // NOI18N
        btnSaveAsProject.setFocusable(false);
        btnSaveAsProject.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveAsProject.setName("btnSaveAsProject"); // NOI18N
        btnSaveAsProject.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveAsProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveAsProjectActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnSaveAsProject);

        jSeparator2.setName("jSeparator2"); // NOI18N
        tbPrincipal.add(jSeparator2);

        btnCopy.setIcon(resourceMap.getIcon("btnCopy.icon")); // NOI18N
        btnCopy.setText(resourceMap.getString("btnCopy.text")); // NOI18N
        btnCopy.setToolTipText(resourceMap.getString("btnCopy.toolTipText")); // NOI18N
        btnCopy.setFocusable(false);
        btnCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCopy.setName("btnCopy"); // NOI18N
        btnCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCopyActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnCopy);

        btnPaste.setIcon(resourceMap.getIcon("btnPaste.icon")); // NOI18N
        btnPaste.setText(resourceMap.getString("btnPaste.text")); // NOI18N
        btnPaste.setToolTipText(resourceMap.getString("btnPaste.toolTipText")); // NOI18N
        btnPaste.setFocusable(false);
        btnPaste.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPaste.setName("btnPaste"); // NOI18N
        btnPaste.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasteActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnPaste);

        jSeparator4.setName("jSeparator4"); // NOI18N
        tbPrincipal.add(jSeparator4);

        buttonGroup1.add(btnSelectMouse);
        btnSelectMouse.setIcon(resourceMap.getIcon("btnSelectMouse.icon")); // NOI18N
        btnSelectMouse.setToolTipText(resourceMap.getString("btnSelectMouse.toolTipText")); // NOI18N
        btnSelectMouse.setFocusable(false);
        btnSelectMouse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSelectMouse.setName("btnSelectMouse"); // NOI18N
        btnSelectMouse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSelectMouse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectMouseActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnSelectMouse);

        jSeparator1.setName("jSeparator1"); // NOI18N
        tbPrincipal.add(jSeparator1);

        btnAddTab.setIcon(resourceMap.getIcon("btnAddTab.icon")); // NOI18N
        btnAddTab.setToolTipText(resourceMap.getString("btnAddTab.toolTipText")); // NOI18N
        btnAddTab.setFocusable(false);
        btnAddTab.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddTab.setName("btnAddTab"); // NOI18N
        btnAddTab.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTabActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnAddTab);

        btnDelTab.setIcon(resourceMap.getIcon("btnDelTab.icon")); // NOI18N
        btnDelTab.setToolTipText(resourceMap.getString("btnDelTab.toolTipText")); // NOI18N
        btnDelTab.setFocusable(false);
        btnDelTab.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelTab.setName("btnDelTab"); // NOI18N
        btnDelTab.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelTab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelTabActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnDelTab);

        jSeparator3.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        jSeparator3.setName("jSeparator3"); // NOI18N
        tbPrincipal.add(jSeparator3);

        buttonGroup1.add(btnAddState);
        btnAddState.setIcon(resourceMap.getIcon("btnAddState.icon")); // NOI18N
        btnAddState.setToolTipText(resourceMap.getString("btnAddState.toolTipText")); // NOI18N
        btnAddState.setFocusable(false);
        btnAddState.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddState.setName("btnAddState"); // NOI18N
        btnAddState.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddStateActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnAddState);

        buttonGroup1.add(btnAddTransition);
        btnAddTransition.setIcon(resourceMap.getIcon("btnAddTransition.icon")); // NOI18N
        btnAddTransition.setToolTipText(resourceMap.getString("btnAddTransition.toolTipText")); // NOI18N
        btnAddTransition.setFocusable(false);
        btnAddTransition.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddTransition.setMaximumSize(new java.awt.Dimension(31, 31));
        btnAddTransition.setMinimumSize(new java.awt.Dimension(31, 31));
        btnAddTransition.setName("btnAddTransition"); // NOI18N
        btnAddTransition.setPreferredSize(new java.awt.Dimension(31, 31));
        btnAddTransition.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnAddTransition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTransitionActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnAddTransition);

        btnDelete.setIcon(resourceMap.getIcon("btnDelete.icon")); // NOI18N
        btnDelete.setText(resourceMap.getString("btnDelete.text")); // NOI18N
        btnDelete.setToolTipText(resourceMap.getString("btnDelete.toolTipText")); // NOI18N
        btnDelete.setFocusable(false);
        btnDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelete.setName("btnDelete"); // NOI18N
        btnDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnDelete);

        jSeparator5.setName("jSeparator5"); // NOI18N
        tbPrincipal.add(jSeparator5);

        btnExportImage.setIcon(resourceMap.getIcon("btnExportImage.icon")); // NOI18N
        btnExportImage.setText(resourceMap.getString("btnExportImage.text")); // NOI18N
        btnExportImage.setFocusable(false);
        btnExportImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExportImage.setName("btnExportImage"); // NOI18N
        btnExportImage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExportImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportImageActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnExportImage);

        btnExport.setIcon(resourceMap.getIcon("btnExport.icon")); // NOI18N
        btnExport.setText(resourceMap.getString("btnExport.text")); // NOI18N
        btnExport.setToolTipText(resourceMap.getString("btnExport.toolTipText")); // NOI18N
        btnExport.setFocusable(false);
        btnExport.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExport.setName("btnExport"); // NOI18N
        btnExport.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnExport);

        jSeparator6.setName("jSeparator6"); // NOI18N
        tbPrincipal.add(jSeparator6);

        btnSimulation.setIcon(resourceMap.getIcon("btnSimulation.icon")); // NOI18N
        btnSimulation.setText(resourceMap.getString("btnSimulation.text")); // NOI18N
        btnSimulation.setToolTipText(resourceMap.getString("btnSimulation.toolTipText")); // NOI18N
        btnSimulation.setFocusable(false);
        btnSimulation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSimulation.setName("btnSimulation"); // NOI18N
        btnSimulation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimulationActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnSimulation);

        btnDebugerSimulation.setIcon(resourceMap.getIcon("btnDebugerSimulation.icon")); // NOI18N
        btnDebugerSimulation.setText(resourceMap.getString("btnDebugerSimulation.text")); // NOI18N
        btnDebugerSimulation.setToolTipText(resourceMap.getString("btnDebugerSimulation.toolTipText")); // NOI18N
        btnDebugerSimulation.setFocusable(false);
        btnDebugerSimulation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDebugerSimulation.setName("btnDebugerSimulation"); // NOI18N
        btnDebugerSimulation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnDebugerSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDebugerSimulationActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnDebugerSimulation);

        jSeparator7.setName("jSeparator7"); // NOI18N
        tbPrincipal.add(jSeparator7);

        btnContinueSimulation.setIcon(resourceMap.getIcon("btnContinueSimulation.icon")); // NOI18N
        btnContinueSimulation.setText(resourceMap.getString("btnContinueSimulation.text")); // NOI18N
        btnContinueSimulation.setToolTipText(resourceMap.getString("btnContinueSimulation.toolTipText")); // NOI18N
        btnContinueSimulation.setFocusable(false);
        btnContinueSimulation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnContinueSimulation.setName("btnContinueSimulation"); // NOI18N
        btnContinueSimulation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnContinueSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinueSimulationActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnContinueSimulation);

        btnStopSimulation.setIcon(resourceMap.getIcon("btnStopSimulation.icon")); // NOI18N
        btnStopSimulation.setText(resourceMap.getString("btnStopSimulation.text")); // NOI18N
        btnStopSimulation.setToolTipText(resourceMap.getString("btnStopSimulation.toolTipText")); // NOI18N
        btnStopSimulation.setFocusable(false);
        btnStopSimulation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStopSimulation.setName("btnStopSimulation"); // NOI18N
        btnStopSimulation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStopSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopSimulationActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnStopSimulation);

        btnStepSimulation.setIcon(resourceMap.getIcon("btnStepSimulation.icon")); // NOI18N
        btnStepSimulation.setText(resourceMap.getString("btnStepSimulation.text")); // NOI18N
        btnStepSimulation.setToolTipText(resourceMap.getString("btnStepSimulation.toolTipText")); // NOI18N
        btnStepSimulation.setFocusable(false);
        btnStepSimulation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStepSimulation.setName("btnStepSimulation"); // NOI18N
        btnStepSimulation.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStepSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepSimulationActionPerformed(evt);
            }
        });
        tbPrincipal.add(btnStepSimulation);

        jSeparator8.setName("jSeparator8"); // NOI18N
        tbPrincipal.add(jSeparator8);

        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setToolTipText(resourceMap.getString("jButton2.toolTipText")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        tbPrincipal.add(jButton2);

        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setToolTipText(resourceMap.getString("jButton3.toolTipText")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        tbPrincipal.add(jButton3);

        jSplitPane1.setBorder(null);
        jSplitPane1.setFocusable(false);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jSplitPane2.setBorder(null);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane2.setFocusable(false);
        jSplitPane2.setName("jSplitPane2"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jtrProject.setName("jtrProject"); // NOI18N
        jtrProject.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtrProjectMouseClicked(evt);
            }
        });
        jtrProject.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jtrProjectValueChanged(evt);
            }
        });
        jtrProject.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jtrProjectKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jtrProject);

        jSplitPane2.setLeftComponent(jScrollPane1);

        scrollProperty.setName("scrollProperty"); // NOI18N
        jSplitPane2.setRightComponent(scrollProperty);

        jSplitPane1.setLeftComponent(jSplitPane2);

        jSplitPane3.setBorder(null);
        jSplitPane3.setDividerLocation(200);
        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setFocusable(false);
        jSplitPane3.setName("jSplitPane3"); // NOI18N

        tbsPrincipal.setName("tbsPrincipal"); // NOI18N
        tbsPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbsPrincipalMouseClicked(evt);
            }
        });
        tbsPrincipal.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tbsPrincipalStateChanged(evt);
            }
        });
        tbsPrincipal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbsPrincipalKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbsPrincipalKeyReleased(evt);
            }
        });
        jSplitPane3.setTopComponent(tbsPrincipal);

        tbsOutput.setName("tbsOutput"); // NOI18N

        jspOutput.setBorder(null);
        jspOutput.setDividerLocation(300);
        jspOutput.setFocusable(false);
        jspOutput.setName("jspOutput"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        txtOutput.setEditable(false);
        txtOutput.setFont(resourceMap.getFont("txtOutput.font")); // NOI18N
        txtOutput.setName("txtOutput"); // NOI18N
        txtOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOutputKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(txtOutput);

        jspOutput.setRightComponent(jScrollPane3);

        jPanel1.setName("jPanel1"); // NOI18N

        jPanel2.setBackground(resourceMap.getColor("jPanel2.background")); // NOI18N
        jPanel2.setFont(resourceMap.getFont("jPanel2.font")); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jButton1.setFont(resourceMap.getFont("jButton1.font")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setBorder(null);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(resourceMap.getFont("jLabel2.font")); // NOI18N
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1)
                .addComponent(jLabel2))
        );

        jspScriptDebugging.setBorder(null);
        jspScriptDebugging.setDividerLocation(150);
        jspScriptDebugging.setFocusable(false);
        jspScriptDebugging.setMinimumSize(new java.awt.Dimension(1, 205));
        jspScriptDebugging.setName("jspScriptDebugging"); // NOI18N
        jspScriptDebugging.setPreferredSize(new java.awt.Dimension(150, 150));

        jPanel3.setName("jPanel3"); // NOI18N

        btnOpenScript.setIcon(resourceMap.getIcon("btnOpenScript.icon")); // NOI18N
        btnOpenScript.setText(resourceMap.getString("btnOpenScript.text")); // NOI18N
        btnOpenScript.setToolTipText(resourceMap.getString("btnOpenScript.toolTipText")); // NOI18N
        btnOpenScript.setName("btnOpenScript"); // NOI18N
        btnOpenScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenScriptActionPerformed(evt);
            }
        });

        txtScript.setBackground(resourceMap.getColor("txtScript.background")); // NOI18N
        txtScript.setEditable(false);
        txtScript.setText(resourceMap.getString("txtScript.text")); // NOI18N
        txtScript.setToolTipText(resourceMap.getString("txtScript.toolTipText")); // NOI18N
        txtScript.setName("txtScript"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        txtScriptEvent.setBackground(resourceMap.getColor("txtScriptEvent.background")); // NOI18N
        txtScriptEvent.setToolTipText(resourceMap.getString("txtScriptEvent.toolTipText")); // NOI18N
        txtScriptEvent.setName("txtScriptEvent"); // NOI18N
        txtScriptEvent.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtScriptEventKeyReleased(evt);
            }
        });

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        txtScriptDelay.setBackground(resourceMap.getColor("txtScriptDelay.background")); // NOI18N
        txtScriptDelay.setToolTipText(resourceMap.getString("txtScriptDelay.toolTipText")); // NOI18N
        txtScriptDelay.setName("txtScriptDelay"); // NOI18N
        txtScriptDelay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtScriptDelayKeyReleased(evt);
            }
        });

        btnCleanEventScript.setIcon(resourceMap.getIcon("btnCleanEventScript.icon")); // NOI18N
        btnCleanEventScript.setToolTipText(resourceMap.getString("btnCleanEventScript.toolTipText")); // NOI18N
        btnCleanEventScript.setName("btnCleanEventScript"); // NOI18N
        btnCleanEventScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCleanEventScriptActionPerformed(evt);
            }
        });

        btnAddEventScript.setIcon(resourceMap.getIcon("btnAddEventScript.icon")); // NOI18N
        btnAddEventScript.setToolTipText(resourceMap.getString("btnAddEventScript.toolTipText")); // NOI18N
        btnAddEventScript.setName("btnAddEventScript"); // NOI18N
        btnAddEventScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEventScriptActionPerformed(evt);
            }
        });

        btnAddEventScript1.setIcon(resourceMap.getIcon("btnAddEventScript1.icon")); // NOI18N
        btnAddEventScript1.setToolTipText(resourceMap.getString("btnAddEventScript1.toolTipText")); // NOI18N
        btnAddEventScript1.setName("btnAddEventScript1"); // NOI18N
        btnAddEventScript1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddEventScript1ActionPerformed(evt);
            }
        });

        btnOpenScript1.setIcon(resourceMap.getIcon("btnOpenScript1.icon")); // NOI18N
        btnOpenScript1.setToolTipText(resourceMap.getString("btnOpenScript1.toolTipText")); // NOI18N
        btnOpenScript1.setName("btnOpenScript1"); // NOI18N
        btnOpenScript1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenScript1ActionPerformed(evt);
            }
        });

        scrollScriptDebugging.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        scrollScriptDebugging.setName("scrollScriptDebugging"); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 150, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        txtScriptRate.setBackground(resourceMap.getColor("txtScriptRate.background")); // NOI18N
        txtScriptRate.setText(resourceMap.getString("txtScriptRate.text")); // NOI18N
        txtScriptRate.setToolTipText(resourceMap.getString("txtScriptRate.toolTipText")); // NOI18N
        txtScriptRate.setName("txtScriptRate"); // NOI18N
        txtScriptRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtScriptRateKeyReleased(evt);
            }
        });

        btnDelEventScript.setIcon(resourceMap.getIcon("btnDelEventScript.icon")); // NOI18N
        btnDelEventScript.setToolTipText(resourceMap.getString("btnDelEventScript.toolTipText")); // NOI18N
        btnDelEventScript.setName("btnDelEventScript"); // NOI18N
        btnDelEventScript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelEventScriptActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollScriptDebugging, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScript, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnOpenScript, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnOpenScript1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtScriptEvent, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAddEventScript1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtScriptRate, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDelEventScript, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCleanEventScript, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtScriptDelay, javax.swing.GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddEventScript, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnOpenScript1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(txtScript, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnOpenScript, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(txtScriptEvent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddEventScript1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtScriptDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnAddEventScript, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtScriptRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(btnCleanEventScript, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDelEventScript, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(scrollScriptDebugging, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
        );

        jspScriptDebugging.setTopComponent(jPanel3);

        jPanel4.setName("jPanel4"); // NOI18N

        jPanel6.setName("jPanel6"); // NOI18N

        btnDelVarWatch.setIcon(resourceMap.getIcon("btnDelVarWatch.icon")); // NOI18N
        btnDelVarWatch.setToolTipText(resourceMap.getString("btnDelVarWatch.toolTipText")); // NOI18N
        btnDelVarWatch.setName("btnDelVarWatch"); // NOI18N
        btnDelVarWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelVarWatchActionPerformed(evt);
            }
        });

        btnAddVarWatch.setIcon(resourceMap.getIcon("btnAddVarWatch.icon")); // NOI18N
        btnAddVarWatch.setToolTipText(resourceMap.getString("btnAddVarWatch.toolTipText")); // NOI18N
        btnAddVarWatch.setName("btnAddVarWatch"); // NOI18N
        btnAddVarWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddVarWatchActionPerformed(evt);
            }
        });

        btnCleanWatch.setIcon(resourceMap.getIcon("btnCleanWatch.icon")); // NOI18N
        btnCleanWatch.setToolTipText(resourceMap.getString("btnCleanWatch.toolTipText")); // NOI18N
        btnCleanWatch.setName("btnCleanWatch"); // NOI18N
        btnCleanWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCleanWatchActionPerformed(evt);
            }
        });

        btnResetWatch.setIcon(resourceMap.getIcon("btnResetWatch.icon")); // NOI18N
        btnResetWatch.setToolTipText(resourceMap.getString("btnResetWatch.toolTipText")); // NOI18N
        btnResetWatch.setName("btnResetWatch"); // NOI18N
        btnResetWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetWatchActionPerformed(evt);
            }
        });

        btnUpdateWatch.setIcon(resourceMap.getIcon("btnUpdateWatch.icon")); // NOI18N
        btnUpdateWatch.setToolTipText(resourceMap.getString("btnUpdateWatch.toolTipText")); // NOI18N
        btnUpdateWatch.setName("btnUpdateWatch"); // NOI18N
        btnUpdateWatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateWatchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(btnAddVarWatch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelVarWatch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCleanWatch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnResetWatch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdateWatch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnDelVarWatch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnAddVarWatch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnCleanWatch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnResetWatch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnUpdateWatch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        scrollVarWatch.setName("scrollVarWatch"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(scrollVarWatch, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollVarWatch, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))
        );

        jspScriptDebugging.setRightComponent(jPanel4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jspScriptDebugging, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspScriptDebugging, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
        );

        jspOutput.setLeftComponent(jPanel1);

        tbsOutput.addTab(resourceMap.getString("jspOutput.TabConstraints.tabTitle"), jspOutput); // NOI18N

        tbsActionEvent.setName("tbsActionEvent"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        txtActionOnEnter.setColumns(20);
        txtActionOnEnter.setRows(5);
        txtActionOnEnter.setName("txtActionOnEnter"); // NOI18N
        txtActionOnEnter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtActionOnEnterKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(txtActionOnEnter);

        tbsActionEvent.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        txtActionOnExit.setColumns(20);
        txtActionOnExit.setRows(5);
        txtActionOnExit.setName("txtActionOnExit"); // NOI18N
        txtActionOnExit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtActionOnExitKeyReleased(evt);
            }
        });
        jScrollPane4.setViewportView(txtActionOnExit);

        tbsActionEvent.addTab(resourceMap.getString("jScrollPane4.TabConstraints.tabTitle"), jScrollPane4); // NOI18N

        jScrollPane5.setName("jScrollPane5"); // NOI18N

        txtActionOnUnrecognized.setColumns(20);
        txtActionOnUnrecognized.setRows(5);
        txtActionOnUnrecognized.setName("txtActionOnUnrecognized"); // NOI18N
        txtActionOnUnrecognized.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtActionOnUnrecognizedKeyReleased(evt);
            }
        });
        jScrollPane5.setViewportView(txtActionOnUnrecognized);

        tbsActionEvent.addTab(resourceMap.getString("jScrollPane5.TabConstraints.tabTitle"), jScrollPane5); // NOI18N

        tbsOutput.addTab(resourceMap.getString("tbsActionEvent.TabConstraints.tabTitle"), tbsActionEvent); // NOI18N

        jSplitPane3.setRightComponent(tbsOutput);

        jSplitPane1.setRightComponent(jSplitPane3);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
            .addComponent(tbPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(tbPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
        );

        mbPrincipal.setName("mbPrincipal"); // NOI18N

        fileMenu.setMnemonic('f');
        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(SysView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setToolTipText(resourceMap.getString("exitMenuItem.toolTipText")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        mbPrincipal.add(fileMenu);

        jMenu1.setMnemonic('V');
        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        mbPrincipal.add(jMenu1);

        helpMenu.setMnemonic('h');
        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setToolTipText(resourceMap.getString("aboutMenuItem.toolTipText")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        mbPrincipal.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 876, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 692, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(mbPrincipal);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTabActionPerformed
        SysActionsGUI.addNewTab(this, tbsPrincipal, "");
    }//GEN-LAST:event_btnAddTabActionPerformed

    private void btnDelTabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelTabActionPerformed
        SysActionsGUI.delTab(this, tbsPrincipal);
    }//GEN-LAST:event_btnDelTabActionPerformed

    private void btnAddStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddStateActionPerformed
        AbstractButton abstractButton = (AbstractButton) evt.getSource();
        if (abstractButton.getModel().isSelected()) {
            SysGUIProject.getInstance().setSelectedTool(SysTool.TOOL_ADD_STATE);
        }
    }//GEN-LAST:event_btnAddStateActionPerformed

    private void btnSelectMouseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectMouseActionPerformed
        AbstractButton abstractButton = (AbstractButton) evt.getSource();
        if (abstractButton.getModel().isSelected()) {
            SysGUIProject.getInstance().setSelectedTool(SysTool.TOOL_MOUSE);
        }
    }//GEN-LAST:event_btnSelectMouseActionPerformed

    private void btnAddTransitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTransitionActionPerformed
        AbstractButton abstractButton = (AbstractButton) evt.getSource();
        if (abstractButton.getModel().isSelected()) {
            SysGUIProject.getInstance().setSelectedTool(SysTool.TOOL_ADD_TRANSITION);
        }
    }//GEN-LAST:event_btnAddTransitionActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        SysActionsGUI.deleteObjectsMEF(this, tbsPrincipal);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveProjectActionPerformed
        SysActionsGUI.SaveProject(this);
    }//GEN-LAST:event_btnSaveProjectActionPerformed

    private void btnOpenProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenProjectActionPerformed
        SysActionsGUI.OpenProject(this, tbsPrincipal);
    }//GEN-LAST:event_btnOpenProjectActionPerformed

    private void btnSaveAsProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveAsProjectActionPerformed
        SysActionsGUI.SaveAsProject(this);
    }//GEN-LAST:event_btnSaveAsProjectActionPerformed

    private void btnNewProjectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewProjectActionPerformed
        SysActionsGUI.newProject(this, tbsPrincipal, true);
    }//GEN-LAST:event_btnNewProjectActionPerformed

    private void tbsPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbsPrincipalKeyReleased
    }//GEN-LAST:event_tbsPrincipalKeyReleased

    private void btnCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCopyActionPerformed
        SysActionsGUI.copyState();
    }//GEN-LAST:event_btnCopyActionPerformed

    private void btnPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasteActionPerformed
        SysActionsGUI.pasteState(tbsPrincipal, this);
    }//GEN-LAST:event_btnPasteActionPerformed

    private void tbsPrincipalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbsPrincipalKeyPressed
    }//GEN-LAST:event_tbsPrincipalKeyPressed

    private void tbsPrincipalStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tbsPrincipalStateChanged
    }//GEN-LAST:event_tbsPrincipalStateChanged

    private void tbsPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbsPrincipalMouseClicked
    }//GEN-LAST:event_tbsPrincipalMouseClicked

    private void jtrProjectValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jtrProjectValueChanged
        SysActionsGUI.treeValueChanged(this, tbsPrincipal);
    }//GEN-LAST:event_jtrProjectValueChanged

    private void jtrProjectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtrProjectMouseClicked
        SysActionsGUI.treeValueChanged(this, tbsPrincipal);
    }//GEN-LAST:event_jtrProjectMouseClicked

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        SysActionsGUI.ExportAsProject(this);
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimulationActionPerformed
        SysActionsGUI.execSimulation(this);
    }//GEN-LAST:event_btnSimulationActionPerformed

    private void txtActionOnEnterKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtActionOnEnterKeyReleased
        SysActionsGUI.KeyReleased(evt, this);
    }//GEN-LAST:event_txtActionOnEnterKeyReleased

    private void txtActionOnExitKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtActionOnExitKeyReleased
        SysActionsGUI.KeyReleased(evt, this);
    }//GEN-LAST:event_txtActionOnExitKeyReleased

    private void jtrProjectKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jtrProjectKeyReleased
        SysActionsGUI.KeyReleased(evt, this);
    }//GEN-LAST:event_jtrProjectKeyReleased

    private void txtOutputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOutputKeyReleased
        SysActionsGUI.KeyReleased(evt, this);
    }//GEN-LAST:event_txtOutputKeyReleased

    private void btnDebugerSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDebugerSimulationActionPerformed
        SysActionsGUI.debugSimulation(this);
    }//GEN-LAST:event_btnDebugerSimulationActionPerformed

    private void btnStopSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopSimulationActionPerformed
        SysActionsGUI.debugStopSimulation(this, true);
    }//GEN-LAST:event_btnStopSimulationActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jspOutput.getLeftComponent().setVisible(false);
        jspOutput.setDividerSize(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        SysActionsGUI.showDebugPanel(this);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void btnOpenScript1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenScript1ActionPerformed
        SysActionsGUI.saveScript(this);
    }//GEN-LAST:event_btnOpenScript1ActionPerformed

    private void btnAddEventScript1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEventScript1ActionPerformed
        SysActionsGUI.debugAddSendEvent(this);
    }//GEN-LAST:event_btnAddEventScript1ActionPerformed

    private void btnAddEventScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddEventScriptActionPerformed
        SysActionsGUI.debugAddEvent(this);
    }//GEN-LAST:event_btnAddEventScriptActionPerformed

    private void btnCleanEventScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCleanEventScriptActionPerformed
        SysActionsGUI.debugClearEvent(this);
    }//GEN-LAST:event_btnCleanEventScriptActionPerformed

    private void btnOpenScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenScriptActionPerformed
        SysActionsGUI.loadScript(this);
    }//GEN-LAST:event_btnOpenScriptActionPerformed

    private void txtScriptEventKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtScriptEventKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (evt.isControlDown()) {
                    SysActionsGUI.debugAddEvent(this);
                } else {
                    SysActionsGUI.debugAddSendEvent(this);
                }
                break;
            default:
                SysActionsGUI.KeyReleased(evt, this);
                break;
        }
    }//GEN-LAST:event_txtScriptEventKeyReleased

    private void txtScriptDelayKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtScriptDelayKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (evt.isControlDown()) {
                    SysActionsGUI.debugAddEvent(this);
                } else {
                    SysActionsGUI.debugAddSendEvent(this);
                }
                break;
            default:
                SysActionsGUI.KeyReleased(evt, this);
                break;
        }
    }//GEN-LAST:event_txtScriptDelayKeyReleased

    private void txtScriptRateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtScriptRateKeyReleased
        switch (evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                if (evt.isControlDown()) {
                    SysActionsGUI.debugAddEvent(this);
                } else {
                    SysActionsGUI.debugAddSendEvent(this);
                }
                break;
        }
    }//GEN-LAST:event_txtScriptRateKeyReleased

    private void btnStepSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepSimulationActionPerformed
        SysActionsGUI.debugStepSimulation(this, false);
    }//GEN-LAST:event_btnStepSimulationActionPerformed

    private void btnDelEventScriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelEventScriptActionPerformed
        SysActionsGUI.debugDeleteEvent(this);
    }//GEN-LAST:event_btnDelEventScriptActionPerformed

    private void btnContinueSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinueSimulationActionPerformed
        SysActionsGUI.debugContinueSimulation(this);
    }//GEN-LAST:event_btnContinueSimulationActionPerformed

    private void btnDelVarWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelVarWatchActionPerformed
        SysActionsGUI.watchDelVar(this);
    }//GEN-LAST:event_btnDelVarWatchActionPerformed

    private void btnAddVarWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddVarWatchActionPerformed
        SysActionsGUI.watchAddVar(this);
    }//GEN-LAST:event_btnAddVarWatchActionPerformed

    private void btnCleanWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCleanWatchActionPerformed
        SysActionsGUI.watchClearVar(this);
    }//GEN-LAST:event_btnCleanWatchActionPerformed

    private void btnResetWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetWatchActionPerformed
        SysActionsGUI.resetVarsWatch(this);
    }//GEN-LAST:event_btnResetWatchActionPerformed

    private void btnUpdateWatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateWatchActionPerformed
        SysActionsGUI.updateVarsWatch(this);
    }//GEN-LAST:event_btnUpdateWatchActionPerformed

    private void btnExportImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportImageActionPerformed
        SysActionsGUI.exportMefToJpeg(this);
    }//GEN-LAST:event_btnExportImageActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        SysActionsGUI.showFormalTable();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtActionOnUnrecognizedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtActionOnUnrecognizedKeyReleased
        SysActionsGUI.KeyReleased(evt, this);
    }//GEN-LAST:event_txtActionOnUnrecognizedKeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        SysActionsGUI.showBibTex();
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddEventScript;
    private javax.swing.JButton btnAddEventScript1;
    private javax.swing.JToggleButton btnAddState;
    private javax.swing.JButton btnAddTab;
    private javax.swing.JToggleButton btnAddTransition;
    private javax.swing.JButton btnAddVarWatch;
    private javax.swing.JButton btnCleanEventScript;
    private javax.swing.JButton btnCleanWatch;
    private javax.swing.JButton btnContinueSimulation;
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnDebugerSimulation;
    private javax.swing.JButton btnDelEventScript;
    private javax.swing.JButton btnDelTab;
    private javax.swing.JButton btnDelVarWatch;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnExportImage;
    private javax.swing.JButton btnNewProject;
    private javax.swing.JButton btnOpenProject;
    private javax.swing.JButton btnOpenScript;
    private javax.swing.JButton btnOpenScript1;
    private javax.swing.JButton btnPaste;
    private javax.swing.JButton btnResetWatch;
    private javax.swing.JButton btnSaveAsProject;
    private javax.swing.JButton btnSaveProject;
    private javax.swing.JToggleButton btnSelectMouse;
    private javax.swing.JButton btnSimulation;
    private javax.swing.JButton btnStepSimulation;
    private javax.swing.JButton btnStopSimulation;
    private javax.swing.JButton btnUpdateWatch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jspOutput;
    private javax.swing.JSplitPane jspScriptDebugging;
    private javax.swing.JTree jtrProject;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar mbPrincipal;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JScrollPane scrollProperty;
    private javax.swing.JScrollPane scrollScriptDebugging;
    private javax.swing.JScrollPane scrollVarWatch;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JToolBar tbPrincipal;
    private javax.swing.JTabbedPane tbsActionEvent;
    private javax.swing.JTabbedPane tbsOutput;
    private javax.swing.JTabbedPane tbsPrincipal;
    private javax.swing.JTextArea txtActionOnEnter;
    private javax.swing.JTextArea txtActionOnExit;
    private javax.swing.JTextArea txtActionOnUnrecognized;
    private javax.swing.JTextPane txtOutput;
    private javax.swing.JTextField txtScript;
    private javax.swing.JTextField txtScriptDelay;
    private javax.swing.JTextField txtScriptEvent;
    private javax.swing.JTextField txtScriptRate;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;

    private class ApplicationCloseEvent extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            if (SysGUIProject.getInstance().getChanged()) {
                int response = SysDialogs.getUserConfirmation("Project was modified. Do you want to save it?");
                if (response == JOptionPane.YES_OPTION) {
                    SysActionsGUI.SaveProject(null);
                }
            }
        }
    }

    public JToggleButton getBtnSelectMouse() {
        return btnSelectMouse;
    }

    public JTree getJtrProject() {
        return jtrProject;
    }

    public JTablePropertyEditor getjPropertyEditor() {
        return jPropertyEditor;
    }

    public JTabbedPane getTbsPrincipal() {
        return tbsPrincipal;
    }

    public void setTxtActionOnEnter(String action) {
        txtActionOnEnter.setText(action);
    }

    public String getTxtActionOnEnter() {
        return txtActionOnEnter.getText();
    }

    public void setEnableTxtActionOnEnter(boolean value) {
        txtActionOnEnter.setEnabled(value);
        if (value) {
            txtActionOnEnter.setBackground(Color.white);
        } else {
            txtActionOnEnter.setBackground(new Color(230, 230, 230));
        }

    }

    public void setTxtActionOnExit(String action) {
        txtActionOnExit.setText(action);
    }

    public String getTxtActionOnExit() {
        return txtActionOnExit.getText();
    }

    public void setEnableTxtActionOnExit(boolean value) {
        txtActionOnExit.setEnabled(value);
        if (value) {
            txtActionOnExit.setBackground(Color.white);
        } else {
            txtActionOnExit.setBackground(new Color(230, 230, 230));
        }

    }

    public void setEnableTxtActionOnUnrecognizedEvent(boolean value) {
        txtActionOnUnrecognized.setEnabled(value);
        if (value) {
            txtActionOnUnrecognized.setBackground(Color.white);
        } else {
            txtActionOnUnrecognized.setBackground(new Color(230, 230, 230));
        }

    }

    public void setTxtActionOnUnrecognizedEvent(String action) {
        txtActionOnUnrecognized.setText(action);
    }

    public String getTxtActionOnUnrecognizedEvent() {
        return txtActionOnUnrecognized.getText();
    }

    public JTabbedPane getTbsOutput() {
        return tbsOutput;
    }

    public JTextPane getTxtOutput() {
        return txtOutput;
    }

    public JTextField getTxtScript() {
        return txtScript;
    }

    public JSplitPane getJspOutput() {
        return jspOutput;
    }

    public JSplitPane getJspScriptDebugging() {
        return jspScriptDebugging;
    }

    public JTableScriptDebugging getJtbScriptDebugging() {
        return jtbScriptDebugging;
    }

    public SysSimulation getSysSimulation() {
        return sysSimulation;
    }

    public void setSysSimulation(SysSimulation sysSimulation) {
        this.sysSimulation = sysSimulation;
    }

    public JTextField getTxtScriptDelay() {
        return txtScriptDelay;
    }

    public void setTxtScriptDelay(JTextField txtScriptDelay) {
        this.txtScriptDelay = txtScriptDelay;
    }

    public JTextField getTxtScriptEvent() {
        return txtScriptEvent;
    }

    public void setTxtScriptEvent(JTextField txtScriptEvent) {
        this.txtScriptEvent = txtScriptEvent;
    }

    public JTextField getTxtScriptRate() {
        return txtScriptRate;
    }

    public void setTxtScriptRate(JTextField txtScriptRate) {
        this.txtScriptRate = txtScriptRate;
    }

    public boolean isInDebug() {
        return inDebug;
    }

    public void setInDebug(boolean inDebug) {
        this.inDebug = inDebug;
    }

    public void startDebug() {
        if (!busyIconTimer.isRunning()) {
            statusAnimationLabel.setIcon(busyIcons[0]);
            busyIconIndex = 0;
            busyIconTimer.start();
        }
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        this.inDebug = true;
        configureButtonsDebuger();
    }

    public void stopDebug() {
        busyIconTimer.stop();
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);
        progressBar.setValue(0);
        this.inDebug = false;
        configureButtonsDebuger();
    }

    public void configureButtonsDebuger() {
        btnDebugerSimulation.setEnabled(!isInDebug());
        btnSimulation.setEnabled(!inDebug);
        btnStepSimulation.setEnabled(inDebug);
        btnStopSimulation.setEnabled(inDebug);
        btnContinueSimulation.setEnabled(inDebug);
    }

    public JTableVarWatch getJtbVarWatch() {
        return jtbVarWatch;
    }

    public JTabbedPane getTbsActionEvent() {
        return tbsActionEvent;
    }

    public void configureButtonsOnSendEvent(boolean enable) {
        if (!enable) {
            btnSimulation.setEnabled(false);
            btnDebugerSimulation.setEnabled(false);
            btnContinueSimulation.setEnabled(false);
            btnStopSimulation.setEnabled(false);
            btnStepSimulation.setEnabled(false);

        } else {
            configureButtonsDebuger();
        }

    }
}
