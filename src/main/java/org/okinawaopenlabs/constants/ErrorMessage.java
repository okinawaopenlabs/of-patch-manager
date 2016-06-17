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

public class ErrorMessage {
	public static final String CONNECTION_FAIL = "Connection failed: %s.";
	public static final String WRONG_RESPONSE  = "Response of %s is wrong.";
	public static final String INVALID_JSON      = "Invalid json syntax.";
	public static final String INVALID_PARAMETER = "%s is invalid parameter.";

	public static final String IS_NULL  = "%s is null.";
	public static final String IS_BLANK = "%s is blank.";
	public static final String IS_NOT_NULL     = "%s is not null.";
	public static final String IS_NOT_BLANK    = "%s is not blank.";
	public static final String IS_NOT_INCLUDED = "%s is not included %s.";
	public static final String IS_OVERLAPPED   = "%s is overlapped.";
	public static final String THERE_IS_NULL              = "There is null in %s.";
	public static final String THERE_IS_BLANK             = "There is blank in %s";
	public static final String THERE_IS_INVALID_PARAMETER = "There is invalid parameter %s.";
	public static final String THERE_ARE_OVERLAPPED       = "There are overlapped %s.";

	public static final String ALREADY_EXIST = "%s is already exists";
	public static final String FIND_NULL = "Find %s thats %s is null";
	public static final String NOT_FOUND = "%s is not found.";
	public static final String IS_FULL = "%s is full.";
	public static final String UNEXPECTED_ERROR = "Unexpected error.";
	public static final String RETURNED_NULL = "%s returned null.";
	public static final String NOW_USED	= "%s is now assigned.";

	public static final String IS_PATCHED = "%s is patched";
	public static final String IS_NO_ROUTE = "There is no route between %s and %s.";
	public static final String IS_NOT_NUMBER = "%s is not number.";

	public static final String PARSE_ERROR = "Parse error: %s";

	public static final String COULD_NOT_DELETE = "Couldn't delete %s";
	public static final String INVALID_NUMBER_OF = "Invalid number of %s.";
	public static final String PATCH_INSERT_FAILD = "patchWiring insert failed. %s[%s]-%s[%s]@%s(%s,%s)";
	public static final String ROUTE_INSERT_FAILD = "route insert failed. sequence_num=%s, logical_link_id=%s, node_id=%s, node_name=%s, in_port_id=%s, in_port_name=%s, in_port_number=%s, out_port_id=%s, out_port_name=%s, out_port_number=%s";
}
