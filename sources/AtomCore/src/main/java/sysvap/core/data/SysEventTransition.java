/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.core.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;
import sysvap.gui.messages.SysGUIMessages;
import sysvap.gui.messages.SysTypeGUIMessages;

public class SysEventTransition implements SysIPropEditor, Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private String event;
    private SysTransition parent;
    private Boolean checked;
    private String outputLabel;
    private String guardCondition;
    private String actionOnEnter;
    private String actionOnExit;

    public SysEventTransition(SysTransition parent, int id, String event) {
        this.event = event;
        this.parent = parent;
        this.id = id;
        this.checked = false;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean Checked) {
        this.checked = Checked;

        if (Checked) {
            getParent().setSelectedEvent(this);
            getParent().getSysMEF().setSelectedObjectID(getId());
            getParent().getSysMEF().sendSysGUIMessages(new SysGUIMessages(SysTypeGUIMessages.STATECHECKED, 0, 0, getId()));
        } else if ((getParent().getSysMEF().getSelectedObjectID() != null) && (getParent().getSysMEF().getSelectedObjectID().equals(getId()))) {
            getParent().setSelectedEvent(null);
            getParent().getSysMEF().setSelectedObjectID(null);
        } else {
            getParent().setSelectedEvent(null);
        }
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOutputLabel() {
        return outputLabel;
    }

    public void setOutputLabel(String outputLabel) {
        this.outputLabel = outputLabel;
    }

    public String getGuardCondition() {
        return guardCondition;
    }

    public void setGuardCondition(String guardCondition) {
        this.guardCondition = guardCondition;
    }
    
    @Override
    public String toString() {
        return getEvent();
    }

    public SysTransition getParent() {
        return parent;
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
        result.add("event");
        result.add("checked");
        result.add("outputLabel");
        result.add("guardCondition");

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
