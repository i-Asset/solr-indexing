package at.srfg.indexing.app.controller;

import org.springframework.web.bind.annotation.RestController;

import at.srfg.indexing.core.controller.BasicIndexingAPI;
//import eu.nimble.utility.LoggerUtils;
import io.swagger.annotations.Api;

/**
 * Basic indexing controller used/activated only when starting
 * the basic indexing service. 
 * <p>
 * The {@link BasicIndexingController} provides methods for manipulating the collections for
 * <ul>
 * <li>Classfifications/Taxonomies (Collection: concept_class)
 * <li>Properties (Collection: property)
 * <li>Codec Value Lists for Propertes (Collection: codes)
 * </ul>
 *   
 * </p>
 * @author dglachs
 *
 */
@RestController
@Api(value = "Basic Controller", description = "Search API to perform Solr operations on classification classes, properties and coded property values")
public class BasicIndexingController extends BasicIndexingAPI {
}
