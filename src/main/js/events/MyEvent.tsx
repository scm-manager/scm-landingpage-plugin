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
import React, { FC } from "react";
import { binder } from "@scm-manager/ui-extensions";
import { Link } from "@scm-manager/ui-types";
import { MyEventComponent, MyEventType } from "../types";
import { Link as ReactLink } from "react-router-dom";
import styled from "styled-components";

type Props = {
  event: MyEventType;
};

const StyledLink = styled(ReactLink)`
  color: inherit;
  :hover {
    color: #33b2e8 !important;
  }
`;

const MyEvent: FC<Props> = ({ event }) => {
  const extensions: MyEventComponent[] = binder.getExtensions("landingpage.myevents");

  let Component = null;
  for (let extension of extensions) {
    if (extension.type === event.type) {
      Component = extension;
      break;
    }
  }

  if (!Component) {
    return null;
  }

  return (
    <>
      <StyledLink to={(event?._links?.self as Link)?.href}>
        <Component event={event} />
      </StyledLink>
    </>
  );
};

export default MyEvent;
