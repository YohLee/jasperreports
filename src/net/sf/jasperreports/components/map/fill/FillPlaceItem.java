/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2013 Jaspersoft Corporation. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.components.map.fill;

import java.util.Map;

import net.sf.jasperreports.components.map.Item;
import net.sf.jasperreports.components.map.ItemProperty;
import net.sf.jasperreports.components.map.MapComponent;
import net.sf.jasperreports.components.map.MapUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.fill.JRFillExpressionEvaluator;
import net.sf.jasperreports.engine.fill.JRFillObjectFactory;
import net.sf.jasperreports.engine.type.ColorEnum;
import net.sf.jasperreports.engine.util.JRColorUtil;


/**
 * @author Teodor Danciu (teodord@users.sourceforge.net)
 * @version $Id$
 */
public class FillPlaceItem extends FillItem
{
	public static final String PROPERTY_COLOR = "color";
	/**
	 *
	 */
	public FillPlaceItem(
		Item item, 
		JRFillObjectFactory factory
		)
	{
		super(item, factory);
	}

	@Override
	public Object getEvaluatedValue(ItemProperty property, JRFillExpressionEvaluator evaluator, byte evaluation) throws JRException
	{
		Object result = super.getEvaluatedValue(property, evaluator, evaluation);
		return MapComponent.PROPERTY_address.equals(property.getName())
			? MapUtils.getCoords((String)result)
			: (PROPERTY_COLOR.equals(property.getName()) 
				? JRColorUtil.getColorHexa(JRColorUtil.getColor((String)result, ColorEnum.RED.getColor()))
				: result);
	}
	
	@Override
	public void verifyValue(ItemProperty property, Object value) throws JRException {
	}
	
	@Override
	public void verifyValues(Map<String, Object> result) throws JRException {
		if(result != null) {
			Object latitude = result.get(MapComponent.PROPERTY_latitude);
			Object longitude = result.get(MapComponent.PROPERTY_longitude);
			Object address = result.get(MapComponent.PROPERTY_address);

			boolean hasLatitude = !(latitude == null || "".equals(latitude));
			boolean hasLongitude = !(longitude == null || "".equals(longitude));

			if(hasLatitude && hasLongitude){
				result.remove(MapComponent.PROPERTY_address);
			} else if (address != null){
				Float[] coords = (Float[])address;
				if(coords[0] != null && coords[1] != null){
					result.put(MapComponent.PROPERTY_latitude, coords[0]);
					result.put(MapComponent.PROPERTY_longitude, coords[1]);
					result.remove(MapComponent.PROPERTY_address);
				} else {
					throw new JRException("Invalid coordinates geocoded from address: (" + coords[0] +", "+coords[1]+").");
				}
			} else {
				String msg = hasLatitude ? "" : MapComponent.PROPERTY_latitude;
				msg += "".equals(msg) ? "" : " and ";
				msg += hasLongitude ? "" : MapComponent.PROPERTY_longitude;
				throw new JRException("Found empty value for "+ msg);
			}
		}
	}
}
