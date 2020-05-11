package at.srfg.indexing.core.service.impl.onto;

public enum OntologyType {
	/**
	 *  import provided RDF File by checking classes and subclasses - also fills properties and 
	 *  assigns properties to classes
	 */
	OWLClass,
	/**
	 *  import provided RDF file by checking SKOS-Concept and SKOS-ConceptScheme, reflects 
	 *  hierarchy with skos:broader & skos:narrower 
	 */
	SKOS,
	;

}
