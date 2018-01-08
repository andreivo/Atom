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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import sysvap.core.data.SysProject;
import sysvap.core.events.SysEvent;
import sysvap.core.events.SysValidationPlan;
import sysvap.gui.helper.SysSendEvent;

public class SysSimulationComplete extends SysSimulation {

    private SysValidationPlan sysValidationPlan;

    public SysSimulationComplete(SysProject sysProject) {
        super(sysProject);
    }

    public SysSimulationComplete(String fileSysProject) throws FileNotFoundException, IOException, ClassNotFoundException {
        super();
        SysProject sysProject = new SysProject();
        sysProject = SysProject.LoadFromFile(sysProject, fileSysProject);
        super.setSysProject(sysProject);
    }

    public SysSimulationComplete(SysProject sysProject, SysValidationPlan sysValidationPlan) {
        super();
        super.setSysProject(sysProject);
        this.sysValidationPlan = sysValidationPlan;
        super.setInitialStatePlan(sysValidationPlan.getMainMefInitialState());
        super.setFinalStatePlan(sysValidationPlan.getMainMefFinalState());
    }

    public SysSimulationComplete(String fileSysProject, String fileSysValidationPlan) throws FileNotFoundException, IOException, ClassNotFoundException {
        super();
        SysProject sysProject = new SysProject();
        sysProject = SysProject.LoadFromFile(sysProject, fileSysProject);
        super.setSysProject(sysProject);

        this.sysValidationPlan = SysValidationPlan.LoadFromXML(fileSysValidationPlan);
    }

    public SysValidationPlan getSysValidationPlan() {
        return sysValidationPlan;
    }

    public void setSysValidationPlan(SysValidationPlan sysValidationPlan) {
        this.sysValidationPlan = sysValidationPlan;
    }

    @Override
    public boolean init() {
        boolean result = super.init();
        if (!result) {
            return false;
        }
        
        executeplan(0);
        result = true;
        return result;
    }

    public void executeplan(int i) {
        SysEvent event;
        if (i < getSysValidationPlan().getPlan().size()) {
            if (!isStopAll()) {
                event = getSysValidationPlan().getPlan().get(i);
                i++;

                //Inicia a simulação
                int delay = 0;
                Integer rateDelay = getSysProject().getRateDelay();

                if (event.getRateDelay() != null) {
                    rateDelay = event.getRateDelay();
                }

                if (event.getMilisecDelay() != null) {
                    delay = event.getMilisecDelay();
                    if (rateDelay != null) {
                        delay = delay / rateDelay;
                    }
                }

                Timer sendEventScheduler = new Timer();
                SysSendEvent sysSendEvent = new SysSendEvent(this, event, getSysValidationPlan().getPlan().size(), i);
                sendEventScheduler.schedule(sysSendEvent, delay);
            }
        }
    }

    public void loadEventsFromFile(String fileSysValidationPlan) throws FileNotFoundException, IOException, ClassNotFoundException {
        this.sysValidationPlan = SysValidationPlan.LoadFromXML(fileSysValidationPlan);
        super.setInitialStatePlan(sysValidationPlan.getMainMefInitialState());
        super.setFinalStatePlan(sysValidationPlan.getMainMefFinalState());
    }
}
