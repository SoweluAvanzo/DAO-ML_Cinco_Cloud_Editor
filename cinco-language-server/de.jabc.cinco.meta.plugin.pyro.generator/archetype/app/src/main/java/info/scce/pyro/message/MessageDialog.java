package info.scce.pyro.message;

import info.scce.pyro.core.command.CommandExecuter;
import info.scce.pyro.sync.DialogAnswer;
import info.scce.pyro.sync.GraphModelWebSocket;
import info.scce.pyro.sync.WebSocketMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author zweihoff
 */
public class MessageDialog {

    private CommandExecuter cmdExecuter;

    private GraphModelWebSocket graphModelWebSocket;

    public MessageDialog(CommandExecuter cmdExecuter, GraphModelWebSocket graphModelWebSocket) {
        this.cmdExecuter = cmdExecuter;
        this.graphModelWebSocket = graphModelWebSocket;
    }

    private void sendMessage(String type, String title, String message) {
        NoAnswerMessage nam = new NoAnswerMessage();
        nam.setMessage(message);
        nam.setTitle(title);
        nam.setType(type);
		if(cmdExecuter.getBatch().getUser() != null) {
		        graphModelWebSocket.send(
		                cmdExecuter.getBatch().getGraphModel().getDelegateId(),
		                WebSocketMessage.fromEntity(
		                        cmdExecuter.getBatch().getUser().id,
		                        nam
		                )
		        );
		}
    }

    private String sendQuestion(String title, String message, List<String> choices) {
        //create random dialog id
        long dialogId = ThreadLocalRandom.current().nextLong(99999999);

        OneAnswerMessage nam = new OneAnswerMessage();
        nam.setMessage(message);
        nam.setTitle(title);
        nam.setChoices(choices);
        nam.setId(dialogId);

        if( cmdExecuter.getBatch().getUser() != null) {

            WebSocketMessage wsm = WebSocketMessage.fromEntity(
                    cmdExecuter.getBatch().getUser().id,
                    nam
            );
            //add dialog to queue
            DialogAnswer da = graphModelWebSocket.getDialogRegistry().add(dialogId,wsm);
            graphModelWebSocket.send(
                    cmdExecuter.getBatch().getGraphModel().getDelegateId(),
                    wsm
            );
            while(!da.isNotified()){
                if(da.isInterrupted()){
                    break;
                }
            }
            if(da.isNotified()){
                return da.getAnswer();
            }
        }
        return null;
    }

    public void openInformation(String title,String message) {
        sendMessage("PRIMARY",title,message);
    }

    public void openQuestion(String title, String message) {
        sendMessage("INFO",title,message);
    }

    public void openWarning(String title, String message) {
        sendMessage("WARNING",title,message);
    }

    public void openError(String title, String message) {
        sendMessage("DANGER",title,message);
    }

    public boolean openConfirm(String title, String message) {
        String answer = sendQuestion(title,message, Collections.singletonList("Ok"));
        if(answer==null)return false;
        return answer.equals("Ok");
    }

    public int openChoices(String title, String message,String[] choices) {
        String answer = sendQuestion(title,message, Arrays.asList(choices));
        if(answer==null)return -1;
        return Arrays.asList(choices).indexOf(answer);
    }
}
