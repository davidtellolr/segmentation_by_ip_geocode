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

package com.liferay.osb.www.ip.geocoder;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

/**
 * @author Allen Ziegenfus
 */
@ExtendedObjectClassDefinition(category = "osb-www-ip-geocoder")
@Meta.OCD(id = "com.liferay.osb.www.ip.geocoder.IPGeocoderConfiguration")
public interface IPGeocoderConfiguration {

	@Meta.AD(deflt = "/opt/liferay/GeoLite2-Country.mmdb", required = false)
	public String countryDatabaseFile();

	@Meta.AD(deflt = "", required = false)
	public String countryOverride();

	@Meta.AD(deflt = "true", required = false)
	public boolean isEnabled();

}