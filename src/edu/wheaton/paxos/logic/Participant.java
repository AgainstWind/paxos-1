package edu.wheaton.paxos.logic;

import java.io.Closeable;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.wheaton.paxos.logic.PaxosListeners.ParticipantDetailsListener;
import edu.wheaton.paxos.utility.Bag;
import edu.wheaton.paxos.utility.RunnableOfT;

public final class Participant implements Closeable
{
	public Participant(int id, RunnableOfT<PaxosMessage> sendMessageRunnable, List<Participant> potentialParticipants)
	{
		m_sendMessageRunnable = sendMessageRunnable;
		m_listeners = Lists.newArrayList();
		m_potentialParticipants = potentialParticipants;
		m_id = id;
		m_leaderId = 1;
		
//		m_clock = new Clock();
		m_log = PaxosLogManager.createPaxosLog(m_id);

		m_lock = new Object();
		m_stopped = false;
		m_paused = false;

		if (m_potentialParticipants.size() == 0)
		{
			m_highestSeenNumber = 1;
			Decree decree = Decree.createSetLeaderDecree(m_highestSeenNumber, m_id, LEADER_INTERVAL);
			m_leaderId = Integer.parseInt(decree.getDecreeValue());
			m_leaderExpiry = decree.getLeaderExpiry();
			m_log.recordDecree(decree);
			m_hasJoined = true;
			m_isPresent = true;
		}

		m_inbox = PaxosMessageQueueManager.createPaxosMessageQueue(m_id);
		m_participants = Lists.newCopyOnWriteArrayList();
		m_mainThread.start();
	}

	@Override
	public void close()
	{
		PaxosLogManager.closeLog(m_id);
		PaxosMessageQueueManager.closeQueue(m_id);
	}

	public void addParticipantDetailsListener(ParticipantDetailsListener listener)
	{
		m_listeners.add(listener);
	}

	public void removeParticipantDetailsListener(ParticipantDetailsListener listener)
	{
		m_listeners.remove(listener);
	}

	public int getId()
	{
		return m_id;
	}

	public void receiveMessage(PaxosMessage message)
	{
		m_inbox.add(message);
	}

	public void executeCommand(CommandMessage command)
	{
		switch (command)
		{
		case TOGGLE_PAUSE_STATE:
			m_paused = !m_paused;
			if (!m_paused)
			{
				synchronized (m_lock)
				{
					m_lock.notifyAll();
				}
			}
			break;
		case ENTER:
			// TODO do we need to assert that we're not present?
			m_shouldEnter = true;
			break;
		case LEAVE:
			// TODO do we need to assert that we're present?
			m_shouldLeave = true;
			m_withAmnesia = false;
			break;
		case LEAVE_WITH_AMNESIA:
			// TODO do we need to assert that we're present?
			m_shouldLeave = true;
			m_withAmnesia = true;
		case SHOW:
		case HIDE:
		}
	}

