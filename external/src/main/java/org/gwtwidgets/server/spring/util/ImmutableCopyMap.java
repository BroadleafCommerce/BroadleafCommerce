/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gwtwidgets.server.spring.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Map implementation of the immutable copy pattern. Grants fast, lock-free read access when
 * writes are rare. Methods operating on multiple entries (i.e. {@link #putAll(Map)}, {@link #clear()} etc)
 * are unsafe to use concurrently.
 * @author g.georgovassilis[at]gmail.com
 *
 * @param <K>
 * @param <V>
 */
public class ImmutableCopyMap<K, V> implements java.util.Map<K, V>{

	private volatile Map<K, V> map = new HashMap<K, V>();
	private Object mutex = new Object();
	
	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

	public V get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public V put(K key, V value) {
		synchronized (mutex){
			Map<K,V> copy = new HashMap<K, V>();
			copy.putAll(map);
			copy.put(key, value);
			map = copy;
			return value;
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(m);
	}

	public V remove(Object key) {
		synchronized (mutex){
			Map<K,V> copy = new HashMap<K, V>();
			copy.putAll(map);
			V value = copy.remove(key);
			map = copy;
			return value;
		}
	}

	public int size() {
		return map.size();
	}

	public Collection<V> values() {
		return map.values();
	}

}
