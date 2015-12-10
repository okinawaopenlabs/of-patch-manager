package org.okinawaopenlabs.ofpm.service;

import static org.junit.Assert.*;

import mockit.Expectations;
import mockit.NonStrictExpectations;
import java.lang.reflect.Type;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.business.*;
import org.okinawaopenlabs.ofpm.json.topology.logical.*;
import org.okinawaopenlabs.ofpm.service.LogicalService;
import org.okinawaopenlabs.ofpm.service.LogicalServiceImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LogicalServiceImplTest {

	private Gson gson = new Gson();

	private String testLogicalTopologyJson = "{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}";
	private String testLogicalTopologyOutJson = "{status:200, message:'null', result:{nodes:[{deviceName:'novaNode01'},{deviceName:'novaNode02'}], links:[{deviceName:['novaNode01', 'novaNode02']}]}}";
	private LogicalTopologyGetJsonOut testLogicalTopologyOut;
	private String validBaseResponseJson = "{status:201, message:''}";
	private BaseResponse validBaseResponse;

	public LogicalServiceImplTest() {
		Type type = new TypeToken<LogicalTopology>(){}.getType();
		type = new TypeToken<LogicalTopologyGetJsonOut>(){}.getType();
		testLogicalTopologyOut = gson.fromJson(testLogicalTopologyOutJson, type);
		type = new TypeToken<BaseResponse>() {}.getType();
		validBaseResponse = gson.fromJson(validBaseResponseJson, type);
	}

/*	@Test
	public void testGetLogicalTopologyTest() {
		new NonStrictExpectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.getLogicalTopology((String) withNotNull());
				result = testLogicalTopologyOutJson;
			}
		};

		Type type = new TypeToken<LogicalTopologyGetJsonOut>(){}.getType();
		LogicalService ls = new LogicalServiceImpl();

		Response res = ls.getLogicalTopology("test");
		String topoOut = gson.toJson(res, type);
		assertEquals(topoOut, testLogicalTopologyOut);
	}
*/
	@Test
	public void updateLogicalTopologyTest() {
		new Expectations() {
			LogicalBusinessImpl logiBiz;
			{
				new LogicalBusinessImpl();
				logiBiz.updateLogicalTopology((String) withNotNull());
//				result = validBaseResponseJson;
			}
		};

		LogicalService ls = new LogicalServiceImpl();
		Response res = ls.updateLogicalTopology(testLogicalTopologyJson);
//		BaseResponse resOut = BaseResponse.fromJson((String)res);
		assertEquals(res, validBaseResponseJson);
//		assertEquals(resOut, validBaseResponse);
	}
}