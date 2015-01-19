/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.model.dataformat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.camel.CamelContext;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.spi.Label;
import org.apache.camel.spi.RouteContext;
import org.apache.camel.util.ObjectHelper;

/**
 * Json data format
 *
 * @version 
 */
@Label("dataformat,transformation")
@XmlRootElement(name = "json")
@XmlAccessorType(XmlAccessType.FIELD)
public class JsonDataFormat extends DataFormatDefinition {
    @XmlAttribute
    private Boolean prettyPrint;
    @XmlAttribute
    private JsonLibrary library = JsonLibrary.XStream;
    @XmlAttribute
    private String unmarshalTypeName;
    @XmlTransient
    private Class<?> unmarshalType;
    @XmlAttribute
    private Class<?> jsonView;
    @XmlAttribute
    private String include;
    @XmlAttribute
    private Boolean allowJmsType;
    @XmlAttribute
    private String collectionTypeName;
    @XmlTransient
    private Class<?> collectionType;
    @XmlAttribute
    private Boolean useList;
    @XmlAttribute
    private Boolean enableJaxbAnnotationModule;

    public JsonDataFormat() {
        super("json");
    }

    public JsonDataFormat(JsonLibrary library) {
        this.library = library;
    }

    public Boolean getPrettyPrint() {
        return prettyPrint;
    }

    /**
     * To enable pretty printing output nicely formatted.
     * <p/>
     * Is by default false.
     */
    public void setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public String getUnmarshalTypeName() {
        return unmarshalTypeName;
    }

    /**
     * Class name of the java type to use when unarmshalling
     */
    public void setUnmarshalTypeName(String unmarshalTypeName) {
        this.unmarshalTypeName = unmarshalTypeName;
    }

    public Class<?> getUnmarshalType() {
        return unmarshalType;
    }

    /**
     * Class of the java type to use when unarmshalling
     */
    public void setUnmarshalType(Class<?> unmarshalType) {
        this.unmarshalType = unmarshalType;
    }

    public JsonLibrary getLibrary() {
        return library;
    }

    /**
     * Which json library to use such.
     * <p/>
     * Is by default xstream
     */
    public void setLibrary(JsonLibrary library) {
        this.library = library;
    }

    public Class<?> getJsonView() {
        return jsonView;
    }

    /**
     * When marshalling a POJO to JSON you might want to exclude certain fields from the JSON output.
     * With Jackson you can use JSON views to accomplish this. This option is to refer to the class
     * which has @JsonView annotations
     */
    public void setJsonView(Class<?> jsonView) {
        this.jsonView = jsonView;
    }

    public String getInclude() {
        return include;
    }

    /**
     * If you want to marshal a pojo to JSON, and the pojo has some fields with null values.
     * And you want to skip these null values, you can set this option to <tt>NOT_NULL</tt>
     */
    public void setInclude(String include) {
        this.include = include;
    }

    public Boolean getAllowJmsType() {
        return allowJmsType;
    }

    /**
     * Used for JMS users to allow the JMSType header from the JMS spec to specify a FQN classname
     * to use to unmarshal to.
     */
    public void setAllowJmsType(Boolean allowJmsType) {
        this.allowJmsType = allowJmsType;
    }

    public String getCollectionTypeName() {
        return collectionTypeName;
    }

    /**
     * Refers to a custom collection type to lookup in the registry to use. This option should rarely be used, but allows
     * to use different collection types than java.util.Collection based as default.
     */
    public void setCollectionTypeName(String collectionTypeName) {
        this.collectionTypeName = collectionTypeName;
    }

    public Boolean getUseList() {
        return useList;
    }

    /**
     * To unarmshal to a List of Map or a List of Pojo.
     */
    public void setUseList(Boolean useList) {
        this.useList = useList;
    }

    public Boolean getEnableJaxbAnnotationModule() {
        return enableJaxbAnnotationModule;
    }

    /**
     * Whether to enable the JAXB annotations module when using jackson. When enabled then JAXB annotations
     * can be used by Jackson.
     */
    public void setEnableJaxbAnnotationModule(Boolean enableJaxbAnnotationModule) {
        this.enableJaxbAnnotationModule = enableJaxbAnnotationModule;
    }

    @Override
    protected DataFormat createDataFormat(RouteContext routeContext) {
        if (library == JsonLibrary.XStream) {
            setProperty(routeContext.getCamelContext(), this, "dataFormatName", "json-xstream");
        } else if (library == JsonLibrary.Jackson) {
            setProperty(routeContext.getCamelContext(), this, "dataFormatName", "json-jackson");
        } else {
            setProperty(routeContext.getCamelContext(), this, "dataFormatName", "json-gson");
        }

        if (unmarshalType == null && unmarshalTypeName != null) {
            try {
                unmarshalType = routeContext.getCamelContext().getClassResolver().resolveMandatoryClass(unmarshalTypeName);
            } catch (ClassNotFoundException e) {
                throw ObjectHelper.wrapRuntimeCamelException(e);
            }
        }
        if (collectionType == null && collectionTypeName != null) {
            try {
                collectionType = routeContext.getCamelContext().getClassResolver().resolveMandatoryClass(collectionTypeName);
            } catch (ClassNotFoundException e) {
                throw ObjectHelper.wrapRuntimeCamelException(e);
            }
        }

        return super.createDataFormat(routeContext);
    }

    @Override
    protected void configureDataFormat(DataFormat dataFormat, CamelContext camelContext) {
        if (unmarshalType != null) {
            setProperty(camelContext, dataFormat, "unmarshalType", unmarshalType);
        }
        if (prettyPrint != null) {
            setProperty(camelContext, dataFormat, "prettyPrint", prettyPrint);
        }
        if (jsonView != null) {
            setProperty(camelContext, dataFormat, "jsonView", jsonView);
        }
        if (include != null) {
            setProperty(camelContext, dataFormat, "include", include);
        }
        if (allowJmsType != null) {
            setProperty(camelContext, dataFormat, "allowJmsType", allowJmsType);
        }
        if (collectionType != null) {
            setProperty(camelContext, dataFormat, "collectionType", collectionType);
        }
        if (useList != null) {
            setProperty(camelContext, dataFormat, "useList", useList);
        }
        if (enableJaxbAnnotationModule != null) {
            setProperty(camelContext, dataFormat, "enableJaxbAnnotationModule", enableJaxbAnnotationModule);
        }
    }

}
