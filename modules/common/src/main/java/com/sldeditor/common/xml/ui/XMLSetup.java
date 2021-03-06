//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: [TEXT REMOVED by maven-replacer-plugin]
//


package com.sldeditor.common.xml.ui;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XMLSetup complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XMLSetup"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FeatureTypeStyle" type="{}XMLIndex" minOccurs="0"/&gt;
 *         &lt;element name="Layer" type="{}XMLIndex" minOccurs="0"/&gt;
 *         &lt;element name="Rule" type="{}XMLIndex" minOccurs="0"/&gt;
 *         &lt;element name="Style" type="{}XMLIndex" minOccurs="0"/&gt;
 *         &lt;element name="Symbolizer" type="{}XMLIndex" minOccurs="0"/&gt;
 *         &lt;element name="SymbolizerDetail" type="{}XMLIndex" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="expectedPanel" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XMLSetup", propOrder = {
    "featureTypeStyle",
    "layer",
    "rule",
    "style",
    "symbolizer",
    "symbolizerDetail"
})
public class XMLSetup {

    @XmlElement(name = "FeatureTypeStyle")
    protected XMLIndex featureTypeStyle;
    @XmlElement(name = "Layer")
    protected XMLIndex layer;
    @XmlElement(name = "Rule")
    protected XMLIndex rule;
    @XmlElement(name = "Style")
    protected XMLIndex style;
    @XmlElement(name = "Symbolizer")
    protected XMLIndex symbolizer;
    @XmlElement(name = "SymbolizerDetail")
    protected XMLIndex symbolizerDetail;
    @XmlAttribute(name = "expectedPanel")
    protected String expectedPanel;
    @XmlAttribute(name = "enabled")
    protected Boolean enabled;

    /**
     * Gets the value of the featureTypeStyle property.
     * 
     * @return
     *     possible object is
     *     {@link XMLIndex }
     *     
     */
    public XMLIndex getFeatureTypeStyle() {
        return featureTypeStyle;
    }

    /**
     * Sets the value of the featureTypeStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLIndex }
     *     
     */
    public void setFeatureTypeStyle(XMLIndex value) {
        this.featureTypeStyle = value;
    }

    /**
     * Gets the value of the layer property.
     * 
     * @return
     *     possible object is
     *     {@link XMLIndex }
     *     
     */
    public XMLIndex getLayer() {
        return layer;
    }

    /**
     * Sets the value of the layer property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLIndex }
     *     
     */
    public void setLayer(XMLIndex value) {
        this.layer = value;
    }

    /**
     * Gets the value of the rule property.
     * 
     * @return
     *     possible object is
     *     {@link XMLIndex }
     *     
     */
    public XMLIndex getRule() {
        return rule;
    }

    /**
     * Sets the value of the rule property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLIndex }
     *     
     */
    public void setRule(XMLIndex value) {
        this.rule = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link XMLIndex }
     *     
     */
    public XMLIndex getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLIndex }
     *     
     */
    public void setStyle(XMLIndex value) {
        this.style = value;
    }

    /**
     * Gets the value of the symbolizer property.
     * 
     * @return
     *     possible object is
     *     {@link XMLIndex }
     *     
     */
    public XMLIndex getSymbolizer() {
        return symbolizer;
    }

    /**
     * Sets the value of the symbolizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLIndex }
     *     
     */
    public void setSymbolizer(XMLIndex value) {
        this.symbolizer = value;
    }

    /**
     * Gets the value of the symbolizerDetail property.
     * 
     * @return
     *     possible object is
     *     {@link XMLIndex }
     *     
     */
    public XMLIndex getSymbolizerDetail() {
        return symbolizerDetail;
    }

    /**
     * Sets the value of the symbolizerDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLIndex }
     *     
     */
    public void setSymbolizerDetail(XMLIndex value) {
        this.symbolizerDetail = value;
    }

    /**
     * Gets the value of the expectedPanel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExpectedPanel() {
        return expectedPanel;
    }

    /**
     * Sets the value of the expectedPanel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExpectedPanel(String value) {
        this.expectedPanel = value;
    }

    /**
     * Gets the value of the enabled property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEnabled() {
        if (enabled == null) {
            return true;
        } else {
            return enabled;
        }
    }

    /**
     * Sets the value of the enabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEnabled(Boolean value) {
        this.enabled = value;
    }

}
