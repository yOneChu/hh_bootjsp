package com.kyhslam.BlockSimul;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * dyna.plmetc.variant.VariantMap 의 독립 버전
 */
public class BlockVariantMap extends LinkedHashMap
{
	private static final long serialVersionUID = -8916206904739626507L;
	private int idx = 1;

	public String toString()
	{
		TreeMap treeMap = new TreeMap(this);
		Iterator Iter = treeMap.keySet().iterator();
		StringBuffer sf = new StringBuffer();
		while(Iter.hasNext())
		{
			String key = (String) Iter.next();
			if(key.contains("OUTPUT"))
				continue;

			sf.append(key + " = " + this.get(key) + "\n");
		}

		if(sf.toString().endsWith("\n"))
			sf.delete(sf.length() - 1,sf.length());

		return sf.toString();
	}

	/** OUTPUT 으로 선언된 키의 값 모음 */
	public BlockVariantMap getOUTPUTMap()
	{
		BlockVariantMap res = new BlockVariantMap();

		Iterator<String> it = this.keySet().iterator();
		while(it.hasNext())
		{
			String key = it.next();
			if(key.startsWith("OUTPUT"))
			{
				String specName = (String) this.get(key);
				String specValue = BlockUtil.NVL(this.get(specName),"");
				res.put(specName,specValue);
			}
		}

		return res;
	}

	public void putOUTPUT(String key, String val) {
		this.put("OUTPUT"+System.identityHashCode(this)+(idx++), key);
		this.put(key,val);
	}
}
