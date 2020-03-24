package com.cloudogu.scm.mydata;

import sonia.scm.web.VndMediaType;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("v2/landingpage/mydata")
public class MyDataResource {

  private final MyDataCollector collector;

  private static final String MEDIATYPE = VndMediaType.PREFIX + "mydata" + VndMediaType.SUFFIX;

  @Inject
  public MyDataResource(MyDataCollector collector) {
    this.collector = collector;
  }

  @GET
  @Path("")
  @Produces(MEDIATYPE)
  public MyDataDto getData(@Context UriInfo uriInfo) {
    String self = uriInfo.getAbsolutePath().toASCIIString();
    return new MyDataDto(self, collector.collect());
  }
}
