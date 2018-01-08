/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.gui.drawer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;
import sysvap.core.data.SysTypeState;
import sysvap.gui.JPanelView;
import sysvap.gui.SysView;
import sysvap.gui.core.data.SysGUIMEF;
import sysvap.gui.core.data.SysGUIState;
import sysvap.gui.core.data.SysGUITransition;
import sysvap.gui.helper.SysConstants;
import sysvap.gui.helper.SysDrawerHelper;

public class SysDrawer {

    public static void paintStates(JPanelView jpVisualizacao, Graphics g, SysView form) {
        //--- Seta tamanho do form para que habilite o scroll
        Dimension dimension = jpVisualizacao.getParent().getSize();
        jpVisualizacao.setSize(dimension);
        jpVisualizacao.setPreferredSize(dimension);

        Color colorStateNormal = new Color(241, 146, 78);
        Color colorStateMEF = new Color(89, 174, 68);
        Color colorStateDebug = new Color(255, 100, 100);
        Color colorState;

        for (SysState state : jpVisualizacao.getSysMEF().getSysStates()) {
            //--- Seta tamanho do form para que habilite o scroll
            SysDrawerHelper.setDimension(jpVisualizacao, dimension, new Point(state.getX(), state.getY()));

            if (state.hasSubMEF()) {
                colorState = colorStateMEF;
            } else {
                colorState = colorStateNormal;
            }

            //Verifica se o estado está em debug
            if (form.isInDebug()) {
                if (((SysGUIMEF) state.getParentSysMEF()).getCurrentDebugState() != null) {
                    if (((SysGUIMEF) state.getParentSysMEF()).getCurrentDebugState().getId() == state.getId()) {
                        colorState = colorStateDebug;
                    }
                }
            }

            Graphics2D g2d = (Graphics2D) g;
            //Ajustando a qualidade dos gráficos
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Estado inicial
            if ((state.getType() == SysTypeState.INITIAL) || (state.getType() == SysTypeState.INITIAL_FINAL)) {
                // desenha a flecha
                // cria um novo Graphics a partir do original
                Graphics2D g2df = (Graphics2D) g2d.create();
                // faz a translação para a coordenada que deve ser a origem
                g2df.translate(state.getX() + 7, state.getY() + 7);
                g2df.rotate(Math.PI / 4);

                // desenha a flecha
                int x0[] = {0, -10, -10, 0};
                int y0[] = {0, -10, 10, 0};
                g2df.setColor(new Color(100, 100, 100));
                g2df.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10));
                g2df.fillPolygon(x0, y0, 4);
                g2df.dispose();

                //--------------
                g2df = (Graphics2D) g2d.create();
                // faz a translação para a coordenada que deve ser a origem
                g2df.translate(state.getX() + 6, state.getY() + 6);
                g2df.rotate(Math.PI / 4);

                // desenha a flecha
                int x1[] = {0, -8, -8, 0};
                int y1[] = {0, -8, 8, 0};
                g2df.setColor(new Color(240, 240, 240));
                g2df.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10));
                g2df.fillPolygon(x1, y1, 4);
                g2df.dispose();
                //--------------
                g2df = (Graphics2D) g2d.create();
                // faz a translação para a coordenada que deve ser a origem
                g2df.translate(state.getX() + 5, state.getY() + 5);
                g2df.rotate(Math.PI / 4);

                // desenha a flecha
                int x2[] = {0, -6, -6, 0};
                int y2[] = {0, -6, 6, 0};
                g2df.setColor(colorState);
                g2df.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10));
                g2df.fillPolygon(x2, y2, 4);

                g2df.dispose();
            }

            //Estado final
            if ((state.getType() == SysTypeState.FINAL) || (state.getType() == SysTypeState.INITIAL_FINAL)) {
                g2d.setColor(new Color(100, 100, 100));
                g.drawOval(state.getX() - 3, state.getY() - 3, SysConstants.BALL_DIAMETER + 5, SysConstants.BALL_DIAMETER + 5);
            }

            //Estado selecionado
            Stroke acStroke = g2d.getStroke();
            if (state.getChecked()) {
                g2d.setColor(Color.RED);
                float[] dashPattern = {5, 5, 5, 5};
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10,
                        dashPattern, 0));

                if (state.getType() == SysTypeState.FINAL) {
                    g.drawOval(state.getX() - 6, state.getY() - 6, SysConstants.BALL_DIAMETER + 11, SysConstants.BALL_DIAMETER + 11);
                } else {
                    g.drawOval(state.getX() - 3, state.getY() - 3, SysConstants.BALL_DIAMETER + 5, SysConstants.BALL_DIAMETER + 5);
                }
            }

            //Retorna o tipo da linha
            g2d.setStroke(acStroke);

            //Desenha o estado
            g2d.setColor(new Color(100, 100, 100));
            g.fillOval(state.getX(), state.getY(), SysConstants.BALL_DIAMETER, SysConstants.BALL_DIAMETER);
            g2d.setColor(new Color(240, 240, 240));
            g.fillOval(state.getX() + 2, state.getY() + 2, SysConstants.BALL_DIAMETER - 4, SysConstants.BALL_DIAMETER - 4);
            g2d.setColor(colorState);
            g.fillOval(state.getX() + 4, state.getY() + 4, SysConstants.BALL_DIAMETER - 8, SysConstants.BALL_DIAMETER - 8);

            //Escreve o nome
            g2d.setColor(new Color(255, 255, 255));
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(state.getName(), g2d);
            int xName = (((SysGUIState) state).getXCentral() - (int) (bounds.getWidth() / 2));
            int yName = (((SysGUIState) state).getYCentral() + (int) (bounds.getHeight() / 2) - 2);

            //Escreve a saída do estado
            if ((state.getOutputLabel() != null) && (state.getOutputLabel().length() > 0)) {
                g2d.setColor(new Color(255, 255, 255));
                bounds = g2d.getFontMetrics().getStringBounds(state.getOutputLabel(), g2d);
                int xOutput = (((SysGUIState) state).getXCentral() - (int) (bounds.getWidth() / 2));
                int yOutput = yName + (int) (bounds.getHeight() / 2) + 3;
                yName = yName - (int) (bounds.getHeight() / 2) - 3;
                g2d.drawString(state.getOutputLabel(), xOutput, yOutput);

                g2d.drawLine(((SysGUIState) state).getXCentral() - (SysConstants.BALL_DIAMETER / 2) + 2, ((SysGUIState) state).getYCentral(),
                        ((SysGUIState) state).getXCentral() + (SysConstants.BALL_DIAMETER / 2) - 3, ((SysGUIState) state).getYCentral());
            }

            //Escreve o nome
            g2d.drawString(state.getName(), xName, yName);
        }
    }

    public static void paintTransitions(JPanelView jpVisualizacao, Graphics g) {
        SysMEF sysMEF = jpVisualizacao.getSysMEF();

        //Desenha a linha em draggen drop.
        if (sysMEF.getPointStateTransitionOUT() != null) {
            //Obtem o estado de saida
            SysGUIState SysStateOUT = ((SysGUIMEF) sysMEF).getStateInCoordenate((int) sysMEF.getPointStateTransitionOUT().getX(), (int) sysMEF.getPointStateTransitionOUT().getY());

            //Armazena os pontos
            Point pointOut = new Point(SysStateOUT.getXCentral(), SysStateOUT.getYCentral());
            Point pointIn = new Point(0, 0);

            //Verifica se existe a transição de entrada
            if (sysMEF.getPointStateTransitionIN() != null) {
                SysGUIState SysStateIN = ((SysGUIMEF) sysMEF).getStateInCoordenate((int) sysMEF.getPointStateTransitionIN().getX(), (int) sysMEF.getPointStateTransitionIN().getY());
                pointIn.setLocation(SysStateIN.getXCentral(), SysStateIN.getYCentral());
            } else {
                pointIn.setLocation(jpVisualizacao.getScreeanX(), jpVisualizacao.getScreeanY());
            }
            //Calcula a intersecção com o circulo
            pointOut = SysDrawerHelper.calcPointIntersectState(pointOut, pointIn, 0);
            //Desenha a transição
            paintLineofTransition(g, pointOut, pointIn, null);
        }

        //desenha as transições ja criadas.
        for (SysState itemState : sysMEF.getSysStates()) {
            for (SysTransition itemTransition : itemState.getSysTransitionOUT()) {
                //Verifica se o estado de destino é diferente do estado origem
                List<Point> coordToDrawTransition = ((SysGUITransition) itemTransition).getCoordToDrawTransition();
                if (!itemTransition.getPreviusState().equals(itemTransition.getNextState())) {
                    //Desenha a transição
                    paintLineofTransition(g, coordToDrawTransition.get(0), coordToDrawTransition.get(1), itemTransition);
                } else {
                    //Desenha a transição
                    paintLineofTransition(g, coordToDrawTransition.get(0), itemTransition);
                }
            }
        }
    }

    //Desenha as transições curvadas (stade de saida = estado de chegada)
    public static void paintLineofTransition(Graphics g, Point pointOut, SysTransition itemTransition) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke acStroke = g2d.getStroke();

        //Verifica se a transição está selecionada, para alterar o tipo e cor da linha
        if (itemTransition != null && (itemTransition.getChecked())) {
            g2d.setColor(Color.RED);
            float[] dashPattern = {5, 5, 5, 5};
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10,
                    dashPattern, 0));
        } else {
            //Transição não selecionada
            if (itemTransition.getTimeout()) {
                float[] dashPattern = {10, 3, 10, 3};
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10,
                        dashPattern, 0));
            } else {
                g2d.setStroke(new BasicStroke(2));
            }

            g2d.setColor(new Color(100, 100, 100));
        }

        //Desenha a linha entre os dois estados
        g2d.draw(new QuadCurve2D.Double(
                pointOut.getX() - 20, pointOut.getY() - (SysConstants.BALL_DIAMETER / 2) + 10,
                pointOut.getX(), pointOut.getY() - (SysConstants.BALL_DIAMETER) - 20,
                pointOut.getX() + 20, pointOut.getY() - (SysConstants.BALL_DIAMETER / 2) + 10));

        //Altera apenas a cor da linha para desenhar a seta
        if (itemTransition != null && (itemTransition.getChecked())) {
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.RED);
        }

        //Desenha o primeiro lado da seta
        g2d.draw(new Line2D.Double(
                pointOut.getX() + 12, pointOut.getY() - (SysConstants.BALL_DIAMETER / 2) + 3,
                pointOut.getX() + 20, pointOut.getY() - (SysConstants.BALL_DIAMETER / 2) + 10));

        //Desenha o segundo lado da seta
        g2d.draw(new Line2D.Double(
                pointOut.getX() + 22, pointOut.getY() - (SysConstants.BALL_DIAMETER / 2) + 1,
                pointOut.getX() + 20, pointOut.getY() - (SysConstants.BALL_DIAMETER / 2) + 10));
        g2d.setStroke(acStroke);


        //Escreve os valores dos evento
        String eventLabel = null;
        String actualEvent;
        for (SysEventTransition itemEvent : itemTransition.getEvents()) {
            actualEvent = (itemEvent.getGuardCondition() != null && itemEvent.getGuardCondition().length() > 0 ? itemEvent.getEvent() + "[" + itemEvent.getGuardCondition() + "]" : itemEvent.getEvent());
            actualEvent = (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0 ? actualEvent + "/" + itemEvent.getOutputLabel() : actualEvent);
            
            if (eventLabel == null) {
                eventLabel = actualEvent;
            } else {
                eventLabel = eventLabel + ", " + actualEvent;
            }
        }

        //Calcula onde deve ser escrito
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(eventLabel, g2d);
        //g2df.drawString(events, -(int) (bounds.getWidth() / 2), -2);
        int xEvent = ((SysGUITransition) itemTransition).getPreviusState().getXCentral()
                + ((((SysGUITransition) itemTransition).getNextState().getXCentral()
                - ((SysGUITransition) itemTransition).getPreviusState().getXCentral()) / 2) - (int) (bounds.getWidth() / 2);;

        for (int i = 0; i < itemTransition.getEvents().size(); i++) {
            SysEventTransition itemEvent = itemTransition.getEvents().get(i);
            
            eventLabel = (itemEvent.getGuardCondition() != null && itemEvent.getGuardCondition().length() > 0 ? itemEvent.getEvent() + "[" + itemEvent.getGuardCondition() + "]" : itemEvent.getEvent());
            eventLabel = (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0 ? eventLabel + "/" + itemEvent.getOutputLabel() : eventLabel);
            if (i < itemTransition.getEvents().size() - 1) {
                eventLabel = eventLabel + ", ";
            }

            acStroke = g2d.getStroke();
            //Verifica se a transição está selecionada, para alterar o tipo e cor da linha
            if (itemEvent.getChecked()) {
                g2d.setColor(Color.RED);
                float[] dashPattern = {5, 5, 5, 5};
                g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10,
                        dashPattern, 0));
            } else {
                //Transição não selecionada
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(new Color(100, 100, 100));
            }

            g2d.drawString(eventLabel, xEvent, ((SysGUITransition) itemTransition).getNextState().getYCentral() - 46);
            bounds = g2d.getFontMetrics().getStringBounds(eventLabel, g2d);
            xEvent = xEvent + (int) bounds.getWidth();
            g2d.setStroke(acStroke);
        }



    }

    //Desenha as transições retas (stade de saida != estado de chegada)
    public static void paintLineofTransition(Graphics g, Point pointOut, Point pointIn, SysTransition itemTransition) {
        if ((pointOut != null) && (pointIn != null)) {
            Graphics2D g2d = (Graphics2D) g;

            Stroke acStroke = g2d.getStroke();
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(100, 100, 100));
            //Verifica se a transição está selecionada, para alterar o tipo e cor da linha
            if (itemTransition != null) {
                if (itemTransition.getChecked()) {
                    g2d.setColor(Color.RED);
                    float[] dashPattern = {5, 5, 5, 5};
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10,
                            dashPattern, 0));
                } else if (itemTransition.getTimeout()) {
                    //Transição não selecionada
                    float[] dashPattern = {10, 3, 10, 3};
                    g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10,
                            dashPattern, 0));
                }
            }

            //Desenha a linha entre os dois estados
            g2d.draw(new Line2D.Double(pointOut.getX(), pointOut.getY(), pointIn.getX(), pointIn.getY()));

            //Altera apenas a cor da linha para desenhar a seta
            if (itemTransition != null && (itemTransition.getChecked())) {
                g2d.setStroke(new BasicStroke(2));
                g2d.setColor(Color.RED);
            }

            // desenha a flecha
            // cria um novo Graphics a partir do original
            Graphics2D g2df = (Graphics2D) g2d.create();

            // faz a translação para a coordenada que deve ser a origem
            g2df.translate(pointIn.getX(), pointIn.getY());

            //Double angle = calcAngleStates(x0, y0, x1, y1);
            Double angle = SysDrawerHelper.calcAngleStates(pointOut, pointIn);
            if (angle == null) {
                angle = 0.;
            }
            if (pointIn.getY() < pointOut.getY()) {
                angle = -angle;
            }
            g2df.rotate(angle);

            // desenha a flecha
            g2df.draw(new Line2D.Double(0, 0, -6, -6));
            g2df.draw(new Line2D.Double(0, 0, -6, 6));

            // libera o graphics, não sendo necessário voltar a translação
            // nem a rotação
            g2df.dispose();
            g2d.setStroke(acStroke);

            ////////////////////////////////////////////////////////////////////
            //Verifica se existe transição para escrever os eventos
            if (itemTransition != null) {
                //Cacula o centro entre os vetores
                int x = (int) pointOut.getX() + (((int) pointIn.getX() - (int) pointOut.getX()) / 2);
                int y = (int) pointOut.getY() + (((int) pointIn.getY() - (int) pointOut.getY()) / 2);

                //Cria um novo gráfico para poder mudar a origem e permitir rotacionar
                g2df = (Graphics2D) g2d.create();
                //faz a translação para a coordenada que deve ser a origem
                g2df.translate(x, y);
                //Calcula o angulo
                angle = SysDrawerHelper.calcAngleStates(pointOut, pointIn);
                if (angle == null) {
                    angle = 0.;
                }
                if (pointIn.getY() < pointOut.getY()) {
                    angle = -angle;
                }

                //Verifica a posição para poder escrever na posição correta.
                //Caso o destino esteja antes da origem no plano do eixo X, é somado mais 180, ou PI no angulo.
                if (((SysGUITransition) itemTransition).getPreviusState().getXCentral() > ((SysGUITransition) itemTransition).getNextState().getXCentral()) {
                    angle = angle + Math.PI;
                }
                g2df.rotate(angle);

                ////////////////////////////////////////////////////////////////
                //Escrever evento
                String eventLabel = null;
                String actualEvent;
                for (SysEventTransition itemEvent : itemTransition.getEvents()) {                    
                    actualEvent = (itemEvent.getGuardCondition() != null && itemEvent.getGuardCondition().length() > 0 ? itemEvent.getEvent() + "[" + itemEvent.getGuardCondition()+ "]" : itemEvent.getEvent());
                    actualEvent = (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0 ? actualEvent + "/" + itemEvent.getOutputLabel() : actualEvent);
                    if (eventLabel == null) {
                        eventLabel = actualEvent;
                    } else {
                        eventLabel = eventLabel + ", " + actualEvent;
                    }
                }

                //Calcula onde deve ser escrito
                Rectangle2D bounds = g2df.getFontMetrics().getStringBounds(eventLabel, g2df);
                //g2df.drawString(events, -(int) (bounds.getWidth() / 2), -2);
                int xEvent = -(int) (bounds.getWidth() / 2);

                for (int i = 0; i < itemTransition.getEvents().size(); i++) {
                    SysEventTransition itemEvent = itemTransition.getEvents().get(i);

                    eventLabel = (itemEvent.getGuardCondition() != null && itemEvent.getGuardCondition().length() > 0 ? itemEvent.getEvent() + "[" + itemEvent.getGuardCondition() + "]" : itemEvent.getEvent());
                    eventLabel = (itemEvent.getOutputLabel() != null && itemEvent.getOutputLabel().length() > 0 ? eventLabel + "/" + itemEvent.getOutputLabel() : eventLabel);
                    if (i < itemTransition.getEvents().size() - 1) {
                        eventLabel = eventLabel + ", ";
                    }

                    acStroke = g2df.getStroke();
                    //Verifica se a transição está selecionada, para alterar o tipo e cor da linha
                    if (itemEvent.getChecked()) {
                        g2df.setColor(Color.RED);
                        float[] dashPattern = {5, 5, 5, 5};
                        g2df.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER, 10,
                                dashPattern, 0));
                    } else {
                        //Transição não selecionada
                        g2df.setStroke(new BasicStroke(2));
                        g2df.setColor(new Color(100, 100, 100));
                    }

                    g2df.drawString(eventLabel, xEvent, -2);
                    bounds = g2df.getFontMetrics().getStringBounds(eventLabel, g2df);
                    xEvent = xEvent + (int) bounds.getWidth();
                    g2df.setStroke(acStroke);
                }

                // libera o graphics, não sendo necessário voltar a translação
                // nem a rotação
                g2df.dispose();
            }
        }
    }
}
