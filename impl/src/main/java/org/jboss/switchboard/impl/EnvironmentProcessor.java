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
package org.jboss.switchboard.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.switchboard.spi.ENCBindingProvider;
import org.jboss.switchboard.spi.EnvironmentVisitor;

/**
 * EnvironmentProcessor
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EnvironmentProcessor<C, E>
{

   private List<EnvironmentVisitor<C, E>> visitors;

   public EnvironmentProcessor()
   {
      this(new ArrayList<EnvironmentVisitor<C, E>>());
   }

   public EnvironmentProcessor(List<EnvironmentVisitor<C, E>> visitors)
   {
      this.visitors = visitors;
   }

   
   public Collection<ENCBindingProvider> process(C context, Collection<E> environments)
   {
      Collection<ENCBindingProvider> encBindingProviders = new ArrayList<ENCBindingProvider>();
      for (E environment : environments)
      {
         encBindingProviders.addAll(this.processEnvironment(context, environment));
      }
      return encBindingProviders;
   }
   
   private Collection<ENCBindingProvider> processEnvironment(C context, E environment)
   {
      Collection<ENCBindingProvider> encBindingProviders = new ArrayList<ENCBindingProvider>();
      for (EnvironmentVisitor<C, E> visitor : this.visitors)
      {
         Collection<ENCBindingProvider> providers = visitor.visit(context, environment);
         if (providers != null)
         {
            encBindingProviders.addAll(providers);
         }
      }
      return encBindingProviders;
   }
}
