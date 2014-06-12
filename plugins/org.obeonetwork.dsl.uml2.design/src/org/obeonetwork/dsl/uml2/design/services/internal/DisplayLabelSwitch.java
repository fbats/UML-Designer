/*******************************************************************************
 * Copyright (c) 2009, 2011 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.uml2.design.services.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.ActivityPartition;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.CallOperationAction;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.DataStoreNode;
import org.eclipse.uml2.uml.Duration;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.ElementImport;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Expression;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.FunctionBehavior;
import org.eclipse.uml2.uml.InstanceSpecification;
import org.eclipse.uml2.uml.InstanceValue;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Interval;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.LiteralBoolean;
import org.eclipse.uml2.uml.LiteralInteger;
import org.eclipse.uml2.uml.LiteralNull;
import org.eclipse.uml2.uml.LiteralReal;
import org.eclipse.uml2.uml.LiteralString;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MultiplicityElement;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.OpaqueAction;
import org.eclipse.uml2.uml.OpaqueBehavior;
import org.eclipse.uml2.uml.OpaqueExpression;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.ParameterableElement;
import org.eclipse.uml2.uml.Pin;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.ProtocolStateMachine;
import org.eclipse.uml2.uml.Slot;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.StructuralFeature;
import org.eclipse.uml2.uml.TemplateBinding;
import org.eclipse.uml2.uml.TemplateParameterSubstitution;
import org.eclipse.uml2.uml.TimeExpression;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.util.UMLSwitch;
import org.obeonetwork.dsl.uml2.design.services.LabelServices;

/**
 * A switch that handle the label computation for each UML types.
 * 
 * @author Gonzague Reydet <a href="mailto:gonzague.reydet@obeo.fr">gonzague.reydet@obeo.fr</a>
 * @author Melanie Bats <a href="mailto:melanie.bats@obeo.fr">melanie.bats@obeo.fr</a>
 */
public class DisplayLabelSwitch extends UMLSwitch<String> implements ILabelConstants {

	/**
	 * Spaced column constant.
	 */
	private static final String SPACED_COLUMN = " : ";

	/**
	 * Spaced column constant.
	 */
	private static final String SPACED_COMMA = ", ";

	/**
	 * Closing brace constant.
	 */
	private static final String CLOSING_BRACE = "]";

	/**
	 * Opening brace constant.
	 */
	private static final String OPENING_BRACE = "[";

