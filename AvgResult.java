package com.org;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class AvgResult implements RowMapper{

	@Override
	public Avges mapRow(ResultSet arg0, int arg1) throws SQLException {
		Avges avges = new Avges();
		avges.setAvg(arg0.getString("average"));
		return avges;
	}

}
