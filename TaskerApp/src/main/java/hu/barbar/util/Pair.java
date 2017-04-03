package hu.barbar.util;

public class Pair<K, V> {

	K key = null;
	V value = null;
	
	
	public Pair(K key, V value) {
		super();
		this.key = key;
		this.value = value;
	}


	public K getKey() {
		return key;
	}


	public V getValue() {
		return value;
	}
	
	
	
	
	
}
