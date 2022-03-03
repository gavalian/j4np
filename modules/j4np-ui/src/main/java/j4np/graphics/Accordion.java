//aa
package j4np.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import twig.studio.StudioWindow;

/**
 * A JOutlookBar provides a component that is similar to a JTabbedPane, but instead of maintaining
 * tabs, it uses Outlook-style bars to control the visible component
 */
public class Accordion extends JPanel implements ActionListener
{
    
    //public static String ARROWRIGHT = "\u25b6";
    //public static String ARROWDOWN = "\u25bc";
    //public static String ARROWUP = "\u25b2";
    //public static String ARROWLEFT = "\u25c0";
    
    public static String ARROWRIGHT = "\u25b8";
    public static String ARROWDOWN = "\u25be";
    public static String ARROWUP = "\u25b4";
    public static String ARROWLEFT = "\u25c2";
    /**
     * The top panel: contains the buttons displayed on the top of the JOutlookBar
     */
    private JPanel topPanel = new JPanel( new GridLayout( 1, 1 ) );
    
    /**
     * The bottom panel: contains the buttons displayed on the bottom of the JOutlookBar
     */
    private JPanel bottomPanel = new JPanel( new GridLayout( 1, 1 ) );
    
    /**
     * A LinkedHashMap of bars: we use a linked hash map to preserve the order of the bars
     */
    private Map bars = new LinkedHashMap();
    
    /**
     * The currently visible bar (zero-based index)
     */
    private int visibleBar = 0;
    
    /**
     * A place-holder for the currently visible component
     */
    private JComponent visibleComponent = null;
    
