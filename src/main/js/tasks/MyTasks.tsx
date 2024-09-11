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
import CollapsibleContainer from "../CollapsibleContainer";
import { useTranslation } from "react-i18next";
import { Loading } from "@scm-manager/ui-components";
import MyTask from "./MyTask";
import { Link } from "@scm-manager/ui-types";
import { useMyTasks } from "./useMyTasks";
import { useIndexLinks } from "@scm-manager/ui-api";
import { useCollapsedState } from "../config/hooks";

const MyTasks: FC = () => {
  const [t] = useTranslation("plugins");
  const links = useIndexLinks();
  const { data, error, isLoading } = useMyTasks((links?.landingpageTasks as Link)?.href);
  const [collapsed, setCollapsed] = useCollapsedState("mytasks");

  if (isLoading) {
    return <Loading />;
  }

  return (
    <CollapsibleContainer
      title={t("scm-landingpage-plugin.mytasks.title")}
      separatedEntries={false}
      emptyMessage={t("scm-landingpage-plugin.mytasks.noData")}
      count={data?._embedded?.tasks?.length}
      initiallyCollapsed={collapsed}
      onCollapseToggle={setCollapsed}
      error={error}
      category="mytasks"
    >
      {data?._embedded?.tasks.map(task => (
        <MyTask key={task.type} task={task} />
      ))}
    </CollapsibleContainer>
  );
};

export default MyTasks;
