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

import com.github.legman.Subscribe;
import com.github.sdorra.ssp.PermissionCheck;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.HealthCheckEvent;
import sonia.scm.repository.Repository;
import sonia.scm.repository.RepositoryPermissions;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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
