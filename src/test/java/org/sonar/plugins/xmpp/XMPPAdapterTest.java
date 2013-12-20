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

import org.jivesoftware.smack.ConnectionConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.sonar.api.config.Settings;
import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sonar.plugins.xmpp.api.XMPPMessage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({XMPPAdapter.class})
public class XMPPAdapterTest {
  
  private Settings settings = new Settings();
  private XMPPSettings xmppSettings;
  private ConnectionConfiguration connConfig;
  private XMPPConnection connection;
  private XMPPAdapter adapter;
  
  @Before
  public void setUp() throws Exception {
    connConfig = mock(ConnectionConfiguration.class);
    connection = mock(XMPPConnection.class);
    settings.setProperty(XMPPConstants.XMPP_SERVER_PORT, "5222");
    settings.setProperty(XMPPConstants.XMPP_SERVER_NAME, "host");
    settings.setProperty(XMPPConstants.XMPP_LOGIN, "login");
    settings.setProperty(XMPPConstants.XMPP_PASSWORD, "pwd");
    xmppSettings = new XMPPSettings(settings);
    PowerMockito.whenNew(ConnectionConfiguration.class).withArguments(
            xmppSettings.getServerName(), xmppSettings.getServerPort()).thenReturn(connConfig);
    PowerMockito.whenNew(XMPPConnection.class).withArguments(connConfig).thenReturn(connection);
    adapter = new XMPPAdapter(xmppSettings);
  }
  
  @Test
  public void shouldConnect() throws XMPPException {
    adapter.connect();
    verify(connection).connect();
    verify(connection).login(xmppSettings.getLoginName(), xmppSettings.getLoginPassword());
  }
  
  @Test
  public void shouldSendMessage() throws XMPPException {
    adapter.connect();
    XMPPMessage xmppMessage = new XMPPMessage();
    ChatManager chatmanager = mock(ChatManager.class);
    Chat chat = mock(Chat.class);
    when(connection.getChatManager()).thenReturn(chatmanager);
    when(chatmanager.createChat(eq(xmppMessage.getTo()), any(MessageListener.class))).thenReturn(chat);
    
    adapter.sendMessage(xmppMessage);
    verify(chat).sendMessage(xmppMessage.getMessage());
  }
  
  @Test
  public void shouldDisonnect() throws XMPPException {
    adapter.connect();
    adapter.disconnect();
    verify(connection).disconnect();
  }
  
  @Test
  public void shouldDisonnectWhenNoConnection() throws XMPPException {
    adapter.disconnect();
    verify(connection, times(0)).disconnect();
  }
  
}
