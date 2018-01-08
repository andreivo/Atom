/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.core.events;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("SysEventState")
public class SysEventState {

    private String mef;
    private String state;
    private SysEventState subState01;
    private SysEventState subState02;

    public SysEventState() {
    }

    public SysEventState(String state, SysEventState subState01, SysEventState subState02) {
        this.state = state;
        this.subState01 = subState01;
        this.subState02 = subState02;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public SysEventState getSubState01() {
        return subState01;
    }

    public void setSubState01(SysEventState subState01) {
        this.subState01 = subState01;
    }

    public SysEventState getSubState02() {
        return subState02;
    }

    public void setSubState02(SysEventState subState02) {
        this.subState02 = subState02;
    }

    public String getMef() {
        return mef;
    }

    public void setMef(String mef) {
        this.mef = mef;
    }
}
