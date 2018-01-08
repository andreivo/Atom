/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.gui.helper;

import sysvap.core.data.SysState;

/**
 *
 * @author Ivo
 */
public interface SysISimulationListener {

    public void stopAll();
    public void OnEvent(SysState currentDebugState);
}
