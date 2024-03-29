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
import styled from "styled-components";
import { AvatarImage, CardColumnSmall, DateFromNow, Icon } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventExtension, MyRepositoryEventType } from "../types";
import DeletableTitle from "./DeletableTitle";

type RepositoryPushEventType = MyRepositoryEventType & {
  authorName: string;
  authorDisplayName: string;
  authorMail: string;
  changesets: number;
  date: Date;
};

const StyledGravatar = styled(AvatarImage)`
  width: 2.5rem;
  align-self: center;
`;

const RepositoryPushEvent: MyEventComponent<RepositoryPushEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = event.deleted ? undefined : "/repo/" + event.repository + "/code/changesets/";

  const icon = binder.hasExtension("avatar.factory") ? (
    <StyledGravatar person={{ name: event.authorName, mail: event.authorMail }} />
  ) : (
    <Icon name="exchange-alt" className="fa-fw fa-lg" color="inherit" />
  );

  const content = (
    <DeletableTitle deleted={event.deleted} className="is-marginless">
      {t("scm-landingpage-plugin.myevents.repositoryPush.title", {
        count: event.changesets,
        repository: event.repository
      })}
    </DeletableTitle>
  );
  const footerLeft = (
    <>
      {t("scm-landingpage-plugin.myevents.repositoryPush.description")} {event.authorDisplayName}
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

RepositoryPushEvent.type = "PushEvent";

binder.bind<MyEventExtension<RepositoryPushEventType>>("landingpage.myevents", RepositoryPushEvent);
