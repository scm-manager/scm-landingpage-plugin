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
import com.github.sdorra.ssp.PermissionCheck;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.HealthCheckEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class HealthCheckEventSubscriber {

  private final MyEventStore store;

  @Inject
  public HealthCheckEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(HealthCheckEvent event) {
    Repository repository = event.getRepository();
    String repositoryString = repository.getNamespace() + "/" + repository.getName();
    PermissionCheck permission = RepositoryPermissions.healthCheck(repository);
    if (event.getCurrentFailures().isEmpty()) {
      store.add(new HealthCheckSucceededEvent(permission, repositoryString));
    } else {
      store.add(new HealthCheckFailureEvent(permission, repositoryString));
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @NoArgsConstructor
  @Getter
  public static class HealthCheckFailureEvent extends MyEvent {
    private String repository;

    public HealthCheckFailureEvent(PermissionCheck permission, String repository) {
      super(HealthCheckFailureEvent.class.getSimpleName(), permission.asShiroString());
      this.repository = repository;
    }
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @NoArgsConstructor
  @Getter
  public static class HealthCheckSucceededEvent extends MyEvent {
    private String repository;

    public HealthCheckSucceededEvent(PermissionCheck permission, String repository) {
      super(HealthCheckSucceededEvent.class.getSimpleName(), permission.asShiroString());
      this.repository = repository;
    }
  }
}
