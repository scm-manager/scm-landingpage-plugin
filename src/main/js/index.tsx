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
import { binder, extensionPoints } from "@scm-manager/ui-extensions";
import FavoriteRepositoryToggleIcon from "./favoriteRepositories/FavoriteRepositoryToggleIcon";
import React, { FC } from "react";
import "./tasks/PluginUpdateTask";
import "./events/RepositoryPushEvent";
import "./events/RepositoryCreatedEvent";
import "./events/PluginInstalledEvent";
import "./events/PluginInstallationFailedEvent";
import "./events/PluginCenterNotAvailableEvent";
import "./events/RepositoryRenamedEvent";
import "./events/RepositoryImportEvent";
import "./events/HealthCheckFailureEvent";
import "./events/HealthCheckSucceededEvent";
import MyData from "./data/MyData";
import MyTasks from "./tasks/MyTasks";
import MyEvents from "./events/MyEvents";
import MyFavoriteRepositories from "./favoriteRepositories/MyFavoriteRepositories";
import { Repository } from "@scm-manager/ui-types";

const LargeToggleIcon: FC<{ repository: Repository }> = props => (
  <FavoriteRepositoryToggleIcon repository={props.repository} classes="fa-2x" />
);

binder.bind("repository.card.beforeTitle", FavoriteRepositoryToggleIcon);
binder.bind("repository.afterTitle", LargeToggleIcon);
binder.bind<extensionPoints.RepositoryOverviewTopExtensionProps>(
  "repository.overview.top",
  MyFavoriteRepositories,
  ({ page, search, namespace }) => page === 1 && !search && !namespace
);
binder.bind("repository.overview.left", MyTasks, { priority: 1000 });
binder.bind("repository.overview.left", MyEvents, { priority: 10 });
binder.bind("repository.overview.left", MyData, { priority: 100 });
