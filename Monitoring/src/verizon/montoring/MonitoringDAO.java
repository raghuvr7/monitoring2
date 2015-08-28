package verizon.montoring;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class MonitoringDAO {
	PreparedStatement ps = null;
	ResultSet rs = null;
	List<PortDetails> portDetails = null;
	List<DeviceDetails> deviceDetails = null;
	List<LocationDetails> locationDetails = null;
	CustomerDeviceDetails customerDeviceDetails = null;
	List<RepairoutOfOrderDevice> roDevice = null;
	CallableStatement cs = null;

	public List<LocationDetails> distinctLocation() {
		Connection conn = new DBConnectionClass().makeConnection();

		String selectQuery = "select zipcode,count(distinct(device_id)) as deviceCount,count(port_id) as portCount,sum(bandwidth_mbps) as bandwidthMbps from Device group by zipcode";
		try {

			ps = conn.prepareStatement(selectQuery);
			rs = ps.executeQuery();

			locationDetails = new ArrayList<LocationDetails>();

			while (rs.next()) {
				int zipcode = rs.getInt("zipcode");
				int deviceCount = rs.getInt("deviceCount");
				int portCount = rs.getInt("portCount");
				int bandwidthMbps = rs.getInt("bandwidthMbps");
				LocationDetails lDetails = new LocationDetails(zipcode,
						deviceCount, portCount, bandwidthMbps);
				locationDetails.add(lDetails);
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			new DBConnectionClass().closeConnection(conn);
		}

		return locationDetails;

	}

	public List<DeviceDetails> distinctDevice(int zipcode) {
		Connection conn = new DBConnectionClass().makeConnection();
		String selectQuery = "select device_id as deviceId,count(port_id) as portCount,sum(bandwidth_mbps) as bandwidthMbps from Device where zipcode=? group by device_id";

		try {
			ps = conn.prepareStatement(selectQuery);
			ps.setInt(1, zipcode);
			rs = ps.executeQuery();
			deviceDetails = new ArrayList<DeviceDetails>();
			while (rs.next()) {
				String deviceId = rs.getString("deviceId");
				int portCount = rs.getInt("portCount");
				int bandwidthMbps = rs.getInt("bandwidthMbps");
				DeviceDetails dDetails = new DeviceDetails(deviceId, portCount,
						bandwidthMbps);
				deviceDetails.add(dDetails);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			new DBConnectionClass().closeConnection(conn);
		}
		return deviceDetails;

	}

	public List<PortDetails> distinctPort(String deviceId) {
		Connection conn = new DBConnectionClass().makeConnection();
		String selectQuery = "select port_id as portId,bandwidth_mbps as bandwidthMbps,status from Device where device_id=?";
		try {
			ps = conn.prepareStatement(selectQuery);
			ps.setString(1, deviceId);
			rs = ps.executeQuery();
			portDetails = new ArrayList<PortDetails>();
			while (rs.next()) {
				int portId = rs.getInt("portId");
				int bandwidthMbps = rs.getInt("bandwidthMbps");
				String status = rs.getString("status");
				PortDetails pDetails = new PortDetails(portId, bandwidthMbps,
						status);
				portDetails.add(pDetails);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			new DBConnectionClass().closeConnection(conn);
		}
		return portDetails;

	}

	public List<RepairoutOfOrderDevice> alertDetails() {
		String selectQuery = "Select device_id as deviceId,part_number as partNumber,manufacturer,zipcode,port_id as portId,bandwidth_mbps as bandwidthMbps,device_type as deviceType,status from device where status=? or status=?";
		Connection conn = new DBConnectionClass().makeConnection();
		try {
			ps = conn.prepareStatement(selectQuery);
			ps.setString(1, "REPAIR");
			ps.setString(2, "OUTOFORDER");
			rs = ps.executeQuery();
			roDevice = new ArrayList<RepairoutOfOrderDevice>();
			while (rs.next()) {
				String deviceId = rs.getString("deviceId");
				String partNumber = rs.getString("partNumber");
				String manufacturer = rs.getString("manufacturer");
				int zipcode = rs.getInt("zipcode");
				int portId = rs.getInt("portId");
				int bandwidthMbps = rs.getInt("bandwidthMbps");
				String deviceType = rs.getString("deviceType");
				String status = rs.getString("status");
				RepairoutOfOrderDevice roofod = new RepairoutOfOrderDevice(
						deviceId, partNumber, manufacturer, zipcode, portId,
						bandwidthMbps, deviceType, status);
				roDevice.add(roofod);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return roDevice;

	}

	public void GenerateTicket(int zipcode, String deviceId, String status) {
		String query = "{call create_group_ticket(?,?,?)}";
		status = "open";
		Connection conn = new DBConnectionClass().makeConnectionTicket();
		try {
			cs = conn.prepareCall(query);
			cs.setInt(1, zipcode);
			cs.setString(2, deviceId);
			cs.setString(3, status);
			cs.execute();
			conn.commit();

		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			new DBConnectionClass().closeConnection(conn);
		}
	}

}
