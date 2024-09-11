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

import { MyEventComponent, MyEventsType } from "../types";
import { CardColumnSmall, Icon } from "@scm-manager/ui-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { binder } from "@scm-manager/ui-extensions";

type HealthCheckSucceededEventType = MyEventsType & {
  repository: string;
};

const HealthCheckSucceededEvent: MyEventComponent<HealthCheckSucceededEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");
  const link = "/repo/" + event.repository + "/settings/general";
  const content = (
    <strong className="is-marginless">
      {t("scm-landingpage-plugin.myevents.healthCheckSucceeded.title", { repository: event.repository })}
    </strong>
  );

  return (
    <CardColumnSmall
      link={link}
      avatar={<Icon name="thumbs-up" className="fa-fw fa-lg" color="inherit" />}
      contentLeft={content}
      contentRight={""}
      footer={t("scm-landingpage-plugin.myevents.healthCheckSucceeded.description")}
    />
  );
};

HealthCheckSucceededEvent.type = "HealthCheckSucceededEvent";

binder.bind("landingpage.myevents", HealthCheckSucceededEvent);
