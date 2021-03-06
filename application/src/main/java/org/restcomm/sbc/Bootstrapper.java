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
 *******************************************************************************/
package org.restcomm.sbc;


import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipServlet;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.interpol.ConfigurationInterpolator;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.log4j.Logger;


public final class Bootstrapper extends SipServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(Bootstrapper.class);

   
    public Bootstrapper() {
        super();
    }

    @Override
    public void destroy() {
        
    }

    private String home(final ServletConfig config) {
        final ServletContext context = config.getServletContext();
        final String path = context.getRealPath("/");
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        } else {
            return path;
        }
    }

    @Override
    public void init(final ServletConfig config) throws ServletException {
        final ServletContext context = config.getServletContext();
        final String path = context.getRealPath("WEB-INF/conf/sbc.xml");
        // Initialize the configuration interpolator.
        final ConfigurationStringLookup strings = new ConfigurationStringLookup();
        strings.addProperty("home", home(config));
        strings.addProperty("uri", uri(config));
        ConfigurationInterpolator.registerGlobalLookup("sbc", strings);
        // Load the RestComm configuration file.
        Configuration xml = null;
        try {
            xml = new XMLConfiguration(path);
        } catch (final ConfigurationException exception) {
            logger.error(exception);
        }
        xml.setProperty("runtime-settings.home-directory", home(config));
        xml.setProperty("runtime-settings.root-uri", uri(config));
        context.setAttribute(Configuration.class.getName(), xml);
        // Initialize global dependencies.
        final ClassLoader loader = getClass().getClassLoader();
        // Create the actor system.
        final Config settings = ConfigFactory.load();
        
        logger.info("Ext IP:"+xml.getString("runtime-settings.external-ip"));
        Version.printVersion();
        
    }


    

    private String uri(final ServletConfig config) {
        return config.getServletContext().getContextPath();
    }
}
