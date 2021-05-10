package info.scce.cincocloud.sync;

public class DialogAnswer {
    private String answer = null;
    private boolean isInterrupted = false;

    public synchronized boolean isNotified() {
        return answer != null;
    }

    public synchronized boolean isInterrupted() {
        return isInterrupted;
    }

    public synchronized String getAnswer() {
        return answer;
    }

    public synchronized void setAnswer(String s) {
        answer = s;
    }

    public synchronized void interrupt() {
        isInterrupted = true;
    }

}
