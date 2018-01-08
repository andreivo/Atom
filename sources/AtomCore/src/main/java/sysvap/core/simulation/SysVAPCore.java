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

public class SysVAPCore {

    public static void main(String[] args) {
        try {
            if (args.length == 2) {
                System.out.println("");
                System.out.println("SysVAP 1.0 - 2013");
                System.out.println("----------------------------------------------------------------------------------------");
                SysSimulationComplete simulation = new SysSimulationComplete(args[0], args[1]);
                simulation.init();
            } else {
                System.out.println("");
                System.out.println("    SysVAP 1.0 - 2013");
                System.out.println("    ----------------------------------------------------------------------------------------");
                System.out.println("    System for validation finite automaton and execution plans.");
                System.out.println("");
                System.out.println("    Usage: \n   > java -jar SysVAPCore.jar <validation file (*.evap)> <plane to be validated (*.xml)>");
                System.out.println("");
            }
        } catch (Exception ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }
}
