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
package sysvap.gui.helper;

import java.util.TimerTask;
import sysvap.core.events.SysEvent;
import sysvap.core.simulation.SysSimulationComplete;

public class SysSendEvent extends TimerTask {
    
    private final SysSimulationComplete sysSimulationComplete;
    private final SysEvent sysEvent;
    private final int sizePlan;
    private final int index;
    
    public SysSendEvent(SysSimulationComplete sysSimulationComplete, SysEvent sysEvent, int sizePlan, int index) {
        this.sysSimulationComplete = sysSimulationComplete;
        this.sysEvent = sysEvent;
        this.sizePlan = sizePlan;
        this.index = index;
    }
    
    @Override
    public void run() {
        //Executa o evento
        sysSimulationComplete.sendEvent(this.sysEvent);
        //Termina a execução
        if (index >= sizePlan) {            
            sysSimulationComplete.release();
            System.out.println("----------------------------------------------------------------------------------------");
            System.out.println("Finished simulation - " + sysSimulationComplete.getSysProject().getNameProject());
            System.out.println("----------------------------------------------------------------------------------------");
        } else {
            sysSimulationComplete.executeplan(index);
        }
    }
}
