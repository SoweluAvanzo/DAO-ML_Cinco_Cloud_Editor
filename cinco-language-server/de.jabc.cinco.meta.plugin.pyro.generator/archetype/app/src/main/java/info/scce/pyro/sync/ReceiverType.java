package info.scce.pyro.sync;

public enum ReceiverType {
    ALL,	// all connected instances
    OTHERS, // all connected instances, that are not the sender
    SENDER	// only the sender
}
