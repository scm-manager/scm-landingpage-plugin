import { Links } from "@scm-manager/ui-types";

export type MyTaskType = {
  type: string
  _links: Links
};

type Props<T = MyTaskType> = {
  task: T
};

export type MyTaskComponent<T = MyTaskType> = React.FC<Props<T>> & {
  type: string
};
