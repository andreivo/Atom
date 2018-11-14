/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.gui.core.data;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import sysvap.core.data.SysBreakPointTransition;
import sysvap.gui.helper.propertyeditor.SysIPropEditor;

/**
 *
 * @author LOCAL\andre.ivo
 */
public class SysGUIBreakPointTransition extends SysBreakPointTransition  implements SysIPropEditor, Observer, Serializable {

    public void update(Observable o, Object arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
