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

import React, { FC } from "react";
import { FavoriteRepository, useFavoriteRepositories } from "./favoriteRepository";
import { ErrorNotification, GroupEntries, Loading, Notification, RepositoryEntry } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import classNames from "classnames";
import { extensionPoints } from "@scm-manager/ui-extensions";
import { useLocalStorage } from "@scm-manager/ui-api";
import { ShowNamespaceInFavoriteOption, useListOptions } from "../config/hooks";
import { Repository } from "@scm-manager/ui-types";

const MyFavoriteRepositories: FC<extensionPoints.RepositoryOverviewTopExtension["props"]> = ({
  page,
  namespace,
  search
}) => {
  const { data, error, isLoading } = useFavoriteRepositories();
  const [t] = useTranslation("plugins");
  const [collapsed, setCollapsed] = useLocalStorage<boolean | null>("favoriteRepositories.collapsed", null);
  const { showNamespace } = useListOptions();

  if (page !== 1 || search || namespace) {
    return null;
  }

  let content;

  if (error) {
    content = <ErrorNotification error={error} />;
  } else if (isLoading) {
    content = <Loading />;
  } else if (data?.repositories?.length === 0) {
    content = <Notification type="info">{t("scm-landingpage-plugin.favoriteRepository.noData")}</Notification>;
  }

  if (content) {
    const header = (
      <div className={classNames("is-flex", "is-align-items-center", "is-size-6", "has-text-weight-bold", "p-3")}>
        {t("scm-landingpage-plugin.favoriteRepository.title")}
      </div>
    );

    return (
      <>
        {header}
        {content}
      </>
    );
  }

  const alwaysShowNamespaces = showNamespace === "ALWAYS";
  const showNamespacesForDuplicates = showNamespace === "DUPLICATES_ONLY";

  const allRepositoryNames = data!.repositories.map(repo => repo.name);
  const duplicateRepositoryNames = allRepositoryNames.filter(
    (item, index) => allRepositoryNames.indexOf(item) !== index
  );

  const showNamespaceForRepository = (repository: Repository) => {
    return (
      alwaysShowNamespaces || (showNamespacesForDuplicates && duplicateRepositoryNames.indexOf(repository.name) >= 0)
    );
  };

  const entries = data!.repositories.map(repository => (
    <RepositoryEntry
      repository={{ ...repository, showNamespace: showNamespaceForRepository(repository) } as FavoriteRepository}
      key={`${repository.namespace}/${repository.name}`}
    />
  ));
  return (
    <GroupEntries
      namespaceHeader={t("scm-landingpage-plugin.favoriteRepository.title")}
      elements={entries}
      collapsed={collapsed}
      onCollapsedChange={setCollapsed}
    />
  );
};

export default MyFavoriteRepositories;
