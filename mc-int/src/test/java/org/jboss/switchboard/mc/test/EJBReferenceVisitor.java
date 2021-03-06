/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.switchboard.mc.test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.javaee.spec.EJBReferenceMetaData;
import org.jboss.metadata.javaee.spec.EJBReferencesMetaData;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.switchboard.spi.ENCBindingProvider;
import org.jboss.switchboard.spi.EnvironmentVisitor;

/**
 * EJBReferenceVisitor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EJBReferenceVisitor implements EnvironmentVisitor<DeploymentUnit, Environment>
{

   @Override
   public Collection<ENCBindingProvider> visit(DeploymentUnit unit, Environment environment)
   {
      EJBReferencesMetaData ejbRefs = environment.getEjbReferences();
      if (ejbRefs == null || ejbRefs.isEmpty())
      {
         return Collections.emptySet();
      }
      Collection<ENCBindingProvider> providers = new HashSet<ENCBindingProvider>();
      for (EJBReferenceMetaData ejbRef : ejbRefs)
      {
         providers.add(new EJBENCBindingProvider(unit, ejbRef));
      }
      return providers;
   }

}