    /**
     * Creates a new JOutlookBar; after which you should make repeated calls to
     * addBar() for each bar
     */
    public Accordion()
    {
        this.setLayout( new BorderLayout() );
        this.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, new Color(245,245,245)));
        this.add( topPanel, BorderLayout.NORTH );
        this.add( bottomPanel, BorderLayout.SOUTH );
    }
    
    /**
     * Adds the specified component to the JOutlookBar and sets the bar's name
     * 
     * @param  name      The name of the outlook bar
     * @param component parent component
     */
    public void addBar( String name, JComponent component )
    {
        BarInfo barInfo = new BarInfo( name, component );
        barInfo.getButton().addActionListener( this );
        this.bars.put( name, barInfo );
        render();
    }
    
    /**
     * Adds the specified component to the JOutlookBar and sets the bar's name
     * 
     * @param  name      The name of the outlook bar
     * @param  icon      An icon to display in the outlook bar
     * @param component parent component
     */
    public void addBar( String name, Icon icon, JComponent component )
    {
        BarInfo barInfo = new BarInfo( name, icon, component );
        barInfo.getButton().addActionListener( this );
        this.bars.put( name, barInfo );
        render();
    }
    
    /**
     * Removes the specified bar from the JOutlookBar
     * 
     * @param  name  The name of the bar to remove
     */
    public void removeBar( String name )
    {
        this.bars.remove( name );
        render();
    }
    
    /**
     * Returns the index of the currently visible bar (zero-based)
     * 
     * @return The index of the currently visible bar
     */
    public int getVisibleBar()
    {
        return this.visibleBar;
    }
    
    /**
     * Programmatically sets the currently visible bar; the visible bar
     * index must be in the range of 0 to size() - 1
     * 
     * @param  visibleBar   The zero-based index of the component to make visible
     */
    public void setVisibleBar( int visibleBar )
    {
        if( visibleBar > 0 &&
           visibleBar < this.bars.size() - 1 )
        {
            this.visibleBar = visibleBar;
            render();
        }
    }
    
    /**
     * Causes the outlook bar component to rebuild itself; this means that
     * it rebuilds the top and bottom panels of bars as well as making the
     * currently selected bar's panel visible
     */
    public void render()
    {
        // Compute how many bars we are going to have where
        int totalBars = this.bars.size();
        int topBars = this.visibleBar + 1;
        int bottomBars = totalBars - topBars;
        
        int counter = 0;
        //for(Map.Entry<String,JComponent> entry : this.bars.entrySet()){            
        //}
        // Get an iterator to walk through out bars with
        Iterator itr = this.bars.keySet().iterator();
        
        
        // Render the top bars: remove all components, reset the GridLayout to
        // hold to correct number of bars, add the bars, and "validate" it to
        // cause it to re-layout its components
        this.topPanel.removeAll();
        GridLayout topLayout = ( GridLayout )this.topPanel.getLayout();
        topLayout.setRows( topBars );
        BarInfo barInfo = null;
        for( int i=0; i<topBars; i++ )
        {
            String barName = ( String ) itr.next();
            barInfo = ( BarInfo ) this.bars.get( barName );
            this.topPanel.add( barInfo.getButton() );
            
            // Gagik Modifications
            String bt = barInfo.button.getText();
            barInfo.button.setText(bt.replace(Accordion.ARROWDOWN, Accordion.ARROWRIGHT));
            
        }
        this.topPanel.validate();
        
        
        // Render the center component: remove the current component (if there
        // is one) and then put the visible component in the center of this panel
        if( this.visibleComponent != null )
        {
            this.remove( this.visibleComponent );
        }
        this.visibleComponent = barInfo.getComponent();
        this.add( visibleComponent, BorderLayout.CENTER );
        String bt = barInfo.button.getText();
            barInfo.button.setText(bt.replace(Accordion.ARROWRIGHT, Accordion.ARROWDOWN));
        
        // Render the bottom bars: remove all components, reset the GridLayout to
        // hold to correct number of bars, add the bars, and "validate" it to
        // cause it to re-layout its components
        this.bottomPanel.removeAll();
        GridLayout bottomLayout = ( GridLayout )this.bottomPanel.getLayout();
        bottomLayout.setRows( bottomBars );
        for( int i=0; i<bottomBars; i++ )
        {
            String barName = ( String )itr.next();
            barInfo = ( BarInfo )this.bars.get( barName );
            this.bottomPanel.add( barInfo.getButton() );
            // Gagik Modifications
            String btb = barInfo.button.getText();
            barInfo.button.setText(btb.replace(Accordion.ARROWDOWN, Accordion.ARROWRIGHT));
            
        }
        this.bottomPanel.validate();
        
        
        // Validate all of our components: cause this container to re-layout its subcomponents
        this.validate();
    }
    
    /**
     * Invoked when one of our bars is selected
     */
    public void actionPerformed( ActionEvent e )
    {
        int currentBar = 0;
        for( Iterator i=this.bars.keySet().iterator(); i.hasNext(); )
        {
            String barName = ( String )i.next();
            BarInfo barInfo = ( BarInfo )this.bars.get( barName );
            if( barInfo.getButton() == e.getSource() )
            {
                // Found the selected button
                this.visibleBar = currentBar;
                render();
                return;
            }
            currentBar++;
        }
    }
    
    /**
     * Debug, dummy method
     */
    public static JPanel getDummyPanel( String name )
    {
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( new JLabel( name, JLabel.CENTER ) );
        return panel;
    }
    
    /**
     * Debug test...
     */
    public static void main( String[] args )
    {
        StudioWindow.changeLook();
        JFrame frame = new JFrame( "JOutlookBar Test" );
        Accordion outlookBar = new Accordion();
        outlookBar.addBar( Accordion.ARROWRIGHT + " Margins", getDummyPanel( "One" ) );
        outlookBar.addBar( Accordion.ARROWRIGHT + " Configuration", getDummyPanel( "Two" ) );
        outlookBar.addBar( Accordion.ARROWRIGHT + " Marker Properties" , getDummyPanel( "Three" ) );
        outlookBar.addBar( Accordion.ARROWRIGHT + " Line Properties", getDummyPanel( "Four" ) );
        outlookBar.addBar( Accordion.ARROWRIGHT + " Fill Properties", getDummyPanel( "Five" ) );
        outlookBar.setVisibleBar( 2 );
        frame.getContentPane().add( outlookBar );
        
        frame.setSize( 800, 600 );
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( d.width / 2 - 400, d.height / 2 - 300 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
    
    /**
     * Internal class that maintains information about individual Outlook bars;
     * specifically it maintains the following information:
     * 
     * name      The name of the bar
     * button     The associated JButton for the bar
     * component    The component maintained in the Outlook bar
     */
    class BarInfo
    {
        /**
         * The name of this bar
         */
        private String name;
        
        /**
         * The JButton that implements the Outlook bar itself
         */
        private JButton button;
        
        /**
         * The component that is the body of the Outlook bar
         */
        private JComponent component;
        
        /**
         * Creates a new BarInfo
         * 
         * @param  name    The name of the bar
         * @param  component  The component that is the body of the Outlook Bar
         */
        public BarInfo( String name, JComponent component )
        {
            this.name = name;
            this.component = component;
            this.button = new JButton( name );
            this.setButtonProperties();
        }
        
        
        private void setButtonProperties(){
            //button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            //button.setBorderPainted(false);
            //button.setContentAreaFilled(false);
            button.setFont(new Font("Helvetica Neue",Font.PLAIN,11));
            //Font font = button.getFont();
            //System.out.printf("font = %s, size = %d\n",
            //        font.getName(),font.getSize());
            button.setBackground(new Color(240,240,240));
            
            //button.setFocusPainted(false);
            button.putClientProperty("JButton.buttonType", "roundRect");
            button.setHorizontalAlignment(SwingConstants.LEFT );
        }
        /**
         * Creates a new BarInfo
         * 
         * @param  name    The name of the bar
         * @param  icon    JButton icon
         * @param  component  The component that is the body of the Outlook Bar
         */
        public BarInfo( String name, Icon icon, JComponent component )
        {
            this.name = name;
            this.component = component;
            this.button = new JButton( name, icon );
        }
        
        /**
         * Returns the name of the bar
         * 
         * @return The name of the bar
         */
        public String getName()
        {
            return this.name;
        }
        
        /**
         * Sets the name of the bar
         * 
         * @param  The name of the bar
         */
        public void setName( String name )
        {
            this.name = name;
        }
        
        /**
         * Returns the outlook bar JButton implementation
         * 
         * @return   The Outlook Bar JButton implementation
         */
        public JButton getButton()
        {
            return this.button;
        }
        
        /**
         * Returns the component that implements the body of this Outlook Bar
         * 
         * @return The component that implements the body of this Outlook Bar
         */
        public JComponent getComponent()
        {
            return this.component;
        }
    }
}
