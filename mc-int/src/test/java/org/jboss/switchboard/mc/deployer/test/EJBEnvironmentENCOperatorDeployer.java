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
 * EJBEnvironmentENCOperatorDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class EJBEnvironmentENCOperatorDeployer extends ENCOperatorDeployer<Environment>
{

   public EJBEnvironmentENCOperatorDeployer()
   {
      super(JBossMetaData.class);
   }

   @Override
   protected Collection<Environment> getEnvironments(DeploymentUnit unit)
   {
      if(unit.isAttachmentPresent(JBossWebMetaData.class))
      {
         return Collections.emptySet();
      }
      JBossMetaData jbossMetadata = unit.getAttachment(JBossMetaData.class);
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
