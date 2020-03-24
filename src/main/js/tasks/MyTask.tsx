import React, { FC } from "react";
import { binder } from "@scm-manager/ui-extensions";
import { Link } from "@scm-manager/ui-types";
import { MyTaskComponent, MyTaskType } from "../types";
import { Link as ReactLink } from "react-router-dom";
import styled from "styled-components";

type Props = {
  task: MyTaskType;
};

const StyledLink = styled(ReactLink)`
  color: inherit;
  :hover {
  color: #33b2e8 !important;
  }
`;

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

  return (
    <StyledLink to={(task?._links?.self as Link)?.href}>
      <Component task={task} />
    </StyledLink>
  );
};

export default MyTask;
