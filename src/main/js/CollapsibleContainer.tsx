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
import React, { FC, useEffect, useState } from "react";
import styled from "styled-components";
import { devices, ErrorNotification, Icon, Tag } from "@scm-manager/ui-components";
import EmptyMessage from "./EmptyMessage";

type Props = {
  title: string;
  separatedEntries: boolean;
  emptyMessage?: string;
  count?: number;
  initiallyCollapsed?: boolean | null;
  onCollapseToggle?: (collapsed: boolean) => void;
  contentWrapper?: React.ElementType;
  error?: Error | null;
};

const Container = styled.div`
  margin-bottom: 1rem;
  a:not(:last-child) div.media {
    border-bottom: solid 1px #cdcdcd;
  }
`;

const Headline = styled.h3`
  font-size: 1.25rem;
  & small {
    font-size: 0.875rem;
  }
`;

const Content = styled.div`
  margin: 1.25rem 0 0;
`;

const Separator = styled.hr`
  margin: 0.5rem 0;
`;

const CollapsibleContainer: FC<Props> = ({
  title,
  count,
  initiallyCollapsed,
  separatedEntries,
  emptyMessage,
  children,
  onCollapseToggle,
  contentWrapper,
  error
}) => {
  const [collapsed, setCollapsed] = useState<boolean>();

  useEffect(
    () => setCollapsed(initiallyCollapsed ?? window.matchMedia(`(max-width: ${devices.mobile.width}px)`).matches),
    [initiallyCollapsed]
  );

  const icon = collapsed ? "angle-right" : "angle-down";
  let content = null;
  if (error) {
    content = <ErrorNotification error={error} />;
  } else if (!collapsed) {
    const childArray = React.Children.toArray(children);
    if (!childArray || childArray.length === 0) {
      content = <EmptyMessage messageKey={emptyMessage} />;
    } else if (separatedEntries) {
      content = childArray.map(child => <Content className="p-2">{child}</Content>);
    } else {
      if (contentWrapper) {
        content = React.createElement(contentWrapper, null, children);
      } else {
        content = <Content className="box p-2">{children}</Content>;
      }
    }
  }

  let headerSuffix;

  if (!error) {
    headerSuffix = (
      <Tag color="info" className="ml-1">
        <b className="is-size-6">{count || 0}</b>
      </Tag>
    );
  }

  const handleCollapseToggle = () => {
    const newState = !collapsed;
    setCollapsed(newState);
    onCollapseToggle && onCollapseToggle(newState);
  };

  return (
    <Container>
      <div className="has-cursor-pointer" onClick={handleCollapseToggle}>
        <Headline>
          <Icon name={icon} color="default" /> {title} {headerSuffix}
        </Headline>
        <Separator />
      </div>
      {content}
    </Container>
  );
};

export default CollapsibleContainer;
