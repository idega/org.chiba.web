package com.idega.chiba;

import com.idega.util.CoreConstants;

public class ChibaConstants {

	public static final String 	MAPPING = "mapping",
			
								NAMESPACE_URL = "http://www.w3.org/2002/xforms",
								NAMESPACE_URL_IDEGA = "http://idega.com/xforms",
								NAMESPACE_PREFIX = "xf",
								
								NODENAME_GROUP = "group",
								NODENAME_ITEM = "item",
								NODENAME_ITEM_WITH_NAMESPACE_PREFIX = 
										NAMESPACE_PREFIX + CoreConstants.COLON + 
										NODENAME_ITEM,
								NODENAME_LABEL = "label",
								NODENAME_LABEL_WITH_NAMESPACE_PREFIX = 
										NAMESPACE_PREFIX + CoreConstants.COLON + 
										NODENAME_LABEL,
								NODENAME_REPEAT = "repeat",
								NODENAME_SELECT1 = "select1",
								NODENAME_VALUE = "value",
								NODENAME_VALUE_WITH_NAMESPACE_PREFIX = 
										NAMESPACE_PREFIX + CoreConstants.COLON + 
										NODENAME_VALUE,
										
								ATTRIBUTE_BIND = "bind";
}