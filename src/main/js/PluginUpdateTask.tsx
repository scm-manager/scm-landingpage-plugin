import React from "react";
import { binder } from "@scm-manager/ui-extensions";
import {MyTaskComponent, MyTaskType} from "./types";

type PluginUpdateTaskType = MyTaskType & {
  outdatedPlugins: number;
}

const PluginUpdateTask: MyTaskComponent<PluginUpdateTaskType> = ({task}) => (
  <div>count: {task.outdatedPlugins}</div>
);

PluginUpdateTask.type = "PluginUpdateTask";

binder.bind("landingpage.mytask.renderer", PluginUpdateTask);

