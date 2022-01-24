class RedirectionStack {
  
  List<dynamic> redirectionStack = [];
  int redirectionPointer = -1;

  dynamic initReditionStack(String ext, int id) {
    redirectionStack = [];
    redirectionPointer = -1;
    pushToRedirectStack(ext, id);
  }

  void pushToRedirectStack(String ext, int id) {
    var currentLocation = { "type": ext, "id": id };
    var pointer = getRedirectStackPointer();
    var redirectStack = getRedirectStack();
    if(redirectStack.length > 0) {
      redirectStack = redirectStack.getRange(0, pointer + 1).toList();
    }
    redirectStack.add(currentLocation);
    setRedirectStack(redirectStack);
    setRedirectStackPointer(pointer + 1);
  }

  dynamic canBackwardsOnRedirectStack() {
    return _moveOnRedirectStack(
      (pointer) => pointer - 1,
      (nextPointer, redirectStack) =>
        nextPointer >= redirectStack.length || nextPointer < 0,
      false
    );
  }

  dynamic canForwardOnRedirectStack() {
    return _moveOnRedirectStack(
      (pointer) => pointer + 1,
      (nextPointer, redirectStack) =>
        nextPointer >= redirectStack.length || nextPointer < 0,
      false
    );
  }

  dynamic backwardsOnRedirectStack() {
    return _moveOnRedirectStack(
      (pointer) => pointer - 1,
      (nextPointer, redirectStack) =>
        nextPointer >= redirectStack.length || nextPointer < 0,
      true
    );
  }

  dynamic forwardOnRedirectStack() {
    return _moveOnRedirectStack(
      (pointer) => pointer + 1,
      (nextPointer, redirectStack) => 
        nextPointer >= redirectStack.length || nextPointer < 0,
      true
    );
  }

  dynamic _moveOnRedirectStack(Function op, Function guard, bool pop) {
    var pointer = getRedirectStackPointer();
    var nextPointer = op(pointer);
    var redirectStack = getRedirectStack();
    if(guard(nextPointer, redirectStack)) {
      return null;
    }
    if(pop) {
      setRedirectStackPointer(nextPointer);
    }
    return redirectStack[nextPointer];
  }

  List<dynamic> getRedirectStack() {
    return redirectionStack;
  }

  void setRedirectStack(List<dynamic> newStack) {
    redirectionStack = newStack;
  }
  
  int getRedirectStackPointer() {
    return redirectionPointer;
  }

  void setRedirectStackPointer(int pointer) {
    redirectionPointer = pointer;
  }
}