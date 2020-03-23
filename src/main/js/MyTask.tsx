import React, { FC } from "react";
import { binder } from "@scm-manager/ui-extensions";
import {MyTaskComponent, MyTaskType} from "./types";

//TODO add type
type Props = {
  task: MyTaskType
};

const MyTask: FC<Props> = ({ task }) => {
  const extensions: MyTaskComponent[] = binder.getExtensions("landingpage.mytask");

  let Component = null;
  for (let extension of extensions) {
    if (extension.type === task.type) {
      Component = extension;
      break;
    }
  }

  if (!Component) {
    return null;
  }

  return <Component task={task} />;
};

export default MyTask;
