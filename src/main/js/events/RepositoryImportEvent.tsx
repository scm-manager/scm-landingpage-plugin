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
import styled from "styled-components";
import { AvatarImage, CardColumnSmall, DateFromNow, Icon } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventExtension, MyRepositoryEventType } from "../types";
import { Link } from "react-router-dom";
import DeletableTitle from "./DeletableTitle";

type RepositoryImportEventType = MyRepositoryEventType & {
  creatorName: string;
  creatorDisplayName: string;
  creatorMail: string;
  date: Date;
  failed: boolean;
  logId?: string;
};

const StyledGravatar = styled(AvatarImage)`
  width: 2.5rem;
  align-self: center;
`;

const RepositoryImportEvent: MyEventComponent<RepositoryImportEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = event.deleted ? undefined : "/repo/" + event.repository;

  const icon = binder.hasExtension("avatar.factory") ? (
    <StyledGravatar person={{ name: event.creatorName, mail: event.creatorMail }} />
  ) : (
    <Icon name="exchange-alt" className="fa-fw fa-lg" color="inherit" />
  );

  const content = (
    <DeletableTitle deleted={event.deleted} className="is-marginless">
      {t(
        event.failed
          ? "scm-landingpage-plugin.myevents.repositoryImport.titleFailed"
          : "scm-landingpage-plugin.myevents.repositoryImport.titleSuccess",
        {
          repository: event.repository
        }
      )}
    </DeletableTitle>
  );

  let logLink;
  if (event.logId) {
    logLink = (
      <>
        {" ("}
        <Link to={`/importlog/${event.logId}`}>{t("scm-landingpage-plugin.myevents.repositoryImport.logLink")}</Link>)
      </>
    );
  }

  const footerLeft = (
    <>
      {t("scm-landingpage-plugin.myevents.repositoryImport.description")} {event.creatorDisplayName}
      {logLink}
    </>
  );

  const card = (
    <CardColumnSmall
      link={link}
      avatar={icon}
      contentLeft={content}
      contentRight={
        <small>
          <DateFromNow date={event.date} />
        </small>
      }
      footer={footerLeft}
    />
  );

  if (link) {
    return card;
  }

  return <div>{card}</div>;
};

RepositoryImportEvent.type = "RepositoryImportEvent";

binder.bind<MyEventExtension<RepositoryImportEventType>>("landingpage.myevents", RepositoryImportEvent);
