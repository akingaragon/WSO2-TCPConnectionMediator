# WSO2-TCPConnectionMediator
TCP Conection Mediator for WSO2 

This is a custom mediator for WSO2 TCP connection.

exported jar file will be uploaded to repository\components\dropins folder of WSO2 installation.

This custom proxy can be used for TCP communication on WSO2 

Inputs of mediator is passed by MessageContext.
-	tcpServerIP
Target server IP

-	tcpServerPort
Target server port

-	tcpRequestMessage
Message to send the server

-	tcpHasWelcomeMessage
Does the server reply your connection request with a welcome message?
If the server has welcome message then send true, else send false

-	tcpWelcomeMessage
If the server has welcome message then pass servers expexted welcome message with this parameter.
Mediator compares tcpWelcomeMessage with servers welcome message, if they are different then stop the execution.

-	tcpHasSpecialEndingMessage
This parameter is used to understand the end of servers response message. If your server send a special character at then end of its reply, you should send true, else false

-	tcpSpecialEndingMessages
If the server has special message ending parameter and if you send tcpHasSpecialEndingMessage true then you should send the ending message with this parameter. Mediator waits until see this parameter. You can send ; seperated multiple values.

-	tcpResponseMessage
This is an out parameter. Response of server is set on this property
  
Sample property values;

<property value="DXX|2341023|X|XXX" name="tcpRequestMessage" scope="default" type="STRING"/>
<property value="000.000.000.000" name="tcpServerIP" scope="default" type="STRING"/>
<property value="9999" name="tcpServerPort" scope="default" type="STRING"/>
<property value="true" name="tcpHasWelcomeMessage" scope="default" type="STRING"/>
<property value="Welcome" name="tcpWelcomeMessage" scope="default" type="STRING"/>
<property value="true" name="tcpHasSpecialEndingMessage" scope="default" type="STRING"/>
<property value="DEX,WES,REJ" name="tcpSpecialEndingMessages" scope="default" type="STRING"/>
<property name="tcpResponseMessage" scope="default" type="STRING" value=""/>
<class name="org.wso2.carbon.mediator.TCPConnectionMediator"/>

The response of TCPConnectionMediator will be in tcpResponseMessage property
