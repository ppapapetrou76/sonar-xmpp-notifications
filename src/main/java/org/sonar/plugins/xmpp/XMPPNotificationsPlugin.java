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
package org.sonar.plugins.xmpp;

import org.sonar.plugins.xmpp.settings.XMPPSettings;
import com.google.common.collect.ImmutableList;
import org.sonar.api.SonarPlugin;
import org.sonar.plugins.xmpp.templates.AlertsXMPPMessageTemplate;

import java.util.List;
import org.sonar.api.PropertyType;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;
import org.sonar.plugins.xmpp.templates.IssueChangesXMPPMessageTemplate;
import org.sonar.plugins.xmpp.templates.NewIssuesXMPPMessageTemplate;

public class XMPPNotificationsPlugin extends SonarPlugin {
  public static final String XMPP_CATEGORY = "XMPP";
  
  @Override
  public List<?> getExtensions() {
    return ImmutableList.of(

      PropertyDefinition.builder(XMPPConstants.XMPP_SERVER_NAME).
        name("XMPP Server name").
        description("Your XMPP server name (example : jabber.com) without the port number").
        index(1).
        onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
        category(XMPP_CATEGORY).
        type(PropertyType.STRING).
        build(),

      PropertyDefinition.builder(XMPPConstants.XMPP_SERVER_PORT).
        name("XMPP Server port").
        description("Your XMPP server port (example : 5222)").
        index(2).
        onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
        category(XMPP_CATEGORY).
        type(PropertyType.INTEGER).
        build(),

      PropertyDefinition.builder(XMPPConstants.XMPP_LOGIN).
        name("XMPP login name").
        description("Your XMPP login name (example : john@gmail.com) ").
        index(3).
        onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
        category(XMPP_CATEGORY).
        type(PropertyType.STRING).
        build(),
            
      PropertyDefinition.builder(XMPPConstants.XMPP_PASSWORD).
        name("XMPP login name").
        description("Your XMPP login password").
        index(4).
        onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE).
        category(XMPP_CATEGORY).
        type(PropertyType.STRING).
        build(),
            
      // Extensions
      XMPPSettings.class,
      XMPPAdapter.class,
      XMPPNotificationChannel.class,
      
      // Smpp Message templates
      AlertsXMPPMessageTemplate.class,
      IssueChangesXMPPMessageTemplate.class,
      NewIssuesXMPPMessageTemplate.class);
  }
}