	private final Thread m_mainThread = new Thread(new Runnable()
	{
		@Override
		public void run()
		{
			while (!m_stopped)
			{
				while (!m_paused)
				{
					// TODO: pick one of these
					// Note: if you have "left", your only options should be enter() or delay()

					if (m_shouldEnter)
					{
						m_shouldEnter = false;
						// enter
						enter();
						continue;
					}
					if (m_shouldLeave)
					{
						m_shouldLeave = false;
						// leave (amnesia?)
						leave(m_withAmnesia);
						continue;
					}
					double choice = Math.random();
					if (choice < 0.1)
					{
						// join
						join();
					}
					else if (choice < 0.2)
					{
						// resign
						resign();
					}
					else if (choice < 0.3 && m_leaderId == m_id)
					{
						// initiate proposal
						initiateProposal();
					}
					else if (choice < 0.5)
					{
						// delay
						delay(DELAY_INTERVAL);
					}
					else
					{
						// receive
						receive(DELAY_INTERVAL);
					}

					updateDetails();
				}

				synchronized (m_lock)
				{
                    try
                    {
                    	m_lock.wait();
                    }
                    catch (InterruptedException e)
                    {
                    	Thread.currentThread().interrupt();
                    	return;
                    }
                }
			}
		}

		private void join()
		{
			Preconditions.checkArgument(!m_hasJoined);

			m_sendMessageRunnable.run(PaxosMessage.createDecreeRequestMessage(m_id, m_leaderId, Decree.createAddParticipantDecree(Decree.NO_ID, m_id)));
		}

		private void resign()
		{
			// TODO
//			Decree decree = null;// = new Decree(DecreeType.REMOVE_PARTICIPANT);
			if (m_id == m_leaderId)
			{
				// TODO messageId needs to go in the Decree
				int messageId = Math.max(m_highestSeenNumber, m_promisedNumber) + 1;
				m_highestSeenNumber = messageId;

//				for (Integer recipientId : m_participants)
//					m_sendMessageRunnable.run(new PaxosMessage(m_id, recipientId.intValue(), decree));
			}
			else
			{
//				m_sendMessageRunnable.run(new PaxosMessage(m_id, m_leaderId, decree));
			}
		}

		private void enter()
		{
			for (Integer recipientId : m_participants)
				m_sendMessageRunnable.run(PaxosMessage.createRequestLogMessage(m_id, recipientId, m_log.getLatestLogId()));
		}

		private void leave(boolean withAmnesia)
		{
			m_isPresent = false;

			if (withAmnesia)
			{
				m_inbox.clear();
				m_participants.clear();
			}
		}

		private void delay(int interval)
		{
			try
			{
				Thread.sleep(interval);
			}
			catch (InterruptedException e)
			{
				System.out.println("delay()");
				e.printStackTrace();
			}
		}

		private void initiateProposal()
		{
			if (m_id == m_leaderId)
			{
				int messageId = Math.max(m_highestSeenNumber, m_promisedNumber) + 1;
				m_highestSeenNumber = messageId;

				Decree decree = Decree.createOpaqueDecree(messageId, "Leader-Initiated Decree");
				m_log.recordDecree(decree);
//				for (Integer recipientId : m_participants)
//					m_sendMessageRunnable.run(new PaxosMessage(m_id, recipientId.intValue(), decree));
			}
			else
			{
//				PaxosMessage message = new PaxosMessage(m_id, m_leaderId,
//						Decree.createOpaqueDecree(Decree.NO_ID, "Woo!"));
//				m_sendMessageRunnable.run(message);
			}
		}

		private void receive(int interval)
		{
			if (!m_inbox.isEmpty())
			{
				PaxosMessage message = m_inbox.poll();
				PaxosMessage responseMessage;
				Decree decree;
				switch (message.getMessageType())
				{
				case PREPARE:
					decree = message.getDecree();
					// 1) ACCEPT/REJECT check if this is already in the log, if so reply as before
					// TODO
					// 2) ACCEPT "completely okay", from the leader, etc...
					// and leader is not expired
					if (message.getSenderId() == m_leaderId && decree.getDecreeId() > Math.max(m_highestSeenNumber, m_promisedNumber))
					{
						responseMessage = PaxosMessage.createAcceptMessage(m_id, m_leaderId, decree);
					}
					// 3) REJECT Definitely not acceptable
					else
					{
						responseMessage = PaxosMessage.createRejectMessage(m_id, m_leaderId, decree);
					}
					// 4) Decree number is too high for your log. Go learn.
					// TODO
					
					break;
				case DECREE_COMMIT:
					processDecree(message.getDecree());
					break;
				case DECREE_REQUEST:
					processDecreeRequest(message.getDecree());
					break;
				case ACCEPT:
					break;
				case REJECT:
					break;
				case REQUEST_LOG:
					if (m_log.getLatestLogId() > message.getLogId())
					{
						responseMessage = PaxosMessage.createSendLogMessage(m_id, message.getSenderId(), 
								m_log.getLogSinceId(message.getLogId()), m_log.getLatestLogId()); 
						m_sendMessageRunnable.run(responseMessage);
					}
					break;
				case SEND_LOG:
					String logData = message.getLogData();
					Scanner scanner = new Scanner(logData);
					while (scanner.hasNextLine())
					{
						if (!processDecree(Decree.fromString(scanner.nextLine())))
							break;
					}

					if (m_log.getLatestLogId() < message.getLogId())
					{
						responseMessage = PaxosMessage.createRequestLogMessage(m_id, message.getSenderId(), m_log.getLatestLogId()); 
						m_sendMessageRunnable.run(responseMessage);
					}
					break;
				}
			}
			else
			{
				if (interval > 0)
				{
					try
					{
						Thread.sleep(1);
						receive(interval - 1);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	});

	private boolean processDecree(Decree decree)
	{
		if (!m_log.recordDecree(decree))
			return false;

		int participantId;
		// TODO do we need to update our promised number or highest seen number here?
		switch (decree.getDecreeType())
		{
		case OPAQUE_DECREE:
			// decree has already been recorded; nothing to do
			break;
		case ADD_PARTICIPANT:
			participantId = Integer.parseInt(decree.getDecreeValue());
			if (participantId != m_id)
				m_participants.add(participantId);
			else
				m_hasJoined = true;
			break;
		case REMOVE_PARTICIPANT:
			participantId = Integer.parseInt(decree.getDecreeValue());
			if (participantId != m_id)
				m_participants.remove(participantId);
			else
				m_hasJoined = false;
			break;
		case SET_LEADER:
			m_leaderId = Integer.parseInt(decree.getDecreeValue());
			m_leaderExpiry = decree.getLeaderExpiry();
			// TODO maintain interval
			break;
		}

		return true;
	}

	private boolean processDecreeRequest(Decree decree)
	{
		// TODO what if we get a decree request before the last ballot is completed?
		if (m_id != m_leaderId)
			return false;

		PaxosMessage message;
		// TODO do we need to update our promised number or highest seen number here?
		switch (decree.getDecreeType())
		{
		case OPAQUE_DECREE:
			// decree has already been recorded; nothing to do
			break;
		case ADD_PARTICIPANT:
			m_ballot = new Bag<Integer>();
			// TODO record prepare
			for (Integer participantId : m_participants)
			{
				Decree proposedDecree = Decree.createAddParticipantDecree(++m_highestSeenNumber, Integer.parseInt(decree.getDecreeValue()));
				m_sendMessageRunnable.run(PaxosMessage.createPrepareMessage(m_id, participantId, proposedDecree));
			}
			break;
		case REMOVE_PARTICIPANT:
			break;
		case SET_LEADER:
			break;
		}

		return true;
	}

	private void updateDetails()
	{
		for (int i = 0; i < m_listeners.size(); i++)
		{
			StringBuilder builder = new StringBuilder();
			builder.append("Leader ID: ")
			.append(m_leaderId)
			.append('\n')
			.append("Leader Interval: ")
			.append(m_leaderExpiry)
			.append('\n')
			.append("Participant List: ")
			.append(m_participants.toString())
			.append('\n')
			.append("Promised Number: ")
			.append(m_promisedNumber)
			.append('\n')
			.append("Highest Seen Number: ")
			.append(m_highestSeenNumber)
			.append('\n')
			.append("Joined: ")
			.append(m_hasJoined)
			.append('\n')
			.append("Present: ")
			.append(m_isPresent)
			.append('\n');
			

			String participantDetails = builder.toString();
			m_listeners.get(i).onParticipantDetailsUpdate(participantDetails);
		}
	}

	private static final int LEADER_INTERVAL = 30;
	private static final int DELAY_INTERVAL = 100;

	private final RunnableOfT<PaxosMessage> m_sendMessageRunnable;
	private final List<ParticipantDetailsListener> m_listeners;
	private final List<Participant> m_potentialParticipants;
	// housekeeping
	private final int m_id;
//	private final Clock m_clock;
	private boolean m_hasJoined;
	private boolean m_isPresent;

	// volatile state
	private final PaxosQueue<PaxosMessage> m_inbox;
	private final List<Integer> m_participants;

	private Bag<Integer> m_ballot;

	private final Object m_lock;
	private volatile boolean m_stopped;
	private volatile boolean m_paused;
	private volatile boolean m_shouldEnter;
	private volatile boolean m_shouldLeave;
	private volatile boolean m_withAmnesia;

	// Paxos state (persistent)
	private final PaxosLog m_log;
	private int m_promisedNumber;
	private int m_highestSeenNumber;

	// Paxos-maintained state
	private int m_leaderId;
	private int m_leaderExpiry;
}
