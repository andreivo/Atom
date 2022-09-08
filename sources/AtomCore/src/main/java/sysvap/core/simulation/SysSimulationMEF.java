package sysvap.core.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.ScriptEngine;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;
import sysvap.core.events.SysEvent;
import sysvap.core.events.SysEventState;

public class SysSimulationMEF {

    private ScriptEngine scriptEngine;
    private SysMEF sysMEF;
    private SysSimulation sysSimulation;
    private SysState initialState;
    private SysState currentState;
    private List<SysState> finalStateList;
    private ConcurrentHashMap sub_MEFsOnline;
    private ConcurrentHashMap sub_MEFsStateOffline;
    private SysEventState planInitialState;
    private SysEventState planFinalState;
    private SysEvent actualEvent;
    private SysTransition sysTransitionTimeOut;
    private SysLock sysLock;
    private boolean online;
    protected Timer timerTimeout = null; //incluir
    protected SysTransitionTimeOut taskTimeOut = null;

    public SysSimulationMEF(SysSimulation sysSimulation, SysMEF sysMEF,
            SysEventState planInitialState, SysEventState planFinalState,
            ScriptEngine scriptEngine, SysLock sysLock) {
        this.sysSimulation = sysSimulation;
        this.sysMEF = sysMEF;
        this.planInitialState = planInitialState;
        this.planFinalState = planFinalState;
        this.sysLock = sysLock;


        //this.sysValidationPlan = new SysValidationPlan(null, null, new ArrayList<SysEvent>());
        this.finalStateList = new ArrayList<SysState>();
        this.sub_MEFsOnline = new ConcurrentHashMap<Integer, SysSimulationSubMEFOnline>();
        this.sub_MEFsStateOffline = new ConcurrentHashMap<Integer, SysSimulationSubMEFStateOffline>();
        this.scriptEngine = scriptEngine;
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void setScriptEngine(ScriptEngine scriptEngine) {
        this.scriptEngine = scriptEngine;
    }

    public SysSimulation getSysSimulation() {
        return sysSimulation;
    }

    public void setSysSimulation(SysSimulation sysSimulation) {
        this.sysSimulation = sysSimulation;
    }

    public SysMEF getSysMEF() {
        return sysMEF;
    }

    public void setSysMEF(SysMEF sysMEF) {
        this.sysMEF = sysMEF;
    }

    public SysState getInitialState() {
        return initialState;
    }

    public void setInitialState(SysState initialState) {
        this.initialState = initialState;
    }

    public SysState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(SysState currentState) {
        this.currentState = currentState;
    }

    public List<SysState> getFinalStateList() {
        return finalStateList;
    }

    public void setFinalStateList(List<SysState> finalStateList) {
        this.finalStateList = finalStateList;
    }

    public ConcurrentHashMap getSub_MEFsOnline() {
        return sub_MEFsOnline;
    }

    public void setSub_MEFsOnline(ConcurrentHashMap sub_MEFs) {
        this.sub_MEFsOnline = sub_MEFs;
    }

    public SysEventState getPlanInitialState() {
        return planInitialState;
    }

    public void setPlanInitialState(SysEventState planInitialState) {
        this.planInitialState = planInitialState;
    }

    public SysEventState getPlanFinalState() {
        return planFinalState;
    }

    public void setPlanFinalState(SysEventState planFinalState) {
        this.planFinalState = planFinalState;
    }

    public SysEvent getActualEvent() {
        return actualEvent;
    }

    public void setActualEvent(SysEvent actualEvent) {
        this.actualEvent = actualEvent;
    }

    public SysTransition getSysTransitionTimeOut() {
        return sysTransitionTimeOut;
    }

    public void setSysTransitionTimeOut(SysTransition sysTransitionTimeOut) {
        this.sysTransitionTimeOut = sysTransitionTimeOut;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean init() {
        //definindo o estado inicial e final.
        if (this.initialState == null) {
            if (this.planInitialState != null) {
                this.initialState = sysMEF.getStateByName(this.planInitialState.getState());
            } else {
                this.initialState = SysSimulateHelper.getInitialState(sysMEF);
            }
        }

        SysState finalState = null;
        if (this.planFinalState != null) {
            finalState = sysMEF.getStateByName(this.planFinalState.getState());
            this.finalStateList.add(finalState);
        } else {
            this.finalStateList = SysSimulateHelper.getFinalState(sysMEF);
        }

        if (initialState == null) {
            System.err.println("Initial state not defined in simulation. Define initial state in domain or plan!");
            return false;
        }

        //Atualiza as variaveis do ambiente LUA
        this.currentState = getInitialState();
        this.sysSimulation.setActualState(this.currentState);
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_MEF", this.sysMEF);
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_initialState", this.initialState);
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_finalState", this.finalStateList);
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_state", this.currentState);

        //Executa as ações onEnter da MEF;
        SysSimulateHelper.execAction(sysLock, scriptEngine, sysMEF.getActionOnEnter());

        if (this.currentState != null) {
            //Executa as ações do onEnter do estado.
            SysSimulateHelper.execAction(sysLock, scriptEngine, currentState.getActionOnEnter());

            //Verifica se existem submefs para ser executadas.
            startSubMEFs(this.currentState);
        }

        setOnline(true);

        getTransitionTimeOut();

        return true;
    }

    public boolean run() {

        //Processa a fila de eventos gerados internamente
        sysSimulation.sendQueueEvents();

        if (isOnline()) {

            //Obtem a transição            
            SysEventTransition transitionToEvent = SysSimulateHelper.getTransitionToEvent(currentState, this.actualEvent.getEvent(), sysLock, scriptEngine);
            if (transitionToEvent != null) {
                    //Verifica se atende a condição de guarda
                    if (SysSimulateHelper.getEvalGuardCondition(sysLock, scriptEngine, transitionToEvent.getGuardCondition())) {

                        //Verifica se existe algum timer para TimeOut ligado.
                        //caso exista desliga
                        stopTimeOut();

                        // Parar subThreads
                        stopSubMEFs(this.currentState);

                        //Executa as ações onExit do Estado;
                        SysSimulateHelper.execAction(sysLock, scriptEngine, this.currentState.getActionOnExit());

                        //Atualiza as variaveis dentro do LUA
                        this.currentState = transitionToEvent.getParent().getNextState();
                        this.sysSimulation.setActualState(this.currentState);
                        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_state", this.currentState);
                        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_transition", transitionToEvent.getParent());
                        SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_event", transitionToEvent);

                        //Exibe a Saída da transição
                        if (transitionToEvent.getOutputLabel() != null && transitionToEvent.getOutputLabel().length() > 0) {
                            System.out.println("Output: " + transitionToEvent.getOutputLabel());
                        }

                        //Executa as ações OnEnter - transition - event
                        SysSimulateHelper.execAction(sysLock, this.scriptEngine, transitionToEvent.getParent().getActionOnEnter());
                        SysSimulateHelper.execAction(sysLock, this.scriptEngine, transitionToEvent.getActionOnEnter());

                        //Executa as ações OnExit - event - transition
                        SysSimulateHelper.execAction(sysLock, this.scriptEngine, transitionToEvent.getActionOnExit());
                        SysSimulateHelper.execAction(sysLock, this.scriptEngine, transitionToEvent.getParent().getActionOnExit());

                        //Exibe a Saída do estado
                        if (this.currentState.getOutputLabel() != null && this.currentState.getOutputLabel().length() > 0) {
                            System.out.println("Output: " + this.currentState.getOutputLabel());
                        }

                        //Executa as ações OnEnter - action
                        SysSimulateHelper.execAction(sysLock, this.scriptEngine, this.currentState.getActionOnEnter());

                        //Verifica se existem submefs para ser executadas.
                        startSubMEFs(this.currentState);

                        getTransitionTimeOut();
                        
                        //Processa a fila de eventos gerados internamente
                        sysSimulation.sendQueueEvents();

                        return true;
                    } else {

                        return false;
                    }
            }
        }
        return false;
    }

    public synchronized boolean runTimeOut() {
        
        //Processa a fila de eventos gerados internamente
        sysSimulation.sendQueueEvents();
        
        if (isOnline()) {
            //Obtem a transição
            if (sysTransitionTimeOut != null) {
                // Parar subThreads
                stopSubMEFs(this.currentState);
                //Executa as ações onExit do Estado;
                SysSimulateHelper.execAction(sysLock, scriptEngine, this.currentState.getActionOnExit());
                //Atualiza as variaveis dentro do LUA
                this.currentState = sysTransitionTimeOut.getNextState();
                this.sysSimulation.setActualState(this.currentState);
                SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_state", this.currentState);
                SysSimulateHelper.putVarAction(sysLock, scriptEngine, getSysMEF().getName() + "_transition", sysTransitionTimeOut);
                //Executa as ações OnEnter - transition - event
                SysSimulateHelper.execAction(sysLock, this.scriptEngine, sysTransitionTimeOut.getActionOnEnter());
                //Executa as ações OnExit - event - transition
                SysSimulateHelper.execAction(sysLock, this.scriptEngine, sysTransitionTimeOut.getActionOnExit());
                //Executa as ações OnEnter - action
                SysSimulateHelper.execAction(sysLock, this.scriptEngine, this.currentState.getActionOnEnter());
                //Verifica se existem submefs para ser executadas.
                startSubMEFs(this.currentState);
                getTransitionTimeOut();
                //Processa a fila de eventos gerados internamente
                sysSimulation.sendQueueEvents();
                return true;
            }
        }
        return false;
    }

    private SysTransition getTransitionTimeOut() {
        this.sysTransitionTimeOut = null;

        for (SysTransition item : this.currentState.getSysTransitionOUT()) {
            if (item.getTimeout()) {
                this.sysTransitionTimeOut = item;
            }
        }

        if (sysTransitionTimeOut != null) {
            timerTimeout = new Timer();
            taskTimeOut = new SysTransitionTimeOut(this, currentState);
            Integer timeout = this.sysTransitionTimeOut.getMilisec_timeout();
            if (this.sysMEF.getSysProject().getRateDelay() != null) {
                timeout = timeout / this.sysMEF.getSysProject().getRateDelay();
            }
            timerTimeout.schedule(taskTimeOut, timeout);
        }

        return sysTransitionTimeOut;
    }

    public boolean sendEvent(SysEvent event) {
        this.actualEvent = event;
        boolean result = run();
        if (!result) {
            result = sendEventSubMEFs(event);
        }
        return result;
    }

    public void stopTimeOut() {
        if (timerTimeout != null) {
            timerTimeout.cancel();
            timerTimeout.purge();
        }

        stopTimeOutSubMEFs();
    }

    public void stopTimeOutSubMEFs() {
        try {
            if (this.currentState != null) {
                Object obj = sub_MEFsOnline.get(this.currentState.getId());
                if (obj != null) {
                    SysSimulationSubMEFOnline subMefs = (SysSimulationSubMEFOnline) obj;
                    if (subMefs != null) {
                        if (subMefs.getSub_MEF01() != null) {
                            subMefs.getSub_MEF01().stopTimeOut();
                        }

                        if (subMefs.getSub_MEF02() != null) {
                            subMefs.getSub_MEF02().stopTimeOut();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("SendEventSubMEFs - Message: " + e.getMessage());
        }
    }

    public boolean sendEventSubMEFs(SysEvent event) {
        boolean result = false;
        try {
            if (this.currentState != null) {
                Object obj = sub_MEFsOnline.get(this.currentState.getId());
                if (obj != null) {
                    SysSimulationSubMEFOnline subMefs = (SysSimulationSubMEFOnline) obj;
                    if (subMefs != null) {
                        boolean result1 = false;
                        if (subMefs.getSub_MEF01() != null) {
                            SysSimulationMEF sub_MEF01 = subMefs.getSub_MEF01();
                            result1 = sub_MEF01.sendEvent(event);
                        }
                        boolean result2 = false;
                        if (subMefs.getSub_MEF02() != null) {
                            result2 = subMefs.getSub_MEF02().sendEvent(event);
                        }
                        result = result1 || result2;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("SendEventSubMEFs - Message: " + e.getMessage());
            return result;
        }
        return result;
    }

    public boolean isFinalState(SysState sysState) {
        for (SysState item : getFinalStateList()) {
            if (item.equals(sysState)) {
                return true;
            }
        }
        return false;
    }

    private void stopSubMEFs(SysState currentState) {
        SysMEF sysMEF01 = (SysMEF) currentState.getSub_MEF01().getSelectedItem();
        SysMEF sysMEF02 = (SysMEF) currentState.getSub_MEF02().getSelectedItem();
        if (sysMEF01 != null || sysMEF02 != null) {

            SysSimulationSubMEFOnline subMefs = (SysSimulationSubMEFOnline) sub_MEFsOnline.get(currentState.getId());
            if (subMefs != null) {
                //Guarda o historico dos estados caso a mef esteja configurada para manter o estado.
                SysSimulationSubMEFStateOffline statesSubMEF = new SysSimulationSubMEFStateOffline();
                if (sysMEF01 != null) {
                    if (sysMEF01.getKeepHistoryStates()) {
                        if (subMefs.getSub_MEF01() != null) {
                            statesSubMEF.setStateSubMEF01(subMefs.getSub_MEF01().getCurrentState().getName());
                        }
                    }
                }

                if (sysMEF02 != null) {
                    if (sysMEF02.getKeepHistoryStates()) {
                        if (subMefs.getSub_MEF02() != null) {
                            statesSubMEF.setStateSubMEF02(subMefs.getSub_MEF02().getCurrentState().getName());
                        }
                    }
                }
                sub_MEFsStateOffline.put(currentState.getId(), statesSubMEF);
            }

            //Para as threds
            if (subMefs.getSub_MEF01() != null) {
                subMefs.getSub_MEF01().stopTimeOut();
                subMefs.getSub_MEF01().release();
                subMefs.setSub_MEF01(null);
            }

            if (subMefs.getSub_MEF02() != null) {
                subMefs.getSub_MEF02().stopTimeOut();
                subMefs.getSub_MEF02().release();
                subMefs.setSub_MEF02(null);
            }

            //Remove da lista
            sub_MEFsOnline.remove(currentState.getId());
        }
    }

    private void startSubMEFs(SysState currentState) {
        //Verifica se é necessário disparar Thread para subEventos;
        SysMEF sysMEF01 = (SysMEF) currentState.getSub_MEF01().getSelectedItem();
        SysMEF sysMEF02 = (SysMEF) currentState.getSub_MEF02().getSelectedItem();

        if (sysMEF01 != null || sysMEF02 != null) {

            SysSimulationSubMEFOnline subMefs = (SysSimulationSubMEFOnline) sub_MEFsOnline.get(currentState.getId());
            if (subMefs == null) {
                subMefs = new SysSimulationSubMEFOnline();
                sub_MEFsOnline.put(currentState.getId(), subMefs);

                SysSimulationSubMEFStateOffline statesSubMEF = (SysSimulationSubMEFStateOffline) sub_MEFsStateOffline.get(currentState.getId());

                if (sysMEF01 != null) {
                    try {
                        //Verifica se existe estado no histórico para a submef
                        //Caso exista significa que a submef ja foi executada e que o sistema irá reiniciar do ponto que havia parado
                        SysEventState eventInitialSubMEF01 = null;
                        if ((statesSubMEF != null) && (statesSubMEF.getStateSubMEF01() != null)) {
                            eventInitialSubMEF01 = new SysEventState();
                            eventInitialSubMEF01.setState(statesSubMEF.getStateSubMEF01());
                        } else {
                            //Nova thread
                            if (this.planInitialState != null) {
                                eventInitialSubMEF01 = this.planInitialState.getSubState01();
                            }
                        }
                        SysEventState eventFinalSubMEF01 = null;
                        if (this.planFinalState != null) {
                            eventFinalSubMEF01 = this.planFinalState.getSubState01();
                        }

                        if (subMefs.getSub_MEF01() == null) {
                            subMefs.setSub_MEF01(new SysSimulationMEF(sysSimulation, sysMEF01, eventInitialSubMEF01,
                                    eventFinalSubMEF01, scriptEngine, sysLock));
                            subMefs.getSub_MEF01().init();
                        }
                    } catch (Exception ex) {
                        System.err.println("Error on state '" + currentState.getName() + "' on sub_MEF01 '" + sysMEF01.getName() + "': " + ex.getMessage());
                    }
                }

                if (sysMEF02 != null) {
                    try {
                        //Verifica se existe estado no histórico para a submef
                        //Caso exista significa que a submef ja foi executada e que o sistema irá reiniciar do ponto que havia parado
                        SysEventState eventInitialSubMEF02 = null;
                        if ((statesSubMEF != null) && (statesSubMEF.getStateSubMEF02() != null)) {
                            eventInitialSubMEF02 = new SysEventState();
                            eventInitialSubMEF02.setState(statesSubMEF.getStateSubMEF02());
                        } else {
                            //Nova thread
                            if (this.planInitialState != null) {
                                eventInitialSubMEF02 = this.planInitialState.getSubState02();
                            }
                        }

                        SysEventState eventFinalSubMEF02 = null;
                        if (this.planFinalState != null) {
                            eventFinalSubMEF02 = this.planFinalState.getSubState02();
                        }

                        if (subMefs.getSub_MEF02() == null) {
                            subMefs.setSub_MEF02(new SysSimulationMEF(sysSimulation, sysMEF02, eventInitialSubMEF02,
                                    eventFinalSubMEF02, scriptEngine, sysLock));
                            subMefs.getSub_MEF02().init();
                        }

                        //Remove o estado atual da lista.
                        if (statesSubMEF != null) {
                            this.sub_MEFsStateOffline.remove(currentState.getId());
                        }

                    } catch (Exception ex) {
                        System.err.println("Error on state '" + currentState.getName() + "' on sub_MEF02 '" + sysMEF02.getName() + "': " + ex.getMessage());
                    }
                }
            }
        }
    }

    public void release() {
        setOnline(false);
        stopTimeOut();
        releaseSubMEFs();
        //Executa as ações onExit da MEF;
        SysSimulateHelper.execAction(sysLock, scriptEngine, sysMEF.getActionOnExit());
    }

    public void releaseSubMEFs() {
        try {
            if (this.currentState != null) {
                Object obj = sub_MEFsOnline.get(this.currentState.getId());
                if (obj != null) {
                    SysSimulationSubMEFOnline subMefs = (SysSimulationSubMEFOnline) obj;
                    if (subMefs != null) {
                        if (subMefs.getSub_MEF01() != null) {
                            subMefs.getSub_MEF01().release();
                        }
                        if (subMefs.getSub_MEF02() != null) {
                            subMefs.getSub_MEF02().release();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("SendEventSubMEFs - Message: " + e.getMessage());
        }
    }
}
