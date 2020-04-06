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
import React, { FC, ReactElement, useEffect, useState } from "react";
import CollapsibleContainer from "../CollapsibleContainer";
import { useTranslation } from "react-i18next";
import { apiClient, ErrorNotification, Loading, Notification } from "@scm-manager/ui-components";
import MyTask from "./MyTask";
import { MyTasksType } from "../types";
import { Link, Links } from "@scm-manager/ui-types";

type Props = {
  links: Links;
};

const MyTasks: FC<Props> = ({ links }) => {
  const [t] = useTranslation("plugins");
  const [content, setContent] = useState<MyTasksType>({ _embedded: { tasks: [] } });
  const [error, setError] = useState(undefined);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    apiClient
      .get((links?.landingpageTasks as Link)?.href)
      .then(r => r.json())
      .then(setContent)
      .then(() => setLoading(false))
      .catch(setError);
  }, []);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <ErrorNotification error={error} />;
  }

  let view: ReactElement | ReactElement[];

  if (content?._embedded?.tasks?.length == 0) {
    view = <Notification type={"info"}>{t("scm-landingpage-plugin.mytasks.noData")}</Notification>;
  } else {
    view = content?._embedded?.tasks.map((task, key) => <MyTask key={key} task={task} />);
  }

  return (
    <CollapsibleContainer title={t("scm-landingpage-plugin.mytasks.title")} separatedEntries={false}>
      {view}
    </CollapsibleContainer>
  );
};

export default MyTasks;
