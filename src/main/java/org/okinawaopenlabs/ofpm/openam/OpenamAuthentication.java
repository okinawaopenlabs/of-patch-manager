/*
 *   Copyright 2015 Okinawa Open Laboratory, General Incorporated Association
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.okinawaopenlabs.ofpm.openam;

import static org.okinawaopenlabs.constants.OfpmDefinition.OPEN_AM_URL;
import static org.okinawaopenlabs.constants.OfpmDefinition.STATUS_INTERNAL_ERROR;
import static org.okinawaopenlabs.constants.OfpmDefinition.STATUS_UNAUTHORIZED;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;

import ool.com.openam.client.OpenAmClient;
import ool.com.openam.client.OpenAmClientException;
import ool.com.openam.client.OpenAmClientImpl;
import ool.com.openam.json.OpenAmAttributesOut;
import ool.com.openam.json.OpenAmIdentitiesOut;
import ool.com.openam.json.TokenIdOut;
import ool.com.openam.json.TokenValidChkOut;

public class OpenamAuthentication{
	
//	private static final Logger logger = Logger.getLogger(OpenamAuthentication.class);
	Config conf = new ConfigImpl();
	
	public boolean authenticationTokenId(String tokenId)throws OpenAmClientException{
		String openamUrl = conf.getString(OPEN_AM_URL);
		OpenAmClient openAmClient = new OpenAmClientImpl(openamUrl);
		boolean isTokenValid = false;
		if (!StringUtils.isBlank(tokenId) && openAmClient != null) {
			TokenValidChkOut tokenValidchkOut = openAmClient.tokenValidateCheck(tokenId);
			isTokenValid = tokenValidchkOut.getIsTokenValid();
		}
		return isTokenValid;
	}
}
