/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.commandHandlers.branch;

import java.util.List;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Jeff C. Phillips
 * @author Paul Waldfogel
 *
 */
public class RenameBranchHandler extends CommandHandler{
   private TreeViewer treeViewer;
   
   /* (non-Javadoc)
    * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      ISelectionProvider selectionProvider = AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();
      
      if (selectionProvider instanceof TreeViewer) {
         this.treeViewer = (TreeViewer) selectionProvider;
         Tree tree = treeViewer.getTree();
         IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();

         final Branch selectedBranch = Handlers.getBranchesFromStructuredSelection(selection).iterator().next();
         TreeItem[] myTreeItemsSelected = tree.getSelection();
         
         if (myTreeItemsSelected.length != 1) {
            return null;
         }
         
         TreeEditor myTreeEditor = new TreeEditor(tree);
         myTreeEditor.horizontalAlignment = SWT.LEFT;
         myTreeEditor.grabHorizontal = true;
         myTreeEditor.minimumWidth = 50;

         
         final TreeItem myTreeItem = myTreeItemsSelected[0];
         Control oldEditor = myTreeEditor.getEditor();
         if (oldEditor != null) {
            oldEditor.dispose();
         }
         final Text textBeingRenamed = new Text(tree, SWT.BORDER);
         textBeingRenamed.setText(selectedBranch.getBranchName());
         
         textBeingRenamed.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
               updateText(textBeingRenamed.getText(), selectedBranch);
               textBeingRenamed.dispose();
            }

            @Override
            public void focusGained(FocusEvent e) {
            }
         });
         textBeingRenamed.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
               if ((e.character == SWT.CR)) {
                  updateText(textBeingRenamed.getText(), selectedBranch);
                  textBeingRenamed.dispose();
               } else if (e.keyCode == SWT.ESC) {
                  textBeingRenamed.dispose();
               }
            }
         });
         textBeingRenamed.selectAll();
         textBeingRenamed.setFocus();
         myTreeEditor.setEditor(textBeingRenamed, myTreeItem);
      }
      return null;
   }
   
   private void updateText(String newLabel, Branch selectedBranch) {
      selectedBranch.setBranchName(newLabel);
      try {
         selectedBranch.rename(newLabel);
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      treeViewer.refresh();
   }
   
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.util.CommandHandler#isEnabledWithException()
    */
   @Override
   public boolean isEnabledWithException() throws OseeCoreException {
      IStructuredSelection selection =
         (IStructuredSelection) AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider().getSelection();
      
      List<Branch> branches = Handlers.getBranchesFromStructuredSelection(selection);
      
      return AccessControlManager.isOseeAdmin() && branches.size() == 1;
   }

}
