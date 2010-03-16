/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2009 Jaspersoft Corporation. All rights reserved.
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
package net.sf.jasperreports.components.table;

import java.util.List;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRExpressionCollector;

/**
 * 
 * 
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class ColumnExpressionCollector implements ColumnVisitor<Void>
{

	private final JRExpressionCollector mainCollector;
	private final JRExpressionCollector datasetCollector;
	
	public ColumnExpressionCollector(JRExpressionCollector mainCollector,
			JRExpressionCollector datasetCollector)
	{
		this.mainCollector = mainCollector;
		this.datasetCollector = datasetCollector;
	}

	public void collectColumns(List<BaseColumn> columns)
	{
		for (BaseColumn column : columns)
		{
			column.visitColumn(this);
		}
	}
	
	public Void visitColumn(Column column)
	{
		mainCollector.addExpression(column.getPrintWhenExpression());
		collectCell(column.getColumnHeader());
		collectCell(column.getColumnFooter());
		collectCell(column.getDetailCell());
		
		// nothing
		return null;
	}

	public Void visitColumnGroup(ColumnGroup columnGroup)
	{
		mainCollector.addExpression(columnGroup.getPrintWhenExpression());
		collectCell(columnGroup.getColumnHeader());
		collectCell(columnGroup.getColumnFooter());
		collectColumns(columnGroup.getColumns());
		
		// nothing
		return null;
	}

	protected void collectCell(Cell cell)
	{
		datasetCollector.collect(cell.getStyle());
		
		JRElement[] elements = cell.getElements();
		if (elements != null)
		{
			for (int i = 0; i < elements.length; i++)
			{
				elements[i].collectExpressions(datasetCollector);
			}
		}
	}
	
}
