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
import java.util.List;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;
import sysvap.gui.messages.SysGUIMessages;
import sysvap.gui.messages.SysTypeGUIMessages;

public class SysTransition implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private SysState nextState;
    private SysState previusState;
    //private int nextIdEvent;
    private List<SysEventTransition> events;
    private Boolean checked;
    private Boolean timeout;
    private SysMEF sysMEF;
    private SysEventTransition selectedEvent;
    private Integer milisec_timeout;
    private String actionOnEnter;
    private String actionOnExit;
    private List<SysBreakPointTransition> points;

    public SysTransition(Integer id, SysState previusState, SysState nextState, SysMEF sysMEF) {
        this.id = id;
        this.previusState = previusState;
        this.nextState = nextState;
        this.events = new ArrayList<SysEventTransition>();
        this.sysMEF = sysMEF;
        this.checked = false;
        this.timeout = false;
        this.points = new ArrayList<SysBreakPointTransition>();        
    }

    public SysState getNextState() {
        return nextState;
    }

    public void setNextState(SysState nextState) {
        this.nextState = nextState;
    }

    public SysState getPreviusState() {
        return previusState;
    }

    public void setPreviusState(SysState previusState) {
        this.previusState = previusState;
    }

    public SysMEF getSysMEF() {
        return sysMEF;
    }

    public void setSysMEF(SysMEF sysMEF) {
        this.sysMEF = sysMEF;
    }

    public Boolean getChecked() {
        return checked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<SysBreakPointTransition> getPoints() {
        return points;
    }

    public void setPoints(List<SysBreakPointTransition> points) {
        this.points = points;
    }
    
    public void addPoint(int x, int y){
        if( this.points==null){
            this.points = new ArrayList<SysBreakPointTransition>();       
        }
        this.points.add(new SysBreakPointTransition(x, y));
    }

    public void setChecked(Boolean Checked) {
        this.checked = Checked;
        if (Checked) {
            getSysMEF().setSelectedObjectID(getId());
            getSysMEF().sendSysGUIMessages(new SysGUIMessages(SysTypeGUIMessages.STATECHECKED, 0, 0, getId()));
        } else if ((getSysMEF().getSelectedObjectID() != null) && (getSysMEF().getSelectedObjectID().equals(getId()))) {
            getSysMEF().setSelectedObjectID(null);
        }
    }

    public Boolean getTimeout() {
        return timeout;
    }

    public void setTimeout(Boolean timeout) {
        this.timeout = timeout;
        if (timeout) {
            getSysMEF().sendSysGUIMessages(new SysGUIMessages(SysTypeGUIMessages.STATETIMEOUT, 0, 0, getId(), getPreviusState()));
        }
    }

    public List<SysEventTransition> getEvents() {
        return events;
    }

    public void setEvents(List<SysEventTransition> events) {
        this.events = events;
    }

    public SysEventTransition addEvent(String event) {
        SysEventTransition sysEventTransition = new SysEventTransition(this, getSysMEF().getNextIdObject(), event);
        addEvent(sysEventTransition);
        return sysEventTransition;
    }

    public SysEventTransition addEvent(SysEventTransition sysEventTransition) {
        if (sysEventTransition.getEvent().equals("")) {
            sysEventTransition.setEvent("e" + getNextIdEvent());
        }
        getEvents().add(sysEventTransition);
        setNextIdEvent(getNextIdEvent() + 1);
        getSysMEF().setNextIdObject(getSysMEF().getNextIdObject() + 1);
        return sysEventTransition;
    }

    public void delEvent(SysEventTransition sysEventTransition) {
        if (getSelectedEvent() == sysEventTransition) {
            setSelectedEvent(null);
        }
        getEvents().remove(sysEventTransition);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SysTransition other = (SysTransition) obj;
        if (!this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + this.id;
        return hash;
    }

    public int getNextIdEvent() {
        return getPreviusState().getNextIdEvent();
    }

    public void setNextIdEvent(int nextIdEvent) {
        getPreviusState().setNextIdEvent(nextIdEvent);
    }

    public SysEventTransition getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(SysEventTransition selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public Integer getMilisec_timeout() {
        return milisec_timeout;
    }

    public void setMilisec_timeout(Integer milisec_timeout) {
        this.milisec_timeout = milisec_timeout;
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
        result.add("checked");
        result.add("previusState*");
        result.add("nextState*");
        result.add("timeout");
        result.add("milisec_timeout");
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
