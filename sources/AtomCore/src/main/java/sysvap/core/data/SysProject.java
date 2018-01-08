/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.core.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sysvap.gui.helper.SysConstants;
import sysvap.gui.helper.SysTool;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

public class SysProject implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private int nextIdMEF;
    private List<SysMEF> MEFs;
    private SysTool selectedTool;
    private SysMEF selectedMEF;
    private String nameProject;
    private String pathProject;
    private Boolean changed;
    private SysLookupMEF mainMEF;
    private String actionOnEnter;
    private String actionOnExit;
    private String actionOnUnrecognizedEvent;
    private Integer rateDelay;

    public SysProject() {
        this.selectedTool = SysTool.TOOL_MOUSE;
        this.MEFs = new ArrayList<SysMEF>();
        this.changed = false;
        this.nextIdMEF = 0;
        this.mainMEF = new SysLookupMEF(null, null, MEFs, this);
    }

    public List<SysMEF> getMEFs() {
        return MEFs;
    }

    public void setMEFs(List<SysMEF> MEFs) {
        this.MEFs = MEFs;
    }

    public int getNextIdMEF() {
        return nextIdMEF;
    }

    public void setNextIdMEF(int nextIdMEF) {
        this.nextIdMEF = nextIdMEF;
    }

    public SysTool getSelectedTool() {
        return selectedTool;
    }

    public void setSelectedTool(SysTool selectedTool) {
        this.selectedTool = selectedTool;
    }

    public SysMEF addMEF(String nameMEF) {
        SysMEF sysMEF = new SysMEF(getNextIdMEF(), nameMEF, this);
        addMEF(sysMEF);
        return sysMEF;
    }

    public void delMEF(SysMEF sysMEF) {
        getMEFs().remove(sysMEF);
    }

    public SysMEF getSelectedMEF() {
        return selectedMEF;
    }

    public void setSelectedMEF(SysMEF selectedMEF) {
        this.selectedMEF = selectedMEF;
    }

    public String getNameProject() {
        return nameProject;
    }

    public void setNameProject(String nameProject) {
        this.nameProject = nameProject;
    }

    public String getPathProject() {
        return pathProject;
    }

    public void setPathProject(String pathProject) {
        this.pathProject = pathProject;
    }

    public Integer getRateDelay() {
        return rateDelay;
    }

    public void setRateDelay(Integer rateDelay) {
        this.rateDelay = rateDelay;
    }
    

    public static ByteArrayOutputStream SaveToStream(SysProject sysProject) throws FileNotFoundException, IOException {
        ByteArrayOutputStream fout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(sysProject);
        oos.close();
        return fout;
    }

    public static void SaveToFile(SysProject sysProject, String file, String path) throws FileNotFoundException, IOException {
        sysProject.setNameProject(file);
        sysProject.setPathProject(path);

        sysProject.uncheckedAll();

        FileOutputStream fout = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(sysProject);
        oos.close();
        sysProject.setChanged(false);
    }

    public static SysProject LoadFromFile(SysProject sysProject, String file) throws FileNotFoundException, IOException, ClassNotFoundException {
        FileInputStream fin = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fin);
        sysProject = (SysProject) ois.readObject();

        ois.close();
        sysProject.setChanged(false);
        return sysProject;
    }

    public static SysProject LoadFromStream(SysProject sysProject, ByteArrayOutputStream file) throws FileNotFoundException, IOException, ClassNotFoundException {
        ByteArrayInputStream fin = new ByteArrayInputStream(file.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(fin);
        sysProject = (SysProject) ois.readObject();

        ois.close();
        sysProject.setChanged(false);
        return sysProject;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    @Override
    public String toString() {
        if (getNameProject() == null) {
            return SysConstants.NAME_DEFAULT_PROJECT;
        } else {
            return getNameProject();
        }
    }

    public SysLookupMEF getMainMEF() {
        return mainMEF;
    }

    public void setMainMEF(SysLookupMEF mainMEF) {
        this.mainMEF = mainMEF;
    }

    @Override
    public String getActionOnEnter() {
        return actionOnEnter;
    }

    @Override
    public void setActionOnEnter(String action) {
        this.actionOnEnter = action;
    }

    @Override
    public List<String> getFieldsName() {
        List<String> result = new ArrayList<String>();
        //Asterisco indica que o campo será readonly
        result.add("nameProject*");
        result.add("pathProject*");
        result.add("changed*");
        result.add("mainMEF");
        result.add("rateDelay");

        return result;
    }

    public SysMEF addMEF(SysMEF sysMEF) {
        getMEFs().add(sysMEF);
        setNextIdMEF(this.nextIdMEF + 1);

        if (mainMEF.getSelectedItem() == null) {
            mainMEF.setSelectedItem(sysMEF);
            setChanged(false);
        }

        return sysMEF;
    }

    public SysMEF getMEFByNameID(Integer id, String name) {
        for (SysMEF item : getMEFs()) {
            if (item.getId() == id && item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public SysMEF getMEFByName(String name) {
        for (SysMEF item : getMEFs()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String getActionOnExit() {
        return this.actionOnExit;
    }

    @Override
    public void setActionOnExit(String action) {
        this.actionOnExit = action;
    }

    @Override
    public String getActionOnUnrecognizedEvent() {
        return actionOnUnrecognizedEvent;
    }

    @Override
    public void setActionOnUnrecognizedEvent(String actionOnUnrecognizedEvent) {
        this.actionOnUnrecognizedEvent = actionOnUnrecognizedEvent;
    }

    private void uncheckedAll() {
        for (SysMEF sysMEF : getMEFs()) {
            for (SysState sysState : sysMEF.getSysStates()) {
                sysState.setChecked(false);
                for (SysTransition sysTransitionIN : sysState.getSysTransitionIN()) {
                    sysTransitionIN.setChecked(false);
                    sysTransitionIN.setSelectedEvent(null);
                    for (SysEventTransition sysEventTransition : sysTransitionIN.getEvents()) {
                        sysEventTransition.setChecked(false);
                    }
                }
                
                for (SysTransition sysTransitionOUT : sysState.getSysTransitionOUT()) {
                    sysTransitionOUT.setChecked(false);
                    sysTransitionOUT.setSelectedEvent(null);
                    for (SysEventTransition sysEventTransition : sysTransitionOUT.getEvents()) {
                        sysEventTransition.setChecked(false);
                    }
                }

            }
        }
    }
}

