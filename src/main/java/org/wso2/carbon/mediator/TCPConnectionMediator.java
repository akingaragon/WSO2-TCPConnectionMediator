package org.wso2.carbon.mediator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;

public class TCPConnectionMediator extends AbstractMediator {
	private static final Log log = LogFactory
			.getLog(TCPConnectionMediator.class);

	public TCPConnectionMediator() {
	}

	public boolean mediate(MessageContext context) {

		// input parameters
		String tcpServerIP = (String) context.getProperty("tcpServerIP");
		int tcpServerPort = Integer.valueOf((String) context.getProperty("tcpServerPort"));
		String tcpRequestMessage = (String) context.getProperty("tcpRequestMessage");
		boolean tcpHasWelcomeMessage = Boolean.valueOf((String) context.getProperty("tcpHasWelcomeMessage"));
		String tcpWelcomeMessage = (String) context.getProperty("tcpWelcomeMessage");
		boolean tcpHasSpecialEndingMessage = Boolean.valueOf((String) context.getProperty("tcpHasSpecialEndingMessage"));
		String tcpSpecialEndingMessages = (String) context.getProperty("tcpSpecialEndingMessages");

		StringBuffer stringBuffer = new StringBuffer();
		String[] specialEndingMessages = null;
		Socket clientSocket = null;
		String serverResponse = "";
		String serverReadLine = "";
		boolean endOfMessage = false;
		
		try {
			// create a socket connection
			clientSocket = new Socket(tcpServerIP, tcpServerPort);
			log.debug(tcpRequestMessage);
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// if tcp has welcome message, read server welcome message first
			if (tcpHasWelcomeMessage) {
				serverResponse = inFromServer.readLine();
				// compare welcome message
				if (serverResponse.trim().equals(tcpWelcomeMessage)) {
					log.debug(tcpServerIP.concat(" - ").concat(Integer.toString(tcpServerPort)).concat(" connected."));
				} else {
					String response=tcpServerIP.concat(" - ").concat(Integer.toString(tcpServerPort)).concat(" connected but welcome message does not exist!").concat(". Waiting welcome message : ").concat(tcpWelcomeMessage).concat(" but received : ").concat(serverResponse.trim());
					context.setProperty("tcpResponseMessage", response);
					log.debug(response);
					return false;
				}
			}

			if (tcpHasSpecialEndingMessage) {
				//if tcp connection has special ending strings, split inout into an array 
				specialEndingMessages = tcpSpecialEndingMessages.split(",");
			}

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			
			log.debug("before sending the message");
			// send message to tcp port on server
			outToServer.writeBytes(tcpRequestMessage+'\r'+'\n');
			log.debug("message has been sent");
			while (!endOfMessage && (serverReadLine = inFromServer.readLine()) != null) {
				stringBuffer.append(serverReadLine);
				if (tcpHasSpecialEndingMessage) {
					for (int i = 0; i < specialEndingMessages.length; i++) {
						if (serverReadLine.startsWith(specialEndingMessages[i])) {
							endOfMessage = true;
						}
					}
				}
			}
			log.debug("server response:".concat(serverResponse));
			serverResponse = stringBuffer.toString();
			log.debug("\n" + serverResponse);
			context.setProperty("tcpResponseMessage", serverResponse);
			return true;
		} catch (Exception e) {
			// an error occurred, log error and return false
			log.error("An error occured!\n".concat(e.getMessage()));
			context.setProperty("tcpResponseMessage","An Error occured - ".concat(e.getMessage()));
			return false;
		} finally {
			if (clientSocket == null) {
				log.error("Connection problem!clientSocket is null!");
				context.setProperty("tcpResponseMessage","Connection problem!");
			} else if (clientSocket.isConnected()) {
				try {
					clientSocket.close();
				} catch (Exception e2) {
					log.error("Client Socket close error!\n".concat(e2.getMessage()));
					context.setProperty("tcpResponseMessage",serverResponse.concat(" - connection can not be closed!"));
				}
			}

		}
	}
}
