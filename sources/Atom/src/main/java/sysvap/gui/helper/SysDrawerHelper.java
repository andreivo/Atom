/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper;

import java.awt.Dimension;
import java.awt.Point;
import sysvap.gui.JPanelView;
   
public class SysDrawerHelper {

    public static int getCenterMouse(int value) {
        return value - (SysConstants.BALL_DIAMETER / 2);
    }

    public static int getCenterState(int value) {
        return (value + (SysConstants.BALL_DIAMETER / 2));
    }

    public static void setDimension(JPanelView jpVisualizacao, Dimension dimension, Point point) {

        int ballDiameter = (SysConstants.BALL_DIAMETER) + 20;
        if ((dimension.getWidth() <= SysDrawerHelper.getCenterMouse((int) point.getX()) + (ballDiameter))
                && (dimension.getHeight() <= SysDrawerHelper.getCenterMouse((int) point.getY()) + (ballDiameter))) {
            dimension.setSize(SysDrawerHelper.getCenterMouse((int) point.getX()) + ballDiameter,
                    SysDrawerHelper.getCenterMouse((int) point.getY()) + ballDiameter);
        } else if (dimension.getWidth() <= (int) point.getX() + ballDiameter) {
            dimension.setSize(SysDrawerHelper.getCenterMouse((int) point.getX()) + ballDiameter, dimension.getHeight());
        } else if (dimension.getHeight() < SysDrawerHelper.getCenterMouse((int) point.getY()) + (ballDiameter)) {
            dimension.setSize(dimension.getWidth(), SysDrawerHelper.getCenterMouse((int) point.getY()) + ballDiameter);
        }

        //--- Seta tamanho do form para que habilite o scroll
        jpVisualizacao.setSize(dimension);
        jpVisualizacao.setPreferredSize(dimension);
    }

    public static Double calcHip(Point pointOut, Point pointIn) {

        double catAdj = (pointIn.getX() - pointOut.getX());
        double catOp = (pointIn.getY() - pointOut.getY());
        double hip = Math.sqrt(catAdj * catAdj + catOp * catOp);
        if (hip < (SysConstants.BALL_DIAMETER / 2)) {
            return null;
        }

        return hip;
    }

    public static Double calcAngleStates(Point pointOut, Point pointIn) {
        double catAdj = (pointIn.getX() - pointOut.getX());

        Double hip = calcHip(pointOut, pointIn);
        if ((hip == null) || (hip < (SysConstants.BALL_DIAMETER / 2))) {
            return null;
        }
        return Math.acos(catAdj / hip);
    }

    public static Point calcPointIntersectState(Point pointOut, Point pointIn, int angleOffSet) {
        Double angle = calcAngleStates(pointOut, pointIn);

        if (angle == null) {
            return null;
        }
        if (pointIn.getY() < pointOut.getY()) {
            angle = angle + (Math.toRadians(angleOffSet));
        } else {
            angle = angle - (Math.toRadians(angleOffSet));
        }

        double dx = Math.cos(angle) * SysConstants.BALL_DIAMETER / 2.;
        double dy = Math.sin(angle) * SysConstants.BALL_DIAMETER / 2.;

        // desenha a linha de ligação        
        if (pointIn.getY() < pointOut.getY()) {
            dy = -dy;
        }

        int x = ((int) pointOut.getX() + (int) dx);
        int y = ((int) pointOut.getY() + (int) dy);

        return new Point(x, y);
    }
}
