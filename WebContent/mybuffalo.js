var END_POINT = "/mybuffalo/bfapp2/";
var buffalo = new Buffalo(END_POINT, false);

if(!console){
    console.log = function(){};
    console.error= function(){};
}
String.prototype.toDate = function(format){
    var d = new Date(this);
    if(isNaN(d.getMilliseconds())){
        console.error("invalid date:"+this);
    }
    return d;
}
Date.prototype.toString=function(format){
    format = format || "yyyy-MM-dd hh:mm:ss";
    /**
     *    yyyy mm dd hh mm ss
     * */
    var d =  this;
    format = format.replace("yyyy",d.getFullYear());
    format = format.replace("MM",d.getMonth() < 9 ? d.getMonth()+1 : "0"+(d.getMonth()+1));
    format = format.replace("dd",d.getDate());
    format = format.replace("hh",d.getHours());
    format = format.replace("mm",d.getMinutes());
    format = format.replace("ss",d.getSeconds());
    return format;
}

String.prototype.trim=function(){
    return this.replace(/(^\s*)|(\s*$)/g, "");
}
function Buffalo(getway,sync){
    this.getway = getway;
    this.sync = sync || true;
    return this; 
}
/**
 *  @param svc : required
 *  @param args : array
 *  @param successFunc 
 *  @param errorFunc
 * 
 * */
Buffalo.prototype.remoteCall = function(svc,args,successFunc,errorFunc){
    svc = svc || "";
    svc = svc.trim();
    if(svc == ""){
        alert("错误 ! buffalo没有指定调用方法!");
        return;
    }
    var xhr;
    if(window.XMLHttpRequest){
       xhr = new XMLHttpRequest();
    }else if(window.ActiveXObject){
        var versions = [ 'Microsoft.XMLHTTP', 'MSXML.XMLHTTP','Microsoft.XMLHTTP', 'Msxml2.XMLHTTP.7.0','Msxml2.XMLHTTP.6.0', 'Msxml2.XMLHTTP.5.0','Msxml2.XMLHTTP.4.0', 'MSXML2.XMLHTTP.3.0','MSXML2.XMLHTTP' ];
        for(var i = 0; i < versions.length; i++) {
            try{
                xhr = new ActiveXObject(versions[i]);
                break;
            }catch (e){}
        }
    }
    if(!xhr){
        alert('请升级您的浏览器');
        return;
    }
    xhr.successFunc = successFunc || function(){},
    xhr.errorFunc = errorFunc || function(){},
    xhr.onreadystatechange = function(){
        if(this.readyState==4){
            if(this.status==200){
                var r = JSON.parse(this.response);
                if(r.error == 0){
                    successFunc.call(xhr,r.res);
                }else{
                    errorFunc.call(xhr,r.msg);
                }
            }else{
                console.log(this);
                console.log(this.readystate);
                errorFunc.call(xhr,"Error Happend !"+this.status);
            }
        }
    }
    var method = END_POINT+svc+"?";
    for(var i = 0;i<args.length;i++){
        method += ("."+getArgType(args[i]));
        args[i] = JSON.stringify(args[i]);
    }
    method = method.replace("?.","?");
    xhr.open("POST",method,this.sync);
    xhr.setRequestHeader("Content-Type","text/xml;charset=utf-8");
    xhr.send(args.join("\n"));
    function getArgType(arg){
       var type = typeof arg;
       switch(type){
       case "number":
           return "number";
       case "string":
          return "string";
       case "boolean":
          return "boolean";
       case "object":
           if(arg instanceof Date) return "date";
           if(arg instanceof Array) return "array";
           return "object";
       default :
          throw "unsupported type , only long,double,string,boolean,object,date,array";
       }
    }
}