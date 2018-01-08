/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sysvap.core.simulation;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.luaj.vm2.LuaBoolean;
import sysvap.core.data.SysEventTransition;
import sysvap.core.data.SysMEF;
import sysvap.core.data.SysState;
import sysvap.core.data.SysTransition;

/**
 *
 * @author Ivo
 */
public class SysSimulateHelper {

    public static SysState getInitialState(SysMEF sysMEF) {
        return sysMEF.getInitialState();
    }

    public static List<SysState> getFinalState(SysMEF sysMEF) {
        return sysMEF.getFinalState();
    }

    public static SysEventTransition getTransitionToEvent(SysState sysState, String event) {
        for (SysTransition transition : sysState.getSysTransitionOUT()) {
            for (SysEventTransition eventTransition : transition.getEvents()) {
                if (eventTransition.getEvent().equals(event)) {
                    return eventTransition;
                }
            }
        }
        return null;
    }

    public static Integer countTransitionToEvent(SysState sysState, String event) {
        Integer result = 0;
        for (SysTransition transition : sysState.getSysTransitionOUT()) {
            for (SysEventTransition eventTransition : transition.getEvents()) {
                if (eventTransition.getEvent().equals(event)) {
                    result = result + 1;
                }
            }
        }
        return result;
    }

    public static void putVarAction(SysLock lockConsole, ScriptEngine scriptEngine, String varKey, Object value) {
        try {
            lockConsole.lock();
            scriptEngine.put(varKey, value);
        } catch (InterruptedException ex) {
        } finally {
            lockConsole.unlock();
        }
    }

    public static Boolean getEvalGuardCondition(SysLock lockConsole, ScriptEngine scriptEngine, String guardCondition) {
        Boolean result = true;
        if (guardCondition != null && guardCondition.length() > 0) {
            try {
                lockConsole.lock();
                scriptEngine.eval("guardCondition = (" + guardCondition + ")");
                Object getResult = scriptEngine.get("guardCondition");
                if (getResult != null && getResult.getClass() == LuaBoolean.class) {
                    result = ((LuaBoolean) getResult).booleanValue();
                } else {
                    System.err.println("Guard condition unrecognized");
                }
            } catch (Exception ex) {
                result = false;
                System.err.println(ex.getMessage());
            } finally {
                lockConsole.unlock();
            }
        }
        return result;
    }

    public static void execAction(SysLock lockConsole, ScriptEngine scriptEngine, String action) {

        //Lock comentado por travar a opção sendEvent dentro do LUA
        //O sistema executava a primeira vez o send e na chamada como o lock não havia sido liberado 
        //o sistema trava.

        try {
            lockConsole.lock();
            try {
                if (action != null) {
                    scriptEngine.eval(action);
                }
            } catch (ScriptException ex) {
                lockConsole.unlock();
                System.err.println("execAction -> " + ex.getMessage());
            }
        } catch (InterruptedException ex) {
        } finally {
            lockConsole.unlock();
        }
    }
}
