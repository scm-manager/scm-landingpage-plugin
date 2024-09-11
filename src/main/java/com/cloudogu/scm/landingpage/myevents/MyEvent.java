/*
 * Copyright (c) 2020 - present Cloudogu GmbH
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 */

package com.cloudogu.scm.landingpage.myevents;

import lombok.Getter;
import sonia.scm.event.Event;
import sonia.scm.xml.XmlInstantAdapter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;

/**
 * Extend this class for your own events. Mind that this class has to be serialized and
 * therefore only serializable types may be used for fields.
 * <br>
 * Subclasses have to
 * <ul>
 *   <li>provide an empty default constructor,</li>
 *   <li>provide a constructor setting all fields that should be persisted and calling
 *   the super constructor {@link #MyEvent(String, String, Instant)},</li>
 *   <li>be annotated with <code>@XmlRootElement</code> and <code>@XmlAccessorType(XmlAccessType.FIELD)</code>.</li>
 * </ul>
 */
@XmlRootElement
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@Event
public class MyEvent {
  private String type;
  private String permission;
  @XmlJavaTypeAdapter(XmlInstantAdapter.class)
  private Instant date;

  /**
   * Used for persistence, only. Overwrite this without setting any values. This will
   * only be called when reading events from the store.
   */
  protected MyEvent() {
  }

  /**
   * Sets the type and the permissions of the event. The date is set to "now".
   * @param type A string to find the renderer in the frontend.
   * @param permission A shiro permission string that determines, whether a logged in user should see this event.
   */
  public MyEvent(String type, String permission) {
    this(type, permission, Instant.now());
  }

  /**
   * Sets the type, the permissions and the date of the event.
   * @param type A string to find the renderer in the frontend.
   * @param permission A shiro permission string that determines, whether a logged in user should see this event.
   * @param date The date of the event.
   */
  public MyEvent(String type, String permission, Instant date) {
    this.type = type;
    this.permission = permission;
    this.date = date;
  }
}
