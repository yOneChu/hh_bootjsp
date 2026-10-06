package com.kyhslam.util.simulate;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.TreeMap;

/**
 * dyna.plmetc.variant.VariantMap 의 독립 버전
 */
public class PidVariantMap extends LinkedHashMap
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
		{
			sf.delete(sf.length() - 1,sf.length());
		}

		return sf.toString();
	}

	public PidVariantMap getOUTPUTMap()
	{
		PidVariantMap res = new PidVariantMap();

		Iterator<String> it = this.keySet().iterator();

		while(it.hasNext())
		{
			String key = it.next();
			if(key.startsWith("OUTPUT"))
			{
				String specName = (String) this.get(key);
				String specValue = PidUtil.NVL(this.get(specName),"");
				res.put(specName,specValue);
			}
		}

		return res;
	}

	public PidVariantMap removeOUTPUTKeyword()
	{
		Iterator<String> it = this.keySet().iterator();

		while(it.hasNext())
		{
			String key = it.next();
			if(key.startsWith("OUTPUT"))
			{
				this.remove(key);
				it = this.keySet().iterator();
			}
		}

		return this;
	}

	public void putOUTPUT(String key, String val) {
		this.put("OUTPUT"+System.identityHashCode(this)+(idx++), key);
		this.put(key,val);
	}
}
