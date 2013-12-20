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
package org.sonar.plugins.xmpp.templates;

import org.junit.Test;
import org.junit.Before;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.sonar.plugins.xmpp.api.XMPPMessage;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AlertsXMPPMessageTemplateTest {
  private XMPPSettings settings;
  private AlertsXMPPMessageTemplate template;
  private Notification notification = new Notification("alerts");
  
  @Before
  public void setUp() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_ID, "PID");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_KEY, "PKEY");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_NAME, "PNAME");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_NAME, "ANAME");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_TEXT, "ATEXT");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_LEVEL, "OK");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_IS_NEW_ALERT, "false");
    settings = mock(XMPPSettings.class);
    when(settings.getServerBaseUrl()).thenReturn("URL");
    template = new AlertsXMPPMessageTemplate(settings);
  }
  
  @Test
  public void shouldFormatMessageWhenProjectGetsGreen() {
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("\"PNAME\" is back to green\nAlert level: ANAME\nAlert: ATEXT\n\nSee it in SonarQube: URL/dashboard/index/PKEY");
    assertThat(message.getMessageId()).isEqualTo("alerts/PID");
  }

  @Test
  public void shouldFormatMessageWhenNewAlerts() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_IS_NEW_ALERT, "true");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_LEVEL, "WARN");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("New alert on \"PNAME\"\nAlert level: ANAME\nNew alert: ATEXT\n\nSee it in SonarQube: URL/dashboard/index/PKEY");
    assertThat(message.getMessageId()).isEqualTo("alerts/PID");
  }

  @Test
  public void shouldFormatMessageWhenChangingAlertLevel() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_IS_NEW_ALERT, "false");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_LEVEL, "WARN");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("Alert level changed on \"PNAME\"\nAlert level: ANAME\nAlert: ATEXT\n\nSee it in SonarQube: URL/dashboard/index/PKEY");
    assertThat(message.getMessageId()).isEqualTo("alerts/PID");
  }

  @Test
  public void shouldFormatMessageWhenMultipleAlerts() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_IS_NEW_ALERT, "true");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_LEVEL, "WARN");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_TEXT, "A1,A2");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("New alert on \"PNAME\"\nAlert level: ANAME\nNew alerts:\n  - A1\n  - A2\n\nSee it in SonarQube: URL/dashboard/index/PKEY");
    assertThat(message.getMessageId()).isEqualTo("alerts/PID");
  }

  @Test
  public void shouldFormatMessageWhenNoAlerts() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_IS_NEW_ALERT, "true");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_LEVEL, "WARN");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_ALERT_TEXT, "");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("New alert on \"PNAME\"\nAlert level: ANAME\n\nSee it in SonarQube: URL/dashboard/index/PKEY");
    assertThat(message.getMessageId()).isEqualTo("alerts/PID");
  }

  @Test
  public void shouldGetNotificationType() {
    assertThat(template.getNotificationType()).isEqualTo("alerts");
  }
  
}
