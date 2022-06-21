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
import { Repository } from "@scm-manager/ui-types";
import React, { FC } from "react";

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

export type MyDataComponent<T = MyDataType> = React.FC<DataProps<T>> & {
  type: string;
};

type DataProps<T = MyDataType> = {
  data: T;
};

export type MyEventType = {
  type: string;
};

export type MyRepositoryEventType = MyEventType & {
  repository: string;
  deleted: boolean;
}

export type MyEventComponent<T = MyEventType> = FC<EventProps<T>> & {
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
