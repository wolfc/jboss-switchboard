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
package org.jboss.switchboard.mc.deployer.test;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.metadata.ejb.jboss.JBossEnterpriseBeansMetaData;
import org.jboss.metadata.ejb.jboss.JBossMetaData;
import org.jboss.metadata.javaee.spec.Environment;
import org.jboss.metadata.web.jboss.JBossWebMetaData;
import org.jboss.switchboard.mc.deployer.ENCOperatorDeployer;

/**
 * WebENCOperatorDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class WebENCOperatorDeployer extends ENCOperatorDeployer<Environment>
{

   public WebENCOperatorDeployer()
   {
      super(JBossWebMetaData.class);
   }

   @Override
   protected Collection<Environment> getEnvironments(DeploymentUnit unit)
   {
      JBossWebMetaData jbosswebMetadata = unit.getAttachment(JBossWebMetaData.class);
      if (!this.isVersionApplicable(jbosswebMetadata))
      {
         return Collections.emptySet();
      }
      Collection<Environment> environments = new HashSet<Environment>();
      if (jbosswebMetadata.getJndiEnvironmentRefsGroup() != null)
      {
         environments.add(jbosswebMetadata.getJndiEnvironmentRefsGroup());
      }
      
      // now check if this unit even contains JBossMetadata (i.e. if EJBs are deployed
      // in this .war)
      if(unit.isAttachmentPresent(JBossMetaData.class))
      {
         JBossMetaData jbossMetadata = unit.getAttachment(JBossMetaData.class);
         environments.addAll(this.getEJBEnvironments(jbossMetadata));
      }
      // return the environments for processing
      return environments;
   }

   private boolean isVersionApplicable(JBossWebMetaData jbosswebMetadata)
   {
      if (jbosswebMetadata.is25() || jbosswebMetadata.is30())
      {
         return true;
      }
      return false;
   }
   
   private Collection<Environment> getEJBEnvironments(JBossMetaData jbossMetadata)
   {
      if (!jbossMetadata.isEJB3x())
      {
         return Collections.emptySet();
      }
      JBossEnterpriseBeansMetaData enterpriseBeans = jbossMetadata.getEnterpriseBeans();
      if (enterpriseBeans == null || enterpriseBeans.isEmpty())
      {
         return Collections.emptySet();
      }
      return new HashSet<Environment>(enterpriseBeans);
   }
}
