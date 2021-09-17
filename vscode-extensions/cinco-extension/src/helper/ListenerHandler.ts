export class ListenerHandler {
    public listeners: ((e: any) => void)[] = [] as ((e: any) => void)[];
    getLatest: () => any;

    constructor( getLatest: () => any) {
        this.getLatest = getLatest;
    }

    public registerListener(listener: (e: any) => void) {
        if(listener) {
            this.listeners.push(listener);
            var latest = this.getLatest();
            if(latest) {
                listener(latest);
            }
        }
    }

    public unregisterListener(listener: (latest: any) => void) {
        const newListeners: ((latest: any) => void)[] | undefined 
            = this.listeners.filter( (l) => l !== listener);
        if(newListeners) {
            this.setListeners(newListeners);
        }
    }

    public getListeners(): ((latest: any) => void)[] | undefined {
        return this.listeners;
    }

    public setListeners(l: ((latest: any) => void)[]) {
        this.listeners = l;
    }

    public triggerListeners() {
        for(let i = 0; i<this.listeners.length; i++) {
            this.listeners[i](this.getLatest());
        }
    }
}