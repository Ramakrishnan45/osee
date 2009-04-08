/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet.update;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Jeff C. Phillips
 */
public class UpdateLabelProvider extends LabelProvider implements IStyledLabelProvider {
   private static final String HIGHLIGHT_WRITE_BG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.writeaccess_highlight"; //$NON-NLS-1$
   private static final Styler HIGHLIGHT_STYLE =
         StyledString.createColorRegistryStyler(null, HIGHLIGHT_WRITE_BG_COLOR_NAME);
   private static final String DASH = " - ";
   private final Map<Image, Image> disabledMap;

   public UpdateLabelProvider() {
      super();

      this.disabledMap = new HashMap<Image, Image>();
   }

   @Override
   public Image getImage(Object element) {
      Image imageToReturn = null;

      if (element instanceof TransferObject) {
         TransferObject transferObject = (TransferObject) element;
         Image artImage = transferObject.getArtifact().getImage();

         if (transferObject.getStatus().equals(TransferStatus.ERROR)) {
            imageToReturn = disabledMap.get(artImage);

            if (imageToReturn == null) {
               imageToReturn = new Image(artImage.getDevice(), artImage, SWT.IMAGE_DISABLE);
               disabledMap.put(artImage, imageToReturn);
            }
         } else {
            imageToReturn = artImage;
         }
      }
      return imageToReturn;
   }

   @Override
   public StyledString getStyledText(Object element) {
      StyledString styledString = new StyledString();

      if (element instanceof TransferObject) {
         TransferObject transferObject = (TransferObject) element;

         if (transferObject.getStatus().equals(TransferStatus.ERROR)) {
            styledString.append(transferObject.getArtifact().getDescriptiveName(), StyledString.DECORATIONS_STYLER);
            styledString.append(DASH);
            styledString.append(transferObject.getStatus().getMessage(), HIGHLIGHT_STYLE);
         } else {
            styledString.append(transferObject.getArtifact().getDescriptiveName());
            styledString.append(DASH);
            styledString.append(transferObject.getStatus().getMessage(), StyledString.COUNTER_STYLER);
         }
      }
      return styledString;
   }

}
