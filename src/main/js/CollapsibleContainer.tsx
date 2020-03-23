import React, {FC, useState} from "react";
import styled from "styled-components";
import { Icon } from "@scm-manager/ui-components";

type Props = {
  title: string;
};

const Container = styled.div`
  margin-bottom: 1rem;
`;

const Headline = styled.h3`
  font-size: 1.25rem;
  & small {
    font-size: 0.875rem;
  }
`;

const Content = styled.div`
  margin: 1rem 0 2rem;
  padding: 1rem;
  border: 1px solid #dbdbdb;
  border-radius: 4px;
`;

const CollapsibleContainer:FC<Props> = ({title, children}) => {
  const [collapsed, setCollapsed] = useState(false);

  const icon = collapsed ? "angle-right" : "angle-down";
  let content = null;
  if (!collapsed) {
    content = (
      <Content>
        {children}
      </Content>
    );
  }

  return (
    <Container>
      <div className="has-cursor-pointer" onClick={() => setCollapsed(!collapsed)}>
        <Headline>
          <Icon name={icon} color="default" /> {title}
        </Headline>
      </div>
      {content}
    </Container>
  );
};

export default CollapsibleContainer;
