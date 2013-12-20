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

import org.apache.commons.lang.builder.ToStringBuilder;

public class XMPPMessage {

  private String from;
  private String to;
  private String message;
  private String messageId;

  /**
   * @param from full name of user, who initiated this message or null, if message was initiated by Sonar
   * @return  the Smpp message
   */
  public XMPPMessage setFrom(String from) {
    this.from = from;
    return this;
  }

  /**
   * @return 
   * @see #setFrom(String)
   */
  public String getFrom() {
    return from;
  }

  /**
   * @param to email address where to send this message
   * @return the Smpp message
   */
  public XMPPMessage setTo(String to) {
    this.to = to;
    return this;
  }

  /**
   * @return 
   * @see #setTo(String)
   */
  public String getTo() {
    return to;
  }

  /**
   * @param message message body
   * @return the Smpp message
   */
  public XMPPMessage setMessage(String message) {
    this.message = message;
    return this;
  }

  /**
   * @return 
   * @see #setMessage(String)
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param messageId id of message for threading
   * @return the Smpp message
   */
  public XMPPMessage setMessageId(String messageId) {
    this.messageId = messageId;
    return this;
  }

  /**
   * @return 
   * @see #setMessageId(String)
   */
  public String getMessageId() {
    return messageId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

}
