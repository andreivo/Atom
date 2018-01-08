/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.core.data;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;
import sysvap.gui.messages.SysGUIMessages;
import sysvap.gui.messages.SysTypeGUIMessages;

public class SysMEF extends Observable implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private int nextIdObject;
    private List<SysState> sysStates;
    private SysGUIMessages sysGUIMessages;
    private Point pointStateTransitionIN;
    private Point pointStateTransitionOUT;
    private Integer selectedObjectID;
    private String actionOnEnter;
    private SysProject sysProject;
    private String actionOnExit;
    private Boolean keepHistoryStates;

    public SysMEF(int id, String name, SysProject sysProject) {
        this.sysGUIMessages = new SysGUIMessages(SysTypeGUIMessages.NONE, 0, 0);
        setChanged(); // marca esse objeto observável como alterado
        notifyObservers(); // notifica todos os observadores que esse objeto foi alterado

        this.id = id;
        this.name = name;
        this.sysStates = new ArrayList<SysState>();
        this.nextIdObject = 0;
        this.sysProject = sysProject;
        this.keepHistoryStates = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SysGUIMessages getLastSysGUIMessages() {
        return sysGUIMessages;
    }

    public void sendSysGUIMessages(SysGUIMessages sysGUIMessages) {
        this.sysGUIMessages = sysGUIMessages;
        setChanged(); // marca esse objeto observável como alterado
        notifyObservers(); // notifica todos os observadores que esse objeto foi alterado
    }

    public List<SysState> getSysStates() {
        return sysStates;
    }

    public void setSysStates(List<SysState> sysStates) {
        this.sysStates = sysStates;
    }

    public int getNextIdObject() {
        return nextIdObject;
    }

    public void setNextIdObject(int nextId) {
        this.nextIdObject = nextId;
    }

    public SysProject getSysProject() {
        return sysProject;
    }

    public void setSysProject(SysProject sysProject) {
        this.sysProject = sysProject;
    }

    public Boolean getKeepHistoryStates() {
        return keepHistoryStates;
    }

    public void setKeepHistoryStates(Boolean keepHistoryStates) {
        this.keepHistoryStates = keepHistoryStates;
    }

    public SysState addState(int X, int Y) {
        getSysProject().setChanged(true);
        SysState sysState = new SysState(getNextIdObject(), "q" + getNextIdObject(), X, Y, this);
        setNextIdObject(getNextIdObject() + 1);
        getSysStates().add(sysState);
        return sysState;
    }

    public SysState addState(SysState sysState) {
        getSysProject().setChanged(true);
        sysState.setId(getNextIdObject());
        sysState.setName("q" + getNextIdObject());
        sysState.setParentSysMEF(this);
        sysState.setChecked(true);
        setNextIdObject(getNextIdObject() + 1);
        getSysStates().add(sysState);
        List<SysTransition> sysTransitionOUT = sysState.getSysTransitionOUT();

        sysState.setSysTransitionOUT(null);
        sysState.setSysTransitionIN(null);

        for (SysTransition item : sysTransitionOUT) {
            SysState nextState = getStateEqual(item.getNextState());
            if (nextState != null) {
                for (SysEventTransition itemEvent : item.getEvents()) {
                    sysState.addSysTransition(nextState, itemEvent.getEvent());
                }
            }
        }

        if (sysState.getType() == SysTypeState.INITIAL) {
            sysState.setType(SysTypeState.INITIAL);
        } else if (sysState.getType() == SysTypeState.FINAL) {
            sysState.setType(SysTypeState.FINAL);
        }

        return sysState;
    }

    public SysState addStateOnly(SysState sysState) {
        getSysProject().setChanged(true);
        sysState.setId(getNextIdObject());
        sysState.setName("q" + getNextIdObject());
        sysState.setParentSysMEF(this);
        sysState.setChecked(true);
        setNextIdObject(getNextIdObject() + 1);
        getSysStates().add(sysState);

        sysState.setSysTransitionOUT(null);
        sysState.setSysTransitionIN(null);

        if (sysState.getType() == SysTypeState.INITIAL) {
            sysState.setType(SysTypeState.INITIAL);
        } else if (sysState.getType() == SysTypeState.FINAL) {
            sysState.setType(SysTypeState.FINAL);
        }

        return sysState;
    }

    public List<Object> delObjectsMEF() {

        getSysProject().setChanged(true);

        List<Object> result = new ArrayList<Object>();

        //Apaga os estados
        Iterator itState = getSysStates().iterator();
        while (itState.hasNext()) {
            SysState sstates = (SysState) itState.next();
            if (sstates.getChecked()) {
                //Apagando as transições que chegam até o estado apagado.
                for (SysTransition transINItem : sstates.getSysTransitionIN()) {
                    Iterator transOUT = transINItem.getPreviusState().getSysTransitionOUT().iterator();
                    while (transOUT.hasNext()) {
                        SysTransition transOUTItem = (SysTransition) transOUT.next();
                        if (transINItem.equals(transOUTItem)) {
                            transOUT.remove();
                            result.add(transOUTItem);
                        }
                    }
                }
                itState.remove();
                result.add(sstates);
            }
        }

        //Apaga as transições
        for (SysState itemState : getSysStates()) {
            //OUT
            Iterator itTransition = itemState.getSysTransitionOUT().iterator();
            while (itTransition.hasNext()) {
                SysTransition transOUT = (SysTransition) itTransition.next();
                if (transOUT.getChecked()) {
                    itTransition.remove();
                    result.add(transOUT);
                }

                Iterator iEvents = transOUT.getEvents().iterator();
                while (iEvents.hasNext()) {
                    SysEventTransition sysEventTransition = (SysEventTransition) iEvents.next();

                    if (sysEventTransition.getChecked()) {
                        transOUT.setSelectedEvent(null);
                        iEvents.remove();
                        result.add(sysEventTransition);
                    }
                }

                if (transOUT.getEvents().isEmpty()) {
                    result.add(transOUT);
                    itTransition.remove();
                }
            }

            //IN
            itTransition = itemState.getSysTransitionIN().iterator();
            while (itTransition.hasNext()) {
                SysTransition transIN = (SysTransition) itTransition.next();
                if (transIN.getChecked()) {
                    itTransition.remove();
                    result.add(transIN);
                }

                Iterator iEvents = transIN.getEvents().iterator();
                while (iEvents.hasNext()) {
                    SysEventTransition sysEventTransition = (SysEventTransition) iEvents.next();

                    if (sysEventTransition.getChecked()) {
                        result.add(sysEventTransition);
                        transIN.setSelectedEvent(null);
                        iEvents.remove();
                    }
                }

                if (transIN.getEvents().isEmpty()) {
                    result.add(transIN);
                    itTransition.remove();
                }

            }
        }

        return result;
    }

    public void updateTransitionIN(int x, int y) {
        this.pointStateTransitionIN = new Point(x, y);
    }

    public void updateTransitionOUT(int x, int y) {
        this.pointStateTransitionOUT = new Point(x, y);
    }

    public Point getPointStateTransitionIN() {
        return pointStateTransitionIN;
    }

    public void setPointStateTransitionIN(Point pointStateTransitionIN) {
        this.pointStateTransitionIN = pointStateTransitionIN;
    }

    public Point getPointStateTransitionOUT() {
        return pointStateTransitionOUT;
    }

    public void setPointStateTransitionOUT(Point pointStateTransitionOUT) {
        this.pointStateTransitionOUT = pointStateTransitionOUT;
    }

    public SysState getSelectedState() {
        for (SysState item : getSysStates()) {
            if (item.getChecked()) {
                return item;
            }
        }
        return null;
    }

    public SysIPropEditor getSelectedObject() {
        for (SysState itemState : getSysStates()) {
            if (getSelectedObjectID() != null) {
                if (itemState.getId().equals(getSelectedObjectID())) {
                    return itemState;
                }

                for (SysTransition itemTrans : itemState.getSysTransitionOUT()) {
                    if (itemTrans.getId().equals(getSelectedObjectID())) {
                        return itemTrans;
                    } else {
                        for (SysEventTransition itemEvent : itemTrans.getEvents()) {
                            if (itemEvent.getId().equals(getSelectedObjectID()) && (itemEvent.getChecked())) {
                                return itemEvent;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public SysState getStateEqual(SysState nextState) {
        for (SysState itemState : getSysStates()) {
            if (itemState.equals(nextState)) {
                return itemState;
            }
        }
        return null;
    }

    public Integer getSelectedObjectID() {
        return selectedObjectID;
    }

    public void setSelectedObjectID(Integer selectedObjectID) {
        this.selectedObjectID = selectedObjectID;
    }

    public void setSelectedNextObject() {
        Integer nextSelect = getSelectedObjectID();

        if ((nextSelect == null) || (nextSelect >= getNextIdObject() - 1)) {
            nextSelect = 0;
        } else {
            nextSelect = nextSelect + 1;
        }

        boolean checked = false;
        while ((!checked) && (nextSelect <= getNextIdObject())) {
            for (SysState itemState : getSysStates()) {
                if (itemState.getId().equals(nextSelect)) {
                    itemState.setChecked(true);
                    checked = true;
                    break;
                }

                for (SysTransition itemTrans : itemState.getSysTransitionOUT()) {
                    if (itemTrans.getId().equals(nextSelect)) {
                        itemTrans.setChecked(true);
                        checked = true;
                        break;
                    }
                }
            }
            nextSelect = nextSelect + 1;
            if (nextSelect >= getNextIdObject() - 1) {
                nextSelect = 0;
            }
        }
    }

    public void setSelectedPreviusObject() {
        Integer nextSelect = getSelectedObjectID();

        if (nextSelect == null) {
            nextSelect = 0;
        } else if (nextSelect <= 0) {
            nextSelect = getNextIdObject() - 1;
        } else {
            nextSelect = nextSelect - 1;
        }

        boolean checked = false;
        while ((!checked) && (nextSelect >= 0)) {
            for (SysState itemState : getSysStates()) {
                if (itemState.getId().equals(nextSelect)) {
                    itemState.setChecked(true);
                    checked = true;
                    break;
                }

                for (SysTransition itemTrans : itemState.getSysTransitionOUT()) {
                    if (itemTrans.getId().equals(nextSelect)) {
                        itemTrans.setChecked(true);
                        checked = true;
                        break;
                    }
                }
            }
            nextSelect = nextSelect - 1;
            if (nextSelect <= 0) {
                nextSelect = getNextIdObject() - 1;
            }
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        result.add("id*");
        result.add("name");
        result.add("keepHistoryStates");

        return result;
    }

    public SysState getStateByNameID(String name, Integer id) {
        for (SysState item : getSysStates()) {
            if (item.getId() == id && item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public SysState getStateByName(String state) {
        for (SysState item : getSysStates()) {
            if (item.getName().equals(state)) {
                return item;
            }
        }
        return null;
    }

    public SysState getInitialState() {
        for (SysState item : getSysStates()) {
            if ((item.getType() == SysTypeState.INITIAL) || (item.getType() == SysTypeState.INITIAL_FINAL)) {
                return item;
            }
        }
        return null;
    }

    public List<SysState> getFinalState() {
        List<SysState> result = new ArrayList<SysState>();
        for (SysState item : getSysStates()) {
            if ((item.getType() == SysTypeState.FINAL) || (item.getType() == SysTypeState.INITIAL_FINAL)) {
                result.add(item);
            }
        }
        return result;
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setActionOnUnrecognizedEvent(String action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
