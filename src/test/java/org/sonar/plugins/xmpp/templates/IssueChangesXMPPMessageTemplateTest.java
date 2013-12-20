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
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.sonar.api.user.User;
import org.sonar.api.user.UserFinder;

public class IssueChangesXMPPMessageTemplateTest {
  private XMPPSettings settings;
  private UserFinder userFinder;
  private IssueChangesXMPPMessageTemplate template;
  private Notification notification = new Notification("issue-changes");
  private static final String USERNAME = "username";
  private User user;
  
  @Before
  public void setUp() {
    
    notification.setFieldValue("componentName", "CNAME");
    notification.setFieldValue("ruleName", "RNAME");
    notification.setFieldValue("message", "MESSAGE");
    notification.setFieldValue("comment", "COMMENT");
    notification.setFieldValue("old.assignee", "OLD_ASSIGNEE");
    notification.setFieldValue("new.assignee", "NEW_ASSIGNEE");
    notification.setFieldValue("old.severity", "OLD_SEVERITY");
    notification.setFieldValue("new.severity", "NEW_SEVERITY");
    notification.setFieldValue("old.resolution", "OLD_RESOLUTION");
    notification.setFieldValue("new.resolution", "NEW_RESOLUTION");
    notification.setFieldValue("old.status", "OLD_STATUS");
    notification.setFieldValue("new.status", "NEW_STATUS");
    notification.setFieldValue("new.message", "NEW_MSG");
    notification.setFieldValue("old.actionPlan", "OLD_ACTION_PLAN");
    notification.setFieldValue("key", "ISSUE_KEY");
    
    settings = mock(XMPPSettings.class);
    user = mock(User.class);
    userFinder = mock(UserFinder.class);
    when(user.name()).thenReturn("NAME");
    when(settings.getServerBaseUrl()).thenReturn("URL");
    when(userFinder.findByLogin(USERNAME)).thenReturn(user);
    template = new IssueChangesXMPPMessageTemplate(settings, userFinder);
  }
  
  @Test
  public void shouldFormatMessage() {
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("CNAME\n" +
                                                "Rule: RNAME\n" +
                                                "Message: MESSAGE\n" +
                                                "\n" +
                                                "Comment: COMMENT\n" +
                                                "Assignee changed to NEW_ASSIGNEE\n" +
                                                "Severity: NEW_SEVERITY (was OLD_SEVERITY)\n" +
                                                "Resolution: NEW_RESOLUTION (was OLD_RESOLUTION)\n" +
                                                "Status: NEW_STATUS (was OLD_STATUS)\n" +
                                                "Message: NEW_MSG\n" +
                                                "Action Plan removed\n" +
                                                "\n" +
                                                "See it in SonarQube: URL/issue/show/ISSUE_KEY\n");
    assertThat(message.getMessageId()).isEqualTo("issue-changes/ISSUE_KEY");
  }

  @Test
  public void shouldFormatMessageWithChangeAuthor() {
    notification.setFieldValue("changeAuthor", USERNAME);
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("CNAME\n" +
                                                "Rule: RNAME\n" +
                                                "Message: MESSAGE\n" +
                                                "\n" +
                                                "Comment: COMMENT\n" +
                                                "Assignee changed to NEW_ASSIGNEE\n" +
                                                "Severity: NEW_SEVERITY (was OLD_SEVERITY)\n" +
                                                "Resolution: NEW_RESOLUTION (was OLD_RESOLUTION)\n" +
                                                "Status: NEW_STATUS (was OLD_STATUS)\n" +
                                                "Message: NEW_MSG\n" +
                                                "Action Plan removed\n" +
                                                "\n" +
                                                "See it in SonarQube: URL/issue/show/ISSUE_KEY\n");
    assertThat(message.getFrom()).isEqualTo("NAME");
    assertThat(message.getMessageId()).isEqualTo("issue-changes/ISSUE_KEY");
  }

  @Test
  public void shouldFormatMessageWithUnknownChangeAuthor() {
    notification.setFieldValue("changeAuthor", "OTHER_AUTHOR");
    XMPPMessage message = template.format(notification);
    assertThat(message.getMessage()).isEqualTo("CNAME\n" +
                                                "Rule: RNAME\n" +
                                                "Message: MESSAGE\n" +
                                                "\n" +
                                                "Comment: COMMENT\n" +
                                                "Assignee changed to NEW_ASSIGNEE\n" +
                                                "Severity: NEW_SEVERITY (was OLD_SEVERITY)\n" +
                                                "Resolution: NEW_RESOLUTION (was OLD_RESOLUTION)\n" +
                                                "Status: NEW_STATUS (was OLD_STATUS)\n" +
                                                "Message: NEW_MSG\n" +
                                                "Action Plan removed\n" +
                                                "\n" +
                                                "See it in SonarQube: URL/issue/show/ISSUE_KEY\n");
    assertThat(message.getFrom()).isEqualTo("OTHER_AUTHOR");
    assertThat(message.getMessageId()).isEqualTo("issue-changes/ISSUE_KEY");
  }

  
  @Test
  public void shouldGetNotificationType() {
    assertThat(template.getNotificationType()).isEqualTo("issue-changes");
  }
  
}
