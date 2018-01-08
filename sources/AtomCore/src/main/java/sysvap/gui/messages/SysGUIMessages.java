/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.messages;

import java.awt.FontMetrics;
import java.io.Serializable;

public class SysGUIMessages implements Serializable {

    private static final long serialVersionUID = 1L;  
    private SysTypeGUIMessages typeMessage;
    private int x;
    private int y;
    private Object otherMessage01;
    private Object otherMessage02;

    public SysGUIMessages(SysTypeGUIMessages typeMessage, int x, int y) {
        this.typeMessage = typeMessage;
        this.x = x;
        this.y = y;
    }

    public SysGUIMessages(SysTypeGUIMessages typeMessage, int x, int y, int otherMessage) {
        this.typeMessage = typeMessage;
        this.x = x;
        this.y = y;
        this.otherMessage01 = otherMessage;
    }
    
    public SysGUIMessages(SysTypeGUIMessages typeMessage, int x, int y, int otherMessage, Object sysState) {
        this.typeMessage = typeMessage;
        this.x = x;
        this.y = y;
        this.otherMessage01 = otherMessage;
        this.otherMessage02 = sysState;
    }
    
    public SysGUIMessages(SysTypeGUIMessages typeMessage, int x, int y, FontMetrics otherMessage) {
        this.typeMessage = typeMessage;
        this.x = x;
        this.y = y;
        this.otherMessage01 = otherMessage;
    }

    public Object getOtherMessage01() {
        return otherMessage01;
    }

    public void setOtherMessage01(Object otherMessage) {
        this.otherMessage01 = otherMessage;
    }

    public Object getOtherMessage02() {
        return otherMessage02;
    }

    public void setOtherMessage02(Object otherMessage02) {
        this.otherMessage02 = otherMessage02;
    }
    
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public SysTypeGUIMessages getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(SysTypeGUIMessages typeMessage) {
        this.typeMessage = typeMessage;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
