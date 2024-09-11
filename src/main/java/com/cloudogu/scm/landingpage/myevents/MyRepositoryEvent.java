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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.event.Event;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@Event
@NoArgsConstructor
@EqualsAndHashCode
public class MyRepositoryEvent extends MyEvent {
  private String repository;
  private boolean deleted;

  public MyRepositoryEvent(String type, String permission, String repository) {
    super(type, permission);
    this.repository = repository;
  }

  public MyRepositoryEvent(String type, String permission, String repository, boolean deleted) {
    super(type, permission);
    this.repository = repository;
    this.deleted = deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }
}
