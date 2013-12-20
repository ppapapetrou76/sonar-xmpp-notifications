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

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.notifications.Notification;
import static org.fest.assertions.api.Assertions.*;

public class AbstractXMPPTemplateTest {
  
  private final AbstractXMPPTemplate xmppTemplate = new AbstractXMPPTemplateImpl();
  private final XMPPMessage expectedXmppMessage = new XMPPMessage();
  @Before
  public void setUp() {
  }

  @Test
  public void isResponsible() {
    Notification notification = new Notification("type");
    assertThat(xmppTemplate.isResponsible(notification)).isTrue();
  }

  @Test
  public void isNotResponsible() {
    Notification notification = new Notification("otherType");
    assertThat(xmppTemplate.isResponsible(notification)).isFalse();
  }

  @Test
  public void shouldGetNullMessage() {
    Notification notification = new Notification("otherType");
    assertThat(xmppTemplate.formatMessage(notification)).isNull();
  }

  @Test
  public void shouldFormatMessage() {
    Notification notification = new Notification("type");
    assertThat(xmppTemplate.formatMessage(notification)).isEqualTo(expectedXmppMessage);
  }

  public class AbstractXMPPTemplateImpl extends AbstractXMPPTemplate {

    public AbstractXMPPTemplateImpl() {
      super(null);
    }

    public XMPPMessage format(Notification notification) {
      return expectedXmppMessage;
    }

    public String getNotificationType() {
      return "type";
    }
  }
  
}
