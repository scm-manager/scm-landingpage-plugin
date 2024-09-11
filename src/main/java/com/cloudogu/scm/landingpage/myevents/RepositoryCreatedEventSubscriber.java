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
public class RepositoryCreatedEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryCreatedEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(RepositoryEvent event) {
    if (event.getEventType() == HandlerEventType.CREATE) {
      Repository eventRepo = event.getItem();
      String permission = RepositoryPermissions.read(event.getItem()).asShiroString();
      String repository = eventRepo.getNamespace() + "/" + eventRepo.getName();
      User creator = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);

      store.add(new RepositoryCreatedEvent(permission, repository, creator.getName(), creator.getDisplayName(), creator.getMail()));
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class RepositoryCreatedEvent extends MyRepositoryEvent {

    private String creatorName;
    private String creatorDisplayName;
    private String creatorMail;

    RepositoryCreatedEvent(String permission, String repository, String creatorName, String creatorDisplayName, String creatorMail) {
      super(RepositoryCreatedEvent.class.getSimpleName(), permission, repository);
      this.creatorName = creatorName;
      this.creatorDisplayName = creatorDisplayName;
      this.creatorMail = creatorMail;
    }
  }
}
