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
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.ServerExtension;
import org.sonar.plugins.xmpp.api.XMPPMessage;

public class XMPPAdapter implements ServerExtension {

  private static final Logger LOG = LoggerFactory.getLogger(XMPPAdapter.class);
  private final XMPPSettings settings;
  private Connection connection;

  public XMPPAdapter(XMPPSettings settings) {
    this.settings = settings;
  }

  public void connect() throws XMPPException {
    ConnectionConfiguration config = new ConnectionConfiguration(settings.getServerName(), settings.getServerPort());
    connection = new XMPPConnection(config);
    connection.connect();
    connection.login(settings.getLoginName(), settings.getLoginPassword());

    LOG.debug("Connected to :" + config.getHost() + ":" + config.getPort());
  }

  public void sendMessage(XMPPMessage xmppMessage) throws XMPPException {
    ChatManager chatmanager = connection.getChatManager();
    Chat newChat = chatmanager.createChat(xmppMessage.getTo(), new MessageListener() {
      @Override
      public void processMessage(Chat chat, Message message) {
        // No need to implement
      }
    });

    LOG.debug("Sending message '" + xmppMessage.getMessage() + "' to " + newChat.getParticipant());
    newChat.sendMessage(xmppMessage.getMessage());

  }

  public void disconnect() {
    if (connection != null) {
      connection.disconnect();
    }
  }

}
