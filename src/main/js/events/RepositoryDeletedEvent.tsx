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
import { MyEventComponent, MyEventExtension, MyEventType } from "../types";

type RepositoryDeletedEventType = MyEventType & {
  repository: string;
  deleterName: string;
  deleterDisplayName: string;
  deleterMail: string;
  date: Date;
};

const StyledGravatar = styled(AvatarImage)`
  width: 2.5rem;
  align-self: center;
`;

const RepositoryDeletedEvent: MyEventComponent<RepositoryDeletedEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const icon = binder.hasExtension("avatar.factory") ? (
    <StyledGravatar person={{ name: event.deleterName, mail: event.deleterMail }} />
  ) : (
    <Icon name="exchange-alt" className="fa-fw fa-lg" color="inherit" />
  );

  const content = (
    <strong className="is-marginless">
      {t("scm-landingpage-plugin.myevents.repositoryDeleted.title", {
        repository: event.repository
      })}
    </strong>
  );
  const footerLeft = (
    <>
      {t("scm-landingpage-plugin.myevents.repositoryDeleted.description")} {event.deleterDisplayName}
    </>
  );

  return (
    <div>
      <CardColumnSmall
        avatar={icon}
        contentLeft={content}
        contentRight={
          <small>
            <DateFromNow date={event.date} />
          </small>
        }
        footer={footerLeft}
      />
    </div>
  );
};

RepositoryDeletedEvent.type = "RepositoryDeletedEvent";

binder.bind<MyEventExtension<RepositoryDeletedEventType>>("landingpage.myevents", RepositoryDeletedEvent);
