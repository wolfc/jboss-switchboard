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

import java.util.Collection;
import java.util.HashSet;

import javax.naming.Context;
import javax.naming.NamingException;

import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.switchboard.spi.ENCBinding;
import org.jboss.util.naming.Util;

/**
 * ENCOperator
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class ENCOperator
{

   /** 
    * {@link JavaEEComponent} on which this {@link ENCOperator} operates
    */
   private JavaEEComponent component;

   /** 
    * The list of {@link ENCBinding}s which this {@link ENCOperator} has
    * to bind/unbind into/from the ENC of the {@link JavaEEComponent}
    */
   private Collection<ENCBinding> encBindings = new HashSet<ENCBinding>();

   /**
    * Constructs a {@link ENCOperator} with the passed {@link ENCBinding}s
    * @param bindings The {@link ENCBinding}s which this {@link ENCOperator} is responsible for binding/unbinding
    *                   from ENC
    */
   public ENCOperator(Collection<ENCBinding> bindings)
   {
      this.encBindings = bindings;
   }
   
   /**
    * Constructs a {@link ENCOperator} for a {@link JavaEEComponent}
    * 
    * @param component The {@link JavaEEComponent} into whose {@link Context}, this {@link ENCOperator} will
    *                   bind/unbind the {@link ENCBinding}s
    * @param bindings The {@link ENCBinding}s which this {@link ENCOperator} is responsible for binding/unbinding
    *                   from ENC
    */
   public ENCOperator(JavaEEComponent component, Collection<ENCBinding> bindings)
   {
      this(bindings);
      this.component = component;
   }

   
   public void bind() throws NamingException
   {
      Context enc = this.getContext();
      for (ENCBinding binding : this.encBindings)
      {
         String jndiName = binding.getJNDIName();
         Object jndiObject = binding.getJNDIObject();
         Util.bind(enc, jndiName, jndiObject);
      }
   }

   public void unbind() throws NamingException
   {
      Context enc = this.getContext();
      for (ENCBinding binding : this.encBindings)
      {
         String jndiName = binding.getJNDIName();
         enc.unbind(jndiName);
      }
   }
   
   public void setJavaEEComponent(JavaEEComponent component)
   {
      if (this.component != null)
      {
         throw new IllegalStateException(JavaEEComponent.class + " is already set on ENCOperator: " + this);
      }
      this.component = component;
   }
   
   private Context getContext()
   {
      if (this.component == null)
      {
         throw new IllegalStateException(JavaEEComponent.class + " is not set, cannot get context");
      }
      return this.component.getContext();
   }
   

}
