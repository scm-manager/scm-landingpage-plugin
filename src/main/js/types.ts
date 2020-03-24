import { Links } from "@scm-manager/ui-types";

export type MyTaskType = {
  type: string
  _links: Links
};

type TaskProps<T = MyTaskType> = {
  task: T
};

export type MyTaskComponent<T = MyTaskType> = React.FC<TaskProps<T>> & {
  type: string
};

export type MyDataType = {
  type: string;
  _links: Links
}

export type MyDataComponent<T = MyDataType> = React.FC<DataProps<T>> & {
  type: string
};

type DataProps<T = MyDataType> = {
  data: T
};
