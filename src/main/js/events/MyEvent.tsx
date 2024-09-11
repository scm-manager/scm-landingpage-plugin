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

import React, { FC } from "react";
import { binder } from "@scm-manager/ui-extensions";
import { MyEventExtension, MyEventType } from "../types";

type Props = {
  event: MyEventType;
};

const MyEvent: FC<Props> = ({ event }) => {
  const extensions = binder.getExtensions<MyEventExtension>("landingpage.myevents", { event });

  let Component = null;
  for (const extension of extensions) {
    if (extension.type === event.type) {
      Component = extension;
      break;
    }
  }

  if (!Component) {
    return null;
  }

  return <Component event={event} />;
};

export default MyEvent;
