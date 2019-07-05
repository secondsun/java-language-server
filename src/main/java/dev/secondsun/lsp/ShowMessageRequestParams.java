package dev.secondsun.lsp;

import java.util.ArrayList;
import java.util.List;


public class ShowMessageRequestParams {
         /**
	 * The message type. See {@link MessageType}
	 */
	public int type;

	/**
	 * The actual message
	 */
	public String message;

	/**
	 * The message action items to present.
	 */
	public List<MessageActionItem>actions = new ArrayList();
}