	/**
	 * Label services.
	 */
	protected LabelServices labelServices = new LabelServices();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseActivityEdge(ActivityEdge object) {
		final ValueSpecification value = object.getGuard();

		if (value instanceof OpaqueExpression) {
			final String expr = ((OpaqueExpression)value).getBodies().get(0);
			if (expr != null && !"".equalsIgnoreCase(expr) && !"true".equalsIgnoreCase(expr)
					&& !"1".equalsIgnoreCase(expr)) {
				return OPENING_BRACE + expr + CLOSING_BRACE;
			}
		}

		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseTransition(Transition object) {
		// {trigger ','}* [guard] /behavior_spec
		// Triggers
		String triggersLabel = null;
		if (object.getTriggers() != null && object.getTriggers().size() > 0) {
			triggersLabel += "{";
			for (Trigger trigger : object.getTriggers()) {
				if (triggersLabel != null) {
					triggersLabel += ",";
				}
				triggersLabel = labelServices.computeUmlLabel(trigger);
			}
			triggersLabel += "}";
		}
		// Guard
		String guardLabel = null;
		final Constraint constraint = object.getGuard();
		if (constraint != null) {
			final ValueSpecification specification = constraint.getSpecification();

			if (specification != null) {
				String specificationLabel = labelServices.computeUmlLabel(specification);
				if (specificationLabel != null && specificationLabel.length() > 0) {
					guardLabel = OPENING_BRACE + specificationLabel + CLOSING_BRACE;
				}
			}
		}

		// Behavior spec
		String effectLabel = null;
		Behavior effect = object.getEffect();
		if (effect != null) {
			String behaviorLabel = labelServices.computeUmlLabel(effect);
			if (behaviorLabel != null) {
				effectLabel = "/" + behaviorLabel;
			}
		}

		StringBuffer transitionLabel = new StringBuffer();
		if (triggersLabel != null) {
			transitionLabel.append(triggersLabel);
		}
		if (guardLabel != null) {
			transitionLabel.append(guardLabel);
		}
		if (effectLabel != null) {
			transitionLabel.append(effectLabel);
		}

		return transitionLabel.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseActivity(Activity object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseStateMachine(StateMachine object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseProtocolStateMachine(ProtocolStateMachine object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseOpaqueBehavior(OpaqueBehavior object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseFunctionBehavior(FunctionBehavior object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseInteraction(Interaction object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseDuration(Duration object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseExpression(Expression object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseInstanceValue(InstanceValue object) {
		return labelServices.computeUmlLabel(object.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseInterval(Interval object) {
		String minLabel = labelServices.computeUmlLabel(object.getMin());
		String maxLabel = labelServices.computeUmlLabel(object.getMax());
		if (minLabel != null && minLabel.length() > 0 && maxLabel != null && maxLabel.length() > 0) {
			return OPENING_BRACE + minLabel + " " + maxLabel + CLOSING_BRACE;
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLiteralBoolean(LiteralBoolean object) {
		return object.stringValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLiteralInteger(LiteralInteger object) {
		return object.stringValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLiteralNull(LiteralNull object) {
		return "null";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLiteralReal(LiteralReal object) {
		return object.stringValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLiteralString(LiteralString object) {
		return object.stringValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLiteralUnlimitedNatural(LiteralUnlimitedNatural object) {
		return object.stringValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseOpaqueExpression(OpaqueExpression object) {
		final String expr = object.getBodies().get(0);
		if (expr != null && !"".equalsIgnoreCase(expr) && !"true".equalsIgnoreCase(expr)
				&& !"1".equalsIgnoreCase(expr)) {
			return expr;
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseTimeExpression(TimeExpression object) {
		return object.stringValue();
	}

	/**
	 * Compute the {@link Stereotype} label part for the given {@link Element}.
	 * 
	 * @param element
	 *            the context element.
	 * @return the {@link Stereotype} label.
	 */
	public static String computeStereotypes(Element element) {
		final Iterator<Stereotype> it = element.getAppliedStereotypes().iterator();

		if (!it.hasNext()) {
			return "";
		}

		final StringBuffer stereotypeLabel = new StringBuffer();
		stereotypeLabel.append(OPEN_QUOTE_MARK);
		for (;;) {
			final Stereotype appliedStereotype = it.next();

			stereotypeLabel.append(appliedStereotype.getName());
			if (it.hasNext()) {
				stereotypeLabel.append(", ");
			} else {
				break;
			}
		}
		stereotypeLabel.append(CLOSE_QUOTE_MARK);
		if (element instanceof Feature) {
			stereotypeLabel.append(" ");
		} else {
			stereotypeLabel.append(NL);
		}

		return stereotypeLabel.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseProperty(Property object) {
		StringBuilder label = new StringBuilder();
		if (object.isDerived()) {
			label.append("/");
		}
		label.append(caseStructuralFeature(object));
		if (object.getDefault() != null && !"".equals(object.getDefault().trim())) {
			// is the label on multiple lines ?
			if (object.getDefault().contains(NL)) {
				label.append(" = ...");
			} else {
				label.append(" = " + object.getDefault());
			}
		} else if (object.getDefaultValue() instanceof InstanceValue) {
			label.append(" = " + ((InstanceValue)object.getDefaultValue()).getName());
		}

		final StringBuilder propertyModifier = new StringBuilder();
		if (object.getRedefinedElements() != null && object.getRedefinedElements().size() > 0
				&& object.getRedefinedElements().get(0) != null) {
			propertyModifier.append("redefines " + object.getRedefinedElements().get(0).getName());
		}
		if (object.getRedefinedElements() != null && object.getSubsettedProperties().size() > 0
				&& object.getRedefinedElements().get(0) != null) {
			propertyModifier.append("subsets " + object.getSubsettedProperties().get(0).getName());
		}
		if (object.isID()) {
			if (propertyModifier.length() > 0) {
				propertyModifier.append(", ");
			}
			propertyModifier.append("id");
		}
		if (object.isReadOnly()) {
			if (propertyModifier.length() > 0) {
				propertyModifier.append(", ");
			}
			propertyModifier.append("readOnly");
		}
		// Ordered applies on multivalued multiplicity only
		if (object.getUpper() != 1 && object.isOrdered()) {
			if (propertyModifier.length() > 0) {
				propertyModifier.append(", ");
			}
			propertyModifier.append("ordered");
		}
		// Unique applies on multivalued multiplicity only
		if (object.getUpper() != 1 && object.isUnique()) {
			if (propertyModifier.length() > 0) {
				propertyModifier.append(", ");
			}
			propertyModifier.append("unique");
		}
		if (!object.isUnique() && !object.isOrdered()) {
			if (propertyModifier.length() > 0) {
				propertyModifier.append(", ");
			}
			propertyModifier.append("seq");
		}
		if (propertyModifier.length() > 0) {
			label.append("{" + propertyModifier + "}");
		}

		return label.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseOperation(Operation object) {
		final StringBuilder label = new StringBuilder(caseNamedElement(object));
		label.append("(");

		boolean first = true;
		for (Parameter parameter : object.getOwnedParameters()) {
			if (!ParameterDirectionKind.RETURN_LITERAL.equals(parameter.getDirection())) {
				if (!first) {
					label.append(", ");
				} else {
					first = false;
				}
				label.append(caseTypedElement(parameter));
			}
		}
		label.append(")");
		if (object.getType() != null) {
			label.append(SPACED_COLUMN + object.getType().getName());
			label.append(getMultiplicity(object.getLower(), object.getUpper()));
		}
		final StringBuilder operProperties = new StringBuilder();
		if (object.getRedefinedElements() != null && object.getRedefinedElements().size() > 0
				&& object.getRedefinedElements().get(0) != null) {
			operProperties.append("redefines " + object.getRedefinedElements().get(0).getName());
		}
		if (object.isQuery()) {
			if (operProperties.length() > 0) {
				operProperties.append(", ");
			}
			operProperties.append("query");
		}
		if (object.isOrdered()) {
			if (operProperties.length() > 0) {
				operProperties.append(", ");
			}
			operProperties.append("ordered");
		}
		if (!object.isUnique()) {
			if (operProperties.length() > 0) {
				operProperties.append(", ");
			}
			operProperties.append("nonunique");
		}
		if (operProperties.length() > 0) {
			label.append("{" + operProperties + "}");
		}
		return label.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseStructuralFeature(StructuralFeature object) {
		return caseTypedElement(object) + " " + caseMultiplicityElement(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseMultiplicityElement(MultiplicityElement object) {
		return getMultiplicity(object.getLower(), object.getUpper());
	}

	/**
	 * Get multiplicity.
	 * 
	 * @param lower
	 *            Lower bound
	 * @param upper
	 *            Upper bound
	 * @return Multiplicity
	 */
	private String getMultiplicity(int lower, int upper) {
		StringBuffer label = new StringBuffer();
		if (lower == upper) {
			// [1..1]
			label.append("[" + lower + "]");
		} else if (lower == 0 && upper == -1) {
			// [0..*]
			label.append("[*]");
		} else {
			label.append(OPENING_BRACE + lower + "..");
			if (upper == -1) {
				label.append("*]");
			} else {
				label.append(upper + CLOSING_BRACE);
			}
		}
		return label.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseTypedElement(TypedElement object) {
		if (object.getType() != null) {
			return caseNamedElement(object) + SPACED_COLUMN + object.getType().getName();
		} else {
			return caseNamedElement(object) + SPACED_COLUMN;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseTemplateBinding(TemplateBinding object) {
		List<TemplateParameterSubstitution> parameterSubstitutions = object.getParameterSubstitutions();
		StringBuffer binding = new StringBuffer();
		boolean first = true;
		for (TemplateParameterSubstitution parameterSubstitution : parameterSubstitutions) {
			if (first) {
				first = false;
			} else {
				binding.append(", ");
			}
			ParameterableElement formal = parameterSubstitution.getFormal().getDefault();
			if (formal instanceof NamedElement) {
				binding.append(((NamedElement)formal).getName() + "->");
				ParameterableElement actual = parameterSubstitution.getActual();
				if (actual != null && actual instanceof NamedElement) {
					binding.append(((NamedElement)actual).getName());
				} else {
					binding.append("?");
				}
			}
		}
		return binding.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseActivityPartition(ActivityPartition object) {
		if (object.getRepresents() instanceof NamedElement) {
			return caseNamedElement(object) + SPACED_COLUMN
					+ ((NamedElement)object.getRepresents()).getName();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String casePin(Pin object) {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(caseTypedElement(object));
		buffer.append(caseMultiplicityElement(object));
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseDataStoreNode(DataStoreNode object) {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(OPEN_QUOTE_MARK);
		buffer.append("Datastore");
		buffer.append(CLOSE_QUOTE_MARK);
		buffer.append(NL);
		buffer.append(caseNamedElement(object));
		return buffer.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseCallOperationAction(CallOperationAction object) {
		if (object.getOperation() != null) {
			String callOperationName = caseNamedElement(object);
			String operationName = object.getOperation().getName();
			if (callOperationName != null && callOperationName.equals(operationName)) {
				return callOperationName;
			}
			return caseNamedElement(object) + SPACED_COLUMN + object.getOperation().getName();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseOpaqueAction(OpaqueAction object) {
		final Iterator<String> it = object.getBodies().iterator();

		if (it.hasNext()) {
			final StringBuffer buffer = new StringBuffer();
			buffer.append(caseNamedElement(object));
			buffer.append(NL);

			while (it.hasNext()) {
				buffer.append(NL);
				buffer.append(it.next());
			}

			return buffer.toString();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseNamedElement(NamedElement object) {
		return computeStereotypes(object) + object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseClass(Class object) {
		String templateParameters = labelServices.getTemplatedParameters(object);
		if (templateParameters != null) {
			return computeStereotypes(object) + object.getName() + templateParameters;
		}

		return computeStereotypes(object) + object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseElementImport(ElementImport object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseAssociation(Association object) {
		return object.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseLifeline(Lifeline lifeline) {
		final StringBuilder label = new StringBuilder();
		if (lifeline.getRepresents() != null && isDependencyDescribed(lifeline)) {
			for (NamedElement context : lifeline.getClientDependencies().get(0).getSuppliers()) {
				label.append(context.getLabel());
				label.append(SPACED_COLUMN.trim());
				label.append(SPACED_COLUMN.trim());
			}
			label.append(doSwitch(lifeline.getRepresents()));
		} else if (lifeline.getRepresents() == null && isDependencyDescribed(lifeline)) {
			for (NamedElement context : lifeline.getClientDependencies().get(0).getSuppliers()) {
				label.append(doSwitch(context));
			}
		} else if (lifeline.getRepresents() instanceof Property) {
			// label.append(SPACED_COLUMN);
			label.append(caseProperty((Property)lifeline.getRepresents()));
		} else {
			label.append(caseNamedElement(lifeline));
		}
		return label.toString();
	}

	/**
	 * Test if a context dependency is added to the lifeline.
	 * 
	 * @param lifeline
	 *            the lifeline
	 * @return true if any
	 */
	private boolean isDependencyDescribed(Lifeline lifeline) {
		return lifeline.getClientDependencies() != null && lifeline.getClientDependencies().size() > 0
				&& lifeline.getClientDependencies().get(0) != null
				&& lifeline.getClientDependencies().get(0).getSuppliers().size() > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseExecutionSpecification(ExecutionSpecification execution) {
		if (execution instanceof BehaviorExecutionSpecification) {
			if (((BehaviorExecutionSpecification)execution).getBehavior() != null
					&& ((BehaviorExecutionSpecification)execution).getBehavior().getSpecification() != null)
				return caseOperation((Operation)((BehaviorExecutionSpecification)execution).getBehavior()
						.getSpecification());
		}
		return execution.getLabel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseMessage(Message message) {
		return message.getName();
	}

	/**
	 * Compute label for association end.
	 * 
	 * @param p
	 *            the {@link Association}'s {@link Property} end.
	 * @return the label of the association end.
	 */
	public String getAssociationEndLabel(Property p) {
		final StringBuilder sb = new StringBuilder("");
		if (p.isDerived()) {
			sb.append("/");
		}
		sb.append(p.getName());
		sb.append(caseMultiplicityElement(p));
		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String caseInstanceSpecification(InstanceSpecification object) {
		final StringBuilder label = new StringBuilder(caseNamedElement(object));

		if (object.getClassifiers() != null && object.getClassifiers().size() > 0) {
			label.append(SPACED_COLUMN);
			final Iterator<Classifier> it = object.getClassifiers().iterator();
			while (it.hasNext()) {
				final Classifier classifier = (Classifier)it.next();
				label.append(doSwitch(classifier).replace("\n", " "));
				if (it.hasNext())
					label.append(SPACED_COMMA);
			}
		}
		return label.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String caseSlot(Slot object) {

		final StringBuilder label = new StringBuilder(object.getDefiningFeature().getName());
		label.append(" = ");
		List<ValueSpecification> values = object.getValues();
		boolean first = true;
		for (ValueSpecification valueSpecification : values) {
			if (first) {
				first = false;
			} else {
				label.append(SPACED_COMMA);
			}

			if (valueSpecification instanceof InstanceValue) {
				InstanceValue anInstanceValue = (InstanceValue)valueSpecification;
				label.append(anInstanceValue.getInstance().getName());
			} else if (valueSpecification instanceof LiteralString) {
				LiteralString aLiteralString = (LiteralString)valueSpecification;
				label.append(aLiteralString.getValue());
			} else if (valueSpecification instanceof LiteralInteger) {
				LiteralInteger aLiteralInteger = (LiteralInteger)valueSpecification;
				label.append(aLiteralInteger.getValue());
			} else if (valueSpecification instanceof LiteralBoolean) {
				LiteralBoolean aLiteralBoolean = (LiteralBoolean)valueSpecification;
				label.append(aLiteralBoolean.booleanValue());
			} else {
				label.append(valueSpecification.getName());
			}
		}
		return label.toString();
	}

	@Override
	public String caseBehavior(Behavior object) {
		return object.getName();
	}

}
