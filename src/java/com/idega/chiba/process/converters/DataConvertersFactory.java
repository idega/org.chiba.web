package com.idega.chiba.process.converters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2007/09/23 06:58:26 $ by $Author: civilis $
 */
public class DataConvertersFactory {
	
	private Map<ConverterDataType, DataConverter> converters = new HashMap<ConverterDataType, DataConverter>();

	public synchronized DataConverter createConverter(String dataType) {

		ConverterDataType cdt = ConverterDataType.valueOf(dataType.toUpperCase());
		
		if(converters.containsKey(cdt))
			return converters.get(cdt);
		
		DataConverter converter = cdt.getConverter();
		converters.put(cdt, converter);
		return converter;
	}
}