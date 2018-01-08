/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.treeview;

import java.util.List;
import sysvap.gui.core.data.SysGUIMEF;
import sysvap.gui.core.data.SysGUIProject;
import sysvap.gui.core.data.SysGUIState;
import sysvap.core.data.SysMEF;

public class SysProjectTreeModel extends SysAbstractTreeModel {

    // Raiz da nossa Ã¡rvore, vamos exibir uma lista de livros.
    private List<SysMEF> mefs;
    private String fakeRoot = SysGUIProject.getInstance().toString();

    public SysProjectTreeModel(List<SysMEF> mefs) {
        this.mefs = mefs;
    }

    public Object getRoot() {
        return SysGUIProject.getInstance().toString();
    }

//      Com esse método, o Java quem é o objeto que está num determinado índice 
//      do pai. Cada nó de uma árvore pode ser encarado como uma lista. Sendo o 
//      pai a lista e o índice um dos filhos. 
//       
//      @param parent 
//                 É o pai, que tem os filhos. No caso do Projeto as MEFs. 
//      @param index 
//                 Índice do filho. No caso do MEFs, o índice corresponde aos 
//                 Estados e transições. 
    public Object getChild(Object parent, int index) {
        if (fakeRoot == parent) {
            return mefs.get(index); // Pegamos da lista de MEFs  
        }

        if (parent instanceof SysGUIMEF) // O pai é uma MEF?  
        {
            // Devolvemos um Estado  
            return ((SysGUIMEF) parent).getSysStates().get(index);
        }

        return null;

        // Se o pai não é nenhum desses. Melhor dar erro.  
//        throw new IllegalArgumentException("Invalid parent class"
//                + parent.getClass().getSimpleName());
    }

//      Retornamos quantos filhos um pai tem. No caso de uma MEF, é a contagem 
//      de Estados. No caso da lista de Projetos, é a quantidade de MEF.      
    public int getChildCount(Object parent) {
        // Mesma lógica.
        if (fakeRoot == parent) {
            return SysGUIProject.getInstance().getMEFs().size();
        }

        if (parent instanceof SysGUIMEF) // O pai é uma MEF?  
        {
            // Devolvemos um Estado  
            return ((SysGUIMEF) parent).getSysStates().size();
        }

        return 0;

        // Se o pai não é nenhum desses. Melhor dar erro.  
        //throw new IllegalArgumentException("Invalid parent class"
        //        + parent.getClass().getSimpleName());
    }

//    Indicamos se um nó é ou não uma folha. Isso é, se ele não tem filhos. No 
//    nosso caso, os estados são as folhas da árvore. 
    public boolean isLeaf(Object node) {
        return node instanceof SysGUIState;
    }

//    Dado um pai, indicamos qual é o indice do filho correspondente.
    public int getIndexOfChild(Object parent, Object child) {
        if (fakeRoot == parent) {
            return SysGUIProject.getInstance().getMEFs().indexOf(child);
        }

        if (parent instanceof SysGUIMEF) {
            return ((SysGUIMEF) parent).getSysStates().indexOf(child);
        }

        return 0;
    }

    public void addChildMEF(SysGUIMEF mef) {
        fireLastPathComponentInserted(fakeRoot, mef);
    }

    public void delChildMEF(SysGUIMEF mef) {
        fireLastPathComponentRemoved(fakeRoot, mef);
    }

    public void addChildState(SysGUIMEF mef, SysGUIState state) {
        fireLastPathComponentInserted(fakeRoot, mef, state);
    }

    public void delChildState(SysGUIMEF mef, SysGUIState state) {
        fireLastPathComponentRemoved(fakeRoot, mef, state);
    }


}
