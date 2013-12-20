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
package org.sonar.plugins.xmpp.settings;

import com.google.common.base.Objects;
import org.sonar.api.CoreProperties;
import org.sonar.api.ServerExtension;
import org.sonar.api.config.Settings;
import org.sonar.plugins.xmpp.XMPPConstants;

public class XMPPSettings implements ServerExtension {
  private final Settings settings;

  public XMPPSettings(Settings settings) {
    this.settings = settings;
  }

  public String getServerName() {
    return settings.getString(XMPPConstants.XMPP_SERVER_NAME);
  }

  public int getServerPort() {
    return settings.getInt(XMPPConstants.XMPP_SERVER_PORT);
  }

  public String getLoginName() {
    return settings.getString(XMPPConstants.XMPP_LOGIN);
  }

  public String getLoginPassword() {
    return settings.getString(XMPPConstants.XMPP_PASSWORD);
  }
  
  public String getServerBaseUrl(){
    return Objects.firstNonNull(settings.getString(CoreProperties.SERVER_BASE_URL), CoreProperties.SERVER_BASE_URL_DEFAULT_VALUE);
  }
}
