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

import java.util.Locale;
import org.junit.Test;
import org.junit.Before;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.sonar.plugins.xmpp.api.XMPPMessage;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.sonar.api.i18n.I18n;

public class NewIssuesXMPPMessageTemplateTest {
  private XMPPSettings settings;
  private I18n i18n;
  private NewIssuesXMPPMessageTemplate template;
  private Notification notification = new Notification("new-issues");
  
  @Before
  public void setUp() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_NAME, "PNAME");
    notification.setFieldValue("count", "6");
    notification.setFieldValue("count-MAJOR", "5");
    notification.setFieldValue("count-INFO", "1");
    notification.setFieldValue("count-MINOR", "0");
    notification.setFieldValue("count-CRITICAL", "0");
    notification.setFieldValue("count-BLOCKER", "0");
    settings = mock(XMPPSettings.class);
    i18n = mock(I18n.class);
    when(settings.getServerBaseUrl()).thenReturn("URL");
    when(i18n.message(Locale.ENGLISH, "severity.INFO", "INFO")).thenReturn("INFO");
    when(i18n.message(Locale.ENGLISH, "severity.MAJOR", "MAJOR")).thenReturn("MAJOR");
    when(i18n.message(Locale.ENGLISH, "severity.MINOR", "MINOR")).thenReturn("MINOR");
    when(i18n.message(Locale.ENGLISH, "severity.CRITICAL", "CRITICAL")).thenReturn("CRITICAL");
    when(i18n.message(Locale.ENGLISH, "severity.BLOCKER", "BLOCKER")).thenReturn("BLOCKER");
    template = new NewIssuesXMPPMessageTemplate(settings, i18n);
  }
  
  @Test
  public void shouldFormatMessage() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_KEY, "PKEY");
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_DATE, "2013-12-31T10:10:10+0200");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("Project: PNAME\n6 new issues\n   BLOCKER: 0   CRITICAL: 0   MAJOR: 5   MINOR: 0   INFO: 1\n\nSee it in SonarQube: URL/issues/search?componentRoots=PKEY&createdAt=2013-12-31T10%3A10%3A10%2B0200\n");
    assertThat(message.getMessageId()).isEqualTo("new-issues/PKEY");
  }

  @Test
  public void shouldFormatMessageWhenProjectKeyIsNull() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_DATE, "2013-12-31T10:10:10+0200");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("Project: PNAME\n6 new issues\n   BLOCKER: 0   CRITICAL: 0   MAJOR: 5   MINOR: 0   INFO: 1\n");
    assertThat(message.getMessageId()).isEqualTo("new-issues/null");
  }

  @Test
  public void shouldFormatMessageWhenProjectDateIsNull() {
    notification.setFieldValue(AbstractXMPPTemplate.FIELD_PROJECT_KEY, "PKEY");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("Project: PNAME\n6 new issues\n   BLOCKER: 0   CRITICAL: 0   MAJOR: 5   MINOR: 0   INFO: 1\n");
    assertThat(message.getMessageId()).isEqualTo("new-issues/PKEY");
  }

  @Test
  public void shouldGetNotificationType() {
    assertThat(template.getNotificationType()).isEqualTo("new-issues");
  }
  
}
