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
