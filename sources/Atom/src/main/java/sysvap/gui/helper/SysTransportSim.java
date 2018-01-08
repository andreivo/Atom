/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper;

import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysProject;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;
import sysvap.gui.core.data.SysGUIProject;

public class SysTransportSim {

    public static SysProject sysGUIProjectToSysProject(SysGUIProject sysGUIProject) {
        //Cria um novo projeto
        SysProject result = new SysProject();
        result.setActionOnEnter(sysGUIProject.getActionOnEnter());
        result.setActionOnExit(sysGUIProject.getActionOnExit());
        result.setActionOnUnrecognizedEvent(sysGUIProject.getActionOnUnrecognizedEvent());
        result.setChanged(false);
        result.setNameProject(sysGUIProject.getNameProject());
        result.setPathProject(sysGUIProject.getPathProject());
        result.setSelectedTool(SysTool.TOOL_MOUSE);
        result.setRateDelay(sysGUIProject.getRateDelay());

        //Cria as MEFS
        for (SysMEF itemMEF : sysGUIProject.getMEFs()) {
            SysMEF resultMEF = new SysMEF(itemMEF.getId(), itemMEF.getName(), result);
            result.addMEF(resultMEF);
            resultMEF.setId(itemMEF.getId());
            resultMEF.setName(itemMEF.getName());
            resultMEF.setActionOnEnter(itemMEF.getActionOnEnter());
            resultMEF.setActionOnExit(itemMEF.getActionOnExit());
            resultMEF.setKeepHistoryStates(itemMEF.getKeepHistoryStates());

            //Cria os estados da MEF
            for (SysState itemState : itemMEF.getSysStates()) {
                SysState resultState = new SysState(itemState.getId(), itemState.getName(),
                        itemState.getX(), itemState.getY(), resultMEF);
                resultMEF.addState(resultState);
                resultState.setId(itemState.getId());
                resultState.setName(itemState.getName());
                resultState.setActionOnEnter(itemState.getActionOnEnter());
                resultState.setActionOnExit(itemState.getActionOnExit());
                resultState.setType(itemState.getType());                
                resultState.setOutputLabel(itemState.getOutputLabel());
            }
        }

        SysMEF mainMEF = (SysMEF) sysGUIProject.getMainMEF().getSelectedItem();
        SysMEF selMEF = result.getMEFByNameID(mainMEF.getId(), mainMEF.getName());
        result.getMainMEF().setSelectedItem(selMEF);

        for (SysMEF itemMEF : sysGUIProject.getMEFs()) {
            for (SysState itemState : itemMEF.getSysStates()) {
                if (itemState.getSub_MEF01().getSelectedItem() != null) {                    
                    SysMEF subMEF = (SysMEF) itemState.getSub_MEF01().getSelectedItem();
                    selMEF = result.getMEFByNameID(subMEF.getId(), subMEF.getName());                    
                    SysMEF sysMEF = result.getMEFByNameID(itemMEF.getId(),itemMEF.getName());                    
                    SysState selState = sysMEF.getStateByNameID(itemState.getName(), itemState.getId());
                    selState.getSub_MEF01().setSelectedItem(selMEF);                                        
                } 
                
                if (itemState.getSub_MEF02().getSelectedItem() != null) {
                    SysMEF subMEF = (SysMEF) itemState.getSub_MEF02().getSelectedItem();
                    selMEF = result.getMEFByNameID(subMEF.getId(), subMEF.getName());
                    SysMEF sysMEF = result.getMEFByNameID(itemMEF.getId(),itemMEF.getName());                    
                    SysState selState = sysMEF.getStateByNameID(itemState.getName(), itemState.getId());
                    selState.getSub_MEF02().setSelectedItem(selMEF);
                }

                for (SysTransition itemTransition : itemState.getSysTransitionOUT()) {
                    selMEF = result.getMEFByNameID(itemMEF.getId(), itemMEF.getName());
                    SysState atualState = selMEF.getStateByNameID(itemTransition.getPreviusState().getName(), itemTransition.getPreviusState().getId());
                    SysState nextState = selMEF.getStateByNameID(itemTransition.getNextState().getName(), itemTransition.getNextState().getId());
                    if (nextState != null) {
                        for (SysEventTransition itemEvent : itemTransition.getEvents()) {
                            SysTransition atualTransi = atualState.addSysTransition(nextState, itemEvent.getEvent(), 
                                    itemEvent.getOutputLabel(), itemEvent.getGuardCondition(),
                                    itemEvent.getActionOnEnter(), itemEvent.getActionOnExit());
                            atualTransi.setActionOnEnter(itemTransition.getActionOnEnter());
                            atualTransi.setActionOnExit(itemTransition.getActionOnExit());
                            atualTransi.setId(itemTransition.getId());
                            atualTransi.setMilisec_timeout(itemTransition.getMilisec_timeout());
                            atualTransi.setTimeout(itemTransition.getTimeout());                                                        
                        }
                    }
                }
            }
        }

        return result;
    }
}
