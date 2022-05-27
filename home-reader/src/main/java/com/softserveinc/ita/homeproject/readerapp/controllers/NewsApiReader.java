package com.softserveinc.ita.homeproject.readerapp.controllers;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.softserveinc.ita.homeproject.readerapp.models.NewsReader;


@Path("/news")
public interface NewsApiReader {

    @GET
    @Produces({ "application/json" })
    Response getAllNews();

    @GET
    @Path("/{id}")
    @Produces({ "application/json" })
    Response getNews(@PathParam("id") Long id);

    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    Response createNews(@Valid @NotNull NewsReader createNews);
}
