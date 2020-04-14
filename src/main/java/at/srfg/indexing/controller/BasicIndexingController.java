package at.srfg.indexing.controller;

import org.springframework.web.bind.annotation.RestController;

import at.srfg.iot.indexing.controller.BasicIndexingAPI;
//import eu.nimble.utility.LoggerUtils;
import io.swagger.annotations.Api;

//@CrossOrigin
@RestController
@Api(value = "Basic Controller", description = "Search API to perform Solr operations on classification classes, properties and coded property values")
public class BasicIndexingController extends BasicIndexingAPI {
}
