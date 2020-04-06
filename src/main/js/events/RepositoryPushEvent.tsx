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
import { CardColumnSmall, AvatarImage } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventType } from "../types";

type RepositoryPushEventType = MyEventType & {
  authorName: string;
  authorDisplayName: string;
  authorMail: string;
  repository: string;
  changesets: number;
  date: Date;
};

const Icon = styled.i`
  width: 2.5rem;
  font-size: 40px;
  margin-right: 0.5rem;
  align-self: center;
`;

const StyledGravatar = styled(AvatarImage)`
  width: 2.5rem;
  align-self: center;
  margin-right: 0.5rem;
`;

const RepositoryPushEvent: MyEventComponent<RepositoryPushEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = "/repo/" + event.repository;

  const icon = binder.hasExtension("avatar.factory") ? (
    <StyledGravatar person={{ name: event.authorName, mail: event.authorMail }} />
  ) : (
    <Icon className="fas fa-square media-left" />
  );

  const content = (
    <strong className="is-marginless">
      {t("scm-landingpage-plugin.myevents.repositoryPush.title", {
        count: event.changesets,
        repository: event.repository
      })}
    </strong>
  );
  const footerLeft = (
    <>
      {t("scm-landingpage-plugin.myevents.repositoryPush.description")}{" "}
      <span className="has-text-info">{event.authorDisplayName}</span>
    </>
  );

  return <CardColumnSmall link={link} icon={icon} contentLeft={content} contentRight={event.date} footer={footerLeft} />;
};

RepositoryPushEvent.type = "PushEvent";

binder.bind("landingpage.myevents", RepositoryPushEvent);
