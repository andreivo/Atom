/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.core.data;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysProject;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

public final class SysGUIProject extends SysProject implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private static SysGUIProject instance;
    private Color backgroundColor;

    private SysGUIProject() {
        super();
    }

    public static SysGUIProject getInstance() {
        // singleton  
        if (instance == null) {
            instance = new SysGUIProject();
        }
        //
        return instance;
    }

    public static void setInstance(SysGUIProject instance) {
        SysGUIProject.instance = instance;
        instance.prepareProjectAfterLoad();
    }

    public static void NewProject() {
        instance = null;
    }

    public void prepareProjectAfterLoad() {
        for (SysMEF sysMEF : getInstance().getMEFs()) {
            for (SysState sysState : sysMEF.getSysStates()) {
                sysMEF.addObserver((SysGUIState) sysState);
                for (SysTransition sysTransition : sysState.getSysTransitionIN()) {
                    sysMEF.addObserver((SysGUITransition) sysTransition);
                    for (SysEventTransition sysEventTransition : sysTransition.getEvents()) {
                        sysMEF.addObserver((SysGUIEventTransition) sysEventTransition);
                    }
                }
            }
        }
    }

    @Override
    public SysGUIMEF addMEF(String nameMEF) {
        SysGUIMEF sysMEF = new SysGUIMEF(getNextIdMEF(), nameMEF, this);
        super.addMEF(sysMEF);
        return sysMEF;
    }

    @Override
    public SysGUIMEF getSelectedMEF() {
        return (SysGUIMEF) super.getSelectedMEF();
    }

    public void setSelectedMEF(SysGUIMEF selectedMEF) {
        super.setSelectedMEF(selectedMEF);
    }

    @Override
    public List<String> getFieldsName() {
        List<String> result = new ArrayList<String>();
        //Asterisco indica que o campo será readonly
        result.add("nameProject*");
        result.add("pathProject*");
        result.add("changed*");
        result.add("mainMEF");
        result.add("backgroundColor");
        result.add("rateDelay");

        return result;
    }

    public boolean mefInUse(SysGUIMEF sysMEF) {
        if ((getMainMEF().getSelectedItem() != null) && (getMainMEF().getSelectedItem().equals(sysMEF))) {
            return true;
        } else {
            for (SysMEF itemSysMEF : getMEFs()) {
                for (SysState itemState : itemSysMEF.getSysStates()) {
                    if ((itemState.getSub_MEF01().getSelectedItem() != null) && (itemState.getSub_MEF01().getSelectedItem().equals(sysMEF))) {
                        return true;
                    } else if ((itemState.getSub_MEF02().getSelectedItem() != null) && (itemState.getSub_MEF02().getSelectedItem().equals(sysMEF))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Color getBackgroundColor() {
        if (backgroundColor == null) {
            backgroundColor = new Color(137, 187, 224);
        }
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
