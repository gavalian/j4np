/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j4np.ui.weblaf;

import com.alee.api.annotations.NotNull;
import com.alee.api.data.BoxOrientation;
import com.alee.api.data.CompassDirection;
import com.alee.api.jdk.SerializableSupplier;
import com.alee.extended.behavior.ComponentResizeBehavior;
import com.alee.extended.canvas.WebCanvas;
import com.alee.extended.dock.SidebarButtonVisibility;
import com.alee.extended.dock.WebDockablePane;
import com.alee.extended.label.WebStyledLabel;
import com.alee.extended.link.WebLink;
import com.alee.extended.memorybar.WebMemoryBar;
import com.alee.extended.overlay.AlignedOverlay;
import com.alee.extended.overlay.WebOverlay;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.WebLookAndFeel;
import com.alee.laf.rootpane.WindowState;
import com.alee.laf.scroll.ScrollPaneState;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.toolbar.WebToolBar;
import com.alee.laf.window.WebFrame;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.settings.Configuration;
import com.alee.managers.settings.SettingsManager;
import com.alee.managers.style.StyleId;
import com.alee.managers.style.StyleManager;
import com.alee.utils.XmlUtils;
import com.alee.utils.CoreSwingUtils;
import com.alee.utils.SystemUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
/**
 *
 * @author gavalian
 */
public class WebLafStudio extends WebFrame {
    private static WebLafStudio instance;
    
    
    private WebDockablePane  dockablePane;
    private StudioFrame      studioFrame;
    
    //private ExamplesFrame examplesFrame;


/**
     * Constructs new {@link DemoApplication}.
     */
    private WebLafStudio ()
    {
        super ();
        //version = new Version ( DemoApplication.class );

        setIconImages ( WebLookAndFeel.getImages () );
        //updateTitle ();

        initializeDocks ();
        initializeToolBar ();
        initializeStatus ();
        
        setDefaultCloseOperation ( WindowConstants.EXIT_ON_CLOSE );
        registerSettings ( new Configuration<WindowState> ( "application", new SerializableSupplier<WindowState> ()
        {
            @Override
            public WindowState get ()
            {
                return new WindowState ( new Dimension ( 1200, 820 ) );
            }
        } ) );
    }
    /**
     * Returns demo application instance.
     *
     * @return demo application instance
     */
    @NotNull
    public static WebLafStudio getInstance ()
    {
        if ( instance == null )
        {
            instance = new WebLafStudio ();
        }
        return instance;
    }

     /**
     * Initializes demo application dockable frames.
     */
    private void initializeDocks ()
    {
        System.out.printf("---> initializing dock");
        dockablePane = new WebDockablePane ( StyleId.dockablepaneCompact );
        dockablePane.setSidebarButtonVisibility ( SidebarButtonVisibility.anyMinimized );
        
        final WebScrollPane examplesTreeScroll = new WebScrollPane ( StyleId.scrollpaneTransparentHoveringExtending, null );
        examplesTreeScroll.registerSettings ( new Configuration<ScrollPaneState> ( "ExamplesScroll" ) );
        
        dockablePane.add(examplesTreeScroll);
        
        
        add ( dockablePane, BorderLayout.CENTER );
    }
    
    /**
     * Initializes demo application toolbar and its content.
     */
    private void initializeToolBar ()
    {
        final WebToolBar toolBar = new WebToolBar ( StyleId.toolbarAttachedNorth );
        toolBar.setFloatable ( false );

        /*toolBar.add ( new SkinChooserTool () );
        toolBar.addSeparator ();
        toolBar.add ( new OrientationChooserTool () );
        toolBar.addSeparator ();
        toolBar.add ( new LanguageChooserTool () );

        toolBar.addToEnd ( new HeatMapTool ( DemoApplication.this ) );
        toolBar.addSeparatorToEnd ();
        toolBar.addToEnd ( new MagnifierToggleTool ( DemoApplication.this ) );
*/
        add ( toolBar, BorderLayout.NORTH );
    }
    
