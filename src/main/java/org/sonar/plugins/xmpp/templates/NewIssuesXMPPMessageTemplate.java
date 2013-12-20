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

import com.google.common.collect.Lists;
import org.sonar.api.i18n.I18n;
import org.sonar.api.notifications.Notification;
import org.sonar.api.rule.Severity;
import org.sonar.api.utils.DateUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;
import org.sonar.plugins.xmpp.api.XMPPMessage;

public class NewIssuesXMPPMessageTemplate extends AbstractXMPPTemplate {

  private final I18n i18n;

  public NewIssuesXMPPMessageTemplate(XMPPSettings settings, I18n i18n) {
    super(settings);
    this.i18n = i18n;
  }

  @Override
  public XMPPMessage format(Notification notification) {
    String projectName = notification.getFieldValue(FIELD_PROJECT_NAME);

    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder.append("Project: ").append(projectName).append(NEW_LINE);
    messageBuilder.append(notification.getFieldValue("count")).append(" new issues").append(NEW_LINE);
    messageBuilder.append("   ");
    for (Iterator<String> severityIterator = Lists.reverse(Severity.ALL).iterator(); severityIterator.hasNext(); ) {
      String severity = severityIterator.next();
      String severityLabel = i18n.message(getLocale(), "severity."+ severity, severity);
      messageBuilder.append(severityLabel).append(": ").append(notification.getFieldValue("count-"+ severity));
      if (severityIterator.hasNext()) {
        messageBuilder.append("   ");
      }
    }
    messageBuilder.append(NEW_LINE);

    appendFooter(messageBuilder, notification);

    XMPPMessage message = new XMPPMessage()
      .setMessageId("new-issues/" + notification.getFieldValue(FIELD_PROJECT_KEY))
      .setMessage(messageBuilder.toString());

    return message;
  }

  private void appendFooter(StringBuilder messageBuilder, Notification notification) {
    String projectKey = notification.getFieldValue(FIELD_PROJECT_KEY);
    String dateString = notification.getFieldValue(FIELD_PROJECT_DATE);
    if (projectKey != null && dateString != null) {
      Date date = DateUtils.parseDateTime(dateString);
      String url = String.format("%s/issues/search?componentRoots=%s&createdAt=%s",
        getSettings().getServerBaseUrl(), encode(projectKey), encode(DateUtils.formatDateTime(date)));
      messageBuilder.append(NEW_LINE).append("See it in SonarQube: ").append(url).append(NEW_LINE);
    }
  }

  public static String encode(String toEncode) {
    try {
      return URLEncoder.encode(toEncode, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Encoding not supported", e);
    }
  }

  private Locale getLocale() {
    return Locale.ENGLISH;
  }

  @Override
  protected String getNotificationType() {
    return "new-issues";
  }

}
