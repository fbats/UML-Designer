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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.google.common.collect.Lists;

/**
 * A set of services to handle the Dashboard.
 *
 * @author Frederic Bats <a href="mailto:frederic.bats@obeo.fr">frederic.bats@obeo.fr</a>
 */
public class DashboardServices {
	/**
	 * A singleton instance to be accessed by other java services.
	 */
	public static final DashboardServices INSTANCE = new DashboardServices();

	/**
	 * Hidden constructor.
	 */
	private DashboardServices() {

	}

	/**
	 * Get all the root uml model elements which define a dashboard.
	 *
	 * @return List of model elements.
	 */
	public List<EObject> getUmlModels() {
		final List<EObject> results = Lists.newArrayList();
		final Collection<Session> sessions = SessionManager.INSTANCE.getSessions();
		for (final Session session : sessions) {
			final Iterator<Resource> iterator = session.getTransactionalEditingDomain().getResourceSet()
					.getResources().iterator();
			boolean missingSession = true;
			while (iterator.hasNext() && missingSession) {
				final Resource resource = iterator.next();
				if (resource instanceof UMLResource) {
					final EObject root = resource.getContents().get(0);
					results.add(root);
					// only one dashboard by session is needed
					missingSession = false;
				}
			}
		}
		return results;
	}
}
