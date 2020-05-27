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
import { useTranslation } from "react-i18next";
import CollapsibleContainer from "../CollapsibleContainer";
import { apiClient, ErrorNotification, Loading } from "@scm-manager/ui-components";
import MyEvent from "./MyEvent";
import { MyEventsType } from "../types";
import { Link, Links } from "@scm-manager/ui-types";

type Props = {
  links: Links;
};

const MyEvents: FC<Props> = ({ links }) => {
  const [t] = useTranslation("plugins");
  const [content, setContent] = useState<MyEventsType>({ _embedded: { events: [] } });
  const [error, setError] = useState<Error | undefined>(undefined);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    apiClient
      .get((links?.landingpageEvents as Link)?.href)
      .then(r => r.json())
      .then(setContent)
      .then(() => setLoading(false))
      .catch(setError);
  }, []);

  if (error) {
    return <ErrorNotification error={error} />;
  }

  if (loading) {
    return <Loading />;
  }

  return (
    <CollapsibleContainer
      title={t("scm-landingpage-plugin.myevents.title")}
      separatedEntries={false}
      emptyMessage={t("scm-landingpage-plugin.myevents.noData")}
    >
      {content?._embedded?.events?.map((event, index) => (
        <MyEvent key={index} event={event} />
      ))}
    </CollapsibleContainer>
  );
};

export default MyEvents;
