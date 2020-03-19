import React, {FC} from "react";
import { Repository, Link } from "@scm-manager/ui-types";
type Props = {
  repository: Repository;
};

const FavouriteRepositoryToggleIcon: FC<Props> = ({repository}) => {

  const getLink = () => {

  };

  const getClassName = () =>{
    if ((repository?._links?.unfavorize as Link)?.href) {
      return "fas fa-address-card";
    }
    return "far fa-address-card";
  };

  return (<i className={getClassName()}/>);
};
