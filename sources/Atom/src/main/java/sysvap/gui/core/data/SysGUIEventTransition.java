/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.gui.core.data;

import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import sysvap.core.data.SysEventTransition;
import sysvap.gui.helper.SysDrawerHelper;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

public class SysGUIEventTransition extends SysEventTransition implements SysIPropEditor, Observer, Serializable {

    private static final long serialVersionUID = 1L;

    public SysGUIEventTransition(SysGUITransition parent, int id, String event) {
        super(parent, id, event);
    }

    public void update(Observable o, Object arg) {
        final SysGUIMEF obj = (SysGUIMEF) o;

        switch (obj.getLastSysGUIMessages().getTypeMessage()) {
            case NONE:
                break;
            case TOOLMOUSE_MOUSEDRAGGED:
                break;
            case TOOLMOUSE_MOUSEPRESSED:
                updateToolMouse_MousePressed();
                break;
            case STATECHECKED:
                updateStateChecked();
                break;
        }
    }

    private void updateToolMouse_MousePressed() {
        setChecked(false);
        FontMetrics g = (FontMetrics) getParent().getSysMEF().getLastSysGUIMessages().getOtherMessage01();
        if (g != null) {
            if (isInCoordenate(getParent().getSysMEF().getLastSysGUIMessages().getX(), getParent().getSysMEF().getLastSysGUIMessages().getY(), g)) {
                getParent().getSysMEF().setSelectedObjectID(getId());
                setChecked(true);
            }
        }
    }

    private void updateStateChecked() {
        if (!getParent().getSysMEF().getLastSysGUIMessages().getOtherMessage01().equals(getId())) {
            setChecked(false);
        }
    }

    private boolean isInCoordenate(int xClick, int yClick, FontMetrics g) {
        //Cacula o centro entre os vetores
        List<Point> coordToDrawTransition = getParent().getCoordToDrawTransition();

        Point pointOut = coordToDrawTransition.get(0);
        Point pointIn = coordToDrawTransition.get(0);
        int yEvent = -46;
        if (!getParent().getPreviusState().equals(getParent().getNextState())) {
            pointIn = coordToDrawTransition.get(1);
            yEvent = -2;
        }

        int xOrigem = (int) pointOut.getX() + (((int) pointIn.getX() - (int) pointOut.getX()) / 2);
        int yOrigem = (int) pointOut.getY() + (((int) pointIn.getY() - (int) pointOut.getY()) / 2);

        //Calcula o angulo
        Double angle = SysDrawerHelper.calcAngleStates(pointOut, pointIn);
        if (angle == null) {
            angle = 0.;
        }
        if (pointIn.getY() < pointOut.getY()) {
            angle = -angle;
        }

        //Verifica a posição para poder escrever na posição correta.
        //Caso o destino esteja antes da origem no plano do eixo X, é somado mais 180, ou PI no angulo.
        if (getParent().getPreviusState().getXCentral() > getParent().getNextState().getXCentral()) {
            angle = angle + Math.PI;
        }

        ////////////////////////////////////////////////////////////////
        //Escrever evento
        String eventLabel = null;
        String actualEvent;
        for (SysEventTransition itemEvent : getParent().getEvents()) {
            actualEvent = (itemEvent.getGuardCondition() != null && itemEvent.getGuardCondition().length() > 0 ? itemEvent.getEvent() + "[" + itemEvent.getGuardCondition()+ "]" : itemEvent.getEvent());
            actualEvent = (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0 ? actualEvent + "/" + itemEvent.getOutputLabel() : actualEvent);
            if (eventLabel == null) {
                eventLabel = actualEvent;
            } else {
                eventLabel = eventLabel + ", " + actualEvent;
            }
        }

        //Calcula onde deve ser começar a ser escrito
        Rectangle2D bounds = g.getStringBounds(eventLabel, null);
        int xEvent = -(int) (bounds.getWidth() / 2);

        //Verifica se o eventoqual evento para ser escrito, para calcular a posição
        for (int i = 0; i < getParent().getEvents().size(); i++) {
            SysEventTransition itemEvent = getParent().getEvents().get(i);

            eventLabel = (itemEvent.getGuardCondition() != null && itemEvent.getGuardCondition().length() > 0 ? itemEvent.getEvent() + "[" + itemEvent.getGuardCondition() + "]" : itemEvent.getEvent());
            eventLabel = (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0 ? eventLabel + "/" + itemEvent.getOutputLabel() : eventLabel);
            if (i < getParent().getEvents().size() - 1) {
                eventLabel = eventLabel + ", ";
            }

            if (itemEvent == this) {
                break;
            }

            //Se chegou até este ponto é porque não encontrou o evento, assim soma o tamanho
            bounds = g.getStringBounds(eventLabel, null);
            xEvent = xEvent + (int) (bounds.getWidth());
        }


        //Calcula o quadrante envolvente da fonte
        Point pOrigem = new Point(xOrigem, yOrigem);
       
        actualEvent = (getGuardCondition() != null && getGuardCondition().length() > 0 ? getEvent() + "[" + getGuardCondition()+ "]" : getEvent());
        actualEvent = (getOutputLabel() != null && getOutputLabel().length() > 0 ? actualEvent + "/" + getOutputLabel() : actualEvent);       
        bounds = g.getStringBounds(actualEvent, null);
        Point p1 = new Point(xEvent, yEvent);
        Point p2 = new Point(xEvent + (int) (bounds.getWidth()), yEvent);
        Point p3 = new Point(xEvent + (int) (bounds.getWidth()), yEvent - (int) bounds.getHeight());
        Point p4 = new Point(xEvent, yEvent - (int) bounds.getHeight());

        //Rataciona o quadrante para o angulo de inclinação da fonte
        double[] pt = {p1.getX(), p1.getY(),
            p2.getX(), p2.getY(),
            p3.getX(), p3.getY(),
            p4.getX(), p4.getY()};
        pt = rotatePoint(pt, angle, 4);

        //Escreve um poligono para testar se click está dentro do quadrante
        Polygon boundOfEvent = new Polygon();
        boundOfEvent.addPoint((int) pt[0], (int) pt[1]);
        boundOfEvent.addPoint((int) pt[2], (int) pt[3]);
        boundOfEvent.addPoint((int) pt[4], (int) pt[5]);
        boundOfEvent.addPoint((int) pt[6], (int) pt[7]);

        //Ponto de click
        Point pClick = new Point(xClick, yClick);
        //Faz o translado do ponto de click para o mesmo da escrita
        pClick = translateNormalizePoint(pOrigem, pClick);
        double[] pt1 = {pClick.getX(), pClick.getY()};

        //Verifica se o click está dentro do quadrante.
        return boundOfEvent.contains(pt1[0], pt1[1]);
    }

    public Point translateNormalizePoint(Point origem, Point point) {
        return new Point((int) (point.getX() - origem.getX()), (int) (point.getY() - origem.getY()));
    }

    public double[] rotatePoint(double[] pt, double angle, int numPoint) {
        AffineTransform rotateInstance = AffineTransform.getRotateInstance(angle);
        rotateInstance.transform(pt, 0, pt, 0, numPoint); // specifying to use this double[] to hold coords
        return pt;
    }

    @Override
    public SysGUITransition getParent() {
        return (SysGUITransition) super.getParent();
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
}
