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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import sonia.scm.EagerSingleton;
import sonia.scm.HandlerEventType;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.RepositoryModificationEvent;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class RepositoryRenamedEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryRenamedEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(RepositoryModificationEvent event) {
    if (isRepositoryRenamedEvent(event)) {
      String oldRepository = event.getOldItem().getNamespace() + "/" + event.getOldItem().getName();
      String newRepository = event.getItem().getNamespace() + "/" + event.getItem().getName();
      User user = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);

      store.add(new RepositoryRenamedEvent(RepositoryPermissions.read(event.getItem()).asShiroString(), oldRepository, newRepository, user.getName(), user.getMail()));
      store.markRepositoryEventsAsDeleted(oldRepository);
    }
  }

  private boolean isRepositoryRenamedEvent(RepositoryModificationEvent event) {
    return event.getEventType() == HandlerEventType.MODIFY
      && (!event.getItem().getNamespace().equals(event.getOldItem().getNamespace())
      || !event.getItem().getName().equals(event.getOldItem().getName()));
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  @EqualsAndHashCode
  static class RepositoryRenamedEvent extends MyEvent {

    private String oldRepository;
    private String newRepository;
    private String username;
    private String userMail;

    private boolean deleted;

    RepositoryRenamedEvent(String permission, String oldRepository, String newRepository, String username, String userMail, boolean deleted) {
      super(RepositoryRenamedEvent.class.getSimpleName(), permission);
      this.oldRepository = oldRepository;
      this.newRepository = newRepository;
      this.username = username;
      this.userMail = userMail;
      this.deleted = deleted;
    }

    RepositoryRenamedEvent(String permission, String oldRepository, String newRepository, String username, String userMail) {
      this(permission, oldRepository, newRepository, username, userMail, false);
    }

    public void setDeleted(boolean deleted) {
      this.deleted = deleted;
    }
  }

}
