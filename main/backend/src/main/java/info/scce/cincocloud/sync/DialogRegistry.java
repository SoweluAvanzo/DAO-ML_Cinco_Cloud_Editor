package info.scce.cincocloud.sync;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;

/**
 * Author zweihoff
 */
@ApplicationScoped
public class DialogRegistry extends WebSocketRegistry {
    /**
     * Map<UserId,Map<DialogId,Session>>
     */
    private final Map<Long, Map<Long, DialogAnswer>> currentWaitingDialogs;

    public DialogRegistry() {
        currentWaitingDialogs = new ConcurrentHashMap<>();
    }

    public Map<Long, Map<Long, DialogAnswer>> getCurrentOpenSockets() {
        return currentWaitingDialogs;
    }

    public DialogAnswer add(long dialogId, WebSocketMessage message) {
        DialogAnswer da = new DialogAnswer();
        if (!currentWaitingDialogs.containsKey(message.getsenderId())) {
            currentWaitingDialogs.put(message.getsenderId(), new ConcurrentHashMap<>());
        }
        currentWaitingDialogs.get(message.getsenderId()).put(dialogId, da);
        return da;
    }

    public void removeWaitingAnswer(long dialogId, long userId) {
        if (currentWaitingDialogs.containsKey(userId)) {
            currentWaitingDialogs.get(userId).remove(dialogId);
        }
    }

    public void notify(long dialogId, long userId, String answer) {
        if (currentWaitingDialogs.containsKey(userId)) {
            if (currentWaitingDialogs.get(userId).containsKey(dialogId)) {
                currentWaitingDialogs.get(userId).get(dialogId).setAnswer(answer);
            }
        }
    }
}

