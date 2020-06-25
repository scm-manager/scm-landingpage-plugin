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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.shiro.SecurityUtils;
import sonia.scm.EagerSingleton;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.RepositoryPermissions;
import sonia.scm.user.User;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Extension
@EagerSingleton
public class RepositoryRenamedEventSubscriber {

  private final MyEventStore store;

  @Inject
  public RepositoryRenamedEventSubscriber(MyEventStore store) {
    this.store = store;
  }

  @Subscribe
  public void handleEvent(sonia.scm.repository.RepositoryRenamedEvent event) {
    String oldRepository = event.getOldItem().getNamespace() + "/" + event.getOldItem().getName();
    String newRepository = event.getItem().getNamespace() + "/" + event.getItem().getName();
    User user = SecurityUtils.getSubject().getPrincipals().oneByType(User.class);

    store.add(new RepositoryRenamedEvent(RepositoryPermissions.read(event.getItem()).asShiroString(), oldRepository, newRepository, user.getName(), user.getMail()));
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  @Getter
  @NoArgsConstructor
  static class RepositoryRenamedEvent extends MyEvent {

    private String oldRepository;
    private String newRepository;
    private String username;
    private String userMail;

    RepositoryRenamedEvent(String permission, String oldRepository, String newRepository, String username, String userMail) {
      super(RepositoryRenamedEvent.class.getSimpleName(), permission);
      this.oldRepository = oldRepository;
      this.newRepository = newRepository;
      this.username = username;
      this.userMail = userMail;
    }
  }

}
