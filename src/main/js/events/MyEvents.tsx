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

type Props = {};

const MyEvents: FC<Props> = ({}) => {
  const [t] = useTranslation("plugins");
  const [content, setContent] = useState([]);
  const [error, setError] = useState(undefined);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    apiClient
      .get("/landingpage/myevents")
      .then(r => r.json())
      .then(setContent)
      .then(() => setLoading(false))
      .catch(setError);
  }, []);

  if (loading) {
    return <Loading />;
  }

  if (error) {
    return <ErrorNotification error={error} />;
  }

  return (
    <>
      <CollapsibleContainer title={t("scm-landingpage-plugin.myevents.title")} separatedEntries={false}>
        {content?._embedded?.events?.map((event, index) => (
          <MyEvent key={index} event={event} />
        ))}
      </CollapsibleContainer>
    </>
  );
};

export default MyEvents;
