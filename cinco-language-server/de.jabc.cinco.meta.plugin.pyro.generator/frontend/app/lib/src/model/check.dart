import 'dart:convert';
import 'dart:js' as js;

class CheckResults {

	List<CheckResult> results = new List();
	
	static CheckResults copy(CheckResults cr) {
		var ncr = new CheckResults();
		if(cr==null) {
			ncr.results = new List();
		} else {
			ncr.results = cr.results.map((n)=>CheckResult.copy(n)).toList();
		}
		return ncr;
	}

	static CheckResults fromJSOG(List jsog)
	{
		CheckResults cr = new CheckResults();
		for(var r in jsog) {
			cr.results.add(CheckResult.fromJSOG(r));
		}
		return cr;
	}
	
	static CheckResults filterChecks(CheckResults checkResults, bool isError, bool isWarning, bool isInfo) {
	  	checkResults.results = checkResults.results.where((cr){
			return (cr.isWarningPresent()&&isWarning) || (cr.isErrorPresent()&&isError) || (cr.isInfoPresent()&&isInfo);
		}).toList();
		checkResults.results.forEach((cr){
			cr.results = cr.getResults(isError,isWarning,isInfo);
		});
		return checkResults;
	  }

	static CheckResults fromJSON(String s)
	{
		return fromJSOG(jsonDecode(s));
	}

	js.JsArray toJS() {
		var arr = new js.JsArray();
		results.forEach((CheckResult cr){
			var js_cr = new js.JsArray();
			js_cr['id'] = cr.id;
			js_cr['errors'] = new js.JsArray();
			if(cr.results.where((rr)=>rr.type=='error').isNotEmpty) {
				js_cr['level'] = 'error';
			}
			else if(cr.results.where((rr)=>rr.type=='warning').isNotEmpty) {
				js_cr['level'] = 'warning';
			}
			else {
				js_cr['level'] = 'info';
			}
			cr.results.forEach((Result r){
				var err = new js.JsArray();
				err['message'] = r.message;
				err['type'] = r.type;
				js_cr['errors'].add(err);
			});
			arr.add(js_cr);
		});
		return arr;
	}

}

class CheckResult {
  int id;
  String name;
  
  
  List<Result> results = new List();
  
  static CheckResult copy(CheckResult cr) {
	var ncr = new CheckResult();
	ncr.id = cr.id;
	ncr.name = cr.name;
	ncr.results = cr.results.map((n)=>Result.copy(n)).toList();
	return ncr;
  }
  
  static CheckResult fromJSOG(Map jsog)
  {
  	CheckResult cr = new CheckResult();
  	cr.id = int.parse(jsog['delegateId'].toString());
	cr.name = jsog['name'];

  	
  	for(var r in jsog['results']) {
  		cr.results.add(Result.fromJSOG(r));
  	}
    
    return cr;
  }

  static CheckResult fromJSON(String s)
  {
    return fromJSOG(jsonDecode(s));
  }
  
    
  List<Result> getResults(bool isError,bool isWarning, bool isInfo) {
		return new List.from(results.where((n){
			return (n.type=='warning'&&isWarning)||
			(n.type=='error'&&isError) ||
			(n.type=='info'&&isInfo);
		}));
	}
  
  isWarningPresent() => results.where((n)=>n.type=='warning').isNotEmpty;
  isErrorPresent() => results.where((n)=>n.type=='error').isNotEmpty;
  isInfoPresent() => results.where((n)=>n.type=='info').isNotEmpty;

}

class Result {
	String message;
	String type;
	
	static Result copy(Result cr) {
		var ncr = new Result();
		ncr.message = cr.message;
		ncr.type = cr.type;
		return ncr;
	}
	
	static Result fromJSOG(Map jsog)
	{
	  	Result cr = new Result();
	  	cr.message = jsog['message'];
	  	cr.type = jsog['type'];
	    return cr;
    }
}
