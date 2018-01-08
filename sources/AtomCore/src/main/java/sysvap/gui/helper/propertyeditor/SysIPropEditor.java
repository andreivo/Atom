/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor;

import java.util.List;

public interface SysIPropEditor {
    public List<String> getFieldsName();
    public String getActionOnEnter();
    public void setActionOnEnter(String action);    
    public String getActionOnExit();
    public void setActionOnExit(String action);    
    public String getActionOnUnrecognizedEvent();
    public void setActionOnUnrecognizedEvent(String action);
    
    
}
