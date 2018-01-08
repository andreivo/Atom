/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper;

import java.awt.Point;

public class SysUtils {

    public static Double calcHip(int x0, int y0, int x1, int y1) {

        double catAdj = (x1 - x0);
        double catOp = (y1 - y0);
        double hip = Math.sqrt(catAdj * catAdj + catOp * catOp);
        if (hip < (SysConstants.BALL_DIAMETER / 2)) {
            return null;
        }

        return hip;
    }

    public static Double calcAngleStates(int x0, int y0, int x1, int y1) {
        double catAdj = (x1 - x0);
        Double hip = calcHip(x0, y0, x1, y1);
        if ((hip == null) || (hip < (SysConstants.BALL_DIAMETER / 2))) {
            return null;
        }
        return Math.acos(catAdj / hip);
    }

    public static Point calcPointIntersectState(int x0, int y0, int x1, int y1) {
        Double angulo = calcAngleStates(x0, y0, x1, y1);

        if (angulo == null) {
            return null;
        }

        double dx = Math.cos(angulo) * SysConstants.BALL_DIAMETER / 2.;
        double dy = Math.sin(angulo) * SysConstants.BALL_DIAMETER / 2.;

        // desenha a linha de ligação        
        if (y1 < y0) {
            dy = -dy;
        }

        int x = (x0 + (int) dx);
        int y = (y0 + (int) dy);

        return new Point(x, y);
    }
}
