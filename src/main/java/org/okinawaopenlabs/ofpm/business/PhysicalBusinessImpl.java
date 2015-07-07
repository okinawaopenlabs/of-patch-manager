package org.okinawaopenlabs.ofpm.business;

import static org.okinawaopenlabs.constants.ErrorMessage.*;
import static org.okinawaopenlabs.constants.OfpmDefinition.*;
import static org.okinawaopenlabs.constants.OrientDBDefinition.*;

import java.sql.Connection;
import java.sql.SQLException;

import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;

import org.okinawaopenlabs.ofpm.exception.ValidateException;
import org.okinawaopenlabs.ofpm.json.common.BaseResponse;
import org.okinawaopenlabs.ofpm.json.device.PortData;
import org.okinawaopenlabs.ofpm.json.topology.physical.ConnectPhysicalLinksJsonIn;
import org.okinawaopenlabs.ofpm.json.topology.physical.DisconnectPhysicalLinksJsonIn;
import org.okinawaopenlabs.ofpm.json.topology.physical.PhysicalLink;
import org.okinawaopenlabs.ofpm.utils.Config;
import org.okinawaopenlabs.ofpm.utils.ConfigImpl;
import org.okinawaopenlabs.ofpm.utils.OFPMUtils;
import org.okinawaopenlabs.ofpm.validate.topology.physical.ConnectPhysicalLinksJsonInValidate;
import org.okinawaopenlabs.ofpm.validate.topology.physical.DisconnectPhysicalLinksJsonInValidate;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbc;
import org.okinawaopenlabs.orientdb.client.ConnectionUtilsJdbcImpl;
import org.okinawaopenlabs.orientdb.client.Dao;
import org.okinawaopenlabs.orientdb.client.DaoImpl;

public class PhysicalBusinessImpl implements PhysicalBusiness {
	private static final Logger logger = Logger.getLogger(PhysicalBusinessImpl.class);

	Config conf = new ConfigImpl();

	@Override
	public String connectPhysicalLink(String physicalLinkJson) {
		final String fname = "connectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */
		ConnectPhysicalLinksJsonIn inParam = null;
		try {
			inParam = ConnectPhysicalLinksJsonIn.fromJson(physicalLinkJson);
			ConnectPhysicalLinksJsonInValidate validator = new ConnectPhysicalLinksJsonInValidate();
			validator.checkValidation(inParam);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Create cable-link */
		ConnectionUtilsJdbc utils = null;
		Connection          conn  = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			for (PhysicalLink link : inParam.getLinks()) {
				PortData port0 = link.getLink().get(0);
				PortData port1 = link.getLink().get(1);

				int status = dao.createCableLink(
						conn,
						port0.getDeviceName(),
						port0.getPortName(),
						port1.getDeviceName(),
						port1.getPortName());

				switch (status) {
					case DB_RESPONSE_STATUS_OK:
						continue;

					case DB_RESPONSE_STATUS_NOT_FOUND:
						utils.rollback(conn);
						res.setMessage(String.format(NOT_FOUND, port0.getPortName() + " or " + port1.getPortName()));
						res.setStatus(STATUS_NOTFOUND);
						return res.toJson();

					case DB_RESPONSE_STATUS_EXIST:
						utils.rollback(conn);
						res.setStatus(STATUS_CONFLICT);
						res.setMessage(String.format(ALREADY_EXIST, port0.getPortName() + "<-->" + port1.getPortName()));
						return res.toJson();

					default:
						utils.rollback(conn);
						res.setStatus(STATUS_INTERNAL_ERROR);
						return res.toJson();
				}
			}

			utils.commit(conn);
			res.setStatus(STATUS_CREATED);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}

	@Override
	public String disconnectPhysicalLink(String physicalLinkJson) {
		final String fname = "disconnectPhysicalLink";
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("%s(physicalLinkJson=%s) - start", fname, physicalLinkJson));
		}
		BaseResponse res = new BaseResponse();

		/* PHASE 1: json -> obj and validation check */
		DisconnectPhysicalLinksJsonIn disconPhysicalLinks = null;
		try {
			disconPhysicalLinks = DisconnectPhysicalLinksJsonIn.fromJson(physicalLinkJson);
			DisconnectPhysicalLinksJsonInValidate validator = new DisconnectPhysicalLinksJsonInValidate();
			validator.checkValidation(disconPhysicalLinks);
		} catch (JsonSyntaxException jse) {
			OFPMUtils.logErrorStackTrace(logger, jse);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(INVALID_JSON);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		} catch (ValidateException ve) {
			OFPMUtils.logErrorStackTrace(logger, ve);
			res.setStatus(STATUS_BAD_REQUEST);
			res.setMessage(ve.getMessage());
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
			return res.toJson();
		}

		/* PHASE 2: Delete cable-link */
		ConnectionUtilsJdbc utils = null;
		Connection          conn  = null;
		try {
			utils = new ConnectionUtilsJdbcImpl();
			conn  = utils.getConnection(false);

			Dao dao = new DaoImpl(utils);
			for (PhysicalLink link : disconPhysicalLinks.getLinks()) {
				PortData port0 = link.getLink().get(0);
				PortData port1 = link.getLink().get(1);

				int status = dao.deleteCableLink(
						conn,
						port0.getDeviceName(),
						port0.getPortName(),
						port1.getDeviceName(),
						port1.getPortName());

				switch (status) {
					case DB_RESPONSE_STATUS_OK:
						continue;

					case DB_RESPONSE_STATUS_NOT_FOUND:
						utils.rollback(conn);
						res.setMessage(String.format(NOT_FOUND, port0.getPortName() + "<-->" + port1.getPortName()));
						res.setStatus(STATUS_NOTFOUND);
						return res.toJson();

					case DB_RESPONSE_STATUS_USED:
						utils.rollback(conn);
						res.setStatus(STATUS_FORBIDDEN);
						res.setMessage(String.format(IS_PATCHED, port0.getPortName() + " or " + port1.getPortName()));
						return res.toJson();

					case DB_RESPONSE_STATUS_FAIL:
						utils.rollback(conn);
						res.setStatus(STATUS_FORBIDDEN);
						res.setMessage(String.format(COULD_NOT_DELETE, port0.getPortName() + "<-->" + port1.getPortName()));
						return res.toJson();

					default:
						utils.rollback(conn);
						res.setStatus(STATUS_INTERNAL_ERROR);
						res.setMessage(UNEXPECTED_ERROR);
						return res.toJson();
				}
			}

			utils.commit(conn);
			res.setStatus(STATUS_CREATED);
			return res.toJson();
		} catch (SQLException | RuntimeException e) {
			utils.rollback(conn);
			OFPMUtils.logErrorStackTrace(logger, e);
			res.setStatus(STATUS_INTERNAL_ERROR);
			res.setMessage(e.getMessage());
			return res.toJson();
		} finally {
			utils.close(conn);
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("%s(ret=%s) - end", fname, res));
			}
		}
	}
}
