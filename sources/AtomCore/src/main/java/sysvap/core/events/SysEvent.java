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

@XStreamAlias("SysEvent")
public class SysEvent {
    public String event;
    public Integer milisecDelay;
    public Integer rateDelay;

    public SysEvent(String event, Integer milisecDelay, Integer rateDelay) {
        this.event = event;
        this.milisecDelay = milisecDelay;
        this.rateDelay = rateDelay;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Integer getMilisecDelay() {
        return milisecDelay;
    }

    public void setMilisecDelay(Integer milisecDelay) {
        this.milisecDelay = milisecDelay;
    }

    public Integer getRateDelay() {
        return rateDelay;
    }

    public void setRateDelay(Integer rateDelay) {
        this.rateDelay = rateDelay;
    }
    
    
}
