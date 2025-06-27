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

import com.cloudogu.scm.landingpage.config.Context;
import com.cloudogu.scm.landingpage.config.LandingpageConfig;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import lombok.extern.slf4j.Slf4j;
import sonia.scm.plugin.Extension;
import sonia.scm.repository.spi.SyncAsyncExecutorProvider;
import sonia.scm.schedule.Scheduler;
import sonia.scm.schedule.Task;
import sonia.scm.store.ConfigurationStore;
import sonia.scm.store.ConfigurationStoreFactory;

@Extension
@Singleton
@Slf4j
public class Cleanup implements ServletContextListener {

  private final Scheduler scheduler;
  private final CleanupTask cleanupTask;
  private final ConfigurationStore<LandingpageConfig> globalConfigStore;
  private final SyncAsyncExecutorProvider syncAsyncExecutorProvider;

  private Task currentTask;

  @Inject
  public Cleanup(Scheduler scheduler,
                 ConfigurationStoreFactory configurationStoreFactory,
                 MyEventStore myEventStore, SyncAsyncExecutorProvider syncAsyncExecutorProvider) {
    this.scheduler = scheduler;
    this.cleanupTask = new CleanupTask(myEventStore);
    this.syncAsyncExecutorProvider = syncAsyncExecutorProvider;
    this.globalConfigStore = configurationStoreFactory
      .withType(LandingpageConfig.class)
      .withName(Context.STORE_NAME)
      .build();
    reSchedule();
  }

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    log.debug("Cleanup myevents on startup");
    syncAsyncExecutorProvider.createExecutorWithDefaultTimeout()
      .execute(cleanupTask);
  }

  /**
   * This method triggers a rescheduling of the cleanup task.
   * This method should always be called, if the cron expression for the cleanup is changed and at the startup of the application.
   */
  public void reSchedule() {
    if (currentTask != null) {
      currentTask.cancel();
    }

    LandingpageConfig config = globalConfigStore
      .getOptional()
      .orElseGet(LandingpageConfig::new);

    this.currentTask = scheduler.schedule(config.getMyEventsCleanupExpression(), cleanupTask);
    log.debug("rescheduled cleanup task for myevents with expression {} and store size {}",
      config.getMyEventsCleanupExpression(),
      config.getMyEventsStoreSize()
    );
  }

  static class CleanupTask implements Runnable {

    private final MyEventStore myEventStore;

    CleanupTask(MyEventStore myEventStore) {
      this.myEventStore = myEventStore;
    }

    @Override
    public void run() {
      myEventStore.cleanup();
    }
  }
}
