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

type PluginCenterEventType = MyEventType & {
  date: Date;
};

const FlexFullHeight = styled.div`
  flex-direction: column;
  justify-content: space-around;
  align-self: stretch;
`;

const ContentLeft = styled.div`
  margin-bottom: 0 !important;
  overflow: hidden;
`;

const ContentRight = styled.div`
  margin-left: auto;
  align-items: start;
`;

const CenteredItems = styled.div`
  align-items: center;
`;

const Icon = styled.i`
  width: 2.5rem;
  margin-right: 0.5rem;
  align-self: center;
`;

const StyledLink = styled(ReactLink)`
  color: inherit;
  :hover {
    color: #33b2e8 !important;
  }
`;

const PluginCenterNotAvailableEvent: MyEventComponent<PluginCenterEventType> = ({ event }) => {
  const [t] = useTranslation("plugins");

  const link = "/admin/plugins/available/";

  return (
    <StyledLink to={link}>
      <div className={"media"}>
        <Icon className="fas fa-puzzle-piece fa-2x media-left" />
        <FlexFullHeight className={classNames("media-content", "text-box", "is-flex")}>
          <CenteredItems className="is-flex">
            <ContentLeft className="content">
              <strong className="is-marginless">
                {t("scm-landingpage-plugin.myevents.pluginCenterNotAvailable.title")}
              </strong>
            </ContentLeft>
            <ContentRight>
              <DateFromNow date={event.date} />
            </ContentRight>
          </CenteredItems>
        </FlexFullHeight>
      </div>
    </StyledLink>
  );
};

PluginCenterNotAvailableEvent.type = "PluginCenterNotAvailableEvent";

binder.bind("landingpage.myevents", PluginCenterNotAvailableEvent);
