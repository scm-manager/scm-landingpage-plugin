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
import { CardColumnSmall, Icon, DateFromNow, AvatarImage } from "@scm-manager/ui-components";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventComponent, MyEventType } from "../types";
import { Link } from "react-router-dom";

type RepositoryImportEventType = MyEventType & {
  repository: string;
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

  const link = "/repo/" + event.repository;

  const icon = binder.hasExtension("avatar.factory") ? (
    <StyledGravatar person={{ name: event.creatorName, mail: event.creatorMail }} />
  ) : (
    <Icon name="exchange-alt fa-fw fa-lg mt-5" color="inherit" />
  );

  const content = (
    <strong className="is-marginless">
      {t(
        event.failed
          ? "scm-landingpage-plugin.myevents.repositoryImport.titleFailed"
          : "scm-landingpage-plugin.myevents.repositoryImport.titleSuccess",
        {
          repository: event.repository
        }
      )}
    </strong>
  );

  let logLink;
  if (event.logId) {
    logLink = (
      <>
        {" ("}
        <Link to={`/importlog/${event.logId}`}>
          {t("scm-landingpage-plugin.myevents.repositoryImport.logLink")}
        </Link>
        )
      </>
    );
  }

  const footerLeft = (
    <>
      {t("scm-landingpage-plugin.myevents.repositoryImport.description")}{" "}
      <span className="has-text-info">{event.creatorDisplayName}</span>
      {logLink}
    </>
  );

  return (
    <CardColumnSmall
      link={link}
      avatar={icon}
      contentLeft={content}
      contentRight={<small><DateFromNow date={event.date} /></small>}
      footer={footerLeft}
    />
  );
};

RepositoryImportEvent.type = "RepositoryImportEvent";

binder.bind("landingpage.myevents", RepositoryImportEvent);