    /**
     * Initializes status bar and its content.
     */
    private void initializeStatus ()
    {
        final WebStatusBar statusBar = new WebStatusBar ();

        //statusBar.add ( new WebLink ( DemoStyles.resourceLink, DemoIcons.java19, "demo.statusbar.resources.weblaf",
         //       new UrlLinkAction ( WEBLAF_SITE ) ) );

        //statusBar.add ( new WebLink ( DemoStyles.resourceLink, DemoIcons.github19, "demo.statusbar.resources.github",
        //        new UrlLinkAction ( WEBLAF_GITHUB ) ) );

        //statusBar.add ( new WebLink ( DemoStyles.resourceLink, DemoIcons.gitter19, "demo.statusbar.resources.gitter",
        //        new UrlLinkAction ( WEBLAF_GITTER ) ) );

        //final WebStyledLabel jvm = new WebStyledLabel ( DemoIcons.java19, SwingConstants.CENTER );
        //jvm.setLanguage ( "demo.statusbar.jvm", SystemUtils.getJavaVersion ().toString () );
        //statusBar.addToEnd ( jvm );

        statusBar.addSpacingToEnd ( 10 );

        final WebOverlay memoryBarOverlay = new WebOverlay ();

        memoryBarOverlay.setContent ( new WebMemoryBar ().setPreferredWidth ( 150 ) );

        final WebCanvas resizeCorner = new WebCanvas ( StyleId.canvasGripperSE );
        new ComponentResizeBehavior ( resizeCorner, CompassDirection.southEast ).install ();

        memoryBarOverlay.addOverlay ( new AlignedOverlay (
                resizeCorner,
                BoxOrientation.right,
                BoxOrientation.bottom,
                new Insets ( 0, 0, -1, -1 )
        ) );

        statusBar.addToEnd ( memoryBarOverlay );

        add ( statusBar, BorderLayout.SOUTH );

        // Custom status bar margin for notification manager
        NotificationManager.setMargin ( 0, 0, statusBar.getPreferredSize ().height, 0 );
    }

    public void display ()
    {
        setVisible ( true );
        //examplesFrame.requestFocusInWindow ();
    }
        
    public static void main(String[] args){
    
        CoreSwingUtils.enableEventQueueLogging ();
        CoreSwingUtils.invokeLater ( new Runnable ()
        {
            @Override
            public void run ()
            {
                // Configuring settings location
                SettingsManager.setDefaultSettingsDirName ( ".weblaf-demo" );
                SettingsManager.setDefaultSettingsGroup ( "WebLookAndFeelDemo" );
                SettingsManager.setSaveOnChange ( true );

                // Adding demo data aliases before styles using it are read
                //XmlUtils.processAnnotations ( FeatureStateBackground.class );

                // Installing Look and Feel
                WebLookAndFeel.setForceSingleEventsThread ( true );
                WebLookAndFeel.install ();

                // Saving skins for reference
                //skins = CollectionUtils.asList ( StyleManager.getSkin (), new WebDarkSkin () );

                // Custom ThreadGroup for demo application
                //TaskManager.registerGroup ( new DemoTaskGroup () );

                // Adding demo application skin extensions
                // They contain all custom styles demo application uses
                //StyleManager.addExtensions ( new DemoAdaptiveExtension (), new DemoLightSkinExtension (), new DemoDarkSkinExtension () );

                // Adding demo language dictionary
                //LanguageManager.addDictionary ( new Dictionary (
                //        new ClassResource ( DemoApplication.class, "language/demo-language.xml" )
                //) );

                // Registering listener to update current Locale according to language changes
                //LanguageManager.addLanguageListener ( new LanguageLocaleUpdater () );

                // Initializing demo application managers
                //ExamplesManager.initialize ();

                // Starting demo application
                WebLafStudio.getInstance ().display ();
            }
        } );
    }
}
