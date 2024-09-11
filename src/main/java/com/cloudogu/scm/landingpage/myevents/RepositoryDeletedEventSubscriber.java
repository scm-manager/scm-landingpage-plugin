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

import com.github.legman.Subscribe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEventType;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryEvent;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class RepositoryDeletedEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryDeletedEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(RepositoryEvent event) {
    if (event.getEventType() == HandlerEventType.DELETE) {
      Repository eventRepo = event.getItem();
      String permission = RepositoryPermissions.read(event.getItem()).asShiroString();
      String repository = eventRepo.getNamespace() + "/" + eventRepo.getName();
      User deleter = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);

      store.add(new RepositoryDeletedEvent(permission, repository, deleter.getName(), deleter.getDisplayName(), deleter.getMail()));
      store.markRepositoryEventsAsDeleted(repository);
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class RepositoryDeletedEvent extends MyEvent {
    private String repository;
    private String deleterName;
    private String deleterDisplayName;
    private String deleterMail;

    RepositoryDeletedEvent(String permission, String repository, String deleterName, String deleterDisplayName, String deleterMail) {
      super(RepositoryDeletedEvent.class.getSimpleName(), permission);
      this.repository = repository;
      this.deleterName = deleterName;
      this.deleterDisplayName = deleterDisplayName;
      this.deleterMail = deleterMail;
    }
  }
}
