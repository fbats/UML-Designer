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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.obeonetwork.dsl.uml2.design.dashboard.DashboardServices;

/**
 * Tester to check viewpoints enablement.
 *
 * @author Melanie Bats <a href="mailto:melanie.bats@obeo.fr">melanie.bats@obeo.fr</a>
 */
public class UMLViewpointEnablementPropertyTester extends PropertyTester {


	private final List<String> umlDesignerViewpoints = new ArrayList<String>(
			Arrays.asList(DashboardServices.INSTANCE.VP_CAPTURE, DashboardServices.INSTANCE.VP_DESIGN,
					DashboardServices.INSTANCE.VP_REVIEW, DashboardServices.INSTANCE.VP_EXTEND,
					DashboardServices.INSTANCE.VP_REUSED));

	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		final Collection<Session> sessions = SessionManager.INSTANCE.getSessions();
		for (final Session session : sessions) {
			// Check if an uml designer viewpoint is active for the current
			// session
			if (session != null) {
				for (final String vp : umlDesignerViewpoints) {
					DashboardServices.INSTANCE.isEnabledVP(session, vp);
				}
			}
		}
		return false;
	}
}
