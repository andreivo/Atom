/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.core.data;

import sysvap.gui.helper.propertyeditor.combofield.enumtype.SysIComboFieldEnum;

public enum SysTypeState implements SysIComboFieldEnum {

    NONE("None"), INITIAL("Initial"), INITIAL_FINAL("Initial/Final"), FINAL("Final");
    private String value;

    private SysTypeState(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public Object getName(String value) {
        if (value.equals(NONE.getValue())) {
            return NONE;
        } else if (value.equals(INITIAL.getValue())) {
            return INITIAL;
        } else if (value.equals(INITIAL_FINAL.getValue())) {
            return INITIAL_FINAL;
        } else if (value.equals(FINAL.getValue())) {
            return FINAL;
        }
        return null;
    }
}
