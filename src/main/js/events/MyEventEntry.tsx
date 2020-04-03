import React, { FC, ReactNode } from "react";
import classNames from "classnames";
import styled from "styled-components";
import { Link as ReactLink } from "react-router-dom";
import { DateFromNow } from "@scm-manager/ui-components";
import { useTranslation } from "react-i18next";

const FlexFullHeight = styled.div`
  flex-direction: column;
  justify-content: space-around;
  align-self: stretch;
`;

const ContentLeft = styled.div`
  margin-bottom: 0 !important;
  overflow: hidden;
`;

const ContentRight = styled.div`
  margin-left: auto;
  align-items: start;
`;

const CenteredItems = styled.div`
  align-items: center;
`;

const StyledLink = styled(ReactLink)`
  color: inherit;
  :hover {
    color: #33b2e8 !important;
  }
`;

type Props = {
  link: string;
  icon: ReactNode;
  contentLeft: ReactNode;
  date: Date;
};

const MyEventEntry: FC<Props> = ({ link, icon, contentLeft, date }) => {
  const [t] = useTranslation("plugins");

  return (
    <StyledLink to={link}>
      <div className={"media"}>
        {icon}
        <FlexFullHeight className={classNames("media-content", "text-box", "is-flex")}>
          <CenteredItems className="is-flex">
            <ContentLeft className="content">{contentLeft}</ContentLeft>
            <ContentRight>
              <DateFromNow date={date} />
            </ContentRight>
          </CenteredItems>
        </FlexFullHeight>
      </div>
    </StyledLink>
  );
};

export default MyEventEntry;
