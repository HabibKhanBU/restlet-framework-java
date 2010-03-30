/*
 * Copyright 2005-2008 Noelios Consulting.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at http://www.opensource.org/licenses/cddl1.txt If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */
package org.restlet.example.jaxrs;

import java.util.Set;

import javax.ws.rs.core.ApplicationConfig;

import org.restlet.ext.jaxrs.util.Util;

/**
 * @author Stephan
 * 
 */
public class ExampleAppConfig extends ApplicationConfig {

    /**
     * creates a new Application configuration for this example.
     */
    public ExampleAppConfig() {
    }

    /**
     * @see javax.ws.rs.core.ApplicationConfig#getResourceClasses()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<Class<?>> getResourceClasses() {
        return Util.createSet(EasyRootResource.class,
                PersonsRootResource.class);
    }
}