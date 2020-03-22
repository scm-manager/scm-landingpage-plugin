import React, {FC} from "react";
import {apiClient} from "@scm-manager/ui-components";
import { Repository, Link } from "@scm-manager/ui-types";
type Props = {
  repository: Repository;
};

const FavouriteRepositoryToggleIcon: FC<Props> = ({repository}) => {

  const getLink = () => {
    if (!!repository?._links?.favorize) {
      return (repository._links.favorize as Link).href
    } else {
      return (repository._links.unfavorize as Link).href
    }
  };

  const getClassName = () =>{
    if ((repository?._links?.unfavorize as Link)?.href) {
      return "fas fa-star";
    }
    return "far fa-star";
  };

  const sendRequest = () => {
    apiClient.post(getLink());
  };

  return (<i className={getClassName()} onClick={() => sendRequest()}/>);
};

export default FavouriteRepositoryToggleIcon;
