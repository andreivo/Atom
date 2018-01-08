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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sysvap.gui.helper.Base64Coder;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;
import sysvap.gui.messages.SysGUIMessages;
import sysvap.gui.messages.SysTypeGUIMessages;

public class SysState implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer x;
    private Integer y;
    private String name;
    private Boolean checked;
    private SysTypeState type;
    private List<SysTransition> sysTransitionIN;
    private List<SysTransition> sysTransitionOUT;
    private SysMEF parentSysMEF;
    private SysLookupMEF sub_MEF01;
    private SysLookupMEF sub_MEF02;
    private String actionOnEnter;
    private String actionOnExit;
    private int nextIdEvent;
    private String outputLabel;

    public SysState(Integer id, String Name, Integer X, Integer Y, SysMEF sysMEF) {
        this.parentSysMEF = sysMEF;
        this.id = id;
        this.x = X;
        this.y = Y;
        this.name = Name;
        this.type = SysTypeState.NONE;
        this.checked = false;
        this.sysTransitionIN = new ArrayList<SysTransition>();
        this.sysTransitionOUT = new ArrayList<SysTransition>();
        this.sub_MEF01 = new SysLookupMEF(null, parentSysMEF, sysMEF.getSysProject().getMEFs(), sysMEF.getSysProject());
        this.sub_MEF02 = new SysLookupMEF(null, parentSysMEF, sysMEF.getSysProject().getMEFs(), sysMEF.getSysProject());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean Checked) {
        this.checked = Checked;
        if (Checked) {
            getParentSysMEF().setSelectedObjectID(getId());
            getParentSysMEF().sendSysGUIMessages(new SysGUIMessages(SysTypeGUIMessages.STATECHECKED, 0, 0, getId()));
        } else if ((getParentSysMEF().getSelectedObjectID() != null) && (getParentSysMEF().getSelectedObjectID().equals(getId()))) {
            getParentSysMEF().setSelectedObjectID(null);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer X) {
        this.x = X;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer Y) {
        this.y = Y;
    }

    public SysTypeState getType() {
        return type;
    }

    public void setType(SysTypeState type) {
        this.type = type;
        if (type == SysTypeState.INITIAL) {
            parentSysMEF.sendSysGUIMessages(new SysGUIMessages(SysTypeGUIMessages.STATEINITIAL, 0, 0, getId()));
        } else if (type == SysTypeState.INITIAL_FINAL) {
            parentSysMEF.sendSysGUIMessages(new SysGUIMessages(SysTypeGUIMessages.STATEINITIALFINAL, 0, 0, getId()));
        }
    }

    public List<SysTransition> getSysTransitionIN() {
        return sysTransitionIN;
    }

    public void setSysTransitionIN(List<SysTransition> sysTransitionIN) {
        this.sysTransitionIN = sysTransitionIN;
        if (this.sysTransitionIN == null) {
            this.sysTransitionIN = new ArrayList<SysTransition>();
        }
    }

    public List<SysTransition> getSysTransitionOUT() {
        return sysTransitionOUT;
    }

    public void setSysTransitionOUT(List<SysTransition> sysTransitionOUT) {
        this.sysTransitionOUT = sysTransitionOUT;
        if (this.sysTransitionOUT == null) {
            this.sysTransitionOUT = new ArrayList<SysTransition>();
        }
    }

    public SysMEF getParentSysMEF() {
        return parentSysMEF;
    }

    public void setParentSysMEF(SysMEF sysMEF) {
        this.parentSysMEF = sysMEF;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SysState other = (SysState) obj;
        if (!this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.id;
        hash = 79 * hash + this.x;
        hash = 79 * hash + this.y;
        hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 79 * hash + (this.checked ? 1 : 0);
        hash = 79 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 79 * hash + (this.sysTransitionIN != null ? this.sysTransitionIN.hashCode() : 0);
        hash = 79 * hash + (this.sysTransitionOUT != null ? this.sysTransitionOUT.hashCode() : 0);
        return hash;
    }

    public SysTransition addSysTransition(SysState nextState, String event) {
        SysTransition sysTransition = new SysTransition(getParentSysMEF().getNextIdObject(), this, nextState, getParentSysMEF());
        getParentSysMEF().setNextIdObject(getParentSysMEF().getNextIdObject() + 1);
        SysEventTransition addEvent = sysTransition.addEvent(event);
        sysTransition = addSysTransition(sysTransition, addEvent);
        return sysTransition;
    }

    public SysTransition addSysTransition(SysState nextState, String event, String outputLabel, String guardCondition, String actionOnEnter, String actionOnExit) {
        SysTransition sysTransition = new SysTransition(getParentSysMEF().getNextIdObject(), this, nextState, getParentSysMEF());
        getParentSysMEF().setNextIdObject(getParentSysMEF().getNextIdObject() + 1);
        SysEventTransition addEvent = sysTransition.addEvent(event);
        addEvent.setActionOnEnter(actionOnEnter);
        addEvent.setActionOnExit(actionOnExit);
        addEvent.setOutputLabel(outputLabel);
        addEvent.setGuardCondition(guardCondition);
        
        
        sysTransition = addSysTransition(sysTransition, addEvent);
        return sysTransition;
    }

    public SysTransition addSysTransition(SysTransition sysTransition, SysEventTransition addEvent) {
        getParentSysMEF().getSysProject().setChanged(true);
        SysTransition sysHasTransition = getTransitionToState(sysTransition.getNextState());

        if (sysHasTransition == null) {
            sysHasTransition = sysTransition;
            getParentSysMEF().setNextIdObject(getParentSysMEF().getNextIdObject() + 1);
            getSysTransitionOUT().add(sysTransition);
            sysHasTransition.getNextState().getSysTransitionIN().add(sysTransition);
        } else {
            sysHasTransition.addEvent(addEvent);
        }

        return sysHasTransition;
    }

    public SysTransition getTransitionToState(SysState toState) {
        SysTransition sysTransition = null;
        //Verifica se já existe uma transição para o nextEvent
        for (SysTransition itemTransition : getSysTransitionOUT()) {
            if (itemTransition.getNextState() == toState) {
                sysTransition = itemTransition;
                break;
            }
        }
        return sysTransition;
    }

    public static SysState deserializeFromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        SysState o = (SysState) ois.readObject();
        ois.close();
        return o;
    }

    public String serializeToString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();
        return new String(Base64Coder.encode(baos.toByteArray()));
    }

    @Override
    public String toString() {
        return getName();
    }

    public SysLookupMEF getSub_MEF01() {
        return sub_MEF01;
    }

    public void setSub_MEF01(SysLookupMEF sub_MEF01) {
        this.sub_MEF01 = sub_MEF01;
    }

    public SysLookupMEF getSub_MEF02() {
        return sub_MEF02;
    }

    public void setSub_MEF02(SysLookupMEF sub_MEF02) {
        this.sub_MEF02 = sub_MEF02;
    }

    public boolean hasSubMEF() {
        return (sub_MEF01.getSelectedItem() != null) || (sub_MEF02.getSelectedItem() != null);
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
        result.add("checked");
        result.add("type");
        result.add("x");
        result.add("y");
        result.add("sub_MEF01");
        result.add("sub_MEF02");
        result.add("outputLabel");

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

    public int getNextIdEvent() {
        return nextIdEvent;
    }

    public void setNextIdEvent(int nextIdEvent) {
        this.nextIdEvent = nextIdEvent;
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public void setOutputLabel(String outputLabel) {
        this.outputLabel = outputLabel;
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
