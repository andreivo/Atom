/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.core.data;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysTransition;
import sysvap.gui.helper.SysConstants;
import sysvap.gui.helper.SysDrawerHelper;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

public class SysGUITransition extends SysTransition implements SysIPropEditor, Observer, Serializable {

    private static final long serialVersionUID = 1L;

    public SysGUITransition(Integer id, SysGUIState previusState, SysGUIState nextState, SysGUIMEF sysMEF) {
        super(id, previusState, nextState, sysMEF);
    }

    @Override
    public SysGUIEventTransition addEvent(String event) {
        SysGUIEventTransition sysEventTransition = new SysGUIEventTransition(this, getSysMEF().getNextIdObject(), event);
        super.addEvent(sysEventTransition);
        getSysMEF().addObserver(sysEventTransition);
        return sysEventTransition;
    }

    @Override
    public void delEvent(SysEventTransition sysEventTransition) {
        super.delEvent(sysEventTransition);
        getSysMEF().deleteObserver((SysGUIEventTransition) sysEventTransition);
    }

    public boolean isInCoordenate(int x, int y) {
        List<Point> coordToDrawTransition = getCoordToDrawTransition();
        if (!getPreviusState().equals(getNextState())) {
            Line2D transition = new Line2D.Double(coordToDrawTransition.get(0).getX(), coordToDrawTransition.get(0).getY(),
                    coordToDrawTransition.get(1).getX(), coordToDrawTransition.get(1).getY());
            double distanciaLinha = transition.ptSegDist(x, y);
            return (distanciaLinha <= 2);
        } else {
            QuadCurve2D transition = new QuadCurve2D.Double(
                    coordToDrawTransition.get(0).getX() - 20, coordToDrawTransition.get(0).getY() - (SysConstants.BALL_DIAMETER / 2) + 10,
                    coordToDrawTransition.get(0).getX(), coordToDrawTransition.get(0).getY() - (SysConstants.BALL_DIAMETER) - 20,
                    coordToDrawTransition.get(0).getX() + 20, coordToDrawTransition.get(0).getY() - (SysConstants.BALL_DIAMETER / 2) + 10);
            return transition.contains(x, y);
        }
    }

    public void update(Observable o, Object o1) {
        final SysGUIMEF obj = (SysGUIMEF) o;

        switch (obj.getLastSysGUIMessages().getTypeMessage()) {
            case NONE:
                break;
            case TOOLMOUSE_MOUSEDRAGGED:
                break;
            case TOOLMOUSE_MOUSEPRESSED:
                updateToolMouse_MousePressed(obj);
                break;
            case STATECHECKED:
                updateStateChecked(obj);
                break;
            case STATETIMEOUT:
                updateStateTimeout(obj);
                break;
        }
    }

    private void updateToolMouse_MousePressed(SysGUIMEF obj) {
        setChecked(false);
        if (isInCoordenate(obj.getLastSysGUIMessages().getX(), obj.getLastSysGUIMessages().getY())) {
            obj.setSelectedObjectID(getId());
            setChecked(true);
        }
    }

    private void updateStateChecked(SysGUIMEF obj) {
        if (!obj.getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
            setChecked(false);
        }
    }

    private void updateStateTimeout(SysGUIMEF obj) {
        if ((!obj.getLastSysGUIMessages().getOtherMessage01().equals(getId()))
                && (obj.getLastSysGUIMessages().getOtherMessage02().equals(getPreviusState()))) {
            setTimeout(false);
        }
    }

    public List<Point> getCoordToDrawTransition() {

        List<Point> result = new ArrayList<Point>();
        //Verifica se o estado de destino é diferente do estado origem
        if (!getPreviusState().equals(getNextState())) {
            //Define os pontos centrais dos stados
            Point pointOut = new Point(getPreviusState().getXCentral(), getPreviusState().getYCentral());
            Point pointIn = new Point(getNextState().getXCentral(), getNextState().getYCentral());

            //Caso tenha alguma transição de volta é inserido um angulo de offset para
            //desviar o centro 
            int angleOffSet = 0;
            if (getNextState().getTransitionToState(getPreviusState()) != null) {
                angleOffSet = SysConstants.ANGLE_OFFSET_TWO_TRANSITION;
            }

            //Calcula a intersecção com o circulo
            Point pointOut2 = SysDrawerHelper.calcPointIntersectState(pointOut, pointIn, angleOffSet);
            Point pointIn2 = SysDrawerHelper.calcPointIntersectState(pointIn, pointOut, -angleOffSet);
            result.add(pointOut2);
            result.add(pointIn2);
        } else {
            //Desenha a transição
            Point pointOut = new Point(getPreviusState().getXCentral(), getPreviusState().getYCentral());
            result.add(pointOut);
        }

        return result;
    }

    @Override
    public SysGUIState getPreviusState() {
        return (SysGUIState) super.getPreviusState();
    }

    @Override
    public SysGUIState getNextState() {
        return (SysGUIState) super.getNextState();
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
}
