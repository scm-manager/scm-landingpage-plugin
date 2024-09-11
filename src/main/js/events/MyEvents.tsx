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
import { useTranslation } from "react-i18next";
import CollapsibleContainer from "../CollapsibleContainer";
import { Loading } from "@scm-manager/ui-components";
import MyEvent from "./MyEvent";
import { Link } from "@scm-manager/ui-types";
import { useMyEvents } from "./useMyEvents";
import { useIndexLinks } from "@scm-manager/ui-api";
import ScrollContainer from "../ScrollContainer";
import { useCollapsedState } from "../config/hooks";

const MyEvents: FC = () => {
  const [t] = useTranslation("plugins");
  const links = useIndexLinks();
  const { data, error, isLoading } = useMyEvents((links?.landingpageEvents as Link)?.href);
  const [collapsed, setCollapsed] = useCollapsedState("myevents");

  if (isLoading) {
    return <Loading />;
  }

  return (
    <CollapsibleContainer
      title={t("scm-landingpage-plugin.myevents.title")}
      separatedEntries={false}
      emptyMessage={t("scm-landingpage-plugin.myevents.noData")}
      count={data?._embedded?.events?.length}
      initiallyCollapsed={collapsed}
      onCollapseToggle={setCollapsed}
      contentWrapper={ScrollContainer}
      error={error}
      category="myevents"
    >
      {data?._embedded?.events?.map((event, index) => (
        <MyEvent key={index} event={event} />
      ))}
    </CollapsibleContainer>
  );
};

export default MyEvents;
