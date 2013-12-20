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

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;
import org.sonar.plugins.xmpp.api.XMPPMessage;
import static org.mockito.Mockito.*;
import org.sonar.api.database.model.User;
import org.sonar.api.notifications.Notification;
import org.sonar.api.security.UserFinder;
import static org.fest.assertions.api.Assertions.*;
import org.jivesoftware.smack.XMPPException;
import org.mockito.InOrder;

public class XMPPNotificationChannelTest {

  private XMPPNotificationChannel channel;
  private UserFinder userFinder;
  private final List<AbstractXMPPTemplate> templates = new ArrayList<AbstractXMPPTemplate>();
  private AbstractXMPPTemplate template;
  private XMPPAdapter xmppAdapter;
  private User user;
  private Notification notification;
  private static final String USERNAME = "username";
  private static final String EMAIL = "someEmail";
  
  
  @Before
  public void init(){
    user = mock(User.class);
    userFinder = mock(UserFinder.class);
    template = mock(AbstractXMPPTemplate.class);
    xmppAdapter = mock(XMPPAdapter.class);
    templates.add(template);
    when(userFinder.findByLogin(USERNAME)).thenReturn(user);
    channel = new XMPPNotificationChannel(userFinder, templates, xmppAdapter);
  }
  
  @Test
  public void shouldNotDeliverIfUserHasNoEmail() {
    
    XMPPNotificationChannel spiedChannel = spy(channel);
    when(user.getEmail()).thenReturn("");
    spiedChannel.deliver(notification, USERNAME);
    
    verify(spiedChannel,times(0)).deliver(any(XMPPMessage.class));
    
  }

  @Test
  public void shouldNotDeliverIfNoTemplateFound() {
    when(template.formatMessage(notification)).thenReturn(null);
    when(user.getEmail()).thenReturn("someEmail");
    XMPPNotificationChannel spiedChannel = spy(channel);
    spiedChannel.deliver(notification, USERNAME);
    
    verify(spiedChannel,times(0)).deliver(any(XMPPMessage.class));
    
  }

  @Test
  public void shouldFailToDeliverIfCannotConnectToXMPPServer() throws XMPPException {
    XMPPMessage xmppMessage = new XMPPMessage();
    when(template.formatMessage(notification)).thenReturn(xmppMessage);
    when(user.getEmail()).thenReturn(EMAIL);
    doThrow(XMPPException.class).when(xmppAdapter).connect();

    XMPPNotificationChannel spiedChannel = spy(channel);
    spiedChannel.deliver(notification, USERNAME);
    
    assertThat(xmppMessage.getTo()).isEqualTo(EMAIL);
    verify(spiedChannel).deliver(xmppMessage);
    
    verify(xmppAdapter).connect();
    verify(xmppAdapter,times(0)).sendMessage(xmppMessage);
    verify(xmppAdapter,times(0)).disconnect();
  }

  @Test
  public void shouldFailToDeliverIfCannotSendMessage() throws XMPPException {
    XMPPMessage xmppMessage = new XMPPMessage();
    when(template.formatMessage(notification)).thenReturn(xmppMessage);
    when(user.getEmail()).thenReturn(EMAIL);
    doThrow(XMPPException.class).when(xmppAdapter).sendMessage(xmppMessage);

    XMPPNotificationChannel spiedChannel = spy(channel);
    spiedChannel.deliver(notification, USERNAME);
    
    assertThat(xmppMessage.getTo()).isEqualTo(EMAIL);
    verify(spiedChannel).deliver(xmppMessage);
    
    verify(xmppAdapter).connect();
    verify(xmppAdapter).sendMessage(xmppMessage);
    verify(xmppAdapter,times(0)).disconnect();
  }

  
  @Test
  public void shouldDeliver() throws XMPPException {
    InOrder inOrder = inOrder(xmppAdapter);

    XMPPMessage xmppMessage = new XMPPMessage();
    when(template.formatMessage(notification)).thenReturn(xmppMessage);
    when(user.getEmail()).thenReturn(EMAIL);
    XMPPNotificationChannel spiedChannel = spy(channel);
    spiedChannel.deliver(notification, USERNAME);
    
    assertThat(xmppMessage.getTo()).isEqualTo(EMAIL);
    verify(spiedChannel).deliver(xmppMessage);
    
    inOrder.verify(xmppAdapter).connect();
    inOrder.verify(xmppAdapter).sendMessage(xmppMessage);
    inOrder.verify(xmppAdapter).disconnect();
  }
  
  
}
