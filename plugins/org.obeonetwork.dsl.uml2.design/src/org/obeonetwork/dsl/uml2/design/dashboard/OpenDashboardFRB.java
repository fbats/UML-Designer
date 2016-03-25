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

import org.eclipse.amalgam.explorer.activity.ui.api.manager.ActivityExplorerManager;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.obeonetwork.dsl.uml2.design.internal.commands.UmlElementConverter;
import org.obeonetwork.dsl.uml2.design.internal.services.LogServices;

public class OpenDashboardFRB extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get for which project we should open the dashboard
		final String paramModel = event
				.getParameter(DashboardContributionItems.OPEN_DASHBOARD_CMD_PARAM_MODEL_KEY);
		final UmlElementConverter converter = new UmlElementConverter();
		try {
			final EObject eObj = (EObject)converter.convertToObject(paramModel);
			final Session session = SessionManager.INSTANCE.getSession(eObj);
			if (session != null) {
				final IEditorPart part = ActivityExplorerManager.INSTANCE.getEditorFromSession(session);
				if (part != null) {
					// Activity explorer already opened
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(part);
				} else {
					ActivityExplorerManager.INSTANCE.openEditor(session);
				}
			}
		} catch (final ParameterValueConversionException e) {
			LogServices.INSTANCE.error("Opening dashboard failed", e); //$NON-NLS-1$
		}

		return null;
	}
}
