/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.www.ip.geocoder.service;

import com.liferay.osb.www.ip.geocoder.IPGeocoder;
import com.liferay.osb.www.ip.geocoder.IPGeocoderConfiguration;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;

import java.io.File;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Allen Ziegenfus
 */
@Component(
	configurationPid = "com.liferay.osb.www.ip.geocoder.IPGeocoderConfiguration",
	immediate = true, service = IPGeocoder.class
)
public class IPGeocoderImpl implements IPGeocoder {

	public String getCountry(HttpServletRequest httpServletRequest) {
		if (!_iPGeocoderConfiguration.isEnabled()) {
			return StringPool.BLANK;
		}

		String countryOverride = _iPGeocoderConfiguration.countryOverride();

		if (Validator.isNotNull(countryOverride)) {
			_log.info(
				"Returning country override for testing: " + countryOverride);

			return countryOverride;
		}

		if (reader == null) {
			_log.info("No IP geocoding database present");

			return StringPool.BLANK;
		}

		InetAddress ipAddress = getInetAddress(httpServletRequest);

		try {
			_log.debug("Found IP address: " + ipAddress);

			if ((reader == null) || (ipAddress == null) ||
				ipAddress.isSiteLocalAddress() ||
				ipAddress.isLoopbackAddress()) {

				return StringPool.BLANK;
			}

			CountryResponse countryResponse = reader.country(ipAddress);

			Country country = countryResponse.getCountry();

			_log.debug(
				"Found country " + country.getName() + " for IP address " +
					ipAddress);

			return country.getIsoCode();
		}
		catch (GeoIp2Exception gie) {
			_log.error("Error geocoding IP address " + ipAddress, gie);
		}
		catch (IOException ioe) {
			_log.error("Error geocoding IP address " + ipAddress, ioe);
		}

		return StringPool.BLANK;
	}

	@Activate
	protected void activate(
			Map<String, Object> properties, BundleContext bundleContext)
		throws IOException {

		_iPGeocoderConfiguration = ConfigurableUtil.createConfigurable(
			IPGeocoderConfiguration.class, properties);

		String databaseFileName =
			_iPGeocoderConfiguration.countryDatabaseFile();

		if (Validator.isNull(databaseFileName)) {
			_log.info("No geocoding database file present");

			return;
		}

		_log.info("Reading geocoding database from " + databaseFileName);

		File database = new File(databaseFileName);

		try {
			reader = new DatabaseReader.Builder(
				database
			).withCache(
				new CHMCache()
			).build();
		}
		catch (IOException ioe) {
			_log.error(
				"Unable to read geocoding database file: " + databaseFileName,
				ioe);
		}
	}

	protected InetAddress getInetAddress(
		HttpServletRequest httpServletRequest) {

		List<String> ipAddresses = StringUtil.split(
			httpServletRequest.getHeader("X-FORWARDED-FOR"));

		if (ipAddresses.isEmpty()) {
			return null;
		}

		InetAddress ipAddress;

		try {
			ipAddress = InetAddress.getByName(ipAddresses.get(0));
		}
		catch (UnknownHostException uhe) {
			return null;
		}

		return ipAddress;
	}

	protected DatabaseReader reader;

	private static final Log _log = LogFactoryUtil.getLog(IPGeocoderImpl.class);

	private volatile IPGeocoderConfiguration _iPGeocoderConfiguration;

}