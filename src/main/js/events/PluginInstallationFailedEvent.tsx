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
import { binder } from "@scm-manager/ui-extensions";
import { DateFromNow } from "@scm-manager/ui-components";
import { MyEventComponent, MyEventType } from "../types";
import { useTranslation } from "react-i18next";
import styled from "styled-components";
import classNames from "classnames";
import { Link as ReactLink } from "react-router-dom";
import MyEventEntry from "./MyEventEntry";

type PluginEventType = MyEventType & {
  pluginName: string;
  newPluginVersion: string;
  date: Date;
};

const Icon = styled.i`
  width: 2.5rem;
  margin-right: 0.5rem;
  align-self: center;
`;

const PluginInstallationFailedEvent: MyEventComponent<PluginEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = "/admin/plugins/available/";
  const icon = <Icon className="fas fa-puzzle-piece fa-2x media-left" />;
  const contentLeft = (
    <>
      {" "}
      <strong className="is-marginless">
        {t("scm-landingpage-plugin.myevents.pluginInstallationFailed.title", { pluginName: event.pluginName })}
      </strong>
      <p>
        {t("scm-landingpage-plugin.myevents.pluginInstallationFailed.description", {
          version: event.newPluginVersion
        })}
      </p>
    </>
  );

  return (
   <MyEventEntry link={link} contentLeft={contentLeft} date={event.date} icon={icon} />
  );
};

PluginInstallationFailedEvent.type = "PluginInstallationFailedEvent";

binder.bind("landingpage.myevents", PluginInstallationFailedEvent);