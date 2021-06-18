import 'package:angular/core.dart';

@Component(
    selector: 'not-found',
    template: '''
    <div class="container my-4">
      <h1 class="text-center m-4" style="font-size: 2rem" >:(</h1>
      <div class="alert alert-danger text-center mx-4">
		    Sorry, there is nothing to see here.
		  </div>
    </div>
    '''
)
class NotFoundComponent {}
