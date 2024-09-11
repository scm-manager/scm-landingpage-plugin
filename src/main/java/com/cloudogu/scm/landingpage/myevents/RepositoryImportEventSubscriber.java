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
import sonia.scm.plugin.Extension;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class RepositoryImportEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryImportEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(sonia.scm.repository.RepositoryImportEvent event) {
    Repository eventRepo = event.getItem();
    String permission = RepositoryPermissions.read(event.getItem()).asShiroString();
    String repository = eventRepo.getNamespace() + "/" + eventRepo.getName();
    User creator = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);

    store.add(new RepositoryImportEvent(permission, repository, creator.getName(), creator.getDisplayName(), creator.getMail(), event.isFailed(), event.getLogId()));
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class RepositoryImportEvent extends MyRepositoryEvent {
    private String creatorName;
    private String creatorDisplayName;
    private String creatorMail;
    private boolean failed;
    private String logId;

    RepositoryImportEvent(String permission, String repository, String creatorName, String creatorDisplayName, String creatorMail, boolean failed, String logId) {
      super(RepositoryImportEvent.class.getSimpleName(), permission, repository);
      this.creatorName = creatorName;
      this.creatorDisplayName = creatorDisplayName;
      this.creatorMail = creatorMail;
      this.failed = failed;
      this.logId = logId;
    }
  }
}
