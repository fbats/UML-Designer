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
import org.eclipse.sirius.viewpoint.description.Viewpoint;
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
	 * UMl-Designer Capture viewpoint name.
	 */
	public final String VP_CAPTURE = "Capture"; //$NON-NLS-1$

	/**
	 * UMl-Designer Design viewpoint name.
	 */
	public final String VP_DESIGN = "Design"; //$NON-NLS-1$

	/**
	 * UMl-Designer Extend viewpoint name.
	 */
	public final String VP_EXTEND = "Extend"; //$NON-NLS-1$

	/**
	 * UMl-Designer Reused viewpoint name.
	 */
	public final String VP_REUSED = "Reused"; //$NON-NLS-1$

	/**
	 * UMl-Designer Review viewpoint name.
	 */
	public final String VP_REVIEW = "Review"; //$NON-NLS-1$

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

	/**
	 * Check if viewpoint is enabled for the session.
	 *
	 * @param session
	 *            sirius session
	 * @param vpName
	 *            name of viewpoint
	 * @return true if enable
	 */
	public boolean isEnabledVP(Session session, String vpName) {
		for (final Viewpoint vp : session.getSelectedViewpoints(false)) {
			if (vp.getName().contains(vpName)) {
				return true;
			}
		}
		return false;
	}
}
