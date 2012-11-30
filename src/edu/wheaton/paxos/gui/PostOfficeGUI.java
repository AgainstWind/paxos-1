package edu.wheaton.paxos.gui;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;

import edu.wheaton.paxos.logic.PostOffice;
import edu.wheaton.paxos.utility.RunnableOfT;


public class PostOfficeGUI extends JFrame
{
//	private static int m_time = 1;

    public PostOfficeGUI()
    {
    	m_postOffice = new PostOffice(m_updateTimeDisplayRunnable);
        initComponents();

//      m_postOffice.addParticipant(m_participantIdGenerator++);
//		m_postOffice.addParticipant(m_participantIdGenerator++);
//		m_postOffice.addParticipant(m_participantIdGenerator++);
//		m_postOffice.addEvent(new PaxosEvent(m_time++,
//				new PaxosMessage(0, 1, Decree.createOpaqueDecree(0, "test"))));
//		m_postOffice.addEvent(new PaxosEvent(m_time++, 
//				new PaxosMessage(1, 2, Decree.createOpaqueDecree(0, "ignore me"))));
//		m_postOffice.addEvent(new PaxosEvent(m_time++, 
//				new PaxosMessage(1, 2, Decree.createOpaqueDecree(1, "test2"))));
    }

    private void initComponents()
    {
        m_participantScrollPane = new JScrollPane();
        m_participantList = new JList();
        m_participantListLabel = new JLabel();
        m_queueScrollPane = new ScrollPane();
        m_queueJScrollPane = new JScrollPane();
        m_queueTextPane = new JTextPane();
        m_logScrollPane = new ScrollPane();
        m_logJScrollPane = new JScrollPane();
        m_logTextPane = new JTextPane();
        m_topPanel = new JPanel();
        m_homeButton = new JButton();
        m_playPauseButton = new JButton();
        m_timeDisplay = new JLabel();
        m_detailsScrollPane = new ScrollPane();
        m_detailsJScrollPane = new JScrollPane();
        m_participantDetailsTextPane = new JTextPane();
        m_operationsPanel = new JPanel();
        m_promoteButton = new JButton();
        m_messagesButton = new JButton();
        m_resignButton = new JButton();
        m_enterButton = new JButton();
        m_delayButton = new JButton();
        m_leaveButton = new JButton();
        m_participantNamePanel = new JPanel();
        m_participantNameLabel = new JLabel();
        m_plusMinusPanel = new JPanel();
        m_plusButton = new JButton();
        m_minusButton = new JButton();

        setResizable(true);
        setTitle("Paxos Simulator");

        m_listModel = new DefaultListModel();
		m_participantList.setModel(m_listModel);
        m_participantScrollPane.setViewportView(m_participantList);

        m_participantListLabel.setText("Participants");

        m_queueTextPane.setText("Queue/Events");
        m_queueJScrollPane.setViewportView(m_queueTextPane);

        m_queueScrollPane.add(m_queueJScrollPane);

        m_logTextPane.setText("Log");
        m_logJScrollPane.setViewportView(m_logTextPane);

        m_logScrollPane.add(m_logJScrollPane);

        m_topPanel.setBorder(BorderFactory.createEtchedBorder());

        m_homeButton.setIcon(new ImageIcon(getClass().getResource("/images/home1.png")));
        m_homeButton.setMinimumSize(new Dimension(10, 10));
        m_homeButton.setPreferredSize(new Dimension(10, 10));

        m_playPauseButton.setIcon(PAUSE_ICON);
        m_playPauseButton.addActionListener(m_playPauseButtonListener);

        GroupLayout TopPanelLayout = new GroupLayout(m_topPanel);
        m_topPanel.setLayout(TopPanelLayout);
        TopPanelLayout.setHorizontalGroup(
            TopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, TopPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(m_homeButton, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 200, Short.MAX_VALUE)
                .addComponent(m_playPauseButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                .addGap(155, 155, 155)
                .addComponent(m_timeDisplay)
                .addContainerGap())
        );
        TopPanelLayout.setVerticalGroup(
            TopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(TopPanelLayout.createSequentialGroup()
                .addGroup(TopPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(m_timeDisplay, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(m_playPauseButton, GroupLayout.PREFERRED_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(m_homeButton, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addContainerGap())
        );

        m_participantDetailsTextPane.setText("Participant Details");
        m_detailsJScrollPane.setViewportView(m_participantDetailsTextPane);

        m_detailsScrollPane.add(m_detailsJScrollPane);

        m_operationsPanel.setBorder(BorderFactory.createEtchedBorder());

        m_promoteButton.setText("Promote");

        m_messagesButton.setText("Messages");

        m_resignButton.setText("Resign");

        m_enterButton.setText("Enter");
        m_enterButton.addActionListener(m_enterButtonListener);

        m_delayButton.setText("Delay");

        m_leaveButton.setText("Leave");

        GroupLayout OperationsPanelLayout = new GroupLayout(m_operationsPanel);
        m_operationsPanel.setLayout(OperationsPanelLayout);
        OperationsPanelLayout.setHorizontalGroup(
            OperationsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(OperationsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(OperationsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(m_leaveButton, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(m_delayButton, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(m_enterButton, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(m_resignButton, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                    .addComponent(m_messagesButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(m_promoteButton, GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))
                .addContainerGap())
        );
        OperationsPanelLayout.setVerticalGroup(
            OperationsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, OperationsPanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addComponent(m_promoteButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_messagesButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_resignButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_enterButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_delayButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_leaveButton)
                .addContainerGap())
        );

        m_participantNamePanel.setBorder(BorderFactory.createEtchedBorder());

        m_participantNameLabel.setText("Participant Name");

        GroupLayout imageNamePanelLayout = new GroupLayout(m_participantNamePanel);
        m_participantNamePanel.setLayout(imageNamePanelLayout);
        imageNamePanelLayout.setHorizontalGroup(
            imageNamePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(imageNamePanelLayout.createSequentialGroup()
                .addGroup(imageNamePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(imageNamePanelLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(m_participantNameLabel)))
                .addContainerGap())
        );
        imageNamePanelLayout.setVerticalGroup(
            imageNamePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(imageNamePanelLayout.createSequentialGroup()
                .addComponent(m_participantNameLabel))
        );

        m_plusMinusPanel.setBorder(BorderFactory.createEtchedBorder());
        m_plusButton.setText("+");
        m_plusButton.addActionListener(m_plusButtonListener);
        
        m_minusButton.setText("-");
        m_minusButton.addActionListener(m_minusButtonListener);


        GroupLayout PlusMinusPanelLayout = new GroupLayout(m_plusMinusPanel);
        m_plusMinusPanel.setLayout(PlusMinusPanelLayout);
        PlusMinusPanelLayout.setHorizontalGroup(
            PlusMinusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PlusMinusPanelLayout.createSequentialGroup()
                .addComponent(m_plusButton, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(m_minusButton, GroupLayout.PREFERRED_SIZE, 41, Short.MAX_VALUE)
                .addContainerGap())
        );
        PlusMinusPanelLayout.setVerticalGroup(
            PlusMinusPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(PlusMinusPanelLayout.createSequentialGroup()
                .addGroup(PlusMinusPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(m_minusButton, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
                    .addComponent(m_plusButton, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                .addContainerGap())
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(m_participantListLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(m_plusMinusPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(m_participantScrollPane, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_queueScrollPane, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_logScrollPane, GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(m_participantNamePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_detailsScrollPane, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(m_operationsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(m_topPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(m_topPanel, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(m_participantListLabel)))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(m_detailsScrollPane, GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(m_operationsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(m_participantNamePanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(m_logScrollPane, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(m_queueScrollPane, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)))
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(m_participantScrollPane, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(m_plusMinusPanel, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        
        pack();

        // Determine the new location of the window
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;

        // Move the window
        setLocation(x, y);

        setVisible(true);
    }

    private final ActionListener m_enterButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			// TODO Auto-generated method stub
		}
	};
    private final ActionListener m_plusButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			m_listModel.addElement("Participant " + m_participantIdGenerator);
			m_postOffice.addParticipant(m_participantIdGenerator++);
		}
	};

    private final ActionListener m_minusButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			for (Object value : m_participantList.getSelectedValues())
			{
				// TODO actually remove the participant from the PostOffice
				m_listModel.removeElement(value);
			}
		}
	};

    private final ActionListener m_playPauseButtonListener = new ActionListener()
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{
			m_playPauseButton.setIcon(m_postOffice.togglePauseState() ? PLAY_ICON : PAUSE_ICON);
		}
	};

	private final RunnableOfT<String> m_updateTimeDisplayRunnable = new RunnableOfT<String>()
	{
		@Override
		public void run(String time)
		{
			m_timeDisplay.setText(time);
		}
	};

    private static final long serialVersionUID = -7049383055209558563L;
    private static final ImageIcon PAUSE_ICON = new ImageIcon(PostOfficeGUI.class.getResource("/images/pause2.png"));
    private static final ImageIcon PLAY_ICON = new ImageIcon(PostOfficeGUI.class.getResource("/images/play2.png"));
    
    private static int m_participantIdGenerator = 1;

    private final PostOffice m_postOffice;

    private JPanel m_topPanel;
    private JPanel m_participantNamePanel;
    private JPanel m_operationsPanel;
    private JPanel m_plusMinusPanel;

    private JTextPane m_participantDetailsTextPane;
    private JScrollPane m_participantScrollPane;
    private JScrollPane m_detailsJScrollPane;
    private ScrollPane m_detailsScrollPane;

    private JTextPane m_logTextPane;
    private JScrollPane m_logJScrollPane;
    private ScrollPane m_logScrollPane;

    private JTextPane m_queueTextPane;
    private JScrollPane m_queueJScrollPane;
    private ScrollPane m_queueScrollPane;

    private JLabel m_participantListLabel;
    private JLabel m_participantNameLabel;
    private JLabel m_timeDisplay;

    private JButton m_homeButton;
    private JButton m_playPauseButton;
    private JButton m_plusButton;
    private JButton m_minusButton;
    private JButton m_promoteButton;
    private JButton m_messagesButton;
    private JButton m_resignButton;
    private JButton m_enterButton;
    private JButton m_delayButton;
    private JButton m_leaveButton;

    private JList m_participantList;
    private DefaultListModel m_listModel;
}
