/*
* generated by Xtext
*/
package org.eclipse.osee.orcs.script.dsl;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class OrcsScriptDslStandaloneSetup extends OrcsScriptDslStandaloneSetupGenerated{

	public static void doSetup() {
		new OrcsScriptDslStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

