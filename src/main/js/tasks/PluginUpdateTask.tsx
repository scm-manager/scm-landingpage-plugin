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

import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import { MyTaskComponent, MyTaskType } from "../types";
import { useTranslation } from "react-i18next";
import { CardColumnSmall, Icon } from "@scm-manager/ui-components";

type PluginUpdateTaskType = MyTaskType & {
  outdatedPlugins: number;
};

const PluginUpdateTask: MyTaskComponent<PluginUpdateTaskType> = ({ task }) => {
  const [t] = useTranslation("plugins");

  const link = "/admin/plugins/installed/";

  return (
    <CardColumnSmall
      link={link}
      avatar={<Icon name="puzzle-piece" className="fa-lg fa-fw" color="inherit" />}
      contentLeft={<strong className="is-marginless">{t("scm-landingpage-plugin.tasks.updatePlugin.title")}</strong>}
      contentRight={""}
      footer={t("scm-landingpage-plugin.tasks.updatePlugin.description", { count: task.outdatedPlugins })}
    />
  );
};

PluginUpdateTask.type = "PluginUpdateTask";

binder.bind("landingpage.mytask", PluginUpdateTask);
