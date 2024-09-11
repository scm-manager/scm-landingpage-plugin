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

import { Repository, HalRepresentation } from "@scm-manager/ui-types";
import React, { FC, ReactNode } from "react";
import { ExtensionPointDefinition } from "@scm-manager/ui-extensions";

export type MyTaskType = {
  type: string;
};

type TaskProps<T = MyTaskType> = {
  task: T;
};

export type MyTaskComponent<T = MyTaskType> = React.FC<TaskProps<T>> & {
  type: string;
};

type MyTaskEmbedded = {
  tasks: MyTaskType[];
};

export type MyTasksType = {
  _embedded: MyTaskEmbedded;
};

type MyDataEmbedded = {
  data: MyDataType[];
};

export type MyDataEntriesType = {
  _embedded: MyDataEmbedded;
};

export type MyDataType = {
  type: string;
};

export type MyEventType<T extends string = string> = {
  type: T;
};

export type MyRepositoryEventType<T extends string = string> = MyEventType<T> & {
  repository: string;
  deleted: boolean;
};

export type MyEventComponent<T extends MyEventType = MyEventType> = FC<EventProps<T>> & {
  type: string;
};

type EventProps<T = MyEventType> = {
  event: T;
};

type MyEventEmbedded = {
  events: MyEventType[];
};

export type MyEventsType = {
  _embedded: MyEventEmbedded;
};

export type FavoriteRepositories = {
  repositories: Repository[];
};

export type LandingpageConfiguration = HalRepresentation & {
  instanceName: string;
};

export type MyEventExtension<T extends MyEventType = MyEventType> = ExtensionPointDefinition<
  "landingpage.myevents",
  MyEventComponent<T>,
  EventProps<T>
>;

export type MyDataExtension<T extends MyDataType = MyDataType> = ExtensionPointDefinition<
  "landingpage.mydata",
  {
    title: string;
    render: (data: T) => React.ReactElement;
    beforeData?: ReactNode;
    separatedEntries: boolean;
    type: string;
    emptyMessage?: string;
  }
>;

export type MyTaskExtension<T extends MyTaskType = MyTaskType> = ExtensionPointDefinition<
  "landingpage.mytask",
  MyTaskComponent<T>
>;
