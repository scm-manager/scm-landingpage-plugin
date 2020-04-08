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
import React from "react";
import { useTranslation } from "react-i18next";
import { CardColumnSmall, Icon, DateFromNow } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventType } from "../types";

type PluginEventType = MyEventType & {
  pluginName: string;
  newPluginVersion: string;
  date: Date;
};

const PluginInstallationFailedEvent: MyEventComponent<PluginEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = "/admin/plugins/available/";
  const icon = <Icon name="puzzle-piece fa-fw fa-2x" color="inherit" />;
  const content = (
    <>
      {" "}
      <strong className="is-marginless">
        {t("scm-landingpage-plugin.myevents.pluginInstallationFailed.title", { pluginName: event.pluginName })}
      </strong>
    </>
  );
  const footer = t("scm-landingpage-plugin.myevents.pluginInstallationFailed.description", {
    version: event.newPluginVersion
  });

  return (
    <CardColumnSmall
      link={link}
      avatar={icon}
      contentLeft={content}
      contentRight={<DateFromNow date={event.date} />}
      footer={footer}
    />
  );
};

PluginInstallationFailedEvent.type = "PluginInstallationFailedEvent";

binder.bind("landingpage.myevents", PluginInstallationFailedEvent);
