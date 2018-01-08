/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.gui.helper;

import javax.swing.JScrollPane;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysState;
import sysvap.gui.JPanelView;
import sysvap.gui.SysView;

/**
 *
 * @author Ivo
 */
public class SysSimulationListener implements SysISimulationListener {

    private SysView form;

    public SysSimulationListener(SysView form) {
        this.form = form;
    }

    public void OnEvent(SysState currentDebugState) {
        if (form.getSysSimulation() != null) {
            SysMEF sysMEF = currentDebugState.getParentSysMEF();

            for (int i = 0; i < form.getTbsPrincipal().getComponentCount(); i++) {
                Object item = form.getTbsPrincipal().getComponent(i);

                if (item.getClass() == JScrollPane.class) {
                    JPanelView jPaneView = (JPanelView) ((JScrollPane) item).getViewport().getComponent(0);

                    if (jPaneView.getSysMEF().getId().equals(sysMEF.getId())) {
                        jPaneView.getSysMEF().setCurrentDebugState(currentDebugState);
                        form.getTbsPrincipal().repaint();
                        return;
                    }
                }
            }
        }
    }

    public void stopAll() {
        SysActionsGUI.debugStopSimulation(form, false);
    }
}
