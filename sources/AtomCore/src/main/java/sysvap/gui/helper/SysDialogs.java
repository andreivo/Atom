/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class SysDialogs {

    public static String getUserString(String title) {
        return JOptionPane.showInputDialog(title);
    }

    public static int getUserConfirmation(String confirm) {
        JDialog.setDefaultLookAndFeelDecorated(true);
        return JOptionPane.showConfirmDialog(null, confirm, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);        
    }
    
    public static void showAlert(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Alert", JOptionPane.INFORMATION_MESSAGE);        
    }
}
