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

import React, { FC, useMemo } from "react";
import { useBinder } from "@scm-manager/ui-extensions";
import { MyTaskExtension, MyTaskType } from "../types";

type Props = {
  task: MyTaskType;
};

const MyTask: FC<Props> = ({ task }) => {
  const binder = useBinder();
  const extensions = useMemo(() => binder.getExtensions<MyTaskExtension>("landingpage.mytask"), [binder]);
  const Component = useMemo(() => extensions.find(extension => extension.type === task.type), [extensions, task.type]);

  if (!Component) {
    return null;
  }

  return <Component task={task} />;
};

export default MyTask;
