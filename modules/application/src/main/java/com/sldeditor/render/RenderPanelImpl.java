/*
 * SLD Editor - The Open Source Java SLD Editor
 *
 * Copyright (C) 2016, SCISYS UK Limited
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sldeditor.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.swing.JPanel;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.resources.CRSUtilities;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.sldeditor.common.console.ConsoleManager;
import com.sldeditor.common.data.SelectedSymbol;
import com.sldeditor.common.localisation.Localisation;
import com.sldeditor.common.output.SLDOutputInterface;
import com.sldeditor.common.preferences.PrefManager;
import com.sldeditor.common.preferences.iface.PrefUpdateInterface;
import com.sldeditor.datasource.DataSourceInterface;
import com.sldeditor.datasource.DataSourceUpdatedInterface;
import com.sldeditor.datasource.RenderSymbolInterface;
import com.sldeditor.datasource.impl.DataSourceFactory;
import com.sldeditor.datasource.impl.GeometryTypeEnum;
import com.sldeditor.filter.v2.envvar.EnvironmentVariableManager;
import com.sldeditor.filter.v2.envvar.WMSEnvVarValues;
import com.sldeditor.ui.render.RuleRenderOptions;

/**
 * Panel in which the rendered symbol is drawn.
 *
 * @author Robert Ward (SCISYS)
 */
