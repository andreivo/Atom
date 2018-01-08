/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.core.events;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

@XStreamAlias("SysValidationPlan")
public class SysValidationPlan {

    @XStreamAlias("MainMEF-InitialState")
    public SysEventState mainMefInitialState;
    @XStreamAlias("MainMEF-FinalState")
    public SysEventState mainMefFinalState;
    @XStreamAlias("MEFS-InitialState")
    public List<SysEventState> MefInitial;
    @XStreamAlias("MEFS-FinalState")
    public List<SysEventState> MefFinal;
    @XStreamAlias("SysEventPlan")
    public List<SysEvent> plan;

    public SysValidationPlan(SysEventState initialState, SysEventState finalState, List<SysEvent> plan) {
        this.mainMefInitialState = initialState;
        this.mainMefFinalState = finalState;
        this.plan = plan;
    }

    public SysEventState getMainMefInitialState() {
        return mainMefInitialState;
    }

    public void setMainMefInitialState(SysEventState mainMefInitialState) {
        this.mainMefInitialState = mainMefInitialState;
    }

    public SysEventState getMainMefFinalState() {
        return mainMefFinalState;
    }

    public void setMainMefFinalState(SysEventState mainMefFinalState) {
        this.mainMefFinalState = mainMefFinalState;
    }

    public List<SysEventState> getMefInitial() {
        return MefInitial;
    }

    public void setMefInitial(List<SysEventState> MefInitial) {
        this.MefInitial = MefInitial;
    }

    public List<SysEventState> getMefFinal() {
        return MefFinal;
    }

    public void setMefFinal(List<SysEventState> MefFinal) {
        this.MefFinal = MefFinal;
    }

    public List<SysEvent> getPlan() {
        return plan;
    }

    public void setPlan(List<SysEvent> plan) {
        this.plan = plan;
    }
    
    public void SaveToXML(String path) throws FileNotFoundException, IOException {
        FileOutputStream fout = new FileOutputStream(path);

        XStream xStream = new XStream(new DomDriver());
        xStream.autodetectAnnotations(true);
        String xmlFile = xStream.toXML(this);
        fout.write(xmlFile.getBytes());
        fout.close();
    }

    public static SysValidationPlan LoadFromXML(String file) throws FileNotFoundException, IOException, ClassNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder xmlFile = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            xmlFile.append(line);
        }
        reader.close();

        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(SysValidationPlan.class);
        SysValidationPlan result = (SysValidationPlan) xStream.fromXML(xmlFile.toString());
        return result;
    }
}
