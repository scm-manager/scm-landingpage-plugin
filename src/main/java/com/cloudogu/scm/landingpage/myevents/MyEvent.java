/*
 * MIT License
 *
 * Copyright (c) 2020-present Cloudogu GmbH and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.cloudogu.scm.landingpage.myevents;

import lombok.Getter;
import sonia.scm.event.Event;
import sonia.scm.xml.XmlInstantAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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
