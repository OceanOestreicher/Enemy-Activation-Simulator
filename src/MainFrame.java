import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {

    private final StateManager stateManager;
    private final JPanel bossPanel;
    private JFrame alphaReferenceFrame;
    public MainFrame(){
        stateManager = new StateManager(this);

        //Utility JPanels
        JPanel mainDisplay = new JPanel();
        mainDisplay.setLayout(new BorderLayout());
        JPanel mainUpperPanel = new JPanel();
        mainUpperPanel.setBackground(Color.DARK_GRAY);
        JPanel mainLowerPanel = new JPanel();
        mainLowerPanel.setBackground(Color.DARK_GRAY);
        mainLowerPanel.setLayout(new GridLayout(1,2));
        JPanel fillerLowerPanel = new JPanel();
        fillerLowerPanel.setBackground(Color.DARK_GRAY);
        mainLowerPanel.add(fillerLowerPanel);
        JPanel lowerControlPanel = new JPanel();
        lowerControlPanel.setBackground(Color.DARK_GRAY);
        mainLowerPanel.add(lowerControlPanel);
        bossPanel = new JPanel();
        bossPanel.setBackground(Color.DARK_GRAY);
        bossPanel.setLayout(new GridLayout(1,2));
        JPanel referenceMainPanel = new JPanel();
        referenceMainPanel.setBackground(Color.DARK_GRAY);
        referenceMainPanel.setLayout(new BorderLayout());
        bossPanel.add(referenceMainPanel);
        JPanel referenceLowerPanel = new JPanel();
        referenceLowerPanel.setBackground(Color.DARK_GRAY);
        JPanel reactionMainPanel = new JPanel();
        reactionMainPanel.setBackground(Color.DARK_GRAY);
        reactionMainPanel.setLayout(new BorderLayout());
        JPanel reactionLowerPanel = new JPanel();
        reactionLowerPanel.setBackground(Color.DARK_GRAY);
        bossPanel.add(reactionMainPanel);



        //Canvas'
        CardCanvas mainCanvas = new CardCanvas();
        int screenWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth());
        mainCanvas.setPreferredSize(new Dimension(screenWidth /4,(int)((screenWidth /4)*.75)));
        mainCanvas.setBackground(Color.BLACK);
        mainCanvas.setBorder(new EmptyBorder(5,5,5,5));
        CardCanvas bossReferenceCanvas = new CardCanvas();
        bossReferenceCanvas.setBackground(Color.DARK_GRAY);
        bossReferenceCanvas.setPreferredSize(new Dimension((int)((screenWidth /6)*.75),(screenWidth /6)));
        CardCanvas reactionaryCanvas = new CardCanvas();
        reactionaryCanvas.setBackground(Color.DARK_GRAY);
        reactionaryCanvas.setPreferredSize(new Dimension((int)((screenWidth /6)*.75),(screenWidth /6)));
        stateManager.addCanvas(mainCanvas, StateManager.MAIN_CANVAS);
        stateManager.addCanvas(bossReferenceCanvas, StateManager.BOSS_REFERENCE_CANVAS);
        stateManager.addCanvas(reactionaryCanvas, StateManager.REACTIONARY_CANVAS);

        //Buttons
        KeyboundButton start = new KeyboundButton("Start",new String[]{"SPACE","ENTER"});
        KeyboundButton reset = new KeyboundButton("Reset Deck", new String[]{"R"});
        reset.setFocusable(false);
        KeyboundButton draw = new KeyboundButton("Draw Card",new String[]{"SPACE","ENTER"});
        KeyboundButton cycleReactions = new KeyboundButton("Next Reaction",new String[]{"SPACE","ENTER"});
        KeyboundButton removeReaction = new KeyboundButton("Remove Reaction",new String[]{"R"});
        KeyboundButton switchPhase = new KeyboundButton("Switch Phase",new String[]{"S"});
        stateManager.addButton(start,StateManager.START_BUTTON);
        stateManager.addButton(reset,StateManager.RESET_DECK_BUTTON);
        stateManager.addButton(draw,StateManager.DRAW_BUTTON);
        stateManager.addButton(cycleReactions,StateManager.NEXT_REACTION_BUTTON);
        stateManager.addButton(removeReaction,StateManager.REMOVE_REACTION_BUTTON);
        stateManager.addButton(switchPhase,StateManager.SWITCH_PHASE_BUTTON);

        //Labels
        JLabel deckCycle = new JLabel();
        deckCycle.setForeground(Color.WHITE);
        JLabel deckCount = new JLabel();
        deckCount.setForeground(Color.WHITE);
        JLabel reactionCount = new JLabel();
        reactionCount.setForeground(Color.WHITE);
        stateManager.addLabel(deckCycle,StateManager.DECK_CYCLE_LABEL);
        stateManager.addLabel(deckCount,StateManager.DECK_COUNT_LABEL);
        stateManager.addLabel(reactionCount,StateManager.REACTION_COUNT_LABEL);

        //ComboBox
        JComboBox<String>decks = new JComboBox<>(Loader.getDeckNames().toArray(new String[0]));
        decks.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"),"Pressed_Down");
        decks.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"),"Pressed_Up");
        decks.getActionMap().put("Pressed_Down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(decks.isEnabled()){
                    int i = decks.getSelectedIndex();
                    if(i+1==decks.getItemCount())i = 0;
                    else i+=1;
                    decks.setSelectedIndex(i);
                }
            }
        });
        decks.getActionMap().put("Pressed_Up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(decks.isEnabled()){
                    int i = decks.getSelectedIndex();
                    if(i-1<0)i = decks.getItemCount()-1;
                    else i-=1;
                    decks.setSelectedIndex(i);
                }
            }
        });
        decks.setFocusable(false);
        stateManager.addComboBox(decks,StateManager.DECK_SELECTOR_COMBO_BOX);

        //Final Panel Configuration
        mainUpperPanel.add(decks);
        mainUpperPanel.add(start);

        lowerControlPanel.add(deckCycle);
        lowerControlPanel.add(reset);
        lowerControlPanel.add(draw);
        lowerControlPanel.add(deckCount);

        referenceLowerPanel.add(switchPhase);

        reactionLowerPanel.add(removeReaction);
        reactionLowerPanel.add(cycleReactions);
        reactionLowerPanel.add(reactionCount);

        referenceMainPanel.add(bossReferenceCanvas,BorderLayout.CENTER);
        referenceMainPanel.add(referenceLowerPanel,BorderLayout.SOUTH);

        reactionMainPanel.add(reactionaryCanvas,BorderLayout.CENTER);
        reactionMainPanel.add(reactionLowerPanel,BorderLayout.SOUTH);

        mainDisplay.add(mainUpperPanel,BorderLayout.NORTH);
        mainDisplay.add(mainCanvas,BorderLayout.CENTER);
        mainDisplay.add(mainLowerPanel,BorderLayout.SOUTH);

        //Final Setup
        if(!stateManager.isReady()){
            System.out.println("System is not setup correctly");
            System.exit(-1);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                alphaReferenceFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                alphaReferenceFrame.dispatchEvent(new WindowEvent(alphaReferenceFrame,WindowEvent.WINDOW_CLOSING));
                if(stateManager.activeState()){
                    Loader.saveState(stateManager);
                }
            }
        });
        this.getRootPane().setFocusTraversalKeysEnabled(false);
        this.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("TAB"),"Pressed");
        this.getRootPane().getActionMap().put("Pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(alphaReferenceFrame.isVisible() && MainFrame.this.isFocused()){
                    alphaReferenceFrame.getRootPane().requestFocus();
                }
            }
        });
        add(mainDisplay);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        setTitle("Enemy Activation Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getRootPane().setDefaultButton(draw);

        alphaReferenceFrame = new JFrame("Alpha Reference");
        alphaReferenceFrame.add(bossPanel);
        alphaReferenceFrame.pack();
        alphaReferenceFrame.setVisible(true);
        alphaReferenceFrame.setLocation(this.getX()+this.getWidth(),this.getY());
        alphaReferenceFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        alphaReferenceFrame.getRootPane().setFocusTraversalKeysEnabled(false);
        alphaReferenceFrame.getRootPane().getInputMap().put(KeyStroke.getKeyStroke("TAB"),"Pressed");
        alphaReferenceFrame.getRootPane().getActionMap().put("Pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(alphaReferenceFrame.isVisible() && alphaReferenceFrame.isFocused()){
                    MainFrame.this.getRootPane().requestFocus();
                }
            }
        });

        if(Loader.previousStateExists()){
            Loader.loadState(stateManager);
            stateManager.restoreState();
        }
        this.getRootPane().requestFocus();
    }
    public void displayBossPanel(boolean display){
        alphaReferenceFrame.setVisible(display);
        alphaReferenceFrame.pack();
        this.getRootPane().requestFocus();
    }
    public static void main(String...args){
        new MainFrame();
    }

}
