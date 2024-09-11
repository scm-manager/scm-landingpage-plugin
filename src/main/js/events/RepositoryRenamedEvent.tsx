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
import { AvatarImage, CardColumnSmall, DateFromNow, Icon } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventExtension, MyEventType } from "../types";
import styled from "styled-components";
import DeletableTitle from "./DeletableTitle";

type RepositoryRenamedEventType = MyEventType & {
  oldRepository: string;
  newRepository: string;
  username: string;
  userMail: string;
  date: Date;
  deleted: boolean;
};

const RightArrowPadding = styled(Icon)`
  padding: 0 0.4rem;
  font-size: 15px;
`;

const StyledGravatar = styled(AvatarImage)`
  width: 2.5rem;
  align-self: center;
`;

const StrikeThroughSpan = styled.span`
  text-decoration: line-through;
`;

const RepositoryRenamedEvent: MyEventComponent<RepositoryRenamedEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");
  const link = event.deleted ? undefined : "/repo/" + event.newRepository;
  const icon = binder.hasExtension("avatar.factory") ? (
    <StyledGravatar person={{ name: event.username, mail: event.userMail }} />
  ) : (
    <Icon name="exchange-alt" className="fa-fw fa-lg" color="inherit" />
  );
  const newRepository = event.deleted ? (
    <StrikeThroughSpan>{event.newRepository}</StrikeThroughSpan>
  ) : (
    event.newRepository
  );
  const content = (
    <div>
      <DeletableTitle deleted={event.deleted} className="is-marginless">{t("scm-landingpage-plugin.myevents.repositoryRenamed.title")}</DeletableTitle>
      <p>
        <StrikeThroughSpan>{event.oldRepository}</StrikeThroughSpan>
        <RightArrowPadding name="arrow-right" />
        {newRepository}
      </p>
    </div>
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
    />
  );

  if (link) {
    return card;
  }

  return <div>{card}</div>;
};

RepositoryRenamedEvent.type = "RepositoryRenamedEvent";

binder.bind<MyEventExtension<RepositoryRenamedEventType>>("landingpage.myevents", RepositoryRenamedEvent);
