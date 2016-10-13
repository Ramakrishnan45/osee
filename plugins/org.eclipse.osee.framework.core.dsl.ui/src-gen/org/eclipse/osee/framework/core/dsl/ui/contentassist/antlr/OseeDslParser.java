/*
 * generated by Xtext
 */
package org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import org.antlr.runtime.RecognitionException;
import org.eclipse.xtext.AbstractElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.AbstractContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.FollowElement;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.AbstractInternalContentAssistParser;

import com.google.inject.Inject;

import org.eclipse.osee.framework.core.dsl.services.OseeDslGrammarAccess;

public class OseeDslParser extends AbstractContentAssistParser {
	
	@Inject
	private OseeDslGrammarAccess grammarAccess;
	
	private Map<AbstractElement, String> nameMappings;
	
	@Override
	protected org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal.InternalOseeDslParser createParser() {
		org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal.InternalOseeDslParser result = new org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal.InternalOseeDslParser(null);
		result.setGrammarAccess(grammarAccess);
		return result;
	}
	
	@Override
	protected String getRuleName(AbstractElement element) {
		if (nameMappings == null) {
			nameMappings = new HashMap<AbstractElement, String>() {
				private static final long serialVersionUID = 1L;
				{
					put(grammarAccess.getOseeDslAccess().getAlternatives_1(), "rule__OseeDsl__Alternatives_1");
					put(grammarAccess.getOseeDslAccess().getAlternatives_2(), "rule__OseeDsl__Alternatives_2");
					put(grammarAccess.getOseeElementAccess().getAlternatives(), "rule__OseeElement__Alternatives");
					put(grammarAccess.getOseeTypeAccess().getAlternatives(), "rule__OseeType__Alternatives");
					put(grammarAccess.getXAttributeTypeAccess().getDataProviderAlternatives_9_0(), "rule__XAttributeType__DataProviderAlternatives_9_0");
					put(grammarAccess.getXAttributeTypeAccess().getMaxAlternatives_13_0(), "rule__XAttributeType__MaxAlternatives_13_0");
					put(grammarAccess.getXAttributeTypeAccess().getTaggerIdAlternatives_14_0_1_0(), "rule__XAttributeType__TaggerIdAlternatives_14_0_1_0");
					put(grammarAccess.getAttributeBaseTypeAccess().getAlternatives(), "rule__AttributeBaseType__Alternatives");
					put(grammarAccess.getOverrideOptionAccess().getAlternatives(), "rule__OverrideOption__Alternatives");
					put(grammarAccess.getAttributeOverrideOptionAccess().getAlternatives(), "rule__AttributeOverrideOption__Alternatives");
					put(grammarAccess.getRelationOrderTypeAccess().getAlternatives(), "rule__RelationOrderType__Alternatives");
					put(grammarAccess.getCONDITION_VALUEAccess().getAlternatives(), "rule__CONDITION_VALUE__Alternatives");
					put(grammarAccess.getConditionAccess().getAlternatives(), "rule__Condition__Alternatives");
					put(grammarAccess.getRoleAccess().getAlternatives_4(), "rule__Role__Alternatives_4");
					put(grammarAccess.getAccessContextAccess().getAlternatives_7(), "rule__AccessContext__Alternatives_7");
					put(grammarAccess.getRelationTypePredicateAccess().getAlternatives(), "rule__RelationTypePredicate__Alternatives");
					put(grammarAccess.getObjectRestrictionAccess().getAlternatives(), "rule__ObjectRestriction__Alternatives");
					put(grammarAccess.getRelationTypeRestrictionAccess().getAlternatives_3(), "rule__RelationTypeRestriction__Alternatives_3");
					put(grammarAccess.getRelationMultiplicityEnumAccess().getAlternatives(), "rule__RelationMultiplicityEnum__Alternatives");
					put(grammarAccess.getCompareOpAccess().getAlternatives(), "rule__CompareOp__Alternatives");
					put(grammarAccess.getXLogicOperatorAccess().getAlternatives(), "rule__XLogicOperator__Alternatives");
					put(grammarAccess.getMatchFieldAccess().getAlternatives(), "rule__MatchField__Alternatives");
					put(grammarAccess.getAccessPermissionEnumAccess().getAlternatives(), "rule__AccessPermissionEnum__Alternatives");
					put(grammarAccess.getXRelationSideEnumAccess().getAlternatives(), "rule__XRelationSideEnum__Alternatives");
					put(grammarAccess.getOseeDslAccess().getGroup(), "rule__OseeDsl__Group__0");
					put(grammarAccess.getImportAccess().getGroup(), "rule__Import__Group__0");
					put(grammarAccess.getQUALIFIED_NAMEAccess().getGroup(), "rule__QUALIFIED_NAME__Group__0");
					put(grammarAccess.getQUALIFIED_NAMEAccess().getGroup_1(), "rule__QUALIFIED_NAME__Group_1__0");
					put(grammarAccess.getXArtifactTypeAccess().getGroup(), "rule__XArtifactType__Group__0");
					put(grammarAccess.getXArtifactTypeAccess().getGroup_3(), "rule__XArtifactType__Group_3__0");
					put(grammarAccess.getXArtifactTypeAccess().getGroup_3_2(), "rule__XArtifactType__Group_3_2__0");
					put(grammarAccess.getXArtifactTypeAccess().getGroup_5(), "rule__XArtifactType__Group_5__0");
					put(grammarAccess.getXAttributeTypeRefAccess().getGroup(), "rule__XAttributeTypeRef__Group__0");
					put(grammarAccess.getXAttributeTypeRefAccess().getGroup_2(), "rule__XAttributeTypeRef__Group_2__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup(), "rule__XAttributeType__Group__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_2(), "rule__XAttributeType__Group_2__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_3(), "rule__XAttributeType__Group_3__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_5(), "rule__XAttributeType__Group_5__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_14_0(), "rule__XAttributeType__Group_14_0__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_14_1(), "rule__XAttributeType__Group_14_1__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_14_2(), "rule__XAttributeType__Group_14_2__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_14_3(), "rule__XAttributeType__Group_14_3__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_14_4(), "rule__XAttributeType__Group_14_4__0");
					put(grammarAccess.getXAttributeTypeAccess().getGroup_14_5(), "rule__XAttributeType__Group_14_5__0");
					put(grammarAccess.getXOseeEnumTypeAccess().getGroup(), "rule__XOseeEnumType__Group__0");
					put(grammarAccess.getXOseeEnumTypeAccess().getGroup_3(), "rule__XOseeEnumType__Group_3__0");
					put(grammarAccess.getXOseeEnumEntryAccess().getGroup(), "rule__XOseeEnumEntry__Group__0");
					put(grammarAccess.getXOseeEnumEntryAccess().getGroup_3(), "rule__XOseeEnumEntry__Group_3__0");
					put(grammarAccess.getXOseeEnumEntryAccess().getGroup_4(), "rule__XOseeEnumEntry__Group_4__0");
					put(grammarAccess.getXOseeEnumOverrideAccess().getGroup(), "rule__XOseeEnumOverride__Group__0");
					put(grammarAccess.getAddEnumAccess().getGroup(), "rule__AddEnum__Group__0");
					put(grammarAccess.getAddEnumAccess().getGroup_3(), "rule__AddEnum__Group_3__0");
					put(grammarAccess.getAddEnumAccess().getGroup_4(), "rule__AddEnum__Group_4__0");
					put(grammarAccess.getRemoveEnumAccess().getGroup(), "rule__RemoveEnum__Group__0");
					put(grammarAccess.getXOseeArtifactTypeOverrideAccess().getGroup(), "rule__XOseeArtifactTypeOverride__Group__0");
					put(grammarAccess.getAddAttributeAccess().getGroup(), "rule__AddAttribute__Group__0");
					put(grammarAccess.getRemoveAttributeAccess().getGroup(), "rule__RemoveAttribute__Group__0");
					put(grammarAccess.getUpdateAttributeAccess().getGroup(), "rule__UpdateAttribute__Group__0");
					put(grammarAccess.getXRelationTypeAccess().getGroup(), "rule__XRelationType__Group__0");
					put(grammarAccess.getXRelationTypeAccess().getGroup_3(), "rule__XRelationType__Group_3__0");
					put(grammarAccess.getSimpleConditionAccess().getGroup(), "rule__SimpleCondition__Group__0");
					put(grammarAccess.getCompoundConditionAccess().getGroup(), "rule__CompoundCondition__Group__0");
					put(grammarAccess.getCompoundConditionAccess().getGroup_2(), "rule__CompoundCondition__Group_2__0");
					put(grammarAccess.getXArtifactMatcherAccess().getGroup(), "rule__XArtifactMatcher__Group__0");
					put(grammarAccess.getXArtifactMatcherAccess().getGroup_4(), "rule__XArtifactMatcher__Group_4__0");
					put(grammarAccess.getRoleAccess().getGroup(), "rule__Role__Group__0");
					put(grammarAccess.getRoleAccess().getGroup_2(), "rule__Role__Group_2__0");
					put(grammarAccess.getReferencedContextAccess().getGroup(), "rule__ReferencedContext__Group__0");
					put(grammarAccess.getUsersAndGroupsAccess().getGroup(), "rule__UsersAndGroups__Group__0");
					put(grammarAccess.getAccessContextAccess().getGroup(), "rule__AccessContext__Group__0");
					put(grammarAccess.getAccessContextAccess().getGroup_2(), "rule__AccessContext__Group_2__0");
					put(grammarAccess.getHierarchyRestrictionAccess().getGroup(), "rule__HierarchyRestriction__Group__0");
					put(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getGroup(), "rule__RelationTypeArtifactTypePredicate__Group__0");
					put(grammarAccess.getRelationTypeArtifactPredicateAccess().getGroup(), "rule__RelationTypeArtifactPredicate__Group__0");
					put(grammarAccess.getArtifactMatchRestrictionAccess().getGroup(), "rule__ArtifactMatchRestriction__Group__0");
					put(grammarAccess.getArtifactTypeRestrictionAccess().getGroup(), "rule__ArtifactTypeRestriction__Group__0");
					put(grammarAccess.getAttributeTypeRestrictionAccess().getGroup(), "rule__AttributeTypeRestriction__Group__0");
					put(grammarAccess.getAttributeTypeRestrictionAccess().getGroup_4(), "rule__AttributeTypeRestriction__Group_4__0");
					put(grammarAccess.getLegacyRelationTypeRestrictionAccess().getGroup(), "rule__LegacyRelationTypeRestriction__Group__0");
					put(grammarAccess.getLegacyRelationTypeRestrictionAccess().getGroup_5(), "rule__LegacyRelationTypeRestriction__Group_5__0");
					put(grammarAccess.getRelationTypeRestrictionAccess().getGroup(), "rule__RelationTypeRestriction__Group__0");
					put(grammarAccess.getOseeDslAccess().getImportsAssignment_0(), "rule__OseeDsl__ImportsAssignment_0");
					put(grammarAccess.getOseeDslAccess().getArtifactTypesAssignment_1_0(), "rule__OseeDsl__ArtifactTypesAssignment_1_0");
					put(grammarAccess.getOseeDslAccess().getRelationTypesAssignment_1_1(), "rule__OseeDsl__RelationTypesAssignment_1_1");
					put(grammarAccess.getOseeDslAccess().getAttributeTypesAssignment_1_2(), "rule__OseeDsl__AttributeTypesAssignment_1_2");
					put(grammarAccess.getOseeDslAccess().getEnumTypesAssignment_1_3(), "rule__OseeDsl__EnumTypesAssignment_1_3");
					put(grammarAccess.getOseeDslAccess().getEnumOverridesAssignment_1_4(), "rule__OseeDsl__EnumOverridesAssignment_1_4");
					put(grammarAccess.getOseeDslAccess().getArtifactTypeOverridesAssignment_1_5(), "rule__OseeDsl__ArtifactTypeOverridesAssignment_1_5");
					put(grammarAccess.getOseeDslAccess().getArtifactMatchRefsAssignment_2_0(), "rule__OseeDsl__ArtifactMatchRefsAssignment_2_0");
					put(grammarAccess.getOseeDslAccess().getAccessDeclarationsAssignment_2_1(), "rule__OseeDsl__AccessDeclarationsAssignment_2_1");
					put(grammarAccess.getOseeDslAccess().getRoleDeclarationsAssignment_2_2(), "rule__OseeDsl__RoleDeclarationsAssignment_2_2");
					put(grammarAccess.getImportAccess().getImportURIAssignment_1(), "rule__Import__ImportURIAssignment_1");
					put(grammarAccess.getXArtifactTypeAccess().getAbstractAssignment_0(), "rule__XArtifactType__AbstractAssignment_0");
					put(grammarAccess.getXArtifactTypeAccess().getNameAssignment_2(), "rule__XArtifactType__NameAssignment_2");
					put(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_1(), "rule__XArtifactType__SuperArtifactTypesAssignment_3_1");
					put(grammarAccess.getXArtifactTypeAccess().getSuperArtifactTypesAssignment_3_2_1(), "rule__XArtifactType__SuperArtifactTypesAssignment_3_2_1");
					put(grammarAccess.getXArtifactTypeAccess().getTypeGuidAssignment_5_1(), "rule__XArtifactType__TypeGuidAssignment_5_1");
					put(grammarAccess.getXArtifactTypeAccess().getIdAssignment_7(), "rule__XArtifactType__IdAssignment_7");
					put(grammarAccess.getXArtifactTypeAccess().getValidAttributeTypesAssignment_8(), "rule__XArtifactType__ValidAttributeTypesAssignment_8");
					put(grammarAccess.getXAttributeTypeRefAccess().getValidAttributeTypeAssignment_1(), "rule__XAttributeTypeRef__ValidAttributeTypeAssignment_1");
					put(grammarAccess.getXAttributeTypeRefAccess().getBranchUuidAssignment_2_1(), "rule__XAttributeTypeRef__BranchUuidAssignment_2_1");
					put(grammarAccess.getXAttributeTypeAccess().getNameAssignment_1(), "rule__XAttributeType__NameAssignment_1");
					put(grammarAccess.getXAttributeTypeAccess().getBaseAttributeTypeAssignment_2_1(), "rule__XAttributeType__BaseAttributeTypeAssignment_2_1");
					put(grammarAccess.getXAttributeTypeAccess().getOverrideAssignment_3_1(), "rule__XAttributeType__OverrideAssignment_3_1");
					put(grammarAccess.getXAttributeTypeAccess().getTypeGuidAssignment_5_1(), "rule__XAttributeType__TypeGuidAssignment_5_1");
					put(grammarAccess.getXAttributeTypeAccess().getIdAssignment_7(), "rule__XAttributeType__IdAssignment_7");
					put(grammarAccess.getXAttributeTypeAccess().getDataProviderAssignment_9(), "rule__XAttributeType__DataProviderAssignment_9");
					put(grammarAccess.getXAttributeTypeAccess().getMinAssignment_11(), "rule__XAttributeType__MinAssignment_11");
					put(grammarAccess.getXAttributeTypeAccess().getMaxAssignment_13(), "rule__XAttributeType__MaxAssignment_13");
					put(grammarAccess.getXAttributeTypeAccess().getTaggerIdAssignment_14_0_1(), "rule__XAttributeType__TaggerIdAssignment_14_0_1");
					put(grammarAccess.getXAttributeTypeAccess().getEnumTypeAssignment_14_1_1(), "rule__XAttributeType__EnumTypeAssignment_14_1_1");
					put(grammarAccess.getXAttributeTypeAccess().getDescriptionAssignment_14_2_1(), "rule__XAttributeType__DescriptionAssignment_14_2_1");
					put(grammarAccess.getXAttributeTypeAccess().getDefaultValueAssignment_14_3_1(), "rule__XAttributeType__DefaultValueAssignment_14_3_1");
					put(grammarAccess.getXAttributeTypeAccess().getFileExtensionAssignment_14_4_1(), "rule__XAttributeType__FileExtensionAssignment_14_4_1");
					put(grammarAccess.getXAttributeTypeAccess().getMediaTypeAssignment_14_5_1(), "rule__XAttributeType__MediaTypeAssignment_14_5_1");
					put(grammarAccess.getXOseeEnumTypeAccess().getNameAssignment_1(), "rule__XOseeEnumType__NameAssignment_1");
					put(grammarAccess.getXOseeEnumTypeAccess().getTypeGuidAssignment_3_1(), "rule__XOseeEnumType__TypeGuidAssignment_3_1");
					put(grammarAccess.getXOseeEnumTypeAccess().getIdAssignment_5(), "rule__XOseeEnumType__IdAssignment_5");
					put(grammarAccess.getXOseeEnumTypeAccess().getEnumEntriesAssignment_6(), "rule__XOseeEnumType__EnumEntriesAssignment_6");
					put(grammarAccess.getXOseeEnumEntryAccess().getNameAssignment_1(), "rule__XOseeEnumEntry__NameAssignment_1");
					put(grammarAccess.getXOseeEnumEntryAccess().getOrdinalAssignment_2(), "rule__XOseeEnumEntry__OrdinalAssignment_2");
					put(grammarAccess.getXOseeEnumEntryAccess().getEntryGuidAssignment_3_1(), "rule__XOseeEnumEntry__EntryGuidAssignment_3_1");
					put(grammarAccess.getXOseeEnumEntryAccess().getDescriptionAssignment_4_1(), "rule__XOseeEnumEntry__DescriptionAssignment_4_1");
					put(grammarAccess.getXOseeEnumOverrideAccess().getOverridenEnumTypeAssignment_1(), "rule__XOseeEnumOverride__OverridenEnumTypeAssignment_1");
					put(grammarAccess.getXOseeEnumOverrideAccess().getInheritAllAssignment_3(), "rule__XOseeEnumOverride__InheritAllAssignment_3");
					put(grammarAccess.getXOseeEnumOverrideAccess().getOverrideOptionsAssignment_4(), "rule__XOseeEnumOverride__OverrideOptionsAssignment_4");
					put(grammarAccess.getAddEnumAccess().getEnumEntryAssignment_1(), "rule__AddEnum__EnumEntryAssignment_1");
					put(grammarAccess.getAddEnumAccess().getOrdinalAssignment_2(), "rule__AddEnum__OrdinalAssignment_2");
					put(grammarAccess.getAddEnumAccess().getEntryGuidAssignment_3_1(), "rule__AddEnum__EntryGuidAssignment_3_1");
					put(grammarAccess.getAddEnumAccess().getDescriptionAssignment_4_1(), "rule__AddEnum__DescriptionAssignment_4_1");
					put(grammarAccess.getRemoveEnumAccess().getEnumEntryAssignment_1(), "rule__RemoveEnum__EnumEntryAssignment_1");
					put(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverridenArtifactTypeAssignment_1(), "rule__XOseeArtifactTypeOverride__OverridenArtifactTypeAssignment_1");
					put(grammarAccess.getXOseeArtifactTypeOverrideAccess().getInheritAllAssignment_3(), "rule__XOseeArtifactTypeOverride__InheritAllAssignment_3");
					put(grammarAccess.getXOseeArtifactTypeOverrideAccess().getOverrideOptionsAssignment_4(), "rule__XOseeArtifactTypeOverride__OverrideOptionsAssignment_4");
					put(grammarAccess.getAddAttributeAccess().getAttributeAssignment_1(), "rule__AddAttribute__AttributeAssignment_1");
					put(grammarAccess.getRemoveAttributeAccess().getAttributeAssignment_2(), "rule__RemoveAttribute__AttributeAssignment_2");
					put(grammarAccess.getUpdateAttributeAccess().getAttributeAssignment_1(), "rule__UpdateAttribute__AttributeAssignment_1");
					put(grammarAccess.getXRelationTypeAccess().getNameAssignment_1(), "rule__XRelationType__NameAssignment_1");
					put(grammarAccess.getXRelationTypeAccess().getTypeGuidAssignment_3_1(), "rule__XRelationType__TypeGuidAssignment_3_1");
					put(grammarAccess.getXRelationTypeAccess().getIdAssignment_5(), "rule__XRelationType__IdAssignment_5");
					put(grammarAccess.getXRelationTypeAccess().getSideANameAssignment_7(), "rule__XRelationType__SideANameAssignment_7");
					put(grammarAccess.getXRelationTypeAccess().getSideAArtifactTypeAssignment_9(), "rule__XRelationType__SideAArtifactTypeAssignment_9");
					put(grammarAccess.getXRelationTypeAccess().getSideBNameAssignment_11(), "rule__XRelationType__SideBNameAssignment_11");
					put(grammarAccess.getXRelationTypeAccess().getSideBArtifactTypeAssignment_13(), "rule__XRelationType__SideBArtifactTypeAssignment_13");
					put(grammarAccess.getXRelationTypeAccess().getDefaultOrderTypeAssignment_15(), "rule__XRelationType__DefaultOrderTypeAssignment_15");
					put(grammarAccess.getXRelationTypeAccess().getMultiplicityAssignment_17(), "rule__XRelationType__MultiplicityAssignment_17");
					put(grammarAccess.getSimpleConditionAccess().getFieldAssignment_0(), "rule__SimpleCondition__FieldAssignment_0");
					put(grammarAccess.getSimpleConditionAccess().getOpAssignment_1(), "rule__SimpleCondition__OpAssignment_1");
					put(grammarAccess.getSimpleConditionAccess().getExpressionAssignment_2(), "rule__SimpleCondition__ExpressionAssignment_2");
					put(grammarAccess.getCompoundConditionAccess().getConditionsAssignment_1(), "rule__CompoundCondition__ConditionsAssignment_1");
					put(grammarAccess.getCompoundConditionAccess().getOperatorsAssignment_2_0(), "rule__CompoundCondition__OperatorsAssignment_2_0");
					put(grammarAccess.getCompoundConditionAccess().getConditionsAssignment_2_1(), "rule__CompoundCondition__ConditionsAssignment_2_1");
					put(grammarAccess.getXArtifactMatcherAccess().getNameAssignment_1(), "rule__XArtifactMatcher__NameAssignment_1");
					put(grammarAccess.getXArtifactMatcherAccess().getConditionsAssignment_3(), "rule__XArtifactMatcher__ConditionsAssignment_3");
					put(grammarAccess.getXArtifactMatcherAccess().getOperatorsAssignment_4_0(), "rule__XArtifactMatcher__OperatorsAssignment_4_0");
					put(grammarAccess.getXArtifactMatcherAccess().getConditionsAssignment_4_1(), "rule__XArtifactMatcher__ConditionsAssignment_4_1");
					put(grammarAccess.getRoleAccess().getNameAssignment_1(), "rule__Role__NameAssignment_1");
					put(grammarAccess.getRoleAccess().getSuperRolesAssignment_2_1(), "rule__Role__SuperRolesAssignment_2_1");
					put(grammarAccess.getRoleAccess().getUsersAndGroupsAssignment_4_0(), "rule__Role__UsersAndGroupsAssignment_4_0");
					put(grammarAccess.getRoleAccess().getReferencedContextsAssignment_4_1(), "rule__Role__ReferencedContextsAssignment_4_1");
					put(grammarAccess.getReferencedContextAccess().getAccessContextRefAssignment_1(), "rule__ReferencedContext__AccessContextRefAssignment_1");
					put(grammarAccess.getUsersAndGroupsAccess().getUserOrGroupGuidAssignment_1(), "rule__UsersAndGroups__UserOrGroupGuidAssignment_1");
					put(grammarAccess.getAccessContextAccess().getNameAssignment_1(), "rule__AccessContext__NameAssignment_1");
					put(grammarAccess.getAccessContextAccess().getSuperAccessContextsAssignment_2_1(), "rule__AccessContext__SuperAccessContextsAssignment_2_1");
					put(grammarAccess.getAccessContextAccess().getGuidAssignment_5(), "rule__AccessContext__GuidAssignment_5");
					put(grammarAccess.getAccessContextAccess().getAccessRulesAssignment_7_0(), "rule__AccessContext__AccessRulesAssignment_7_0");
					put(grammarAccess.getAccessContextAccess().getHierarchyRestrictionsAssignment_7_1(), "rule__AccessContext__HierarchyRestrictionsAssignment_7_1");
					put(grammarAccess.getHierarchyRestrictionAccess().getArtifactMatcherRefAssignment_1(), "rule__HierarchyRestriction__ArtifactMatcherRefAssignment_1");
					put(grammarAccess.getHierarchyRestrictionAccess().getAccessRulesAssignment_3(), "rule__HierarchyRestriction__AccessRulesAssignment_3");
					put(grammarAccess.getRelationTypeArtifactTypePredicateAccess().getArtifactTypeRefAssignment_1(), "rule__RelationTypeArtifactTypePredicate__ArtifactTypeRefAssignment_1");
					put(grammarAccess.getRelationTypeArtifactPredicateAccess().getArtifactMatcherRefAssignment_1(), "rule__RelationTypeArtifactPredicate__ArtifactMatcherRefAssignment_1");
					put(grammarAccess.getArtifactMatchRestrictionAccess().getPermissionAssignment_0(), "rule__ArtifactMatchRestriction__PermissionAssignment_0");
					put(grammarAccess.getArtifactMatchRestrictionAccess().getArtifactMatcherRefAssignment_3(), "rule__ArtifactMatchRestriction__ArtifactMatcherRefAssignment_3");
					put(grammarAccess.getArtifactTypeRestrictionAccess().getPermissionAssignment_0(), "rule__ArtifactTypeRestriction__PermissionAssignment_0");
					put(grammarAccess.getArtifactTypeRestrictionAccess().getArtifactTypeRefAssignment_3(), "rule__ArtifactTypeRestriction__ArtifactTypeRefAssignment_3");
					put(grammarAccess.getAttributeTypeRestrictionAccess().getPermissionAssignment_0(), "rule__AttributeTypeRestriction__PermissionAssignment_0");
					put(grammarAccess.getAttributeTypeRestrictionAccess().getAttributeTypeRefAssignment_3(), "rule__AttributeTypeRestriction__AttributeTypeRefAssignment_3");
					put(grammarAccess.getAttributeTypeRestrictionAccess().getArtifactTypeRefAssignment_4_2(), "rule__AttributeTypeRestriction__ArtifactTypeRefAssignment_4_2");
					put(grammarAccess.getLegacyRelationTypeRestrictionAccess().getPermissionAssignment_0(), "rule__LegacyRelationTypeRestriction__PermissionAssignment_0");
					put(grammarAccess.getLegacyRelationTypeRestrictionAccess().getRelationTypeRefAssignment_3(), "rule__LegacyRelationTypeRestriction__RelationTypeRefAssignment_3");
					put(grammarAccess.getLegacyRelationTypeRestrictionAccess().getRestrictedToSideAssignment_4(), "rule__LegacyRelationTypeRestriction__RestrictedToSideAssignment_4");
					put(grammarAccess.getLegacyRelationTypeRestrictionAccess().getArtifactMatcherRefAssignment_5_1(), "rule__LegacyRelationTypeRestriction__ArtifactMatcherRefAssignment_5_1");
					put(grammarAccess.getRelationTypeRestrictionAccess().getPermissionAssignment_0(), "rule__RelationTypeRestriction__PermissionAssignment_0");
					put(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeMatchAssignment_3_0(), "rule__RelationTypeRestriction__RelationTypeMatchAssignment_3_0");
					put(grammarAccess.getRelationTypeRestrictionAccess().getRelationTypeRefAssignment_3_1(), "rule__RelationTypeRestriction__RelationTypeRefAssignment_3_1");
					put(grammarAccess.getRelationTypeRestrictionAccess().getRestrictedToSideAssignment_4(), "rule__RelationTypeRestriction__RestrictedToSideAssignment_4");
					put(grammarAccess.getRelationTypeRestrictionAccess().getPredicateAssignment_5(), "rule__RelationTypeRestriction__PredicateAssignment_5");
					put(grammarAccess.getXAttributeTypeAccess().getUnorderedGroup_14(), "rule__XAttributeType__UnorderedGroup_14");
				}
			};
		}
		return nameMappings.get(element);
	}
	
	@Override
	protected Collection<FollowElement> getFollowElements(AbstractInternalContentAssistParser parser) {
		try {
			org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal.InternalOseeDslParser typedParser = (org.eclipse.osee.framework.core.dsl.ui.contentassist.antlr.internal.InternalOseeDslParser) parser;
			typedParser.entryRuleOseeDsl();
			return typedParser.getFollowElements();
		} catch(RecognitionException ex) {
			throw new RuntimeException(ex);
		}		
	}
	
	@Override
	protected String[] getInitialHiddenTokens() {
		return new String[] { "RULE_WS", "RULE_ML_COMMENT", "RULE_SL_COMMENT" };
	}
	
	public OseeDslGrammarAccess getGrammarAccess() {
		return this.grammarAccess;
	}
	
	public void setGrammarAccess(OseeDslGrammarAccess grammarAccess) {
		this.grammarAccess = grammarAccess;
	}
}
