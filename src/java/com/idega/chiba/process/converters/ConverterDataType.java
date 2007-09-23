package com.idega.chiba.process.converters;


/**
 * 
 *  Last modified: $Date: 2007/09/23 06:58:26 $ by $Author: civilis $
 * 
 * @author <a href="mailto:civilis@idega.com">Vytautas Čivilis</a>
 * @version $Revision: 1.1 $
 */
public enum ConverterDataType {
	
	DATE {
		public DataConverter getConverter() { 
			return new DateConverter();
		}
	},	
	STRING {
		public DataConverter getConverter() { 
			return new StringConverter();
		}
	},	
	LIST {
		public DataConverter getConverter() { 
			return new CollectionConverter();
		}
	};
	
	public abstract DataConverter getConverter();
}