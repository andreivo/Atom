/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 *          EMBRACE - Estudo e Monitoramento Brasileiro do Clima Espacial        
 *                               CTIS Tecnologia S/A                             
 * -----------------------------------------------------------------------------
 * Arquiteto de Software - André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * Analista de Sistemas  - Fernando de O. Pereira <fernando@dpi.inpe.br>        
 * Analista de Sistemas  - Rodolfo G. Lotte <rodolfo.lotte@inpe.br>             
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JScrollPane;
import sysvap.gui.JPanelView;
import sysvap.gui.SysView;
import sysvap.gui.helper.propertyeditor.cellbutton.SysITableCellButtonRepaint;

public class SysTableCellColorChooseFormUpdate implements SysITableCellButtonRepaint {

    private SysView form;

    public SysTableCellColorChooseFormUpdate(SysView form) {
        this.form = form;
    }

    public SysView getForm() {
        return form;
    }

    public void setForm(SysView form) {
        this.form = form;
    }

    public void repaint(Color color) {
        for (Component itemComp : form.getTbsPrincipal().getComponents()) {
            if (itemComp.getClass() == JScrollPane.class) {
                if (((JScrollPane) itemComp).getViewport().getView().getClass() == JPanelView.class) {
                   JPanelView tbView = (JPanelView)((JScrollPane) itemComp).getViewport().getView();
                   tbView.setBackground(color);
                }
            }
        }
    }
}
