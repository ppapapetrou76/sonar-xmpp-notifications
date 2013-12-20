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

import org.apache.commons.lang.StringUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.notifications.Notification;
import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.sonar.plugins.xmpp.api.XMPPMessage;
import org.sonar.plugins.xmpp.api.AbstractXMPPTemplate;

public class AlertsXMPPMessageTemplate extends AbstractXMPPTemplate {
  public AlertsXMPPMessageTemplate(XMPPSettings settings) {
    super(settings);
  }

  @Override
  public XMPPMessage format(Notification notification) {
    
    String projectId = notification.getFieldValue(FIELD_PROJECT_ID);
    String projectKey = notification.getFieldValue(FIELD_PROJECT_KEY);
    String projectName = notification.getFieldValue(FIELD_PROJECT_NAME);
    String alertName = notification.getFieldValue(FIELD_ALERT_NAME);
    String alertText = notification.getFieldValue(FIELD_ALERT_TEXT);
    String alertLevel = notification.getFieldValue(FIELD_ALERT_LEVEL);
    boolean isNewAlert = Boolean.valueOf(notification.getFieldValue(FIELD_IS_NEW_ALERT));
    String messageBody = generateMessageBody(projectName, projectKey, alertName, alertLevel, alertText, isNewAlert);

    return new XMPPMessage()
        .setMessageId("alerts/" + projectId)
        .setMessage(messageBody);
  }

  private String generateMessageBody(String projectName, String projectKey, String alertName, String alertLevel, String alertText, boolean isNewAlert) {
    StringBuilder messageBody = new StringBuilder();
    if (Metric.Level.OK.toString().equals(alertLevel)) {
      messageBody.append("\"").append(projectName).append("\" is back to green");
    } else if (isNewAlert) {
      messageBody.append("New alert on \"").append(projectName).append("\"");
    } else {
      messageBody.append("Alert level changed on \"").append(projectName).append("\"");
    }
    messageBody.append(NEW_LINE).append("Alert level: ").append(alertName).append(NEW_LINE);

    String[] alerts = StringUtils.split(alertText, ",");
    if (alerts.length > 0) {
      if (isNewAlert) {
        messageBody.append("New alert");
      } else {
        messageBody.append("Alert");
      }
      if (alerts.length == 1) {
        messageBody.append(": ").append(alerts[0].trim()).append(NEW_LINE);
      } else {
        messageBody.append("s:").append(NEW_LINE);
        for (String alert : alerts) {
          messageBody.append("  - ").append(alert.trim()).append(NEW_LINE);
        }
      }
    }

    messageBody.append(NEW_LINE).append("See it in SonarQube: ").append(getSettings().getServerBaseUrl()).append("/dashboard/index/").append(projectKey);

    return messageBody.toString();
  }

  @Override
  protected String getNotificationType() {
    return "alerts";
  }

}
