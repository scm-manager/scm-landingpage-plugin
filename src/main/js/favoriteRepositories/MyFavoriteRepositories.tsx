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
import { extensionPoints } from "@scm-manager/ui-extensions";
import { useTranslation } from "react-i18next";
import classNames from "classnames";

const MyFavoriteRepositories: FC<extensionPoints.RepositoryOverviewTopExtensionProps> = ({
  page,
  search,
  namespace
}) => {
  const { data, error, isLoading } = useFavoriteRepositories();
  const [t] = useTranslation("plugins");

  if (page > 1 || !!search || !!namespace) {
    return null;
  }

  const header = (
    <div className={classNames("is-flex", "is-align-items-center", "is-size-6", "has-text-weight-bold", "p-3")}>
      {t("scm-landingpage-plugin.favoriteRepository.title")}
    </div>
  );

  if (error) {
    return (
      <>
        {header}
        <ErrorNotification error={error} />
      </>
    );
  }

  if (isLoading) {
    return (
      <>
        {header}
        <Loading />
      </>
    );
  }

  if (!data || !data.repositories || data.repositories.length === 0) {
    return (
      <>
        {header}
        <Notification type="info">{t("scm-landingpage-plugin.favoriteRepository.noData")}</Notification>
      </>
    );
  }

  const entries = data.repositories.map((repository, index) => <RepositoryEntry repository={repository} key={index} />);
  return <GroupEntries namespaceHeader={t("scm-landingpage-plugin.favoriteRepository.title")} elements={entries} />;
};

export default MyFavoriteRepositories;
