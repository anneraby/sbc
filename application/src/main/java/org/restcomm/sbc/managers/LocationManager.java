/*******************************************************************************
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc, Eolos IT Corp and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package org.restcomm.sbc.managers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;




import java.util.List;
import java.util.concurrent.TimeUnit;

import org.infinispan.Cache;
import org.infinispan.commons.util.CloseableIteratorCollection;
import org.infinispan.manager.DefaultCacheManager;
import org.restcomm.sbc.bo.Location;

/**
 * @author  ocarriles@eolos.la (Oscar Andres Carriles)
 * @date    3/5/2016 22:47:34
 * @class   LocationHelper.java
 * @project Servlet2.5SBC
 *
 */
public class LocationManager {
	
	//private Hashtable<String, Location> registers=new Hashtable<String, Location>();
	private Cache<Object, Object> registers;
	private static LocationManager locationManager;
	
	private LocationManager() {
		registers = new DefaultCacheManager().getCache("location");
		
	}
	
	public static LocationManager getLocationManager() {
		if(locationManager==null) {
			locationManager=new LocationManager();
		}
		locationManager.register("00", "192.168.0.2", 5060, "friendly-scanner", "udp", 30);
		return locationManager;
	}
	
	
	public Location register(String user, String host, int port, String userAgent, String transport, int ttl) {
		Location location=new Location();
		location.setHost(host);
		location.setPort(port);
		location.setUserAgent(userAgent);
		location.setTransport(transport);
		location.setUser(user);
		
		registers.put(user, location, ttl, TimeUnit.SECONDS);
		
		return location;
			
	}
	
	public Location getLocation(String user) {
		return (Location) registers.get(user);
	}
	
	public Collection<Location> getLocations() {
		ArrayList al=new ArrayList(registers.values());
		//Collections.sort(al);
		return al;
	}
	
	public boolean isMzAlive(String user) {
		Location location=getLocation(user);
		return (location!=null && !location.isMzExpired())?true:false;
		
	}
	
	public boolean isDmzAlive(String user) {
		Location location=getLocation(user);
		return (location!=null && !location.isDmzExpired())?true:false;
		
	}
	
	public boolean exists(String user) {
		return getLocation(user)!=null;
	}
	
	public boolean match(String user, String host, int port) {
		Location location=getLocation(user);
		if(location!=null && location.getHost().equals(host) && location.getPort()==port) {
			return true;
		}
		return false;
		
	}

	public Location unregister(String user) {	
		return (Location) registers.remove(user);	
	}
	
	public void setDmzExpirationTimeInSeconds(String user, int expires) {
		Location location=getLocation(user);
		location.setDmzExpireTimestamp((Calendar.getInstance().getTimeInMillis())+(((long)expires)*1000L));
		
	}
	
	public void setMzExpirationTimeInSeconds(String user, int expires) {
		Location location=getLocation(user);
		location.setMzExpireTimestamp((Calendar.getInstance().getTimeInMillis())+(((long)expires)*1000L));
		
	}
	
	
	
	
	
	

}
