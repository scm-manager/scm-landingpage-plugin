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
