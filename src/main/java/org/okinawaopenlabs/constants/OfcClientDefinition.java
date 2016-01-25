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

package org.okinawaopenlabs.constants;

public class OfcClientDefinition {

	
	/* Define OpenFlow Action type */
	public static final String 	ACTION_TYPE_OUTPUT 				= "OUTPUT";			/* Output packet from “port” */
	
	public static final String 	ACTION_TYPE_SET_VLAN 			= "SET_VLAN_VID";	/* Set the 802.1Q VLAN ID using “vlan_vid” */
	public static final String 	ACTION_TYPE_STRIP_VLAN 			= "STRIP_VLAN";		/* Strip the 802.1Q header */

	public static final String 	ACTION_TYPE_POP_VLAN			= "POP_VLAN";
	public static final String 	ACTION_TYPE_PUSH_VLAN 			= "PUSH_VLAN";		/* Push a new VLAN tag with “ethertype” */
	public static final Long 	ACTION_TYPE_PUSH_VLAN_ETH_TYPE 	= 33024L;			/* Ethertype 0x8100(=33024): IEEE 802.1Q VLAN-tagged frame */	
	public static final String 	ACTION_TYPE_SET_FIELD 			= "SET_FIELD";
	public static final String 	ACTION_TYPE_SET_FIELD_VLAN_VID 	= "vlan_vid";		/* Set VLAN ID */

	/* Define OpenFlow priority */
	public static final Long 	OPENFLOW_FLOWENTRY_PRIORITY_DROP 	= 100L;
	public static final Long 	OPENFLOW_FLOWENTRY_PRIORITY_NORMAL 	= 200L;
}
