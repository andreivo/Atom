/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

public final class SysGUIMEF extends SysMEF implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private SysState currentDebugState;
    
    public SysGUIMEF(int id, String name, SysGUIProject sysProject) {
        super(id, name, sysProject);
    }

    @Override
    public SysGUIState addState(int X, int Y) {
        SysGUIState sysState = new SysGUIState(getNextIdObject(), "q" + getNextIdObject(), X, Y, this);
        addState(sysState);
        return sysState;
    }

    public SysGUIState addState(SysGUIState sysState) {
        List<SysTransition> sysTransitionOUT = sysState.getSysTransitionOUT();
        super.addStateOnly(sysState);

        for (SysTransition item : sysTransitionOUT) {
            SysGUIState nextState = (SysGUIState) getStateEqual(item.getNextState());
            if (nextState != null) {
                for (SysEventTransition itemEvent : item.getEvents()) {
                    sysState.addSysTransition(nextState, itemEvent.getEvent());
                }
            }
        }

        addObserver(sysState);
        return sysState;
    }

    @Override
    public List<Object> delObjectsMEF() {
        List<Object> delObjectsMEF = super.delObjectsMEF();
        for (Object item : delObjectsMEF) {
            deleteObserver((Observer) item);
        }
        return delObjectsMEF;
    }

    public SysGUIState getStateInCoordenate(int x, int y) {
        SysGUIState sysState = null;

        for (SysState item : getSysStates()) {
            if (((SysGUIState) item).isInCoordenate(x, y)) {
                sysState = (SysGUIState) item;
            }
        }
        return sysState;
    }

    public SysState getCurrentDebugState() {
        return currentDebugState;
    }

    public void setCurrentDebugState(SysState currentDebugState) {
        this.currentDebugState = currentDebugState;
    }
    
    @Override
    public List<String> getFieldsName() {
        List<String> result = new ArrayList<String>();
        //Asterisco indica que o campo será readonly
        result.add("id*");
        result.add("name");
        result.add("keepHistoryStates");

        return result;
    }
}
