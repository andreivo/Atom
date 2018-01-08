/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.gui.core.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTypeState;
import sysvap.gui.helper.Base64Coder;
import sysvap.gui.helper.SysConstants;
import sysvap.gui.helper.SysDrawerHelper;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

public class SysGUIState extends SysState implements SysIPropEditor, Observer, Serializable {

    private static final long serialVersionUID = 1L;

    public SysGUIState(Integer id, String Name, Integer X, Integer Y, SysGUIMEF sysMEF) {
        super(id, Name, X, Y, sysMEF);
    }

    public void update(Observable o, Object o1) {
        final SysGUIMEF obj = (SysGUIMEF) o;

        switch (obj.getLastSysGUIMessages().getTypeMessage()) {
            case NONE:
                break;
            case TOOLMOUSE_MOUSEDRAGGED:
                updateToolMouse_MouseDragged();
                break;
            case TOOLMOUSE_MOUSEPRESSED:
                updateToolMouse_MousePressed();
                break;
            case STATECHECKED:
                updateStateChecked();
                break;
            case STATEINITIAL:
                updateStateTypeInitial();
                break;
            case STATEINITIALFINAL:
                updateStateTypeInitialFinal();
                break;
        }
    }

    private void updateToolMouse_MouseDragged() {
        if (getChecked()) {
            SysGUIProject.getInstance().setChanged(true);
            setX(getParentSysMEF().getLastSysGUIMessages().getX());
            setY(getParentSysMEF().getLastSysGUIMessages().getY());
        }
    }

    public boolean isInCoordenate(int x, int y) {
        boolean result = false;

        if ((x >= getX() && x <= getX() + SysConstants.BALL_DIAMETER)
                && (y >= getY() && y <= getY() + SysConstants.BALL_DIAMETER)) {
            result = true;
        }
        return result;
    }

    private void updateToolMouse_MousePressed() {
        setChecked(false);
        if (isInCoordenate(getParentSysMEF().getLastSysGUIMessages().getX(), getParentSysMEF().getLastSysGUIMessages().getY())) {
            SysGUIProject.getInstance().setChanged(true);
            getParentSysMEF().setSelectedObjectID(getId());
            setChecked(true);
        }
    }

    private void updateStateChecked() {
        if (!getParentSysMEF().getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
            SysGUIProject.getInstance().setChanged(true);
            setChecked(false);
        }
    }

    private void updateStateTypeInitial() {
        if (getType() == SysTypeState.INITIAL) {
            if (!getParentSysMEF().getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
                SysGUIProject.getInstance().setChanged(true);
                setType(SysTypeState.NONE);
            }
        } else if (getType() == SysTypeState.INITIAL_FINAL) {
            if (!getParentSysMEF().getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
                SysGUIProject.getInstance().setChanged(true);
                setType(SysTypeState.FINAL);
            }
        }
    }

    private void updateStateTypeInitialFinal() {
        if (getType() == SysTypeState.INITIAL_FINAL) {
            if (!getParentSysMEF().getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
                SysGUIProject.getInstance().setChanged(true);
                setType(SysTypeState.FINAL);
            }
        } else if (getType() == SysTypeState.INITIAL) {
            if (!getParentSysMEF().getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
                SysGUIProject.getInstance().setChanged(true);
                setType(SysTypeState.NONE);
            }
        }
    }

    public int getXCentral() {
        return SysDrawerHelper.getCenterState(getX());
    }

    public int getYCentral() {
        return SysDrawerHelper.getCenterState(getY());
    }

    public SysGUITransition addSysTransition(SysGUIState nextState, String event) {
        getParentSysMEF().getSysProject().setChanged(true);
        SysGUITransition sysTransition = (SysGUITransition) getTransitionToState(nextState);

        if (sysTransition == null) {
            sysTransition = new SysGUITransition(getParentSysMEF().getNextIdObject(), this, nextState, (SysGUIMEF) getParentSysMEF());
            getParentSysMEF().setNextIdObject(getParentSysMEF().getNextIdObject() + 1);
            sysTransition.addEvent(event);
            getSysTransitionOUT().add(sysTransition);
            sysTransition.getNextState().getSysTransitionIN().add(sysTransition);
        } else {
            sysTransition.addEvent(event);
        }
        getParentSysMEF().addObserver(sysTransition);
        return sysTransition;
    }

    public static SysGUIState deserializeFromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        SysGUIState o = (SysGUIState) ois.readObject();
        ois.close();
        return o;
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
}
