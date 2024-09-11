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
import { useTranslation } from "react-i18next";
import { CardColumnSmall, Icon, DateFromNow } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventType } from "../types";

type PluginEventType = MyEventType & {
  pluginName: string;
  previousPluginVersion?: string;
  newPluginVersion: string;
  date: Date;
};

const PluginInstalledEvent: MyEventComponent<PluginEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = "/admin/plugins/available/";
  const icon = <Icon name="puzzle-piece" className="fa-fw fa-lg" color="inherit" />;
  const content = (
    <strong className="is-marginless">
      {event.previousPluginVersion
        ? t("scm-landingpage-plugin.myevents.pluginUpdated.title", { pluginName: event.pluginName })
        : t("scm-landingpage-plugin.myevents.pluginInstalled.title", { pluginName: event.pluginName })}
    </strong>
  );
  const footer = event.previousPluginVersion
    ? t("scm-landingpage-plugin.myevents.pluginUpdated.description", {
        version: event.newPluginVersion,
        previousVersion: event.previousPluginVersion
      })
    : t("scm-landingpage-plugin.myevents.pluginInstalled.description", {
        version: event.newPluginVersion
      });

  return (
    <CardColumnSmall
      link={link}
      avatar={icon}
      contentLeft={content}
      contentRight={
        <small>
          <DateFromNow date={event.date} />
        </small>
      }
      footer={footer}
    />
  );
};

PluginInstalledEvent.type = "PluginInstalledEvent";

binder.bind("landingpage.myevents", PluginInstalledEvent);
