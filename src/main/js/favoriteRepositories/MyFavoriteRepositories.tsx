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

import React, { FC } from "react";
import { useFavoriteRepositories } from "./favoriteRepository";
import { ErrorNotification, GroupEntries, Loading, Notification, RepositoryEntry } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";
import classNames from "classnames";
import { extensionPoints } from "@scm-manager/ui-extensions";
import { useLocalStorage } from "@scm-manager/ui-api";

const MyFavoriteRepositories: FC<extensionPoints.RepositoryOverviewTopExtension["props"]> = ({
  page,
  namespace,
  search
}) => {
  const { data, error, isLoading } = useFavoriteRepositories();
  const [t] = useTranslation("plugins");
  const [collapsed, setCollapsed] = useLocalStorage<boolean | null>(`favoriteRepositories.collapsed`, null);

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

  const entries = data!.repositories.map((repository) => (
    <RepositoryEntry repository={repository} key={repository.name} />
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
