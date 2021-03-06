/**
 */
package org.eclipse.osee.orcs.script.dsl.orcsScriptDsl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc --> A representation of the literals of the enumeration '<em><b>Os Relation Side</b></em>', and
 * utility methods for working with them. <!-- end-user-doc -->
 * 
 * @see org.eclipse.osee.orcs.script.dsl.orcsScriptDsl.OrcsScriptDslPackage#getOsRelationSide()
 * @model
 * @generated
 */
public enum OsRelationSide implements Enumerator {
   /**
    * The '<em><b>SIDE A</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #SIDE_A_VALUE
    * @generated
    * @ordered
    */
   SIDE_A(0, "SIDE_A", "side-A"),

   /**
    * The '<em><b>SIDE B</b></em>' literal object. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @see #SIDE_B_VALUE
    * @generated
    * @ordered
    */
   SIDE_B(1, "SIDE_B", "side-B");

   /**
    * The '<em><b>SIDE A</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>SIDE A</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #SIDE_A
    * @model literal="side-A"
    * @generated
    * @ordered
    */
   public static final int SIDE_A_VALUE = 0;

   /**
    * The '<em><b>SIDE B</b></em>' literal value. <!-- begin-user-doc -->
    * <p>
    * If the meaning of '<em><b>SIDE B</b></em>' literal object isn't clear, there really should be more of a
    * description here...
    * </p>
    * <!-- end-user-doc -->
    * 
    * @see #SIDE_B
    * @model literal="side-B"
    * @generated
    * @ordered
    */
   public static final int SIDE_B_VALUE = 1;

   /**
    * An array of all the '<em><b>Os Relation Side</b></em>' enumerators. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private static final OsRelationSide[] VALUES_ARRAY = new OsRelationSide[] {SIDE_A, SIDE_B,};

   /**
    * A public read-only list of all the '<em><b>Os Relation Side</b></em>' enumerators. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static final List<OsRelationSide> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

   /**
    * Returns the '<em><b>Os Relation Side</b></em>' literal with the specified literal value. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsRelationSide get(String literal) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsRelationSide result = VALUES_ARRAY[i];
         if (result.toString().equals(literal)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Relation Side</b></em>' literal with the specified name. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   public static OsRelationSide getByName(String name) {
      for (int i = 0; i < VALUES_ARRAY.length; ++i) {
         OsRelationSide result = VALUES_ARRAY[i];
         if (result.getName().equals(name)) {
            return result;
         }
      }
      return null;
   }

   /**
    * Returns the '<em><b>Os Relation Side</b></em>' literal with the specified integer value. <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * 
    * @generated
    */
   public static OsRelationSide get(int value) {
      switch (value) {
         case SIDE_A_VALUE:
            return SIDE_A;
         case SIDE_B_VALUE:
            return SIDE_B;
      }
      return null;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private final int value;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private final String name;

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private final String literal;

   /**
    * Only this class can construct instances. <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   private OsRelationSide(int value, String name, String literal) {
      this.value = value;
      this.name = name;
      this.literal = literal;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public int getValue() {
      return value;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * <!-- begin-user-doc --> <!-- end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String getLiteral() {
      return literal;
   }

   /**
    * Returns the literal value of the enumerator, which is its string representation. <!-- begin-user-doc --> <!--
    * end-user-doc -->
    * 
    * @generated
    */
   @Override
   public String toString() {
      return literal;
   }

} //OsRelationSide
