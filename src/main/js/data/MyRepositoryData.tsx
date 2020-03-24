import React, { FC } from "react";
import { binder } from "@scm-manager/ui-extensions";
import { Link } from "@scm-manager/ui-types";
import { MyDataComponent, MyDataType } from "../types";
import { Link as ReactLink } from "react-router-dom";
import styled from "styled-components";

type Props = {
  data: MyDataType;
};

const StyledLink = styled(ReactLink)`
  color: inherit;
  :hover {
    color: #33b2e8 !important;
  }
`;

const MyRepositoryData: FC<Props> = ({ data }) => {
  const extensions: MyDataComponent[] = binder.getExtensions("landingpage.myFavoriteRepository");

  let Component = null;
  for (let extension of extensions) {
    if (extension.type === data.type) {
      Component = extension;
      break;
    }
  }

  if (!Component) {
    return null;
  }

  return (
    <StyledLink to={(data?._links?.self as Link)?.href}>
      <Component data={data} />
    </StyledLink>
  );
};

export default MyRepositoryData;
