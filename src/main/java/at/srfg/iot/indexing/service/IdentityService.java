package at.srfg.iot.indexing.service;



import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import at.srfg.iot.indexing.controller.OpenIdConnectUserDetails;

@Service
public class IdentityService {

	private static final Logger logger = LoggerFactory.getLogger(IdentityService.class);


	public OpenIdConnectUserDetails getUserDetails(String bearer) throws IOException {
		return OpenIdConnectUserDetails.fromBearer(bearer);
	}

	/**
	 * Checks if the bearer contains at least one of the given roles.
	 *
	 * @param bearer Token containing roles
	 * @param roles  Roles to check
	 * @return True if at least one matching role was found.
	 * @throws IOException if roles could not be extracted from token
	 */
	public boolean hasAnyRole(String bearer, OpenIdConnectUserDetails.Role... roles) {
		try {
			// check token
			OpenIdConnectUserDetails details = getUserDetails(bearer);
			return Arrays.stream(roles).anyMatch(r -> details.hasRole(r.toString()));
		} catch (IOException e) {
			return  false;
		}
	}
}
