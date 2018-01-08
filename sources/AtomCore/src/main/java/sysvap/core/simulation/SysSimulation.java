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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysProject;
import sysvap.core.data.SysState;
import sysvap.core.events.SysEvent;
import sysvap.core.events.SysEventState;
import sysvap.gui.helper.SysISimulationListener;

public class SysSimulation {

    private SysProject sysProject;
    private final ScriptEngineManager scriptEngineManager;
    private final ScriptEngine scriptEngine;
    private SysMEF sysInitialMEF;
    private SysState actualState;
    private SysSimulationMEF sysSimulationMEF;
    private SysEventState initialStatePlan;
    private SysEventState finalStatePlan;
    private SysEvent actualEvent;
    private SysLock sysLock = new SysLock();
    private SysISimulationListener sysSimListener;
    private List<String> eventQueue;
    private boolean stopAll;

    public SysSimulation() {
        this.scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = this.scriptEngineManager.getEngineByExtension(".lua");
        this.eventQueue = new CopyOnWriteArrayList<String>();
    }

    public SysSimulation(SysProject sysProject) {
        this.sysProject = sysProject;

        this.scriptEngineManager = new ScriptEngineManager();
        this.scriptEngine = this.scriptEngineManager.getEngineByExtension(".lua");
        this.eventQueue = new CopyOnWriteArrayList<String>();
    }

    public SysProject getSysProject() {
        return sysProject;
    }

    public void setSysProject(SysProject sysProject) {
        this.sysProject = sysProject;
    }

    public SysEvent getActualEvent() {
        return actualEvent;
    }

    public void setActualEvent(SysEvent actualEvent) {
        this.actualEvent = actualEvent;
    }

    public SysEventState getFinalStatePlan() {
        return finalStatePlan;
    }

    public void setFinalStatePlan(SysEventState finalStatePlan) {
        this.finalStatePlan = finalStatePlan;
    }

    public SysEventState getInitialStatePlan() {
        return initialStatePlan;
    }

    public void setInitialStatePlan(SysEventState initialStatePlan) {
        this.initialStatePlan = initialStatePlan;
    }

    public SysMEF getSysInitialMEF() {
        return sysInitialMEF;
    }

    public void setSysInitialMEF(SysMEF sysInitialMEF) {
        this.sysInitialMEF = sysInitialMEF;
    }

    public SysSimulationMEF getSysSimulationMEF() {
        return sysSimulationMEF;
    }

    public void setSysSimulationMEF(SysSimulationMEF sysSimulationMEF) {
        this.sysSimulationMEF = sysSimulationMEF;
    }

    public SysState getActualState() {
        return actualState;
    }

    public void setActualState(SysState actualState) {
        this.actualState = actualState;
        if (sysSimListener != null) {
            sysSimListener.OnEvent(actualState);
        }
    }

    public SysLock getSysLock() {
        return sysLock;
    }

    public void setSysLock(SysLock sysLock) {
        this.sysLock = sysLock;
    }

    public SysISimulationListener getSysSimListener() {
        return sysSimListener;
    }

    public void setSysSimListener(SysISimulationListener sysSimListener) {
        this.sysSimListener = sysSimListener;
    }

    public boolean isStopAll() {
        return stopAll;
    }

    public void setStopAll(boolean stopAll) {
        this.stopAll = stopAll;
    }

    public boolean init() {
        //definindo o estado inicial e final.
        this.sysInitialMEF = (SysMEF) sysProject.getMainMEF().getSelectedItem();
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, "sysProject", this.sysProject);
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, "initialMEF", this.sysInitialMEF);

        OneArgFunction functionSendEvent = new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                sendEvent(arg.tojstring());
                return null;
            }
        };
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, "sendEvent", functionSendEvent);

        OneArgFunction functionStopAll = new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                setStopAll(true);
                return null;
            }
        };
        SysSimulateHelper.putVarAction(sysLock, scriptEngine, "stopAll", functionStopAll);

        //Executa as ações onEnter do projeto;
        SysSimulateHelper.execAction(sysLock, scriptEngine, sysProject.getActionOnEnter());

        this.sysSimulationMEF = new SysSimulationMEF(this, sysInitialMEF, getInitialStatePlan(),
                getFinalStatePlan(), scriptEngine, sysLock);

        return this.sysSimulationMEF.init();
    }

    public void release() {
        this.sysSimulationMEF.release();
        //Executa as ações onExit do projeto;
        SysSimulateHelper.execAction(sysLock, scriptEngine, sysProject.getActionOnExit());
    }

    public void sendEvent(String event) {
        eventQueue.add(event);
    }

    public void sendEvent(SysEvent event) {
        //Processa a fila de eventos gerados internamente
        sendQueueEvents();
        //Executa o evento atual
        if (!eventControl(event.getEvent())) {
            this.actualEvent = event;
            run();
        }
    }

    public void sendQueueEvents() {
        if (stopAll) {
            if (sysSimListener != null) {
                this.sysSimListener.stopAll();
            }
        } else {
            while (!eventQueue.isEmpty()) {
                Iterator<String> itEvent = eventQueue.iterator();
                while (itEvent.hasNext()) {
                    String itemEvent = itEvent.next();
                    SysEvent ev = new SysEvent(itemEvent, null, null);
                    eventQueue.remove(itemEvent);
                    sendEvent(ev);
                }
            }
        }
    }

    private boolean eventControl(String event) {
        //Verifica se é um evento de controle.
        boolean result = false;
        if (event.startsWith("$")) {
            //Controle para atualização de variáveis
            if (event.startsWith("$var")) {
                String var = event.substring(5, event.indexOf(","));
                String value = event.substring(event.indexOf(",") + 1, event.length() - 1);
                setVarValueFromLUA(var, value);
            } else {
            }

            result = true;
        }
        return result;
    }

    public void run() {
        if (!this.sysSimulationMEF.sendEvent(this.actualEvent)) {
            SysSimulateHelper.putVarAction(sysLock, scriptEngine, "event", this.actualEvent);
            //Executa as ações OnUnrecognizedEvent do projeto;
            SysSimulateHelper.execAction(sysLock, scriptEngine, sysProject.getActionOnUnrecognizedEvent());
        }
    }

    public String getVarValueFromLUA(String variable) {
        Object getValue;
        try {
            String function = "result = " + variable + "\n"
                    + "return result";
            getValue = scriptEngine.eval(function);
        } catch (ScriptException ex) {
            getValue = ex.getMessage();
        }

        if (getValue != null) {
            return getValue.toString();
        }
        return null;
    }

    public String setVarValueFromLUA(String variable, String value) {
        Object getValue;
        try {

            //Atualiza o valor da variável
            String function = variable + " = " + value;
            scriptEngine.eval(function);

            //Retorna o valor atualizado
            function = "result = " + variable + "\n"
                    + "return result";
            getValue = scriptEngine.eval(function);
        } catch (ScriptException ex) {
            getValue = ex.getMessage();
        }

        if (getValue != null) {
            return getValue.toString();
        }
        return null;
    }
}
