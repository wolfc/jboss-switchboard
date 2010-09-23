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
package org.jboss.switchboard.mc.deployer;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.beans.metadata.plugins.AbstractInjectionValueMetaData;
import org.jboss.beans.metadata.plugins.builder.BeanMetaDataBuilderFactory;
import org.jboss.beans.metadata.spi.BeanMetaData;
import org.jboss.beans.metadata.spi.builder.BeanMetaDataBuilder;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.spi.deployer.DeploymentStages;
import org.jboss.deployers.spi.deployer.helpers.AbstractDeployer;
import org.jboss.deployers.structure.spi.DeploymentUnit;
import org.jboss.reloaded.naming.deployers.javaee.JavaEEComponentInformer;
import org.jboss.reloaded.naming.spi.JavaEEComponent;
import org.jboss.switchboard.impl.ENCOperator;
import org.jboss.switchboard.impl.EnvironmentProcessor;
import org.jboss.switchboard.spi.ENCBinding;
import org.jboss.switchboard.spi.ENCBindingProvider;

/**
 * ENCOperatorDeployer
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public abstract class ENCOperatorDeployer<E> extends AbstractDeployer
{

   private EnvironmentProcessor<DeploymentUnit, E> environmentProcessor;

   private JavaEEComponentInformer javaeeComponentInformer;

   public ENCOperatorDeployer(Class<?> input)
   {
      this.setStage(DeploymentStages.REAL);
      this.setInput(input);
      
      this.setOutput(BeanMetaData.class);

   }
   
   @Override
   public void deploy(DeploymentUnit unit) throws DeploymentException
   {
      Collection<E> environments = this.getEnvironments(unit);

      if (environments == null || environments.isEmpty())
      {
         return;
      }
      this.process(unit, environments);
   }

   @Inject
   public void setEnvironmentProcessor(EnvironmentProcessor<DeploymentUnit, E> processor)
   {
      this.environmentProcessor = processor;
   }

   @Inject
   public void setJavaEEComponentInformer(JavaEEComponentInformer informer)
   {
      this.javaeeComponentInformer = informer;
   }

   protected abstract Collection<E> getEnvironments(DeploymentUnit unit);

   private void process(DeploymentUnit unit, Collection<E> environments)
   {
      Collection<ENCBindingProvider> providers = this.environmentProcessor.process(unit, environments);
      Collection<ENCBinding> encBindings = new ArrayList<ENCBinding>();
      for (ENCBindingProvider provider : providers)
      {
         encBindings.add(provider.provide());
      }
      ENCOperator encOperator = new ENCOperator(encBindings);
      this.createAndAttachBMD(unit, encOperator);
   }

   private void createAndAttachBMD(DeploymentUnit unit, ENCOperator encOperator)
   {
      String mcBeanName = this.getENCOperatorMCBeanName(unit, encOperator);
      BeanMetaDataBuilder builder = BeanMetaDataBuilderFactory.createBuilder(mcBeanName, ENCOperator.class.getName());

      builder.setConstructorValue(encOperator);

      String javaeeCompMCBeanName = this.getJavaEEComponentMCBeanName(unit); 
      AbstractInjectionValueMetaData javaeeComponentInjection = new AbstractInjectionValueMetaData(javaeeCompMCBeanName);
      builder.addPropertyMetaData("javaEEComponent", javaeeComponentInjection);

      unit.addAttachment(BeanMetaData.class + ":" + mcBeanName, builder.getBeanMetaData());

   }

   private String getENCOperatorMCBeanName(DeploymentUnit unit, ENCOperator encOperator)
   {
      StringBuilder sb = new StringBuilder("jboss-switchboard-encoperator:");
      String appName = this.javaeeComponentInformer.getApplicationName(unit);
      if (appName != null)
      {
         sb.append("appName=");
         sb.append(appName);
      }
      String moduleName = this.javaeeComponentInformer.getModuleName(unit);
      sb.append("module=");
      sb.append(moduleName);
      String componentName = this.javaeeComponentInformer.getComponentName(unit);
      sb.append("component=");
      sb.append(componentName);

      return sb.toString();
   }

   /**
    * Returns the {@link JavaEEComponent} MC bean name
    * @param deploymentUnit
    * @return
    */
   private String getJavaEEComponentMCBeanName(DeploymentUnit deploymentUnit)
   {
      String applicationName = this.javaeeComponentInformer.getApplicationName(deploymentUnit);
      String moduleName = this.javaeeComponentInformer.getModuleName(deploymentUnit);
      String componentName = this.javaeeComponentInformer.getComponentName(deploymentUnit);

      final StringBuilder builder = new StringBuilder("jboss.naming:");
      if (applicationName != null)
      {
         builder.append("application=").append(applicationName).append(",");
      }
      builder.append("module=").append(moduleName);
      if (componentName != null)
      {
         builder.append(",component=").append(componentName);
      }
      return builder.toString();
   }
}