public class RenderPanelImpl extends JPanel implements RenderSymbolInterface, PrefUpdateInterface, DataSourceUpdatedInterface
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant BOUNDINGBOX_BUFFER_ANGLE. */
    private static final double BOUNDINGBOX_BUFFER_ANGLE = 0.1;

    /** The Constant BOUNDINGBOX_BUFFER_LINEAR. */
    private static final double BOUNDINGBOX_BUFFER_LINEAR = 1000.0;

    /** The Constant BOUNDINGBOX_BUFFER_MIN_ANGLE. */
    private static final double BOUNDINGBOX_BUFFER_MIN_ANGLE = 1.0;

    /** The Constant BOUNDINGBOX_BUFFER_MIN_LINEAR. */
    private static final double BOUNDINGBOX_BUFFER_MIN_LINEAR = 1000.0;

    /** The Constant BOUNDINGBOX_BUFFER_THRESHOLD_LINEAR. */
    private static final double BOUNDINGBOX_BUFFER_THRESHOLD_LINEAR = 1.0;

    /** The Constant BOUNDINGBOX_BUFFER_THRESHOLD_ANGLE. */
    private static final double BOUNDINGBOX_BUFFER_THRESHOLD_ANGLE = 0.001;

    /** The Constant NO_DATA_SOURCE. */
    private static final String NO_DATA_SOURCE = Localisation.getString(RenderPanelImpl.class, "RenderPanelImpl.noDataSource");

    /** The Constant INVALID_SYMBOL_STRING. */
    private static final String INVALID_SYMBOL_STRING = Localisation.getString(RenderPanelImpl.class, "RenderPanelImpl.invalidSymbol");

    /** The Constant DPI. */
    private static final int DPI = 96;

    /** The Constant ST_WIDTH, width of the area containing the rendered image. */
    private static final int ST_WIDTH = 200;

    /** The Constant ST_HEIGHT, height of the area containing the rendered image. */
    private static final int ST_HEIGHT = 200;

    /**  The image to display. */
    private BufferedImage bImage = null;

    /** The feature list. */
    private FeatureSource<SimpleFeatureType, SimpleFeature> featureList = null;

    /** The sld output list. */
    private List<SLDOutputInterface> sldOutputList = new ArrayList<SLDOutputInterface>();

    /** The use anti alias. */
    private boolean useAntiAlias = false;

    /** The render symbol. */
    private RenderSymbol renderSymbol = new RenderSymbol();

    /** The data loaded. */
    private boolean dataLoaded = false;

    /** The vector renderer. */
    private GTRenderer vectorRenderer = new StreamingRenderer();

    /** The geometry type. */
    private GeometryTypeEnum geometryType = GeometryTypeEnum.UNKNOWN;

    /** The valid symbol. */
    private boolean validSymbol = false;

    /** The WMS environment variable values. */
    private WMSEnvVarValues wmsEnvVarValues = new WMSEnvVarValues();

    /** The background colour. */
    private Color backgroundColour = Color.WHITE;
    
    /**
     * Instantiates a new render panel.
     */
    public RenderPanelImpl() {

        setBounds(0, 0, ST_WIDTH, ST_HEIGHT);
        setPreferredSize(new Dimension(ST_WIDTH, ST_HEIGHT));

        wmsEnvVarValues.setImageWidth(ST_WIDTH);
        wmsEnvVarValues.setImageHeight(ST_HEIGHT);

        PrefManager.getInstance().addListener(this);
    }

    /**
     * Instantiates a new render panel.
     *
     * @param layout the layout
     */
    public RenderPanelImpl(LayoutManager layout) {
        super(layout);
    }

    /**
     * Instantiates a new render panel.
     *
     * @param isDoubleBuffered the is double buffered
     */
    public RenderPanelImpl(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }

    /**
     * Instantiates a new render panel.
     *
     * @param layout the layout
     * @param isDoubleBuffered the is double buffered
     */
    public RenderPanelImpl(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
    }

    /**
     * Paint component.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(backgroundColour);
        g.fillRect(0, 0, ST_WIDTH - 1, ST_HEIGHT - 1);

        if(validSymbol)
        {
            if(bImage != null)
            {
                g.drawImage(bImage, 0, 0, null);
            }
        }
        else
        {
            String displayString = dataLoaded ? INVALID_SYMBOL_STRING : NO_DATA_SOURCE;
            g.setColor(Color.black);
            Rectangle2D bounds = g.getFontMetrics().getStringBounds(displayString, g);
            double x = (ST_WIDTH / 2) - (bounds.getWidth() / 2);
            double y = (ST_HEIGHT / 2) - (bounds.getHeight() / 2);
            g.drawString(displayString, (int) x, (int)y);
        }
        g.setColor(Color.black);
        g.drawRect(0, 0, ST_WIDTH - 1, ST_HEIGHT - 1);
    }

    /**
     * Render symbol.
     *
     * @param style the style
     */
    private void renderSymbol(Style style)
    {
        if(!dataLoaded)
        {
            createFeature();
        }

        Rectangle imageSize = new Rectangle(0, 0, ST_WIDTH, this.getHeight());

        switch(geometryType)
        {
        case RASTER:
            renderRasterMap(imageSize, style, DPI);
            break;
        case POINT:
        case LINE:
        case POLYGON:
            renderVectorMap(featureList, imageSize, style, DPI);
            break;
        default:
            validSymbol = false;
            break;
        }

        repaint();
    }

    /**
     * Render raster map.
     *
     * @param imageSize the image size
     * @param style the style
     * @param dpi the dpi
     */
    private void renderRasterMap(Rectangle imageSize, Style style, int dpi) {
        DataSourceInterface dataSource = DataSourceFactory.getDataSource();
        AbstractGridCoverage2DReader gridCoverage = dataSource.getGridCoverageReader();

        GridReaderLayer rasterLayer = null;
        List<Layer> layerList = new ArrayList<Layer>();
        if(style != null)
        {
            rasterLayer = new GridReaderLayer(gridCoverage, style);
            layerList.add(rasterLayer);
        }

        boolean hasGeometry = true;

        MapContent map = new MapContent();
        map.addLayers(layerList);
        try {
            Map<Object,Object> hints = new HashMap<Object,Object>();
            hints.put(StreamingRenderer.DPI_KEY, dpi);
            // This ensures all the labelling is cleared
            hints.put(StreamingRenderer.LABEL_CACHE_KEY, new LabelCacheImpl());

            vectorRenderer.setRendererHints(hints);
            vectorRenderer.setMapContent(map);
            BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();

            if(useAntiAlias)
            {
                graphics.setRenderingHints(new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));
            }

            try {
                if(!hasGeometry){
                    graphics.setColor(Color.BLACK);
                    int y = imageSize.height / 2;
                    Font font = new Font(Font.SERIF,Font.BOLD, 14);
                    graphics.setFont(font);
                    graphics.drawString(Localisation.getString(RenderPanelImpl.class, "RenderPanelImpl.error1"), 10, y - 14);
                }
                else {
                    if(rasterLayer != null)
                    {
                        vectorRenderer.paint(graphics, imageSize, rasterLayer.getBounds());
                    }

                    this.bImage = image;
                }
            }
            finally {
                graphics.dispose();
            }
        }
        finally {
            map.dispose();
        }
    }

    /**
     * Render vector map.
     *
     * @param features the results
     * @param imageSize the image size
     * @param style the style
     * @param dpi the dpi
     */
    private void renderVectorMap(FeatureSource<SimpleFeatureType, SimpleFeature> features,
            Rectangle imageSize,
            Style style,
            int dpi)
    {
        List<Layer> layerList = new ArrayList<Layer>();
        if(style != null)
        {
            FeatureLayer featureLayer = new FeatureLayer(features, style);
            layerList.add(featureLayer);
        }

        boolean hasGeometry = false;
        ReferencedEnvelope bounds = null;

        if(features != null)
        {
            bounds = calculateBounds();

            wmsEnvVarValues.setMapBounds(bounds);

            EnvironmentVariableManager.getInstance().setWMSEnvVarValues(wmsEnvVarValues);

            if(features.getSchema() != null)
            {
                hasGeometry = (features.getSchema().getGeometryDescriptor() != null);
            }
        }

        internal_renderMap(layerList, bounds, imageSize, hasGeometry, dpi);
    }

    /**
     * Calculate bounds.
     *
     * @return the referenced envelope
     */
    private ReferencedEnvelope calculateBounds() {
        ReferencedEnvelope bounds = null;

        try {
            bounds = featureList.getBounds();

            if(bounds == null)
            {
                // It could be that the above call was too costly!
                bounds = featureList.getFeatures().getBounds();
            }

            if(bounds.getCoordinateReferenceSystem() == null)
            {
                // We need a coordinate reference system set otherwise transformations fail to render
                bounds = ReferencedEnvelope.create(bounds, DefaultGeographicCRS.WGS84);
            }

            if(bounds != null)
            {
                Unit<?> unit = CRSUtilities.getUnit(bounds.getCoordinateReferenceSystem().getCoordinateSystem());

                double width;
                double height;
                if(unit == NonSI.DEGREE_ANGLE)
                {
                    width = (bounds.getWidth() < BOUNDINGBOX_BUFFER_THRESHOLD_ANGLE) ? BOUNDINGBOX_BUFFER_MIN_ANGLE : (bounds.getWidth() * BOUNDINGBOX_BUFFER_ANGLE);
                    height = (bounds.getHeight() < BOUNDINGBOX_BUFFER_THRESHOLD_ANGLE) ? BOUNDINGBOX_BUFFER_MIN_ANGLE : (bounds.getHeight() * BOUNDINGBOX_BUFFER_ANGLE);
                }
                else
                {
                    width = (bounds.getWidth() < BOUNDINGBOX_BUFFER_THRESHOLD_LINEAR) ? BOUNDINGBOX_BUFFER_MIN_LINEAR : (bounds.getWidth() * BOUNDINGBOX_BUFFER_LINEAR);
                    height = (bounds.getHeight() < BOUNDINGBOX_BUFFER_THRESHOLD_LINEAR) ? BOUNDINGBOX_BUFFER_MIN_LINEAR : (bounds.getHeight() * BOUNDINGBOX_BUFFER_LINEAR);
                }

                bounds.expandBy(width, height);
            }
        }
        catch (IOException e) {
            ConsoleManager.getInstance().exception(this, e);
        }
        return bounds;
    }

    /**
     * Internal_render map.
     *
     * @param layers the layers
     * @param bounds the bounds
     * @param imageSize the image size
     * @param hasGeometry the has geometry
     * @param dpi the dpi
     */
    private void internal_renderMap(List<Layer> layers,
            ReferencedEnvelope bounds,
            Rectangle imageSize,
            boolean hasGeometry,
            int dpi)
    {
        MapContent map = new MapContent();
        map.addLayers(layers);
        try {
            Map<Object,Object> hints = new HashMap<Object,Object>();
            hints.put(StreamingRenderer.DPI_KEY, dpi);
            // This ensures all the labelling is cleared
            hints.put(StreamingRenderer.LABEL_CACHE_KEY, new LabelCacheImpl());

            vectorRenderer.setRendererHints(hints);
            vectorRenderer.setMapContent(map);
            BufferedImage image = new BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();

            if(useAntiAlias)
            {
                graphics.setRenderingHints(new RenderingHints(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON));
            }

            try {
                if(!hasGeometry){
                    graphics.setColor(Color.BLACK);
                    int y = imageSize.height / 2;
                    Font font = new Font(Font.SERIF,Font.BOLD, 14);
                    graphics.setFont(font);
                    graphics.drawString(Localisation.getString(RenderPanelImpl.class, "RenderPanelImpl.error1"), 10, y - 14);
                }
                else {
                    vectorRenderer.paint(graphics, imageSize, bounds);

                    this.bImage = image;
                }
            }
            finally {
                graphics.dispose();
            }
        }
        finally {
            map.dispose();
        }
    }

    /**
     * Creates the feature.
     */
    private void createFeature() {

        if(geometryType == GeometryTypeEnum.UNKNOWN)
        {
            dataLoaded = false;
        }
        else
        {
            DataSourceInterface dataSource = DataSourceFactory.getDataSource();

            featureList = dataSource.getExampleFeatureSource();

            dataLoaded = true;
        }
    }

    /**
     * Render symbol.
     */
    /* (non-Javadoc)
     * @see com.sldeditor.marker.iface.RenderSymbolInterface#renderSymbol()
     */
    @Override
    public void renderSymbol() {

        validSymbol = SelectedSymbol.getInstance().isValid();

        if(validSymbol)
        {
            StyledLayerDescriptor sld = SelectedSymbol.getInstance().getSld();

            if(sld != null)
            {
                for(SLDOutputInterface sldOutput : sldOutputList)
                {
                    sldOutput.updatedSLD(sld);
                }

                renderSymbol((Style) renderSymbol.getRenderStyle(SelectedSymbol.getInstance()));
            }
            else
            {
                renderSymbol(null);
            }
        }
        else
        {
            repaint();
        }
    }

    /**
     * Adds the sld output listener.
     *
     * @param sldOutput the sld output
     */
    @Override
    public void addSLDOutputListener(SLDOutputInterface sldOutput) {
        sldOutputList.add(sldOutput);
    }

    /**
     * Use anti alias updated.
     *
     * @param value the value
     */
    /* (non-Javadoc)
     * @see com.sldeditor.preferences.PrefUpdateInterface#useAntiAliasUpdated(boolean)
     */
    @Override
    public void useAntiAliasUpdated(boolean value) {
        useAntiAlias = value;

        renderSymbol();
    }

    /**
     * Data source loaded.
     *
     * @param geometryType the geometry type
     * @param isConnectedToDataSourceFlag the is connected to data source flag
     */
    /* (non-Javadoc)
     * @see com.sldeditor.datasource.DataSourceUpdatedInterface#dataSourceLoaded(com.sldeditor.datasource.impl.GeometryTypeEnum, boolean)
     */
    @Override
    public void dataSourceLoaded(GeometryTypeEnum geometryType, boolean isConnectedToDataSourceFlag) {
        this.geometryType = geometryType;

        if(dataLoaded)
        {
            dataLoaded = false;
        }
    }

    /**
     * Gets the rule render options.
     *
     * @return the rule render options
     */
    @Override
    public RuleRenderOptions getRuleRenderOptions() {
        return renderSymbol.getRenderOptions();
    }

    /**
     * Background colour update.
     *
     * @param backgroundColour the background colour
     */
    @Override
    public void backgroundColourUpdate(Color backgroundColour) {
        this.backgroundColour = backgroundColour;
    }

}
