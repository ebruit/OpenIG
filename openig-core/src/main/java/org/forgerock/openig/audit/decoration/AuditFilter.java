/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.openig.audit.decoration;

import java.io.IOException;
import java.util.Set;

import org.forgerock.openig.audit.AuditSource;
import org.forgerock.openig.audit.AuditSystem;
import org.forgerock.openig.filter.Filter;
import org.forgerock.openig.handler.Handler;
import org.forgerock.openig.handler.HandlerException;
import org.forgerock.openig.http.Exchange;

/**
 * Intercept execution flow and send audit notifications with relevant tags.
 */
class AuditFilter extends AuditBaseObject implements Filter {
    private final Filter delegate;


    public AuditFilter(final AuditSystem auditSystem,
                       final AuditSource source,
                       final Filter delegate,
                       final Set<String> additionalTags) {
        super(source, auditSystem, additionalTags);
        this.delegate = delegate;

    }

    @Override
    public void filter(final Exchange exchange, final Handler next) throws HandlerException, IOException {
        try {
            fireAuditEvent(exchange, requestTags);
            delegate.filter(exchange, next);
            fireAuditEvent(exchange, completedResponseTags);
        } catch (HandlerException e) {
            fireAuditEvent(exchange, failedResponseTags);
            throw e;
        } catch (IOException e) {
            fireAuditEvent(exchange, failedResponseTags);
            throw e;
        }
    }
}