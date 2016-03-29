/*******************************************************************************
 * Copyright (c) 2014 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.obeonetwork.dsl.uml2.design.internal.testers;

import java.util.Collection;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.viewpoint.description.Viewpoint;

/**
 * Tester to check viewpoints enablement.
 *
 * @author Melanie Bats <a href="mailto:melanie.bats@obeo.fr">melanie.bats@obeo.fr</a>
 */
public class UMLViewpointEnablementPropertyTester extends PropertyTester {
	private final String vpCapture = "Capture"; //$NON-NLS-1$

	private final String vpDesgin = "Desgin"; //$NON-NLS-1$

	private final String vpReview = "Review"; //$NON-NLS-1$

	private final String vpExtend = "Extend"; //$NON-NLS-1$

	private final String vpReused = "Reused"; //$NON-NLS-1$

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		final Collection<Session> sessions = SessionManager.INSTANCE.getSessions();
		for (final Session session : sessions) {
			// Check if an uml designer viewpoint is active for the current
			// session
			if (session != null) {
				for (final Viewpoint vp : session.getSelectedViewpoints(false)) {
					if (vp.getName().contains(vpCapture) || vp.getName().contains(vpDesgin)
							|| vp.getName().contains(vpReview) || vp.getName().contains(vpExtend)
							|| vp.getName().contains(vpReused)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
