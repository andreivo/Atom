/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.core.data;

import java.io.Serializable;
import java.util.List;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

/**
 *
 * @author LOCAL\andre.ivo
 */
public class SysBreakPointTransition implements SysIPropEditor, Serializable {
    
    private Integer x;
    private Integer y;

    public SysBreakPointTransition(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public SysBreakPointTransition() {
    }
        

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
    
    public List<String> getFieldsName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getActionOnEnter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setActionOnEnter(String action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getActionOnExit() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setActionOnExit(String action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getActionOnUnrecognizedEvent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setActionOnUnrecognizedEvent(String action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
