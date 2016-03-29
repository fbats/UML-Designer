/*******************************************************************************
 * Copyright (c) 2015 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.uml2.design.dashboard;

import org.eclipse.amalgam.explorer.activity.ui.api.hyperlinkadapter.NewDiagramHyperLinkAdapter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.obeonetwork.dsl.uml2.design.internal.services.ElementServices;

public class CreateSequenceDiagram extends NewDiagramHyperLinkAdapter {
	private static final String SEQUENCE_DIAGRAM = "Sequence Diagram"; //$NON-NLS-1$

	@Override
	protected boolean createDiagram(EObject project, Session session) {
		if (!(project instanceof Package)) {
			return false;
		}

		final Interaction interaction = createInteraction(project);
		final TransactionalEditingDomain domain = session.getTransactionalEditingDomain();
		final CommandStack commandStack = domain.getCommandStack();

		final Command command = AddCommand.create(domain, project, null, interaction);
		commandStack.execute(command);
		return super.createDiagram(interaction, session);
	}

	/**
	 * Create interaction a new interaction in package.
	 *
	 * @param pkg
	 *            Package containing new interaction.
	 * @return Interaction
	 */
	public Interaction createInteraction(EObject pkg) {
		final UMLFactory factory = UMLFactory.eINSTANCE;
		final Interaction interaction = factory.createInteraction();
		interaction.setName(ElementServices.INSTANCE.getNewInteractionName(pkg));
		return interaction;
	}

	@Override
	public String getRepresentationName() {
		return SEQUENCE_DIAGRAM;
	}
}
