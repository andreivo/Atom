/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação
 *                INPE - Instituto Nacional de Pesquisas Espaciais
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>
 * *****************************************************************************
 */
package sysvap.gui.helper.scriptdebugging;

import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import sysvap.core.events.SysEvent;
import sysvap.core.events.SysValidationPlan;

public class SysScriptDebuggingTableModel extends DefaultTableModel {

    public SysScriptDebuggingTableModel(Object[][] dataProperties, String[] columnNames) {
        super(dataProperties, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    public void addEventRow(SysEvent sysEvent) {
        Object[] dataEvent = new Object[4];
        dataEvent[0] = sysEvent.getEvent();
        dataEvent[1] = sysEvent.getMilisecDelay();
        dataEvent[2] = sysEvent.getRateDelay();
        super.addRow(dataEvent);
    }

    public SysValidationPlan getValidationPlan() {
        SysValidationPlan sysVa = new SysValidationPlan(null, null, null);
        sysVa.setPlan(new ArrayList<SysEvent>());
        SysEvent sysEvent;
        for (int i = 0; i < super.getRowCount(); i++) {
            String event = null;
            Integer delay = null;
            Integer rate = null;
            if (getValueAt(i, 0) != null) {
                event = getValueAt(i, 0).toString();
            }
            if (getValueAt(i, 1) != null) {
                Object obj;
                obj = getValueAt(i, 1);
                if (obj.getClass() == Integer.class) {
                    delay = (Integer) obj;
                } else {
                    delay = Integer.parseInt((String) obj);
                }
            }
            if (getValueAt(i, 2) != null) {
                Object obj;
                obj = getValueAt(i, 2);
                if (obj.getClass() == Integer.class) {
                    rate = (Integer) obj;
                } else {
                    rate = Integer.parseInt((String) obj);
                }
            }

            sysEvent = new SysEvent(event, delay, rate);
            sysVa.getPlan().add(sysEvent);
        }
        return sysVa;
    }
}
