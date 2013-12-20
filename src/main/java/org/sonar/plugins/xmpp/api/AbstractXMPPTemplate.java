/*
 * SonarQube XMPP Notifications Plugin
 * Copyright (C) 2013 Patroklos PAPAPETROU
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.xmpp.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerExtension;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.xmpp.settings.XMPPSettings;

public abstract class AbstractXMPPTemplate implements ServerExtension {
  
  private final XMPPSettings settings;
  public static final String FIELD_PROJECT_NAME = "projectName";
  public static final String FIELD_PROJECT_KEY = "projectKey";
  public static final String FIELD_PROJECT_DATE = "projectDate";
  public static final String FIELD_PROJECT_ID = "projectId";
  public static final String FIELD_ALERT_NAME = "alertName";
  public static final String FIELD_ALERT_LEVEL = "alertLevel";
  public static final String FIELD_ALERT_TEXT = "alertText";
  public static final String FIELD_IS_NEW_ALERT = "isNewAlert";


  public AbstractXMPPTemplate(XMPPSettings settings) {
    this.settings = settings;
  }
  
  protected static final char NEW_LINE = '\n';
  private static final Logger LOG = LoggerFactory.getLogger(AbstractXMPPTemplate.class);
  
  protected abstract XMPPMessage format(Notification notification);
  protected abstract String getNotificationType();
  
  public boolean isResponsible (Notification notification){
    return getNotificationType().equals(notification.getType());
  }

  public XMPPSettings getSettings() {
    return settings;
  }
  
  public XMPPMessage formatMessage(Notification notification){
    if (isResponsible(notification)){
      LOG.warn("Preparing message based on notification:" + notification.toString());
      return format(notification);
    } else {
      return null;
    }
    
  }

}
