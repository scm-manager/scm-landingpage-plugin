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
import "./events/RepositoryDeletedEvent";
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
import { Links, Repository } from "@scm-manager/ui-types";
import { ProtectedRoute } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import ConfigPage from "./config/ConfigPage";
import styled from "styled-components";
import MyTips from "./tips/MyTips";
import { useIsCategoryDisabled, useListOptions } from "./config/hooks";

const RelativeLink = styled(Link)`
  position: relative;
`;

const AbsoluteIcon = styled.i`
  position: absolute;
  top: 11px;
`;

type ConfigRouteProps = {
  authenticated: boolean;
  links: Links;
};

const ConfigRoute: FC<ConfigRouteProps> = props => {
  return (
    <ProtectedRoute authenticated={props.authenticated} path={"/landingPageConfig"}>
      <ConfigPage />
    </ProtectedRoute>
  );
};

const LargeToggleIcon: FC<{ repository: Repository }> = props => (
  <FavoriteRepositoryToggleIcon repository={props.repository} classes="fa-2x" />
);

const Subtitle: FC = () => {
  const [t] = useTranslation("plugins");
  return t("scm-landingpage-plugin.subtitle");
};

const Title: FC = () => {
  const [t] = useTranslation("plugins");
  return (
    <>
      {t("scm-landingpage-plugin.title")}
      <RelativeLink to="/landingPageConfig" aria-label={t("scm-landingpage-plugin.config.title")}>
        <AbsoluteIcon className="fa fa-cog fa-fw is-size-5 ml-2 has-text-is-link" />
      </RelativeLink>
    </>
  );
};

binder.bind("repository.card.beforeTitle", FavoriteRepositoryToggleIcon);
binder.bind("repository.afterTitle", LargeToggleIcon);
binder.bind<extensionPoints.RepositoryOverviewTopExtension>(
  "repository.overview.top",
  MyFavoriteRepositories,
  ({ page, search, namespace }) => page === 1 && !search && !namespace && !useIsCategoryDisabled("favoriteRepository")
);
binder.bind<extensionPoints.RepositoryOverviewLeftExtension>(
  "repository.overview.left",
  MyTips,
  () => !useIsCategoryDisabled("mytips"),
  {
    priority: 2000
  }
);
binder.bind<extensionPoints.RepositoryOverviewLeftExtension>(
  "repository.overview.left",
  MyTasks,
  () => !useIsCategoryDisabled("mytasks"),
  {
    priority: 1000
  }
);
binder.bind<extensionPoints.RepositoryOverviewLeftExtension>("repository.overview.left", MyData, {
  priority: 100,
  predicate: () => binder.hasExtension("landingpage.mydata")
});
binder.bind<extensionPoints.RepositoryOverviewLeftExtension>(
  "repository.overview.left",
  MyEvents,
  () => !useIsCategoryDisabled("myevents"),
  {
    priority: 10
  }
);
binder.bind("main.route", ConfigRoute);
binder.bind<extensionPoints.RepositoryOverviewTitleExtension>("repository.overview.title", Title);
binder.bind<extensionPoints.RepositoryOverviewSubtitleExtension>("repository.overview.subtitle", Subtitle);
binder.bind<extensionPoints.RepositoryOverviewListOptionsExtensionPoint>("repository.overview.listOptions", () =>
  useListOptions()
);

export { MyDataExtension, MyEventExtension, MyTaskExtension } from "./types";
