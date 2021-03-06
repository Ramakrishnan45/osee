/*
* generated by Xtext
*/
package org.eclipse.osee.ats.dsl.ui.labeling;

import com.google.inject.Inject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

/**
 * Provides labels for a EObjects. see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class AtsDslLabelProvider extends DefaultEObjectLabelProvider {

   @Inject
   public AtsDslLabelProvider(AdapterFactoryLabelProvider delegate) {
      super(delegate);
   }

   /*
    * //Labels and icons can be computed like this: String text(MyModel ele) { return "my "+ele.getName(); } String
    * image(MyModel ele) { return "MyModel.gif"; }
    */
}
