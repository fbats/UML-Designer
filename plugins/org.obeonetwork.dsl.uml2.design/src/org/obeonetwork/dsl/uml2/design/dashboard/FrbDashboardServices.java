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

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.google.common.collect.Lists;

public class FrbDashboardServices {
	/**
	 * A singleton instance to be accessed by other java services.
	 */
	public static final FrbDashboardServices INSTANCE = new FrbDashboardServices();

	/**
	 * Hidden constructor.
	 */
	private FrbDashboardServices() {

	}

	/**
	 * Get all the root uml model elements which define a dashboard.
	 *
	 * @return List of model elements.
	 */
	public List<EObject> getUmlModels() {
		final List<EObject> results = Lists.newArrayList();
		// Get all available dashboards
		final Collection<Session> sessions = SessionManager.INSTANCE.getSessions();
		for (final Session session : sessions) {
			for (final Resource resource : session.getTransactionalEditingDomain().getResourceSet()
					.getResources()) {
				// FIXME ya un pb si il y a plusieurs UML
				if (resource instanceof UMLResource) {
					final EObject root = resource.getContents().get(0);
					results.add(root);
				}
			}
		}
		return results;
	}
}
