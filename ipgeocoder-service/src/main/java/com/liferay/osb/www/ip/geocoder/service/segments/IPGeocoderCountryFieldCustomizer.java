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

package com.liferay.osb.www.ip.geocoder.service.segments;

import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.segments.field.Field;
import com.liferay.segments.field.customizer.SegmentsFieldCustomizer;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Allen Ziegenfus
 */
@Component(
	immediate = true,
	property = {
		"segments.field.customizer.entity.name=Context",
		"segments.field.customizer.key=" + IPGeocoderCountryRequestContextContributor.KEY,
		"segments.field.customizer.priority:Integer=50"
	},
	service = SegmentsFieldCustomizer.class
)
public class IPGeocoderCountryFieldCustomizer
	implements SegmentsFieldCustomizer {

	@Override
	public List<String> getFieldNames() {
		return _fieldNames;
	}

	@Override
	public String getKey() {
		return IPGeocoderCountryRequestContextContributor.KEY;
	}

	public String getLabel(String fieldName, Locale locale) {
		return LanguageUtil.get(
			locale, IPGeocoderCountryRequestContextContributor.KEY,
			"IPGeocoder Country");
	}

	@Override
	public List<Field.Option> getOptions(Locale locale) {
		List<Country> countries = _countryService.getCountries();

		Stream<Country> stream = countries.stream();

		return stream.map(
			country -> new Field.Option(
				country.getName(locale), String.valueOf(country.getA2()))
		).collect(
			Collectors.toList()
		);
	}

	private static final List<String> _fieldNames = ListUtil.fromArray(
		IPGeocoderCountryRequestContextContributor.KEY);

	@Reference
	private CountryService _countryService;

}