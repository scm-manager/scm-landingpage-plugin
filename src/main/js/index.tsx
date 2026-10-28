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
import { Repository } from "@scm-manager/ui-types";
import { ConfigurationBinder } from "@scm-manager/ui-components";
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

const ConfigRoute = () => ({
  path: "/landingPageConfig",
  element: <ConfigPage />,
});

const LargeToggleIcon: FC<{ repository: Repository }> = (props) => (
  <FavoriteRepositoryToggleIcon repository={props.repository} classes="fa-2x" />
);

const Subtitle: FC = () => {
  const [t] = useTranslation("plugins");
  return t("scm-landingpage-plugin.subtitle");
};

const Title: FC = () => {
  const [t] = useTranslation("plugins");
  const index = useIndex();
  const instanceName = (index._embedded?.landingpageConfiguration as LandingpageConfiguration)?.instanceName;

  return (
    <>
      {instanceName ?? t("scm-landingpage-plugin.title")}
      <RelativeLink to="/landingPageConfig" aria-label={t("scm-landingpage-plugin.config.title")}>
        <AbsoluteIcon className="fa fa-cog fa-fw is-size-5 ml-2 has-text-is-link" />
      </RelativeLink>
    </>
  );
};

const DocumentTitle: FC = () => {
  const index = useIndex();

  useEffect(() => {
    const instanceName = (index._embedded?.landingpageConfiguration as LandingpageConfiguration)?.instanceName;
    binder.bind<extensionPoints.DocumentTitleExtensionPoint>("document.title", { documentTitle: instanceName });
  }, [index]);

  return null;
};

// We have to use this in order to render our component on every page
binder.bind("footer.information", DocumentTitle);

binder.bind("repository.card.beforeTitle", BeforeTitle);
binder.bind("repository.afterTitle", LargeToggleIcon);
binder.bind<extensionPoints.RepositoryOverviewTop>(
  "repository.overview.top",
  MyFavoriteRepositories,
  () => !useIsCategoryDisabled("favoriteRepository"),
);
binder.bind<extensionPoints.RepositoryOverviewLeft>(
  "repository.overview.left",
  MyTips,
  () => !useIsCategoryDisabled("mytips"),
  {
    priority: 2000,
  },
);
binder.bind<extensionPoints.RepositoryOverviewLeft>(
  "repository.overview.left",
  MyTasks,
  () => !useIsCategoryDisabled("mytasks"),
  {
    priority: 1000,
  },
);
binder.bind<extensionPoints.RepositoryOverviewLeft>("repository.overview.left", MyData, {
  priority: 100,
  predicate: () => binder.hasExtension<MyDataExtension>("landingpage.mydata"),
});
binder.bind<extensionPoints.RepositoryOverviewLeft>(
  "repository.overview.left",
  MyEvents,
  () => !useIsCategoryDisabled("myevents"),
  {
    priority: 10,
  },
);
binder.bind("main.route", ConfigRoute);
binder.bind("repository.overview.title", Title);
binder.bind("repository.overview.subtitle", Subtitle);
binder.bind<extensionPoints.RepositoryOverviewListOptionsExtensionPoint>("repository.overview.listOptions", () =>
  useListOptions(),
);

ConfigurationBinder.bindAdminSetting(
  "/landingpageConfig",
  "scm-landingpage-plugin.globalConfig.link",
  "landingpageConfig",
  GlobalConfigPage,
);

export { MyDataExtension, MyEventExtension, MyTaskExtension } from "./types";
