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

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.user.User;
import org.sonar.api.user.UserFinder;

import javax.annotation.Nullable;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;
import org.sonar.plugins.xmpp.api.XMPPMessage;

public class IssueChangesXMPPMessageTemplate extends AbstractXMPPTemplate {

  private final UserFinder userFinder;

  public IssueChangesXMPPMessageTemplate(XMPPSettings settings, UserFinder userFinder) {
    super(settings);
    this.userFinder = userFinder;
  }

  @Override
  public XMPPMessage format(Notification notification) {
    StringBuilder messageBuilder = new StringBuilder();
    appendHeader(notification, messageBuilder);
    messageBuilder.append(NEW_LINE);
    appendChanges(notification, messageBuilder);
    messageBuilder.append(NEW_LINE);
    appendFooter(messageBuilder, notification);

    String issueKey = notification.getFieldValue("key");
    String author = notification.getFieldValue("changeAuthor");

    XMPPMessage message = new XMPPMessage()
      .setMessageId("issue-changes/" + issueKey)
      .setMessage(messageBuilder.toString())
      .setFrom(getUserFullName(author));
    return message;
  }

  public XMPPMessage formatForDuplication(Notification notification) {
    StringBuilder messageBuilder = new StringBuilder();
    appendHeader(notification, messageBuilder);
    messageBuilder.append(NEW_LINE);
    appendChanges(notification, messageBuilder);
    messageBuilder.append(NEW_LINE);
    appendFooter(messageBuilder, notification);

    String issueKey = notification.getFieldValue("key");
    String author = notification.getFieldValue("changeAuthor");

    XMPPMessage message = new XMPPMessage()
      .setMessageId("issue-changes/" + issueKey)
      .setMessage(messageBuilder.toString())
      .setFrom(getUserFullName(author));
    return message;
  }

  private void appendChanges(Notification notification, StringBuilder messageBuilder) {
    appendField(messageBuilder, "Comment", null, notification.getFieldValue("comment"));
    appendFieldWithoutHistory(messageBuilder, "Assignee", notification.getFieldValue("old.assignee"), notification.getFieldValue("new.assignee"));
    appendField(messageBuilder, "Severity", notification.getFieldValue("old.severity"), notification.getFieldValue("new.severity"));
    appendField(messageBuilder, "Resolution", notification.getFieldValue("old.resolution"), notification.getFieldValue("new.resolution"));
    appendField(messageBuilder, "Status", notification.getFieldValue("old.status"), notification.getFieldValue("new.status"));
    appendField(messageBuilder, "Message", notification.getFieldValue("old.message"), notification.getFieldValue("new.message"));
    appendField(messageBuilder, "Author", notification.getFieldValue("old.author"), notification.getFieldValue("new.author"));
    appendFieldWithoutHistory(messageBuilder, "Action Plan", notification.getFieldValue("old.actionPlan"), notification.getFieldValue("new.actionPlan")) ;
  }

  private void appendHeader(Notification notification, StringBuilder messageBuilder) {
    appendLine(messageBuilder, StringUtils.defaultString(notification.getFieldValue("componentName"), notification.getFieldValue("componentKey")));
    appendField(messageBuilder, "Rule", null, notification.getFieldValue("ruleName"));
    appendField(messageBuilder, "Message", null, notification.getFieldValue("message"));
  }

  private void appendFooter(StringBuilder messageBuilder, Notification notification) {
    String issueKey = notification.getFieldValue("key");
    messageBuilder.append("See it in SonarQube: ").append(getSettings().getServerBaseUrl()).append("/issue/show/").append(issueKey).append(NEW_LINE);
  }

  private void appendLine(StringBuilder messageBuilder, @Nullable String line) {
    if (!Strings.isNullOrEmpty(line)) {
      messageBuilder.append(line).append(NEW_LINE);
    }
  }

  private void appendField(StringBuilder messageBuilder, String name, @Nullable String oldValue, @Nullable String newValue) {
    if (oldValue != null || newValue != null) {
      messageBuilder.append(name).append(": ");
      if (newValue != null) {
        messageBuilder.append(newValue);
      }
      if (oldValue != null) {
        messageBuilder.append(" (was ").append(oldValue).append(")");
      }
      messageBuilder.append(NEW_LINE);
    }
  }

  private void appendFieldWithoutHistory(StringBuilder messageBuilder, String name, @Nullable String oldValue, @Nullable String newValue) {
    if (oldValue != null || newValue != null) {
      messageBuilder.append(name);
      if (newValue == null) {
        messageBuilder.append(" removed");
      } else {
        messageBuilder.append(" changed to ");
        messageBuilder.append(newValue);
      }
      messageBuilder.append(NEW_LINE);
    }
  }

  private String getUserFullName(@Nullable String login) {
    if (login == null) {
      return null;
    }
    User user = userFinder.findByLogin(login);
    if (user == null) {
      // most probably user was deleted
      return login;
    }
    return StringUtils.defaultIfBlank(user.name(), login);
  }

  @Override
  protected String getNotificationType() {
    return "issue-changes";
  }
}
