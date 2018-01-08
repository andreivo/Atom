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
package sysvap.core.simulation;

import java.util.TimerTask;
import sysvap.core.data.SysState;

public class SysTransitionTimeOut extends TimerTask {
    private final SysSimulationMEF sysSimulationMEF;
    private final SysState sysStateTransition;

    public SysTransitionTimeOut(SysSimulationMEF sysSimulationMEF, SysState sysStateTransition) {
        this.sysSimulationMEF = sysSimulationMEF;
        this.sysStateTransition = sysStateTransition;
    }

    public SysSimulationMEF getSysSimulationMEF() {
        return sysSimulationMEF;
    }

    public SysState getSysStateTransition() {
        return sysStateTransition;
    }
    
    @Override
    public void run() {
        if(sysSimulationMEF.getCurrentState().equals(sysStateTransition)){
            sysSimulationMEF.runTimeOut();
        }
    }
    
}
