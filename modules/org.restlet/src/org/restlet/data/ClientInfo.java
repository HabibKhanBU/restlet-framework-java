/*
 * Copyright 2005-2008 Noelios Consulting.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at http://www.opensource.org/licenses/cddl1.txt If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package org.restlet.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.Resource;
import org.restlet.resource.Variant;
import org.restlet.util.Engine;
import org.restlet.util.Template;
import org.restlet.util.Variable;

/**
 * Client specific data related to a call.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public final class ClientInfo {
    /** The character set preferences. */
    private volatile List<Preference<CharacterSet>> acceptedCharacterSets;

    /** The encoding preferences. */
    private volatile List<Preference<Encoding>> acceptedEncodings;

    /** The language preferences. */
    private volatile List<Preference<Language>> acceptedLanguages;

    /** The media preferences. */
    private volatile List<Preference<MediaType>> acceptedMediaTypes;

    /** The IP addresses. */
    private volatile List<String> addresses;

    /** The agent name. */
    private volatile String agent;

    /** The main product data taken from the agent name. */
    private volatile Product agentMainProduct;

    /** The list of product tokens taken from the agent name. */
    private volatile List<Product> agentProducts;

    /** The port number. */
    private volatile int port;

    /**
     * Constructor.
     */
    public ClientInfo() {
        this.addresses = null;
        this.agent = null;
        this.port = -1;
        this.acceptedCharacterSets = null;
        this.acceptedEncodings = null;
        this.acceptedLanguages = null;
        this.acceptedMediaTypes = null;
        this.agentProducts = null;
    }

    /**
     * Returns the modifiable list of character set preferences. Creates a new
     * instance if no one has been set.
     * 
     * @return The character set preferences.
     */
    public List<Preference<CharacterSet>> getAcceptedCharacterSets() {
        if (this.acceptedCharacterSets == null)
            this.acceptedCharacterSets = new ArrayList<Preference<CharacterSet>>();
        return this.acceptedCharacterSets;
    }

    /**
     * Returns the modifiable list of encoding preferences. Creates a new
     * instance if no one has been set.
     * 
     * @return The encoding preferences.
     */
    public List<Preference<Encoding>> getAcceptedEncodings() {
        if (this.acceptedEncodings == null)
            this.acceptedEncodings = new ArrayList<Preference<Encoding>>();
        return this.acceptedEncodings;
    }

    /**
     * Returns the modifiable list of language preferences. Creates a new
     * instance if no one has been set.
     * 
     * @return The language preferences.
     */
    public List<Preference<Language>> getAcceptedLanguages() {
        if (this.acceptedLanguages == null)
            this.acceptedLanguages = new ArrayList<Preference<Language>>();
        return this.acceptedLanguages;
    }

    /**
     * Returns the modifiable list of media type preferences. Creates a new
     * instance if no one has been set.
     * 
     * @return The media type preferences.
     */
    public List<Preference<MediaType>> getAcceptedMediaTypes() {
        if (this.acceptedMediaTypes == null)
            this.acceptedMediaTypes = new ArrayList<Preference<MediaType>>();
        return this.acceptedMediaTypes;
    }

    /**
     * Returns the client's IP address which is the first address in the list of
     * client addresses, if this list exists and isn't empty.
     * 
     * @return The client's IP address.
     */
    public String getAddress() {
        return (this.addresses == null) ? null
                : (this.addresses.isEmpty() ? null : this.addresses.get(0));
    }

    /**
     * Returns the modifiable list of client IP addresses.<br>
     * <br>
     * The first address is the one of the immediate client component as
     * returned by the getClientAdress() method and the last address should
     * correspond to the origin client (frequently a user agent).<br>
     * <br>
     * This is useful when the user agent is separated from the origin server by
     * a chain of intermediary components. Creates a new instance if no one has
     * been set.
     * 
     * @return The client IP addresses.
     */
    public List<String> getAddresses() {
        if (this.addresses == null)
            this.addresses = new ArrayList<String>();
        return this.addresses;
    }

    /**
     * Returns the agent name (ex: "Noelios Restlet Engine/1.1").
     * 
     * @return The agent name.
     */
    public String getAgent() {
        return this.agent;
    }

    /**
     * Returns a list of attributes taken from the name of the user agent.
     * 
     * @return A list of attributes taken from the name of the user agent.
     */
    public Map<String, Object> getAgentAttributes() {
        Map<String, Object> result = new HashMap<String, Object>();

        if (this.agentMainProduct == null) {
            // Loop on a list of user-agent templates until a template match
            // the current user-agent string. The list of templates is
            // located in a file named "agent.properties" available on
            // the classpath.
            // Soem defined variables are used in order to catch the name,
            // version and facultative comment. Respectively, these
            // variables are called "agentName", "agentVersion" and
            // "agentComment".
            URL userAgentPropertiesUrl = Engine.getClassLoader().getResource(
                    "agent.properties");
            if (userAgentPropertiesUrl != null) {
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(
                            userAgentPropertiesUrl.openStream(),
                            CharacterSet.UTF_8.getName()));
                    Template template = null;
                    // Predefined variables.
                    Variable agentName = new Variable(Variable.TYPE_TOKEN);
                    Variable agentVersion = new Variable(Variable.TYPE_TOKEN);
                    Variable agentComment = new Variable(Variable.TYPE_COMMENT);
                    Variable agentCommentAttribute = new Variable(
                            Variable.TYPE_COMMENT_ATTRIBUTE);
                    Variable facultativeData = new Variable(Variable.TYPE_ALL,
                            null, false, false);
                    String line = reader.readLine();
                    for (; line != null; line = reader.readLine()) {
                        if (line.trim().length() > 0
                                && !line.trim().startsWith("#")) {
                            template = new Template(line, Template.MODE_EQUALS);
                            // Update the predefined variables.
                            template.getVariables().put("agentName", agentName);
                            template.getVariables().put("agentVersion",
                                    agentVersion);
                            template.getVariables().put("agentComment",
                                    agentComment);
                            template.getVariables().put("agentOs",
                                    agentCommentAttribute);
                            template.getVariables().put("commentAttribute",
                                    agentCommentAttribute);
                            template.getVariables().put("facultativeData",
                                    facultativeData);
                            // Parse the template
                            if (template.parse(getAgent(), result) > -1) {
                                break;
                            }

                        }
                    }
                } catch (IOException e) {
                    return result;
                }
            }
        }

        return result;
    }

    /**
     * Returns a Product object based on the name of the user agent.
     * 
     * @return A Product object based on name of the user agent.
     */
    public Product getAgentMainProduct() {
        if (this.agentMainProduct == null) {
            // Loop on a list of user-agent templates until a template match
            // the current user-agent string. The list of templates is
            // located in a file named "agent.properties" available on
            // the classpath.
            // Soem defined variables are used in order to catch the name,
            // version and facultative comment. Respectively, these
            // variables are called "agentName", "agentVersion" and
            // "agentComment".
            URL userAgentPropertiesUrl = Engine.getClassLoader().getResource(
                    "agent.properties");
            if (userAgentPropertiesUrl != null) {
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(
                            userAgentPropertiesUrl.openStream(),
                            CharacterSet.UTF_8.getName()));
                    Template template = null;
                    // Predefined variables.
                    Variable agentName = new Variable(Variable.TYPE_TOKEN);
                    Variable agentVersion = new Variable(Variable.TYPE_TOKEN);
                    Variable agentComment = new Variable(Variable.TYPE_COMMENT);
                    String line = reader.readLine();
                    for (; line != null; line = reader.readLine()) {
                        if (line.trim().length() > 0
                                && !line.trim().startsWith("#")) {
                            template = new Template(line, Template.MODE_EQUALS);
                            // Update the predefined variables.
                            template.getVariables().put("agentName", agentName);
                            template.getVariables().put("agentVersion",
                                    agentVersion);
                            template.getVariables().put("agentComment",
                                    agentComment);
                            Map<String, Object> map = new HashMap<String, Object>();
                            if (template.parse(getAgent(), map) > -1) {
                                this.agentMainProduct = new Product(
                                        (String) map.get("agentName"),
                                        (String) map.get("agentVersion"),
                                        (String) map.get("agentComment"));
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    return this.agentMainProduct;
                }
            }
        }

        return this.agentMainProduct;
    }

    /**
     * Returns the name of the user agent.
     * 
     * @return The name of the user agent.
     */
    public String getAgentName() {
        if (getAgentMainProduct() != null) {
            return getAgentMainProduct().getName();
        }

        return null;
    }

    /**
     * Returns the list of product tokens from the user agent name.
     * 
     * @return The list of product tokens from the user agent name.
     */
    public List<Product> getAgentProducts() {
        if (this.agentProducts == null) {
            this.agentProducts = Engine.getInstance()
                    .parseUserAgent(getAgent());
        }
        return this.agentProducts;
    }

    /**
     * Returns the version of the user agent.
     * 
     * @return The version of the user agent.
     */
    public String getAgentVersion() {
        if (getAgentMainProduct() != null) {
            return getAgentMainProduct().getVersion();
        }
        return null;

    }

    /**
     * Returns the port number which sent the call. If no port is specified, -1
     * is returned.
     * 
     * @return The port number which sent the call.
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Returns the best variant for a given resource according the the client
     * preferences: accepted languages, accepted character sets, accepted media
     * types and accepted encodings.<br>
     * A default language is provided in case the variants don't match the
     * client preferences.
     * 
     * @param variants
     *                The list of variants to compare.
     * @param defaultLanguage
     *                The default language.
     * @return The best variant.
     * @see <a
     *      href="http://httpd.apache.org/docs/2.2/en/content-negotiation.html#algorithm">Apache
     *      content negotiation algorithm</a>
     */
    public Variant getPreferredVariant(List<Variant> variants,
            Language defaultLanguage) {
        return Engine.getInstance().getPreferredVariant(this, variants,
                defaultLanguage);
    }

    /**
     * Returns the best variant for a given resource according the the client
     * preferences.<br>
     * A default language is provided in case the resource's variants don't
     * match the client preferences.
     * 
     * @param resource
     *                The resource for which the best representation needs to be
     *                set.
     * @param defaultLanguage
     *                The default language.
     * @return The best variant.
     * @see <a
     *      href="http://httpd.apache.org/docs/2.2/en/content-negotiation.html#algorithm">Apache
     *      content negotiation algorithm</a>
     */
    public Variant getPreferredVariant(Resource resource,
            Language defaultLanguage) {
        return getPreferredVariant(resource.getVariants(), defaultLanguage);
    }

    /**
     * Sets the character set preferences.
     * 
     * @param acceptedCharacterSets
     *                The character set preferences.
     */
    public void setAcceptedCharacterSets(
            List<Preference<CharacterSet>> acceptedCharacterSets) {
        this.acceptedCharacterSets = acceptedCharacterSets;
    }

    /**
     * Sets the encoding preferences.
     * 
     * @param acceptedEncodings
     *                The encoding preferences.
     */
    public void setAcceptedEncodings(
            List<Preference<Encoding>> acceptedEncodings) {
        this.acceptedEncodings = acceptedEncodings;
    }

    /**
     * Sets the language preferences.
     * 
     * @param acceptedLanguages
     *                The language preferences.
     */
    public void setAcceptedLanguages(
            List<Preference<Language>> acceptedLanguages) {
        this.acceptedLanguages = acceptedLanguages;
    }

    /**
     * Sets the media type preferences.
     * 
     * @param acceptedMediaTypes
     *                The media type preferences.
     */
    public void setAcceptedMediaTypes(
            List<Preference<MediaType>> acceptedMediaTypes) {
        this.acceptedMediaTypes = acceptedMediaTypes;
    }

    /**
     * Sets the client's IP address.
     * 
     * @param address
     *                The client's IP address.
     */
    public void setAddress(String address) {
        if (getAddresses().isEmpty()) {
            getAddresses().add(address);
        } else {
            getAddresses().set(0, address);
        }
    }

    /**
     * Sets the list of client IP addresses.
     * 
     * @param addresses
     *                The list of client IP addresses.
     */
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses;
    }

    /**
     * Sets the agent name (ex: "Noelios Restlet Engine/1.1").
     * 
     * @param agent
     *                The agent name.
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * Sets the port number which sent the call.
     * 
     * @param port
     *                The port number which sent the call.
     */
    public void setPort(int port) {
        this.port = port;
    }

}
