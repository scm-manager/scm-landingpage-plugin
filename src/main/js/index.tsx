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

import { binder, extensionPoints } from "@scm-manager/ui-extensions";
import FavoriteRepositoryToggleIcon from "./favoriteRepositories/FavoriteRepositoryToggleIcon";
import React, { FC, useEffect } from "react";
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
import { ConfigurationBinder, ProtectedRoute } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import ConfigPage from "./config/ConfigPage";
import styled from "styled-components";
import MyTips from "./tips/MyTips";
import { useIsCategoryDisabled, useListOptions } from "./config/hooks";
import { LandingpageConfiguration, MyDataExtension } from "./types";
import { useIndex } from "@scm-manager/ui-api";
import GlobalConfigPage from "./config/GlobalConfigPage";
import BeforeTitle from "./favoriteRepositories/BeforeTitle";

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
  const index = useIndex();

  return (
    <>
      {index.isLoading || index.error
        ? t("scm-landingpage-plugin.title")
        : (index?.data?._embedded?.landingpageConfiguration as LandingpageConfiguration)?.instanceName}
      <RelativeLink to="/landingPageConfig" aria-label={t("scm-landingpage-plugin.config.title")}>
        <AbsoluteIcon className="fa fa-cog fa-fw is-size-5 ml-2 has-text-is-link" />
      </RelativeLink>
    </>
  );
};

const DocumentTitle: FC = () => {
  const index = useIndex();

  useEffect(() => {
    const instanceName = (index?.data?._embedded?.landingpageConfiguration as LandingpageConfiguration)?.instanceName;
    binder.bind<extensionPoints.DocumentTitleExtensionPoint>("document.title", { documentTitle: instanceName });
  }, [index]);

  return null;
};

// We have to use this in order to render our component on every page
binder.bind("footer.information", DocumentTitle);

binder.bind("repository.card.beforeTitle", BeforeTitle);
binder.bind("repository.afterTitle", LargeToggleIcon);
binder.bind<extensionPoints.RepositoryOverviewTopExtension>(
  "repository.overview.top",
  MyFavoriteRepositories,
  () => !useIsCategoryDisabled("favoriteRepository")
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
  predicate: () => binder.hasExtension<MyDataExtension>("landingpage.mydata")
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

ConfigurationBinder.bindGlobal(
  "/landingpageConfig",
  "scm-landingpage-plugin.globalConfig.link",
  "landingpageConfig",
  GlobalConfigPage
);

export { MyDataExtension, MyEventExtension, MyTaskExtension } from "./types";
