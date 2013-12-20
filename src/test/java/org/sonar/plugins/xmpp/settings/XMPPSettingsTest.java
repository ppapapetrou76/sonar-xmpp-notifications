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

import org.sonar.plugins.xmpp.settings.XMPPSettings;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import static org.fest.assertions.api.Assertions.*;
import org.sonar.api.CoreProperties;
import org.sonar.plugins.xmpp.XMPPConstants;

public class XMPPSettingsTest {

  final Settings settings = new Settings();
  final XMPPSettings xmppSettings = new XMPPSettings(settings);
  
  @Before
  public void setUp() {
    settings.setProperty(XMPPConstants.XMPP_SERVER_NAME, "host");
    settings.setProperty(XMPPConstants.XMPP_SERVER_PORT, "5222");
    settings.setProperty(XMPPConstants.XMPP_LOGIN, "login");
    settings.setProperty(XMPPConstants.XMPP_PASSWORD, "password");
  }

  @Test
  public void shouldGetServerName() {
    assertThat(xmppSettings.getServerName()).isEqualTo("host");
  }
  
  @Test
  public void shouldGetServerPort() {
    assertThat(xmppSettings.getServerPort()).isEqualTo(5222);
  }
  
  @Test
  public void shouldGetLoginName() {
    assertThat(xmppSettings.getLoginName()).isEqualTo("login");
  }
  
  @Test
  public void shouldGetLoginPassword() {
    assertThat(xmppSettings.getLoginPassword()).isEqualTo("password");
  }
  
  @Test
  public void shouldGetServerBaseUrl() {
    settings.setProperty(CoreProperties.SERVER_BASE_URL, "localhost");
    assertThat(xmppSettings.getServerBaseUrl()).isEqualTo("localhost");
  }
  
  @Test
  public void shouldGetDefaultServerBaseUrl() {
    assertThat(xmppSettings.getServerBaseUrl()).isEqualTo("http://localhost:9000");
  }
}
