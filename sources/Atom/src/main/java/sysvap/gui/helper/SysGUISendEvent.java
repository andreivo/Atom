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
import sysvap.gui.SysView;
import static sysvap.gui.helper.SysActionsGUI.debugContinueSimulation;
import static sysvap.gui.helper.SysActionsGUI.getNameProject;
import static sysvap.gui.helper.SysActionsGUI.updateVarsWatch;

public class SysGUISendEvent extends TimerTask {

    private final SysView form;
    private final SysEvent sysEvent;
    private final int index;
    private final boolean stepAllItens;
    private final boolean stopOnfinish;

    public SysGUISendEvent(SysView form, SysEvent sysEvent, int index, boolean stepAllItens, boolean stopOnfinish) {
        this.form = form;
        //Desabilita os botoes da tela, para evitar o envio indevido
        this.form.configureButtonsOnSendEvent(false);
        this.sysEvent = sysEvent;
        this.index = index;
        this.stepAllItens = stepAllItens;
        this.stopOnfinish = stopOnfinish;
    }

    @Override
    public void run() {
        //Executa o evento
        this.form.getSysSimulation().sendEvent(this.sysEvent);
        updateVarsWatch(form);

        //Verifica se existem mais eventos a serem enviados
        if (form.getJtbScriptDebugging().getRowCount() > index + 1) {
            //Atualiza o indice para o próximo evento
            form.getJtbScriptDebugging().setRowSelectionInterval(index + 1, index + 1);

            //Caso seja para executar até o fim chama novamente a execução
            if (stepAllItens) {
                debugContinueSimulation(form);
            }
        } else {
            //Termina a execução
            if (stopOnfinish) {
                form.configureButtonsOnSendEvent(true);
                form.getSysSimulation().release();
                form.setSysSimulation(null);
                form.stopDebug();
                System.out.println("");
                if (stepAllItens) {
                    System.out.println(SysConstants.OUT_ALERT + "Finished simulation - " + getNameProject());
                } else {
                    System.out.println(SysConstants.OUT_ALERT + "Finished debug simulation - " + getNameProject());
                }
                System.out.println(SysConstants.OUT_ALERT + "--------------------------------------------------");
            }
        }

        //Reconfigura os botões da tela
        if (!stepAllItens) {
            form.configureButtonsOnSendEvent(true);
        }
    }
}
