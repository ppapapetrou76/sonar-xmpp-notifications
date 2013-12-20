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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.database.model.User;
import org.sonar.api.notifications.Notification;
import org.sonar.api.notifications.NotificationChannel;
import org.sonar.api.security.UserFinder;
import org.sonar.plugins.xmpp.api.XMPPMessage;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;

import java.util.List;
import org.jivesoftware.smack.XMPPException;

public class XMPPNotificationChannel extends NotificationChannel {

  private static final Logger LOG = LoggerFactory.getLogger(XMPPNotificationChannel.class);

  private final UserFinder userFinder;
  private final List<AbstractXMPPTemplate> templates;
  private final XMPPAdapter xmppAdapter;

  public XMPPNotificationChannel(UserFinder userFinder, List<AbstractXMPPTemplate> templates, XMPPAdapter xmppAdapter) {
    super();
    this.userFinder = userFinder;
    this.templates = templates;
    this.xmppAdapter = xmppAdapter;
  }

  @Override
  public void deliver(Notification notification, String username) {
    User user = userFinder.findByLogin(username);
    if (StringUtils.isBlank(user.getEmail())) {
      LOG.warn("Email not defined for user: " + username);
      return;
    }
    XMPPMessage xmppMesage = format(notification);
    if (xmppMesage != null) {
      xmppMesage.setTo(user.getEmail());
      deliver(xmppMesage);
    }
  }

  private XMPPMessage format(Notification notification) {
    for (AbstractXMPPTemplate template : templates) {
      XMPPMessage message = template.formatMessage(notification);
      if (message != null) {
        return message;
      }
    }
    LOG.warn("XMPP template not found for notification: {}", notification);
    return null;
  }

  void deliver(XMPPMessage xmppMesage) {
    try {
      xmppAdapter.connect();
      xmppAdapter.sendMessage(xmppMesage);
      xmppAdapter.disconnect();
    } catch (XMPPException e) {
      LOG.warn("Cannot send XMPP notification message.", e);
    }

  }
}
