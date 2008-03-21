/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(typeof dojo=="undefined"){
var dj_global=this;
var dj_currentContext=this;
function dj_undef(_1,_2){
return (typeof (_2||dj_currentContext)[_1]=="undefined");
}
if(dj_undef("djConfig",this)){
var djConfig={};
}
if(dj_undef("dojo",this)){
var dojo={};
}
dojo.global=function(){
return dj_currentContext;
};
dojo.locale=djConfig.locale;
dojo.version={major:0,minor:4,patch:0,flag:"",revision:Number("$Rev: 2525 $".match(/[0-9]+/)[0]),toString:function(){
with(dojo.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
dojo.evalProp=function(_3,_4,_5){
if((!_4)||(!_3)){
return undefined;
}
if(!dj_undef(_3,_4)){
return _4[_3];
}
return (_5?(_4[_3]={}):undefined);
};
dojo.parseObjPath=function(_6,_7,_8){
var _9=(_7||dojo.global());
var _a=_6.split(".");
var _b=_a.pop();
for(var i=0,l=_a.length;i<l&&_9;i++){
_9=dojo.evalProp(_a[i],_9,_8);
}
return {obj:_9,prop:_b};
};
dojo.evalObjPath=function(_e,_f){
if(typeof _e!="string"){
return dojo.global();
}
if(_e.indexOf(".")==-1){
return dojo.evalProp(_e,dojo.global(),_f);
}
var ref=dojo.parseObjPath(_e,dojo.global(),_f);
if(ref){
return dojo.evalProp(ref.prop,ref.obj,_f);
}
return null;
};
dojo.errorToString=function(_11){
if(!dj_undef("message",_11)){
return _11.message;
}else{
if(!dj_undef("description",_11)){
return _11.description;
}else{
return _11;
}
}
};
dojo.raise=function(_12,_13){
if(_13){
_12=_12+": "+dojo.errorToString(_13);
}
try{
if(djConfig.isDebug){
dojo.hostenv.println("FATAL exception raised: "+_12);
}
}
catch(e){
}
throw _13||Error(_12);
};
dojo.debug=function(){
};
dojo.debugShallow=function(obj){
};
dojo.profile={start:function(){
},end:function(){
},stop:function(){
},dump:function(){
}};
function dj_eval(_15){
return dj_global.eval?dj_global.eval(_15):eval(_15);
}
dojo.unimplemented=function(_16,_17){
var _18="'"+_16+"' not implemented";
if(_17!=null){
_18+=" "+_17;
}
dojo.raise(_18);
};
dojo.deprecated=function(_19,_1a,_1b){
var _1c="DEPRECATED: "+_19;
if(_1a){
_1c+=" "+_1a;
}
if(_1b){
_1c+=" -- will be removed in version: "+_1b;
}
dojo.debug(_1c);
};
dojo.render=(function(){
function vscaffold(_1d,_1e){
var tmp={capable:false,support:{builtin:false,plugin:false},prefixes:_1d};
for(var i=0;i<_1e.length;i++){
tmp[_1e[i]]=false;
}
return tmp;
}
return {name:"",ver:dojo.version,os:{win:false,linux:false,osx:false},html:vscaffold(["html"],["ie","opera","khtml","safari","moz"]),svg:vscaffold(["svg"],["corel","adobe","batik"]),vml:vscaffold(["vml"],["ie"]),swf:vscaffold(["Swf","Flash","Mm"],["mm"]),swt:vscaffold(["Swt"],["ibm"])};
})();
dojo.hostenv=(function(){
var _21={isDebug:false,allowQueryConfig:false,baseScriptUri:"",baseRelativePath:"",libraryScriptUri:"",iePreventClobber:false,ieClobberMinimal:true,preventBackButtonFix:true,delayMozLoadingFix:false,searchIds:[],parseWidgets:true};
if(typeof djConfig=="undefined"){
djConfig=_21;
}else{
for(var _22 in _21){
if(typeof djConfig[_22]=="undefined"){
djConfig[_22]=_21[_22];
}
}
}
return {name_:"(unset)",version_:"(unset)",getName:function(){
return this.name_;
},getVersion:function(){
return this.version_;
},getText:function(uri){
dojo.unimplemented("getText","uri="+uri);
}};
})();
dojo.hostenv.getBaseScriptUri=function(){
if(djConfig.baseScriptUri.length){
return djConfig.baseScriptUri;
}
var uri=new String(djConfig.libraryScriptUri||djConfig.baseRelativePath);
if(!uri){
dojo.raise("Nothing returned by getLibraryScriptUri(): "+uri);
}
var _25=uri.lastIndexOf("/");
djConfig.baseScriptUri=djConfig.baseRelativePath;
return djConfig.baseScriptUri;
};
(function(){
var _26={pkgFileName:"__package__",loading_modules_:{},loaded_modules_:{},addedToLoadingCount:[],removedFromLoadingCount:[],inFlightCount:0,modulePrefixes_:{dojo:{name:"dojo",value:"src"}},setModulePrefix:function(_27,_28){
this.modulePrefixes_[_27]={name:_27,value:_28};
},moduleHasPrefix:function(_29){
var mp=this.modulePrefixes_;
return Boolean(mp[_29]&&mp[_29].value);
},getModulePrefix:function(_2b){
if(this.moduleHasPrefix(_2b)){
return this.modulePrefixes_[_2b].value;
}
return _2b;
},getTextStack:[],loadUriStack:[],loadedUris:[],post_load_:false,modulesLoadedListeners:[],unloadListeners:[],loadNotifying:false};
for(var _2c in _26){
dojo.hostenv[_2c]=_26[_2c];
}
})();
dojo.hostenv.loadPath=function(_2d,_2e,cb){
var uri;
if(_2d.charAt(0)=="/"||_2d.match(/^\w+:/)){
uri=_2d;
}else{
uri=this.getBaseScriptUri()+_2d;
}
if(djConfig.cacheBust&&dojo.render.html.capable){
uri+="?"+String(djConfig.cacheBust).replace(/\W+/g,"");
}
try{
return !_2e?this.loadUri(uri,cb):this.loadUriAndCheck(uri,_2e,cb);
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.hostenv.loadUri=function(uri,cb){
if(this.loadedUris[uri]){
return true;
}
var _33=this.getText(uri,null,true);
if(!_33){
return false;
}
this.loadedUris[uri]=true;
if(cb){
_33="("+_33+")";
}
var _34=dj_eval(_33);
if(cb){
cb(_34);
}
return true;
};
dojo.hostenv.loadUriAndCheck=function(uri,_36,cb){
var ok=true;
try{
ok=this.loadUri(uri,cb);
}
catch(e){
dojo.debug("failed loading ",uri," with error: ",e);
}
return Boolean(ok&&this.findModule(_36,false));
};
dojo.loaded=function(){
};
dojo.unloaded=function(){
};
dojo.hostenv.loaded=function(){
this.loadNotifying=true;
this.post_load_=true;
var mll=this.modulesLoadedListeners;
for(var x=0;x<mll.length;x++){
mll[x]();
}
this.modulesLoadedListeners=[];
this.loadNotifying=false;
dojo.loaded();
};
dojo.hostenv.unloaded=function(){
var mll=this.unloadListeners;
while(mll.length){
(mll.pop())();
}
dojo.unloaded();
};
dojo.addOnLoad=function(obj,_3d){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.modulesLoadedListeners.push(obj);
}else{
if(arguments.length>1){
dh.modulesLoadedListeners.push(function(){
obj[_3d]();
});
}
}
if(dh.post_load_&&dh.inFlightCount==0&&!dh.loadNotifying){
dh.callLoaded();
}
};
dojo.addOnUnload=function(obj,_40){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.unloadListeners.push(obj);
}else{
if(arguments.length>1){
dh.unloadListeners.push(function(){
obj[_40]();
});
}
}
};
dojo.hostenv.modulesLoaded=function(){
if(this.post_load_){
return;
}
if(this.loadUriStack.length==0&&this.getTextStack.length==0){
if(this.inFlightCount>0){
dojo.debug("files still in flight!");
return;
}
dojo.hostenv.callLoaded();
}
};
dojo.hostenv.callLoaded=function(){
if(typeof setTimeout=="object"){
setTimeout("dojo.hostenv.loaded();",0);
}else{
dojo.hostenv.loaded();
}
};
dojo.hostenv.getModuleSymbols=function(_42){
var _43=_42.split(".");
for(var i=_43.length;i>0;i--){
var _45=_43.slice(0,i).join(".");
if((i==1)&&!this.moduleHasPrefix(_45)){
_43[0]="../"+_43[0];
}else{
var _46=this.getModulePrefix(_45);
if(_46!=_45){
_43.splice(0,i,_46);
break;
}
}
}
return _43;
};
dojo.hostenv._global_omit_module_check=false;
dojo.hostenv.loadModule=function(_47,_48,_49){
if(!_47){
return;
}
_49=this._global_omit_module_check||_49;
var _4a=this.findModule(_47,false);
if(_4a){
return _4a;
}
if(dj_undef(_47,this.loading_modules_)){
this.addedToLoadingCount.push(_47);
}
this.loading_modules_[_47]=1;
var _4b=_47.replace(/\./g,"/")+".js";
var _4c=_47.split(".");
var _4d=this.getModuleSymbols(_47);
var _4e=((_4d[0].charAt(0)!="/")&&!_4d[0].match(/^\w+:/));
var _4f=_4d[_4d.length-1];
var ok;
if(_4f=="*"){
_47=_4c.slice(0,-1).join(".");
while(_4d.length){
_4d.pop();
_4d.push(this.pkgFileName);
_4b=_4d.join("/")+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,!_49?_47:null);
if(ok){
break;
}
_4d.pop();
}
}else{
_4b=_4d.join("/")+".js";
_47=_4c.join(".");
var _51=!_49?_47:null;
ok=this.loadPath(_4b,_51);
if(!ok&&!_48){
_4d.pop();
while(_4d.length){
_4b=_4d.join("/")+".js";
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
_4d.pop();
_4b=_4d.join("/")+"/"+this.pkgFileName+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
}
}
if(!ok&&!_49){
dojo.raise("Could not load '"+_47+"'; last tried '"+_4b+"'");
}
}
if(!_49&&!this["isXDomain"]){
_4a=this.findModule(_47,false);
if(!_4a){
dojo.raise("symbol '"+_47+"' is not defined after loading '"+_4b+"'");
}
}
return _4a;
};
dojo.hostenv.startPackage=function(_52){
var _53=String(_52);
var _54=_53;
var _55=_52.split(/\./);
if(_55[_55.length-1]=="*"){
_55.pop();
_54=_55.join(".");
}
var _56=dojo.evalObjPath(_54,true);
this.loaded_modules_[_53]=_56;
this.loaded_modules_[_54]=_56;
return _56;
};
dojo.hostenv.findModule=function(_57,_58){
var lmn=String(_57);
if(this.loaded_modules_[lmn]){
return this.loaded_modules_[lmn];
}
if(_58){
dojo.raise("no loaded module named '"+_57+"'");
}
return null;
};
dojo.kwCompoundRequire=function(_5a){
var _5b=_5a["common"]||[];
var _5c=_5a[dojo.hostenv.name_]?_5b.concat(_5a[dojo.hostenv.name_]||[]):_5b.concat(_5a["default"]||[]);
for(var x=0;x<_5c.length;x++){
var _5e=_5c[x];
if(_5e.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_5e);
}else{
dojo.hostenv.loadModule(_5e);
}
}
};
dojo.require=function(_5f){
dojo.hostenv.loadModule.apply(dojo.hostenv,arguments);
};
dojo.requireIf=function(_60,_61){
var _62=arguments[0];
if((_62===true)||(_62=="common")||(_62&&dojo.render[_62].capable)){
var _63=[];
for(var i=1;i<arguments.length;i++){
_63.push(arguments[i]);
}
dojo.require.apply(dojo,_63);
}
};
dojo.requireAfterIf=dojo.requireIf;
dojo.provide=function(_65){
return dojo.hostenv.startPackage.apply(dojo.hostenv,arguments);
};
dojo.registerModulePath=function(_66,_67){
return dojo.hostenv.setModulePrefix(_66,_67);
};
dojo.setModulePrefix=function(_68,_69){
dojo.deprecated("dojo.setModulePrefix(\""+_68+"\", \""+_69+"\")","replaced by dojo.registerModulePath","0.5");
return dojo.registerModulePath(_68,_69);
};
dojo.exists=function(obj,_6b){
var p=_6b.split(".");
for(var i=0;i<p.length;i++){
if(!obj[p[i]]){
return false;
}
obj=obj[p[i]];
}
return true;
};
dojo.hostenv.normalizeLocale=function(_6e){
return _6e?_6e.toLowerCase():dojo.locale;
};
dojo.hostenv.searchLocalePath=function(_6f,_70,_71){
_6f=dojo.hostenv.normalizeLocale(_6f);
var _72=_6f.split("-");
var _73=[];
for(var i=_72.length;i>0;i--){
_73.push(_72.slice(0,i).join("-"));
}
_73.push(false);
if(_70){
_73.reverse();
}
for(var j=_73.length-1;j>=0;j--){
var loc=_73[j]||"ROOT";
var _77=_71(loc);
if(_77){
break;
}
}
};
dojo.hostenv.localesGenerated;
dojo.hostenv.registerNlsPrefix=function(){
dojo.registerModulePath("nls","nls");
};
dojo.hostenv.preloadLocalizations=function(){
if(dojo.hostenv.localesGenerated){
dojo.hostenv.registerNlsPrefix();
function preload(_78){
_78=dojo.hostenv.normalizeLocale(_78);
dojo.hostenv.searchLocalePath(_78,true,function(loc){
for(var i=0;i<dojo.hostenv.localesGenerated.length;i++){
if(dojo.hostenv.localesGenerated[i]==loc){
dojo["require"]("nls.dojo_"+loc);
return true;
}
}
return false;
});
}
preload();
var _7b=djConfig.extraLocale||[];
for(var i=0;i<_7b.length;i++){
preload(_7b[i]);
}
}
dojo.hostenv.preloadLocalizations=function(){
};
};
dojo.requireLocalization=function(_7d,_7e,_7f){
dojo.hostenv.preloadLocalizations();
var _80=[_7d,"nls",_7e].join(".");
var _81=dojo.hostenv.findModule(_80);
if(_81){
if(djConfig.localizationComplete&&_81._built){
return;
}
var _82=dojo.hostenv.normalizeLocale(_7f).replace("-","_");
var _83=_80+"."+_82;
if(dojo.hostenv.findModule(_83)){
return;
}
}
_81=dojo.hostenv.startPackage(_80);
var _84=dojo.hostenv.getModuleSymbols(_7d);
var _85=_84.concat("nls").join("/");
var _86;
dojo.hostenv.searchLocalePath(_7f,false,function(loc){
var _88=loc.replace("-","_");
var _89=_80+"."+_88;
var _8a=false;
if(!dojo.hostenv.findModule(_89)){
dojo.hostenv.startPackage(_89);
var _8b=[_85];
if(loc!="ROOT"){
_8b.push(loc);
}
_8b.push(_7e);
var _8c=_8b.join("/")+".js";
_8a=dojo.hostenv.loadPath(_8c,null,function(_8d){
var _8e=function(){
};
_8e.prototype=_86;
_81[_88]=new _8e();
for(var j in _8d){
_81[_88][j]=_8d[j];
}
});
}else{
_8a=true;
}
if(_8a&&_81[_88]){
_86=_81[_88];
}else{
_81[_88]=_86;
}
});
};
(function(){
var _90=djConfig.extraLocale;
if(_90){
if(!_90 instanceof Array){
_90=[_90];
}
var req=dojo.requireLocalization;
dojo.requireLocalization=function(m,b,_94){
req(m,b,_94);
if(_94){
return;
}
for(var i=0;i<_90.length;i++){
req(m,b,_90[i]);
}
};
}
})();
}
if(typeof window!="undefined"){
(function(){
if(djConfig.allowQueryConfig){
var _96=document.location.toString();
var _97=_96.split("?",2);
if(_97.length>1){
var _98=_97[1];
var _99=_98.split("&");
for(var x in _99){
var sp=_99[x].split("=");
if((sp[0].length>9)&&(sp[0].substr(0,9)=="djConfig.")){
var opt=sp[0].substr(9);
try{
djConfig[opt]=eval(sp[1]);
}
catch(e){
djConfig[opt]=sp[1];
}
}
}
}
}
if(((djConfig["baseScriptUri"]=="")||(djConfig["baseRelativePath"]==""))&&(document&&document.getElementsByTagName)){
var _9d=document.getElementsByTagName("script");
var _9e=/(__package__|dojo|bootstrap1)\.js([\?\.]|$)/i;
for(var i=0;i<_9d.length;i++){
var src=_9d[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_9e);
if(m){
var _a2=src.substring(0,m.index);
if(src.indexOf("bootstrap1")>-1){
_a2+="../";
}
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=_a2;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=_a2;
}
break;
}
}
}
var dr=dojo.render;
var drh=dojo.render.html;
var drs=dojo.render.svg;
var dua=(drh.UA=navigator.userAgent);
var dav=(drh.AV=navigator.appVersion);
var t=true;
var f=false;
drh.capable=t;
drh.support.builtin=t;
dr.ver=parseFloat(drh.AV);
dr.os.mac=dav.indexOf("Macintosh")>=0;
dr.os.win=dav.indexOf("Windows")>=0;
dr.os.linux=dav.indexOf("X11")>=0;
drh.opera=dua.indexOf("Opera")>=0;
drh.khtml=(dav.indexOf("Konqueror")>=0)||(dav.indexOf("Safari")>=0);
drh.safari=dav.indexOf("Safari")>=0;
var _aa=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_aa>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_aa+6,_aa+14);
}
drh.ie=(document.all)&&(!drh.opera);
drh.ie50=drh.ie&&dav.indexOf("MSIE 5.0")>=0;
drh.ie55=drh.ie&&dav.indexOf("MSIE 5.5")>=0;
drh.ie60=drh.ie&&dav.indexOf("MSIE 6.0")>=0;
drh.ie70=drh.ie&&dav.indexOf("MSIE 7.0")>=0;
var cm=document["compatMode"];
drh.quirks=(cm=="BackCompat")||(cm=="QuirksMode")||drh.ie55||drh.ie50;
dojo.locale=dojo.locale||(drh.ie?navigator.userLanguage:navigator.language).toLowerCase();
dr.vml.capable=drh.ie;
drs.capable=f;
drs.support.plugin=f;
drs.support.builtin=f;
var _ac=window["document"];
var tdi=_ac["implementation"];
if((tdi)&&(tdi["hasFeature"])&&(tdi.hasFeature("org.w3c.dom.svg","1.0"))){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
if(drh.safari){
var tmp=dua.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.render.name=dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
dojo.hostenv._XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _b0=null;
var _b1=null;
try{
_b0=new XMLHttpRequest();
}
catch(e){
}
if(!_b0){
for(var i=0;i<3;++i){
var _b3=dojo.hostenv._XMLHTTP_PROGIDS[i];
try{
_b0=new ActiveXObject(_b3);
}
catch(e){
_b1=e;
}
if(_b0){
dojo.hostenv._XMLHTTP_PROGIDS=[_b3];
break;
}
}
}
if(!_b0){
return dojo.raise("XMLHTTP not available",_b1);
}
return _b0;
};
dojo.hostenv._blockAsync=false;
dojo.hostenv.getText=function(uri,_b5,_b6){
if(!_b5){
this._blockAsync=true;
}
var _b7=this.getXmlhttpObject();
function isDocumentOk(_b8){
var _b9=_b8["status"];
return Boolean((!_b9)||((200<=_b9)&&(300>_b9))||(_b9==304));
}
if(_b5){
var _ba=this,_bb=null,gbl=dojo.global();
var xhr=dojo.evalObjPath("dojo.io.XMLHTTPTransport");
_b7.onreadystatechange=function(){
if(_bb){
gbl.clearTimeout(_bb);
_bb=null;
}
if(_ba._blockAsync||(xhr&&xhr._blockAsync)){
_bb=gbl.setTimeout(function(){
_b7.onreadystatechange.apply(this);
},10);
}else{
if(4==_b7.readyState){
if(isDocumentOk(_b7)){
_b5(_b7.responseText);
}
}
}
};
}
_b7.open("GET",uri,_b5?true:false);
try{
_b7.send(null);
if(_b5){
return null;
}
if(!isDocumentOk(_b7)){
var err=Error("Unable to load "+uri+" status:"+_b7.status);
err.status=_b7.status;
err.responseText=_b7.responseText;
throw err;
}
}
catch(e){
this._blockAsync=false;
if((_b6)&&(!_b5)){
return null;
}else{
throw e;
}
}
this._blockAsync=false;
return _b7.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_bf){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_bf);
}else{
try{
var _c0=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_c0){
_c0=dojo.body();
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_bf));
_c0.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_bf+"</div>");
}
catch(e2){
window.status=_bf;
}
}
}
};
dojo.addOnLoad(function(){
dojo.hostenv._println_safe=true;
while(dojo.hostenv._println_buffer.length>0){
dojo.hostenv.println(dojo.hostenv._println_buffer.shift());
}
});
function dj_addNodeEvtHdlr(_c2,_c3,fp,_c5){
var _c6=_c2["on"+_c3]||function(){
};
_c2["on"+_c3]=function(){
fp.apply(_c2,arguments);
_c6.apply(_c2,arguments);
};
return true;
}
function dj_load_init(e){
var _c8=(e&&e.type)?e.type.toLowerCase():"load";
if(arguments.callee.initialized||(_c8!="domcontentloaded"&&_c8!="load")){
return;
}
arguments.callee.initialized=true;
if(typeof (_timer)!="undefined"){
clearInterval(_timer);
delete _timer;
}
var _c9=function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
};
if(dojo.hostenv.inFlightCount==0){
_c9();
dojo.hostenv.modulesLoaded();
}else{
dojo.addOnLoad(_c9);
}
}
if(document.addEventListener){
if(dojo.render.html.opera||(dojo.render.html.moz&&!djConfig.delayMozLoadingFix)){
document.addEventListener("DOMContentLoaded",dj_load_init,null);
}
window.addEventListener("load",dj_load_init,null);
}
if(dojo.render.html.ie&&dojo.render.os.win){
document.attachEvent("onreadystatechange",function(e){
if(document.readyState=="complete"){
dj_load_init();
}
});
}
if(/(WebKit|khtml)/i.test(navigator.userAgent)){
var _timer=setInterval(function(){
if(/loaded|complete/.test(document.readyState)){
dj_load_init();
}
},10);
}
if(dojo.render.html.ie){
dj_addNodeEvtHdlr(window,"beforeunload",function(){
dojo.hostenv._unloading=true;
window.setTimeout(function(){
dojo.hostenv._unloading=false;
},0);
});
}
dj_addNodeEvtHdlr(window,"unload",function(){
dojo.hostenv.unloaded();
if((!dojo.render.html.ie)||(dojo.render.html.ie&&dojo.hostenv._unloading)){
dojo.hostenv.unloaded();
}
});
dojo.hostenv.makeWidgets=function(){
var _cb=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_cb=_cb.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_cb=_cb.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_cb.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
var _cc=new dojo.xml.Parse();
if(_cb.length>0){
for(var x=0;x<_cb.length;x++){
var _ce=document.getElementById(_cb[x]);
if(!_ce){
continue;
}
var _cf=_cc.parseElement(_ce,null,true);
dojo.widget.getParser().createComponents(_cf);
}
}else{
if(djConfig.parseWidgets){
var _cf=_cc.parseElement(dojo.body(),null,true);
dojo.widget.getParser().createComponents(_cf);
}
}
}
}
};
dojo.addOnLoad(function(){
if(!dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
});
try{
if(dojo.render.html.ie){
document.namespaces.add("v","urn:schemas-microsoft-com:vml");
document.createStyleSheet().addRule("v\\:*","behavior:url(#default#VML)");
}
}
catch(e){
}
dojo.hostenv.writeIncludes=function(){
};
if(!dj_undef("document",this)){
dj_currentDocument=this.document;
}
dojo.doc=function(){
return dj_currentDocument;
};
dojo.body=function(){
return dojo.doc().body||dojo.doc().getElementsByTagName("body")[0];
};
dojo.byId=function(id,doc){
if((id)&&((typeof id=="string")||(id instanceof String))){
if(!doc){
doc=dj_currentDocument;
}
var ele=doc.getElementById(id);
if(ele&&(ele.id!=id)&&doc.all){
ele=null;
eles=doc.all[id];
if(eles){
if(eles.length){
for(var i=0;i<eles.length;i++){
if(eles[i].id==id){
ele=eles[i];
break;
}
}
}else{
ele=eles;
}
}
}
return ele;
}
return id;
};
dojo.setContext=function(_d4,_d5){
dj_currentContext=_d4;
dj_currentDocument=_d5;
};
dojo._fireCallback=function(_d6,_d7,_d8){
if((_d7)&&((typeof _d6=="string")||(_d6 instanceof String))){
_d6=_d7[_d6];
}
return (_d7?_d6.apply(_d7,_d8||[]):_d6());
};
dojo.withGlobal=function(_d9,_da,_db,_dc){
var _dd;
var _de=dj_currentContext;
var _df=dj_currentDocument;
try{
dojo.setContext(_d9,_d9.document);
_dd=dojo._fireCallback(_da,_db,_dc);
}
finally{
dojo.setContext(_de,_df);
}
return _dd;
};
dojo.withDoc=function(_e0,_e1,_e2,_e3){
var _e4;
var _e5=dj_currentDocument;
try{
dj_currentDocument=_e0;
_e4=dojo._fireCallback(_e1,_e2,_e3);
}
finally{
dj_currentDocument=_e5;
}
return _e4;
};
}
(function(){
if(typeof dj_usingBootstrap!="undefined"){
return;
}
var _e6=false;
var _e7=false;
var _e8=false;
if((typeof this["load"]=="function")&&((typeof this["Packages"]=="function")||(typeof this["Packages"]=="object"))){
_e6=true;
}else{
if(typeof this["load"]=="function"){
_e7=true;
}else{
if(window.widget){
_e8=true;
}
}
}
var _e9=[];
if((this["djConfig"])&&((djConfig["isDebug"])||(djConfig["debugAtAllCosts"]))){
_e9.push("debug.js");
}
if((this["djConfig"])&&(djConfig["debugAtAllCosts"])&&(!_e6)&&(!_e8)){
_e9.push("browser_debug.js");
}
var _ea=djConfig["baseScriptUri"];
if((this["djConfig"])&&(djConfig["baseLoaderUri"])){
_ea=djConfig["baseLoaderUri"];
}
for(var x=0;x<_e9.length;x++){
var _ec=_ea+"src/"+_e9[x];
if(_e6||_e7){
load(_ec);
}else{
try{
document.write("<scr"+"ipt type='text/javascript' src='"+_ec+"'></scr"+"ipt>");
}
catch(e){
var _ed=document.createElement("script");
_ed.src=_ec;
document.getElementsByTagName("head")[0].appendChild(_ed);
}
}
}
})();
dojo.provide("dojo.dom");
dojo.dom.ELEMENT_NODE=1;
dojo.dom.ATTRIBUTE_NODE=2;
dojo.dom.TEXT_NODE=3;
dojo.dom.CDATA_SECTION_NODE=4;
dojo.dom.ENTITY_REFERENCE_NODE=5;
dojo.dom.ENTITY_NODE=6;
dojo.dom.PROCESSING_INSTRUCTION_NODE=7;
dojo.dom.COMMENT_NODE=8;
dojo.dom.DOCUMENT_NODE=9;
dojo.dom.DOCUMENT_TYPE_NODE=10;
dojo.dom.DOCUMENT_FRAGMENT_NODE=11;
dojo.dom.NOTATION_NODE=12;
dojo.dom.dojoml="http://www.dojotoolkit.org/2004/dojoml";
dojo.dom.xmlns={svg:"http://www.w3.org/2000/svg",smil:"http://www.w3.org/2001/SMIL20/",mml:"http://www.w3.org/1998/Math/MathML",cml:"http://www.xml-cml.org",xlink:"http://www.w3.org/1999/xlink",xhtml:"http://www.w3.org/1999/xhtml",xul:"http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul",xbl:"http://www.mozilla.org/xbl",fo:"http://www.w3.org/1999/XSL/Format",xsl:"http://www.w3.org/1999/XSL/Transform",xslt:"http://www.w3.org/1999/XSL/Transform",xi:"http://www.w3.org/2001/XInclude",xforms:"http://www.w3.org/2002/01/xforms",saxon:"http://icl.com/saxon",xalan:"http://xml.apache.org/xslt",xsd:"http://www.w3.org/2001/XMLSchema",dt:"http://www.w3.org/2001/XMLSchema-datatypes",xsi:"http://www.w3.org/2001/XMLSchema-instance",rdf:"http://www.w3.org/1999/02/22-rdf-syntax-ns#",rdfs:"http://www.w3.org/2000/01/rdf-schema#",dc:"http://purl.org/dc/elements/1.1/",dcq:"http://purl.org/dc/qualifiers/1.0","soap-env":"http://schemas.xmlsoap.org/soap/envelope/",wsdl:"http://schemas.xmlsoap.org/wsdl/",AdobeExtensions:"http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"};
dojo.dom.isNode=function(wh){
if(typeof Element=="function"){
try{
return wh instanceof Element;
}
catch(E){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.dom.getUniqueId=function(){
var _ef=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_ef.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_f1,_f2){
var _f3=_f1.firstChild;
while(_f3&&_f3.nodeType!=dojo.dom.ELEMENT_NODE){
_f3=_f3.nextSibling;
}
if(_f2&&_f3&&_f3.tagName&&_f3.tagName.toLowerCase()!=_f2.toLowerCase()){
_f3=dojo.dom.nextElement(_f3,_f2);
}
return _f3;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_f4,_f5){
var _f6=_f4.lastChild;
while(_f6&&_f6.nodeType!=dojo.dom.ELEMENT_NODE){
_f6=_f6.previousSibling;
}
if(_f5&&_f6&&_f6.tagName&&_f6.tagName.toLowerCase()!=_f5.toLowerCase()){
_f6=dojo.dom.prevElement(_f6,_f5);
}
return _f6;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(_f7,_f8){
if(!_f7){
return null;
}
do{
_f7=_f7.nextSibling;
}while(_f7&&_f7.nodeType!=dojo.dom.ELEMENT_NODE);
if(_f7&&_f8&&_f8.toLowerCase()!=_f7.tagName.toLowerCase()){
return dojo.dom.nextElement(_f7,_f8);
}
return _f7;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(_f9,_fa){
if(!_f9){
return null;
}
if(_fa){
_fa=_fa.toLowerCase();
}
do{
_f9=_f9.previousSibling;
}while(_f9&&_f9.nodeType!=dojo.dom.ELEMENT_NODE);
if(_f9&&_fa&&_fa.toLowerCase()!=_f9.tagName.toLowerCase()){
return dojo.dom.prevElement(_f9,_fa);
}
return _f9;
};
dojo.dom.moveChildren=function(_fb,_fc,_fd){
var _fe=0;
if(_fd){
while(_fb.hasChildNodes()&&_fb.firstChild.nodeType==dojo.dom.TEXT_NODE){
_fb.removeChild(_fb.firstChild);
}
while(_fb.hasChildNodes()&&_fb.lastChild.nodeType==dojo.dom.TEXT_NODE){
_fb.removeChild(_fb.lastChild);
}
}
while(_fb.hasChildNodes()){
_fc.appendChild(_fb.firstChild);
_fe++;
}
return _fe;
};
dojo.dom.copyChildren=function(_ff,_100,trim){
var _102=_ff.cloneNode(true);
return this.moveChildren(_102,_100,trim);
};
dojo.dom.removeChildren=function(node){
var _104=node.childNodes.length;
while(node.hasChildNodes()){
node.removeChild(node.firstChild);
}
return _104;
};
dojo.dom.replaceChildren=function(node,_106){
dojo.dom.removeChildren(node);
node.appendChild(_106);
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_109,_10a){
var _10b=[];
var _10c=(_109&&(_109 instanceof Function||typeof _109=="function"));
while(node){
if(!_10c||_109(node)){
_10b.push(node);
}
if(_10a&&_10b.length>0){
return _10b[0];
}
node=node.parentNode;
}
if(_10a){
return null;
}
return _10b;
};
dojo.dom.getAncestorsByTag=function(node,tag,_10f){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_10f);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_114,_115){
if(_115&&node){
node=node.parentNode;
}
while(node){
if(node==_114){
return true;
}
node=node.parentNode;
}
return false;
};
dojo.dom.innerXML=function(node){
if(node.innerXML){
return node.innerXML;
}else{
if(node.xml){
return node.xml;
}else{
if(typeof XMLSerializer!="undefined"){
return (new XMLSerializer()).serializeToString(node);
}
}
}
};
dojo.dom.createDocument=function(){
var doc=null;
var _118=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _119=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_119.length;i++){
try{
doc=new ActiveXObject(_119[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_118.implementation)&&(_118.implementation.createDocument)){
doc=_118.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_11c){
if(!_11c){
_11c="text/xml";
}
if(!dj_undef("DOMParser")){
var _11d=new DOMParser();
return _11d.parseFromString(str,_11c);
}else{
if(!dj_undef("ActiveXObject")){
var _11e=dojo.dom.createDocument();
if(_11e){
_11e.async=false;
_11e.loadXML(str);
return _11e;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _11f=dojo.doc();
if(_11f.createElement){
var tmp=_11f.createElement("xml");
tmp.innerHTML=str;
if(_11f.implementation&&_11f.implementation.createDocument){
var _121=_11f.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_121.importNode(tmp.childNodes.item(i),true);
}
return _121;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_124){
if(_124.firstChild){
_124.insertBefore(node,_124.firstChild);
}else{
_124.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_127){
if(_127!=true&&(node===ref||node.nextSibling===ref)){
return false;
}
var _128=ref.parentNode;
_128.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_12b){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_12b!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_12b);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_12f){
if((!node)||(!ref)||(!_12f)){
return false;
}
switch(_12f.toLowerCase()){
case "before":
return dojo.dom.insertBefore(node,ref);
case "after":
return dojo.dom.insertAfter(node,ref);
case "first":
if(ref.firstChild){
return dojo.dom.insertBefore(node,ref.firstChild);
}else{
ref.appendChild(node);
return true;
}
break;
default:
ref.appendChild(node);
return true;
}
};
dojo.dom.insertAtIndex=function(node,_131,_132){
var _133=_131.childNodes;
if(!_133.length){
_131.appendChild(node);
return true;
}
var _134=null;
for(var i=0;i<_133.length;i++){
var _136=_133.item(i)["getAttribute"]?parseInt(_133.item(i).getAttribute("dojoinsertionindex")):-1;
if(_136<_132){
_134=_133.item(i);
}
}
if(_134){
return dojo.dom.insertAfter(node,_134);
}else{
return dojo.dom.insertBefore(node,_133.item(0));
}
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _139=dojo.doc();
dojo.dom.replaceChildren(node,_139.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _13a="";
if(node==null){
return _13a;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_13a+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_13a+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _13a;
}
};
dojo.dom.hasParent=function(node){
return node&&node.parentNode&&dojo.dom.isNode(node.parentNode);
};
dojo.dom.isTag=function(node){
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName==String(arguments[i])){
return String(arguments[i]);
}
}
}
return "";
};
dojo.dom.setAttributeNS=function(elem,_140,_141,_142){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_140,_141,_142);
}else{
var _143=elem.ownerDocument;
var _144=_143.createNode(2,_141,_140);
_144.nodeValue=_142;
elem.setAttributeNode(_144);
}
};
dojo.provide("dojo.xml.Parse");
dojo.xml.Parse=function(){
function getTagName(node){
return ((node)&&(node.tagName)?node.tagName.toLowerCase():"");
}
function getDojoTagName(node){
var _147=getTagName(node);
if(!_147){
return "";
}
if((dojo.widget)&&(dojo.widget.tags[_147])){
return _147;
}
var p=_147.indexOf(":");
if(p>=0){
return _147;
}
if(_147.substr(0,5)=="dojo:"){
return _147;
}
if(dojo.render.html.capable&&dojo.render.html.ie&&node.scopeName!="HTML"){
return node.scopeName.toLowerCase()+":"+_147;
}
if(_147.substr(0,4)=="dojo"){
return "dojo:"+_147.substring(4);
}
var djt=node.getAttribute("dojoType")||node.getAttribute("dojotype");
if(djt){
if(djt.indexOf(":")<0){
djt="dojo:"+djt;
}
return djt.toLowerCase();
}
djt=node.getAttributeNS&&node.getAttributeNS(dojo.dom.dojoml,"type");
if(djt){
return "dojo:"+djt.toLowerCase();
}
try{
djt=node.getAttribute("dojo:type");
}
catch(e){
}
if(djt){
return "dojo:"+djt.toLowerCase();
}
if((!dj_global["djConfig"])||(djConfig["ignoreClassNames"])){
var _14a=node.className||node.getAttribute("class");
if((_14a)&&(_14a.indexOf)&&(_14a.indexOf("dojo-")!=-1)){
var _14b=_14a.split(" ");
for(var x=0,c=_14b.length;x<c;x++){
if(_14b[x].slice(0,5)=="dojo-"){
return "dojo:"+_14b[x].substr(5).toLowerCase();
}
}
}
}
return "";
}
this.parseElement=function(node,_14f,_150,_151){
var _152={};
var _153=getTagName(node);
if((_153)&&(_153.indexOf("/")==0)){
return null;
}
var _154=true;
if(_150){
var _155=getDojoTagName(node);
_153=_155||_153;
_154=Boolean(_155);
}
if(node&&node.getAttribute&&node.getAttribute("parseWidgets")&&node.getAttribute("parseWidgets")=="false"){
return {};
}
_152[_153]=[];
var pos=_153.indexOf(":");
if(pos>0){
var ns=_153.substring(0,pos);
_152["ns"]=ns;
if((dojo.ns)&&(!dojo.ns.allow(ns))){
_154=false;
}
}
if(_154){
var _158=this.parseAttributes(node);
for(var attr in _158){
if((!_152[_153][attr])||(typeof _152[_153][attr]!="array")){
_152[_153][attr]=[];
}
_152[_153][attr].push(_158[attr]);
}
_152[_153].nodeRef=node;
_152.tagName=_153;
_152.index=_151||0;
}
var _15a=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
_15a++;
var ctn=getDojoTagName(tcn)||getTagName(tcn);
if(!_152[ctn]){
_152[ctn]=[];
}
_152[ctn].push(this.parseElement(tcn,true,_150,_15a));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_152[ctn][_152[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_152[_153].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _152;
};
this.parseAttributes=function(node){
var _15f={};
var atts=node.attributes;
var _161,i=0;
while((_161=atts[i++])){
if((dojo.render.html.capable)&&(dojo.render.html.ie)){
if(!_161){
continue;
}
if((typeof _161=="object")&&(typeof _161.nodeValue=="undefined")||(_161.nodeValue==null)||(_161.nodeValue=="")){
continue;
}
}
var nn=_161.nodeName.split(":");
nn=(nn.length==2)?nn[1]:_161.nodeName;
_15f[nn]={value:_161.nodeValue};
}
return _15f;
};
};
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_164,_165){
if(typeof _165!="function"){
dojo.raise("dojo.inherits: superclass argument ["+_165+"] must be a function (subclass: ["+_164+"']");
}
_164.prototype=new _165();
_164.prototype.constructor=_164;
_164.superclass=_165.prototype;
_164["super"]=_165.prototype;
};
dojo.lang._mixin=function(obj,_167){
var tobj={};
for(var x in _167){
if((typeof tobj[x]=="undefined")||(tobj[x]!=_167[x])){
obj[x]=_167[x];
}
}
if(dojo.render.html.ie&&(typeof (_167["toString"])=="function")&&(_167["toString"]!=obj["toString"])&&(_167["toString"]!=tobj["toString"])){
obj.toString=_167.toString;
}
return obj;
};
dojo.lang.mixin=function(obj,_16b){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(obj,arguments[i]);
}
return obj;
};
dojo.lang.extend=function(_16e,_16f){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(_16e.prototype,arguments[i]);
}
return _16e;
};
dojo.inherits=dojo.lang.inherits;
dojo.mixin=dojo.lang.mixin;
dojo.extend=dojo.lang.extend;
dojo.lang.find=function(_172,_173,_174,_175){
if(!dojo.lang.isArrayLike(_172)&&dojo.lang.isArrayLike(_173)){
dojo.deprecated("dojo.lang.find(value, array)","use dojo.lang.find(array, value) instead","0.5");
var temp=_172;
_172=_173;
_173=temp;
}
var _177=dojo.lang.isString(_172);
if(_177){
_172=_172.split("");
}
if(_175){
var step=-1;
var i=_172.length-1;
var end=-1;
}else{
var step=1;
var i=0;
var end=_172.length;
}
if(_174){
while(i!=end){
if(_172[i]===_173){
return i;
}
i+=step;
}
}else{
while(i!=end){
if(_172[i]==_173){
return i;
}
i+=step;
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(_17b,_17c,_17d){
return dojo.lang.find(_17b,_17c,_17d,true);
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(_17e,_17f){
return dojo.lang.find(_17e,_17f)>-1;
};
dojo.lang.isObject=function(it){
if(typeof it=="undefined"){
return false;
}
return (typeof it=="object"||it===null||dojo.lang.isArray(it)||dojo.lang.isFunction(it));
};
dojo.lang.isArray=function(it){
return (it&&it instanceof Array||typeof it=="array");
};
dojo.lang.isArrayLike=function(it){
if((!it)||(dojo.lang.isUndefined(it))){
return false;
}
if(dojo.lang.isString(it)){
return false;
}
if(dojo.lang.isFunction(it)){
return false;
}
if(dojo.lang.isArray(it)){
return true;
}
if((it.tagName)&&(it.tagName.toLowerCase()=="form")){
return false;
}
if(dojo.lang.isNumber(it.length)&&isFinite(it.length)){
return true;
}
return false;
};
dojo.lang.isFunction=function(it){
if(!it){
return false;
}
if((typeof (it)=="function")&&(it=="[object NodeList]")){
return false;
}
return (it instanceof Function||typeof it=="function");
};
dojo.lang.isString=function(it){
return (typeof it=="string"||it instanceof String);
};
dojo.lang.isAlien=function(it){
if(!it){
return false;
}
return !dojo.lang.isFunction()&&/\{\s*\[native code\]\s*\}/.test(String(it));
};
dojo.lang.isBoolean=function(it){
return (it instanceof Boolean||typeof it=="boolean");
};
dojo.lang.isNumber=function(it){
return (it instanceof Number||typeof it=="number");
};
dojo.lang.isUndefined=function(it){
return ((typeof (it)=="undefined")&&(it==undefined));
};
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_189,_18a){
var fcn=(dojo.lang.isString(_18a)?_189[_18a]:_18a)||function(){
};
return function(){
return fcn.apply(_189,arguments);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_18c,_18d,_18e){
var nso=(_18d||dojo.lang.anon);
if((_18e)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_18c){
return x;
}
}
catch(e){
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_18c;
return ret;
};
dojo.lang.forward=function(_192){
return function(){
return this[_192].apply(this,arguments);
};
};
dojo.lang.curry=function(ns,func){
var _195=[];
ns=ns||dj_global;
if(dojo.lang.isString(func)){
func=ns[func];
}
for(var x=2;x<arguments.length;x++){
_195.push(arguments[x]);
}
var _197=(func["__preJoinArity"]||func.length)-_195.length;
function gather(_198,_199,_19a){
var _19b=_19a;
var _19c=_199.slice(0);
for(var x=0;x<_198.length;x++){
_19c.push(_198[x]);
}
_19a=_19a-_198.length;
if(_19a<=0){
var res=func.apply(ns,_19c);
_19a=_19b;
return res;
}else{
return function(){
return gather(arguments,_19c,_19a);
};
}
}
return gather([],_195,_197);
};
dojo.lang.curryArguments=function(ns,func,args,_1a2){
var _1a3=[];
var x=_1a2||0;
for(x=_1a2;x<args.length;x++){
_1a3.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[ns,func].concat(_1a3));
};
dojo.lang.tryThese=function(){
for(var x=0;x<arguments.length;x++){
try{
if(typeof arguments[x]=="function"){
var ret=(arguments[x]());
if(ret){
return ret;
}
}
}
catch(e){
dojo.debug(e);
}
}
};
dojo.lang.delayThese=function(farr,cb,_1a9,_1aa){
if(!farr.length){
if(typeof _1aa=="function"){
_1aa();
}
return;
}
if((typeof _1a9=="undefined")&&(typeof cb=="number")){
_1a9=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_1a9){
_1a9=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_1a9,_1aa);
},_1a9);
};
dojo.provide("dojo.lang.array");
dojo.lang.has=function(obj,name){
try{
return typeof obj[name]!="undefined";
}
catch(e){
return false;
}
};
dojo.lang.isEmpty=function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _1af=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_1af++;
break;
}
}
return _1af==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
};
dojo.lang.map=function(arr,obj,_1b3){
var _1b4=dojo.lang.isString(arr);
if(_1b4){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_1b3)){
_1b3=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_1b3){
var _1b5=obj;
obj=_1b3;
_1b3=_1b5;
}
}
if(Array.map){
var _1b6=Array.map(arr,_1b3,obj);
}else{
var _1b6=[];
for(var i=0;i<arr.length;++i){
_1b6.push(_1b3.call(obj,arr[i]));
}
}
if(_1b4){
return _1b6.join("");
}else{
return _1b6;
}
};
dojo.lang.reduce=function(arr,_1b9,obj,_1bb){
var _1bc=_1b9;
var ob=obj?obj:dj_global;
dojo.lang.map(arr,function(val){
_1bc=_1bb.call(ob,_1bc,val);
});
return _1bc;
};
dojo.lang.forEach=function(_1bf,_1c0,_1c1){
if(dojo.lang.isString(_1bf)){
_1bf=_1bf.split("");
}
if(Array.forEach){
Array.forEach(_1bf,_1c0,_1c1);
}else{
if(!_1c1){
_1c1=dj_global;
}
for(var i=0,l=_1bf.length;i<l;i++){
_1c0.call(_1c1,_1bf[i],i,_1bf);
}
}
};
dojo.lang._everyOrSome=function(_1c4,arr,_1c6,_1c7){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_1c4?"every":"some"](arr,_1c6,_1c7);
}else{
if(!_1c7){
_1c7=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _1ca=_1c6.call(_1c7,arr[i],i,arr);
if(_1c4&&!_1ca){
return false;
}else{
if((!_1c4)&&(_1ca)){
return true;
}
}
}
return Boolean(_1c4);
}
};
dojo.lang.every=function(arr,_1cc,_1cd){
return this._everyOrSome(true,arr,_1cc,_1cd);
};
dojo.lang.some=function(arr,_1cf,_1d0){
return this._everyOrSome(false,arr,_1cf,_1d0);
};
dojo.lang.filter=function(arr,_1d2,_1d3){
var _1d4=dojo.lang.isString(arr);
if(_1d4){
arr=arr.split("");
}
var _1d5;
if(Array.filter){
_1d5=Array.filter(arr,_1d2,_1d3);
}else{
if(!_1d3){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_1d3=dj_global;
}
_1d5=[];
for(var i=0;i<arr.length;i++){
if(_1d2.call(_1d3,arr[i],i,arr)){
_1d5.push(arr[i]);
}
}
}
if(_1d4){
return _1d5.join("");
}else{
return _1d5;
}
};
dojo.lang.unnest=function(){
var out=[];
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isArrayLike(arguments[i])){
var add=dojo.lang.unnest.apply(this,arguments[i]);
out=out.concat(add);
}else{
out.push(arguments[i]);
}
}
return out;
};
dojo.lang.toArray=function(_1da,_1db){
var _1dc=[];
for(var i=_1db||0;i<_1da.length;i++){
_1dc.push(_1da[i]);
}
return _1dc;
};
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_1df){
var _1e0=window,_1e1=2;
if(!dojo.lang.isFunction(func)){
_1e0=func;
func=_1df;
_1df=arguments[2];
_1e1++;
}
if(dojo.lang.isString(func)){
func=_1e0[func];
}
var args=[];
for(var i=_1e1;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_1e0,args);
},_1df);
};
dojo.lang.clearTimeout=function(_1e4){
dojo.global().clearTimeout(_1e4);
};
dojo.lang.getNameInObj=function(ns,item){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===item){
return new String(x);
}
}
return null;
};
dojo.lang.shallowCopy=function(obj,deep){
var i,ret;
if(obj===null){
return null;
}
if(dojo.lang.isObject(obj)){
ret=new obj.constructor();
for(i in obj){
if(dojo.lang.isUndefined(ret[i])){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}
}else{
if(dojo.lang.isArray(obj)){
ret=[];
for(i=0;i<obj.length;i++){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}else{
ret=obj;
}
}
return ret;
};
dojo.lang.firstValued=function(){
for(var i=0;i<arguments.length;i++){
if(typeof arguments[i]!="undefined"){
return arguments[i];
}
}
return undefined;
};
dojo.lang.getObjPathValue=function(_1ed,_1ee,_1ef){
with(dojo.parseObjPath(_1ed,_1ee,_1ef)){
return dojo.evalProp(prop,obj,_1ef);
}
};
dojo.lang.setObjPathValue=function(_1f0,_1f1,_1f2,_1f3){
if(arguments.length<4){
_1f3=true;
}
with(dojo.parseObjPath(_1f0,_1f2,_1f3)){
if(obj&&(_1f3||(prop in obj))){
obj[prop]=_1f1;
}
}
};
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_1f4,_1f5,init,_1f7){
if((dojo.lang.isFunction(_1f7))||((!_1f7)&&(!dojo.lang.isFunction(init)))){
var temp=_1f7;
_1f7=init;
init=temp;
}
var _1f9=[];
if(dojo.lang.isArray(_1f5)){
_1f9=_1f5;
_1f5=_1f9.shift();
}
if(!init){
init=dojo.evalObjPath(_1f4,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_1f5?_1f5.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _1f5();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_1f9;
for(var i=0,l=_1f9.length;i<l;i++){
dojo.lang.extend(ctor,_1f9[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_1f4;
if(dojo.lang.isArray(_1f7)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_1f7));
}else{
dojo.lang.extend(ctor,(_1f7)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
dojo.lang.setObjPathValue(_1f4,ctor,null,true);
return ctor;
};
dojo.lang.declare._makeConstructor=function(){
return function(){
var self=this._getPropContext();
var s=self.constructor.superclass;
if((s)&&(s.constructor)){
if(s.constructor==arguments.callee){
this._inherited("constructor",arguments);
}else{
this._contextMethod(s,"constructor",arguments);
}
}
var ms=(self.constructor.mixins)||([]);
for(var i=0,m;(m=ms[i]);i++){
(((m.prototype)&&(m.prototype.initializer))||(m)).apply(this,arguments);
}
if((!this.prototyping)&&(self.initializer)){
self.initializer.apply(this,arguments);
}
};
};
dojo.lang.declare._common={_getPropContext:function(){
return (this.___proto||this);
},_contextMethod:function(_203,_204,args){
var _206,_207=this.___proto;
this.___proto=_203;
try{
_206=_203[_204].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_207;
}
return _206;
},_inherited:function(prop,args){
var p=this._getPropContext();
do{
if((!p.constructor)||(!p.constructor.superclass)){
return;
}
p=p.constructor.superclass;
}while(!(prop in p));
return (dojo.lang.isFunction(p[prop])?this._contextMethod(p,prop,args):p[prop]);
}};
dojo.declare=dojo.lang.declare;
dojo.provide("dojo.ns");
dojo.ns={namespaces:{},failed:{},loading:{},loaded:{},register:function(name,_20c,_20d,_20e){
if(!_20e||!this.namespaces[name]){
this.namespaces[name]=new dojo.ns.Ns(name,_20c,_20d);
}
},allow:function(name){
if(this.failed[name]){
return false;
}
if((djConfig.excludeNamespace)&&(dojo.lang.inArray(djConfig.excludeNamespace,name))){
return false;
}
return ((name==this.dojo)||(!djConfig.includeNamespace)||(dojo.lang.inArray(djConfig.includeNamespace,name)));
},get:function(name){
return this.namespaces[name];
},require:function(name){
var ns=this.namespaces[name];
if((ns)&&(this.loaded[name])){
return ns;
}
if(!this.allow(name)){
return false;
}
if(this.loading[name]){
dojo.debug("dojo.namespace.require: re-entrant request to load namespace \""+name+"\" must fail.");
return false;
}
var req=dojo.require;
this.loading[name]=true;
try{
if(name=="dojo"){
req("dojo.namespaces.dojo");
}else{
if(!dojo.hostenv.moduleHasPrefix(name)){
dojo.registerModulePath(name,"../"+name);
}
req([name,"manifest"].join("."),false,true);
}
if(!this.namespaces[name]){
this.failed[name]=true;
}
}
finally{
this.loading[name]=false;
}
return this.namespaces[name];
}};
dojo.ns.Ns=function(name,_215,_216){
this.name=name;
this.module=_215;
this.resolver=_216;
this._loaded=[];
this._failed=[];
};
dojo.ns.Ns.prototype.resolve=function(name,_218,_219){
if(!this.resolver||djConfig["skipAutoRequire"]){
return false;
}
var _21a=this.resolver(name,_218);
if((_21a)&&(!this._loaded[_21a])&&(!this._failed[_21a])){
var req=dojo.require;
req(_21a,false,true);
if(dojo.hostenv.findModule(_21a,false)){
this._loaded[_21a]=true;
}else{
if(!_219){
dojo.raise("dojo.ns.Ns.resolve: module '"+_21a+"' not found after loading via namespace '"+this.name+"'");
}
this._failed[_21a]=true;
}
}
return Boolean(this._loaded[_21a]);
};
dojo.registerNamespace=function(name,_21d,_21e){
dojo.ns.register.apply(dojo.ns,arguments);
};
dojo.registerNamespaceResolver=function(name,_220){
var n=dojo.ns.namespaces[name];
if(n){
n.resolver=_220;
}
};
dojo.registerNamespaceManifest=function(_222,path,name,_225,_226){
dojo.registerModulePath(name,path);
dojo.registerNamespace(name,_225,_226);
};
dojo.registerNamespace("dojo","dojo.widget");
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_228){
var dl=dojo.lang;
var ao={srcObj:dj_global,srcFunc:null,adviceObj:dj_global,adviceFunc:null,aroundObj:null,aroundFunc:null,adviceType:(args.length>2)?args[0]:"after",precedence:"last",once:false,delay:null,rate:0,adviceMsg:false};
switch(args.length){
case 0:
return;
case 1:
return;
case 2:
ao.srcFunc=args[0];
ao.adviceFunc=args[1];
break;
case 3:
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isFunction(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
var _22b=dl.nameAnonFunc(args[2],ao.adviceObj,_228);
ao.adviceFunc=_22b;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _22b=dl.nameAnonFunc(args[0],ao.srcObj,_228);
ao.srcFunc=_22b;
ao.adviceObj=args[1];
ao.adviceFunc=args[2];
}
}
}
}
break;
case 4:
if((dl.isObject(args[0]))&&(dl.isObject(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isString(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isFunction(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
var _22b=dl.nameAnonFunc(args[1],dj_global,_228);
ao.srcFunc=_22b;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _22b=dl.nameAnonFunc(args[3],dj_global,_228);
ao.adviceObj=dj_global;
ao.adviceFunc=_22b;
}else{
if(dl.isObject(args[1])){
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=dj_global;
ao.adviceFunc=args[3];
}else{
if(dl.isObject(args[2])){
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
ao.srcObj=ao.adviceObj=ao.aroundObj=dj_global;
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
ao.aroundFunc=args[3];
}
}
}
}
}
}
break;
case 6:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundFunc=args[5];
ao.aroundObj=dj_global;
break;
default:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundObj=args[5];
ao.aroundFunc=args[6];
ao.once=args[7];
ao.delay=args[8];
ao.rate=args[9];
ao.adviceMsg=args[10];
break;
}
if(dl.isFunction(ao.aroundFunc)){
var _22b=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_228);
ao.aroundFunc=_22b;
}
if(dl.isFunction(ao.srcFunc)){
ao.srcFunc=dl.getNameInObj(ao.srcObj,ao.srcFunc);
}
if(dl.isFunction(ao.adviceFunc)){
ao.adviceFunc=dl.getNameInObj(ao.adviceObj,ao.adviceFunc);
}
if((ao.aroundObj)&&(dl.isFunction(ao.aroundFunc))){
ao.aroundFunc=dl.getNameInObj(ao.aroundObj,ao.aroundFunc);
}
if(!ao.srcObj){
dojo.raise("bad srcObj for srcFunc: "+ao.srcFunc);
}
if(!ao.adviceObj){
dojo.raise("bad adviceObj for adviceFunc: "+ao.adviceFunc);
}
if(!ao.adviceFunc){
dojo.debug("bad adviceFunc for srcFunc: "+ao.srcFunc);
dojo.debugShallow(ao);
}
return ao;
}
this.connect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.connect(ao);
}
ao.srcFunc="onkeypress";
}
if(dojo.lang.isArray(ao.srcObj)&&ao.srcObj!=""){
var _22d={};
for(var x in ao){
_22d[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_22d.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_22d));
});
return mjps;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
if(ao.adviceFunc){
var mjp2=dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj,ao.adviceFunc);
}
mjp.kwAddAdvice(ao);
return mjp;
};
this.log=function(a1,a2){
var _235;
if((arguments.length==1)&&(typeof a1=="object")){
_235=a1;
}else{
_235={srcObj:a1,srcFunc:a2};
}
_235.adviceFunc=function(){
var _236=[];
for(var x=0;x<arguments.length;x++){
_236.push(arguments[x]);
}
dojo.debug("("+_235.srcObj+")."+_235.srcFunc,":",_236.join(", "));
};
this.kwConnect(_235);
};
this.connectBefore=function(){
var args=["before"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectAround=function(){
var args=["around"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.once=true;
return this.connect(ao);
};
this._kwConnectImpl=function(_23d,_23e){
var fn=(_23e)?"disconnect":"connect";
if(typeof _23d["srcFunc"]=="function"){
_23d.srcObj=_23d["srcObj"]||dj_global;
var _240=dojo.lang.nameAnonFunc(_23d.srcFunc,_23d.srcObj,true);
_23d.srcFunc=_240;
}
if(typeof _23d["adviceFunc"]=="function"){
_23d.adviceObj=_23d["adviceObj"]||dj_global;
var _240=dojo.lang.nameAnonFunc(_23d.adviceFunc,_23d.adviceObj,true);
_23d.adviceFunc=_240;
}
_23d.srcObj=_23d["srcObj"]||dj_global;
_23d.adviceObj=_23d["adviceObj"]||_23d["targetObj"]||dj_global;
_23d.adviceFunc=_23d["adviceFunc"]||_23d["targetFunc"];
return dojo.event[fn](_23d);
};
this.kwConnect=function(_241){
return this._kwConnectImpl(_241,false);
};
this.disconnect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(!ao.adviceFunc){
return;
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.disconnect(ao);
}
ao.srcFunc="onkeypress";
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
return mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
};
this.kwDisconnect=function(_244){
return this._kwConnectImpl(_244,true);
};
};
dojo.event.MethodInvocation=function(_245,obj,args){
this.jp_=_245;
this.object=obj;
this.args=[];
for(var x=0;x<args.length;x++){
this.args[x]=args[x];
}
this.around_index=-1;
};
dojo.event.MethodInvocation.prototype.proceed=function(){
this.around_index++;
if(this.around_index>=this.jp_.around.length){
return this.jp_.object[this.jp_.methodname].apply(this.jp_.object,this.args);
}else{
var ti=this.jp_.around[this.around_index];
var mobj=ti[0]||dj_global;
var meth=ti[1];
return mobj[meth].call(mobj,this);
}
};
dojo.event.MethodJoinPoint=function(obj,_24d){
this.object=obj||dj_global;
this.methodname=_24d;
this.methodfunc=this.object[_24d];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_24f){
if(!obj){
obj=dj_global;
}
if(!obj[_24f]){
obj[_24f]=function(){
};
if(!obj[_24f]){
dojo.raise("Cannot set do-nothing method on that object "+_24f);
}
}else{
if((!dojo.lang.isFunction(obj[_24f]))&&(!dojo.lang.isAlien(obj[_24f]))){
return null;
}
}
var _250=_24f+"$joinpoint";
var _251=_24f+"$joinpoint$method";
var _252=obj[_250];
if(!_252){
var _253=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_253=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_250,_251,_24f]);
}
}
var _254=obj[_24f].length;
obj[_251]=obj[_24f];
_252=obj[_250]=new dojo.event.MethodJoinPoint(obj,_251);
obj[_24f]=function(){
var args=[];
if((_253)&&(!arguments.length)){
var evt=null;
try{
if(obj.ownerDocument){
evt=obj.ownerDocument.parentWindow.event;
}else{
if(obj.documentElement){
evt=obj.documentElement.ownerDocument.parentWindow.event;
}else{
if(obj.event){
evt=obj.event;
}else{
evt=window.event;
}
}
}
}
catch(e){
evt=window.event;
}
if(evt){
args.push(dojo.event.browser.fixEvent(evt,this));
}
}else{
for(    var x=0;x<arguments.length;x++){
if((x==0)&&(_253)&&(typeof(dojo)!="undefined")&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x],this));
}else{
args.push(arguments[x]);
}
}
}
return _252.run.apply(_252,args);
};
obj[_24f].__preJoinArity=_254;
}
return _252;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _25a=[];
for(var x=0;x<args.length;x++){
_25a[x]=args[x];
}
var _25c=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _25e=marr[0]||dj_global;
var _25f=marr[1];
if(!_25e[_25f]){
dojo.raise("function \""+_25f+"\" does not exist on \""+_25e+"\"");
}
var _260=marr[2]||dj_global;
var _261=marr[3];
var msg=marr[6];
var _263;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _25e[_25f].apply(_25e,to.args);
}};
to.args=_25a;
var _265=parseInt(marr[4]);
var _266=((!isNaN(_265))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _269=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_25c(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_261){
_260[_261].call(_260,to);
}else{
if((_266)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_25e[_25f].call(_25e,to);
}else{
_25e[_25f].apply(_25e,args);
}
},_265);
}else{
if(msg){
_25e[_25f].call(_25e,to);
}else{
_25e[_25f].apply(_25e,args);
}
}
}
};
var _26c=function(){
if(this.squelch){
try{
return _25c.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _25c.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_26c);
}
var _26d;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_26d=mi.proceed();
}else{
if(this.methodfunc){
_26d=this.object[this.methodname].apply(this.object,args);
}
}
}
catch(e){
if(!this.squelch){
dojo.raise(e);
}
}
if((this["after"])&&(this.after.length>0)){
dojo.lang.forEach(this.after.concat(new Array()),_26c);
}
return (this.methodfunc)?_26d:null;
},getArr:function(kind){
var type="after";
if((typeof kind=="string")&&(kind.indexOf("before")!=-1)){
type="before";
}else{
if(kind=="around"){
type="around";
}
}
if(!this[type]){
this[type]=[];
}
return this[type];
},kwAddAdvice:function(args){
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"]);
},addAdvice:function(_272,_273,_274,_275,_276,_277,once,_279,rate,_27b){
var arr=this.getArr(_276);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_272,_273,_274,_275,_279,rate,_27b];
if(once){
if(this.hasAdvice(_272,_273,_276,arr)>=0){
return;
}
}
if(_277=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_27e,_27f,_280,arr){
if(!arr){
arr=this.getArr(_280);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _27f=="object")?(new String(_27f)).toString():_27f;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_27e)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_286,_287,_288,once){
var arr=this.getArr(_288);
var ind=this.hasAdvice(_286,_287,_288,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_286,_287,_288,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_28c){
if(!this.topics[_28c]){
this.topics[_28c]=new this.TopicImpl(_28c);
}
return this.topics[_28c];
};
this.registerPublisher=function(_28d,obj,_28f){
var _28d=this.getTopic(_28d);
_28d.registerPublisher(obj,_28f);
};
this.subscribe=function(_290,obj,_292){
var _290=this.getTopic(_290);
_290.subscribe(obj,_292);
};
this.unsubscribe=function(_293,obj,_295){
var _293=this.getTopic(_293);
_293.unsubscribe(obj,_295);
};
this.destroy=function(_296){
this.getTopic(_296).destroy();
delete this.topics[_296];
};
this.publishApply=function(_297,args){
var _297=this.getTopic(_297);
_297.sendMessage.apply(_297,args);
};
this.publish=function(_299,_29a){
var _299=this.getTopic(_299);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_299.sendMessage.apply(_299,args);
};
};
dojo.event.topic.TopicImpl=function(_29d){
this.topicName=_29d;
this.subscribe=function(_29e,_29f){
var tf=_29f||_29e;
var to=(!_29f)?dj_global:_29e;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_2a2,_2a3){
var tf=(!_2a3)?_2a2:_2a3;
var to=(!_2a3)?null:_2a2;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_2a6){
this._getJoinPoint().squelch=_2a6;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_2a7,_2a8){
dojo.event.connect(_2a7,_2a8,this,"sendMessage");
};
this.sendMessage=function(_2a9){
};
};
dojo.provide("dojo.event.browser");
dojo._ie_clobber=new function(){
this.clobberNodes=[];
function nukeProp(node,prop){
try{
node[prop]=null;
}
catch(e){
}
try{
delete node[prop];
}
catch(e){
}
try{
node.removeAttribute(prop);
}
catch(e){
}
}
this.clobber=function(_2ac){
var na;
var tna;
if(_2ac){
tna=_2ac.all||_2ac.getElementsByTagName("*");
na=[_2ac];
for(var x=0;x<tna.length;x++){
if(tna[x]["__doClobber__"]){
na.push(tna[x]);
}
}
}else{
try{
window.onload=null;
}
catch(e){
}
na=(this.clobberNodes.length)?this.clobberNodes:document.all;
}
tna=null;
var _2b0={};
for(var i=na.length-1;i>=0;i=i-1){
var el=na[i];
try{
if(el&&el["__clobberAttrs__"]){
for(var j=0;j<el.__clobberAttrs__.length;j++){
nukeProp(el,el.__clobberAttrs__[j]);
}
nukeProp(el,"__clobberAttrs__");
nukeProp(el,"__doClobber__");
}
}
catch(e){
}
}
na=null;
};
};
if(dojo.render.html.ie){
dojo.addOnUnload(function(){
dojo._ie_clobber.clobber();
try{
if((dojo["widget"])&&(dojo.widget["manager"])){
dojo.widget.manager.destroyAll();
}
}
catch(e){
}
try{
window.onload=null;
}
catch(e){
}
try{
window.onunload=null;
}
catch(e){
}
dojo._ie_clobber.clobberNodes=[];
});
}
dojo.event.browser=new function(){
var _2b4=0;
this.normalizedEventName=function(_2b5){
switch(_2b5){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _2b5;
break;
default:
return _2b5.toLowerCase();
break;
}
};
this.clean=function(node){
if(dojo.render.html.ie){
dojo._ie_clobber.clobber(node);
}
};
this.addClobberNode=function(node){
if(!dojo.render.html.ie){
return;
}
if(!node["__doClobber__"]){
node.__doClobber__=true;
dojo._ie_clobber.clobberNodes.push(node);
node.__clobberAttrs__=[];
}
};
this.addClobberNodeAttrs=function(node,_2b9){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_2b9.length;x++){
node.__clobberAttrs__.push(_2b9[x]);
}
};
this.removeListener=function(node,_2bc,fp,_2be){
if(!_2be){
var _2be=false;
}
_2bc=dojo.event.browser.normalizedEventName(_2bc);
if((_2bc=="onkey")||(_2bc=="key")){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_2be);
}
_2bc="onkeypress";
}
if(_2bc.substr(0,2)=="on"){
_2bc=_2bc.substr(2);
}
if(node.removeEventListener){
node.removeEventListener(_2bc,fp,_2be);
}
};
this.addListener=function(node,_2c0,fp,_2c2,_2c3){
if(!node){
return;
}
if(!_2c2){
var _2c2=false;
}
_2c0=dojo.event.browser.normalizedEventName(_2c0);
if((_2c0=="onkey")||(_2c0=="key")){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_2c2,_2c3);
}
_2c0="onkeypress";
}
if(_2c0.substr(0,2)!="on"){
_2c0="on"+_2c0;
}
if(!_2c3){
var _2c4=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_2c2){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_2c4=fp;
}
if(node.addEventListener){
node.addEventListener(_2c0.substr(2),_2c4,_2c2);
return _2c4;
}else{
if(typeof node[_2c0]=="function"){
var _2c7=node[_2c0];
node[_2c0]=function(e){
_2c7(e);
return _2c4(e);
};
}else{
node[_2c0]=_2c4;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_2c0]);
}
return _2c4;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_2ca,_2cb){
if(typeof _2ca!="function"){
dojo.raise("listener not a function: "+_2ca);
}
dojo.event.browser.currentEvent.currentTarget=_2cb;
return _2ca.call(_2cb,dojo.event.browser.currentEvent);
};
this._stopPropagation=function(){
dojo.event.browser.currentEvent.cancelBubble=true;
};
this._preventDefault=function(){
dojo.event.browser.currentEvent.returnValue=false;
};
this.keys={KEY_BACKSPACE:8,KEY_TAB:9,KEY_CLEAR:12,KEY_ENTER:13,KEY_SHIFT:16,KEY_CTRL:17,KEY_ALT:18,KEY_PAUSE:19,KEY_CAPS_LOCK:20,KEY_ESCAPE:27,KEY_SPACE:32,KEY_PAGE_UP:33,KEY_PAGE_DOWN:34,KEY_END:35,KEY_HOME:36,KEY_LEFT_ARROW:37,KEY_UP_ARROW:38,KEY_RIGHT_ARROW:39,KEY_DOWN_ARROW:40,KEY_INSERT:45,KEY_DELETE:46,KEY_HELP:47,KEY_LEFT_WINDOW:91,KEY_RIGHT_WINDOW:92,KEY_SELECT:93,KEY_NUMPAD_0:96,KEY_NUMPAD_1:97,KEY_NUMPAD_2:98,KEY_NUMPAD_3:99,KEY_NUMPAD_4:100,KEY_NUMPAD_5:101,KEY_NUMPAD_6:102,KEY_NUMPAD_7:103,KEY_NUMPAD_8:104,KEY_NUMPAD_9:105,KEY_NUMPAD_MULTIPLY:106,KEY_NUMPAD_PLUS:107,KEY_NUMPAD_ENTER:108,KEY_NUMPAD_MINUS:109,KEY_NUMPAD_PERIOD:110,KEY_NUMPAD_DIVIDE:111,KEY_F1:112,KEY_F2:113,KEY_F3:114,KEY_F4:115,KEY_F5:116,KEY_F6:117,KEY_F7:118,KEY_F8:119,KEY_F9:120,KEY_F10:121,KEY_F11:122,KEY_F12:123,KEY_F13:124,KEY_F14:125,KEY_F15:126,KEY_NUM_LOCK:144,KEY_SCROLL_LOCK:145};
this.revKeys=[];
for(var key in this.keys){
this.revKeys[this.keys[key]]=key;
}
this.fixEvent=function(evt,_2ce){
if(!evt){
if(window["event"]){
evt=window.event;
}
}
if((evt["type"])&&(evt["type"].indexOf("key")==0)){
evt.keys=this.revKeys;
for(var key in this.keys){
evt[key]=this.keys[key];
}
if(evt["type"]=="keydown"&&dojo.render.html.ie){
switch(evt.keyCode){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_LEFT_WINDOW:
case evt.KEY_RIGHT_WINDOW:
case evt.KEY_SELECT:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
case evt.KEY_NUMPAD_0:
case evt.KEY_NUMPAD_1:
case evt.KEY_NUMPAD_2:
case evt.KEY_NUMPAD_3:
case evt.KEY_NUMPAD_4:
case evt.KEY_NUMPAD_5:
case evt.KEY_NUMPAD_6:
case evt.KEY_NUMPAD_7:
case evt.KEY_NUMPAD_8:
case evt.KEY_NUMPAD_9:
case evt.KEY_NUMPAD_PERIOD:
break;
case evt.KEY_NUMPAD_MULTIPLY:
case evt.KEY_NUMPAD_PLUS:
case evt.KEY_NUMPAD_ENTER:
case evt.KEY_NUMPAD_MINUS:
case evt.KEY_NUMPAD_DIVIDE:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
case evt.KEY_PAGE_UP:
case evt.KEY_PAGE_DOWN:
case evt.KEY_END:
case evt.KEY_HOME:
case evt.KEY_LEFT_ARROW:
case evt.KEY_UP_ARROW:
case evt.KEY_RIGHT_ARROW:
case evt.KEY_DOWN_ARROW:
case evt.KEY_INSERT:
case evt.KEY_DELETE:
case evt.KEY_F1:
case evt.KEY_F2:
case evt.KEY_F3:
case evt.KEY_F4:
case evt.KEY_F5:
case evt.KEY_F6:
case evt.KEY_F7:
case evt.KEY_F8:
case evt.KEY_F9:
case evt.KEY_F10:
case evt.KEY_F11:
case evt.KEY_F12:
case evt.KEY_F12:
case evt.KEY_F13:
case evt.KEY_F14:
case evt.KEY_F15:
case evt.KEY_CLEAR:
case evt.KEY_HELP:
evt.key=evt.keyCode;
break;
default:
if(evt.ctrlKey||evt.altKey){
var _2d0=evt.keyCode;
if(_2d0>=65&&_2d0<=90&&evt.shiftKey==false){
_2d0+=32;
}
if(_2d0>=1&&_2d0<=26&&evt.ctrlKey){
_2d0+=96;
}
evt.key=String.fromCharCode(_2d0);
}
}
}else{
if(evt["type"]=="keypress"){
if(dojo.render.html.opera){
if(evt.which==0){
evt.key=evt.keyCode;
}else{
if(evt.which>0){
switch(evt.which){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
evt.key=evt.which;
break;
default:
var _2d0=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_2d0+=32;
}
evt.key=String.fromCharCode(_2d0);
}
}
}
}else{
if(dojo.render.html.ie){
if(!evt.ctrlKey&&!evt.altKey&&evt.keyCode>=evt.KEY_SPACE){
evt.key=String.fromCharCode(evt.keyCode);
}
}else{
if(dojo.render.html.safari){
switch(evt.keyCode){
case 63232:
evt.key=evt.KEY_UP_ARROW;
break;
case 63233:
evt.key=evt.KEY_DOWN_ARROW;
break;
case 63234:
evt.key=evt.KEY_LEFT_ARROW;
break;
case 63235:
evt.key=evt.KEY_RIGHT_ARROW;
break;
default:
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}else{
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}
}
}
}
}
if(dojo.render.html.ie){
if(!evt.target){
evt.target=evt.srcElement;
}
if(!evt.currentTarget){
evt.currentTarget=(_2ce?_2ce:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _2d2=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_2d2.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_2d2.scrollTop||0);
}
if(evt.type=="mouseover"){
evt.relatedTarget=evt.fromElement;
}
if(evt.type=="mouseout"){
evt.relatedTarget=evt.toElement;
}
this.currentEvent=evt;
evt.callListener=this.callListener;
evt.stopPropagation=this._stopPropagation;
evt.preventDefault=this._preventDefault;
}
return evt;
};
this.stopEvent=function(evt){
if(window.event){
evt.returnValue=false;
evt.cancelBubble=true;
}else{
evt.preventDefault();
evt.stopPropagation();
}
};
};
dojo.provide("dojo.event.*");
dojo.provide("dojo.widget.Manager");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _2d4={};
var _2d5=[];
this.getUniqueId=function(_2d6){
var _2d7;
do{
_2d7=_2d6+"_"+(_2d4[_2d6]!=undefined?++_2d4[_2d6]:_2d4[_2d6]=0);
}while(this.getWidgetById(_2d7));
return _2d7;
};
this.add=function(_2d8){
this.widgets.push(_2d8);
if(!_2d8.extraArgs["id"]){
_2d8.extraArgs["id"]=_2d8.extraArgs["ID"];
}
if(_2d8.widgetId==""){
if(_2d8["id"]){
_2d8.widgetId=_2d8["id"];
}else{
if(_2d8.extraArgs["id"]){
_2d8.widgetId=_2d8.extraArgs["id"];
}else{
_2d8.widgetId=this.getUniqueId(_2d8.widgetType);
}
}
}
if(this.widgetIds[_2d8.widgetId]){
dojo.debug("widget ID collision on ID: "+_2d8.widgetId);
}
this.widgetIds[_2d8.widgetId]=_2d8;
};
this.destroyAll=function(){
for(var x=this.widgets.length-1;x>=0;x--){
try{
this.widgets[x].destroy(true);
delete this.widgets[x];
}
catch(e){
}
}
};
this.remove=function(_2da){
if(dojo.lang.isNumber(_2da)){
var tw=this.widgets[_2da].widgetId;
delete this.widgetIds[tw];
this.widgets.splice(_2da,1);
}else{
this.removeById(_2da);
}
};
this.removeById=function(id){
if(!dojo.lang.isString(id)){
id=id["widgetId"];
if(!id){
dojo.debug("invalid widget or id passed to removeById");
return;
}
}
for(var i=0;i<this.widgets.length;i++){
if(this.widgets[i].widgetId==id){
this.remove(i);
break;
}
}
};
this.getWidgetById=function(id){
if(dojo.lang.isString(id)){
return this.widgetIds[id];
}
return id;
};
this.getWidgetsByType=function(type){
var lt=type.toLowerCase();
var _2e1=(type.indexOf(":")<0?function(x){
return x.widgetType.toLowerCase();
}:function(x){
return x.getNamespacedType();
});
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_2e1(x)==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsByFilter=function(_2e6,_2e7){
var ret=[];
dojo.lang.every(this.widgets,function(x){
if(_2e6(x)){
ret.push(x);
if(_2e7){
return false;
}
}
return true;
});
return (_2e7?ret[0]:ret);
};
this.getAllWidgets=function(){
return this.widgets.concat();
};
this.getWidgetByNode=function(node){
var w=this.getAllWidgets();
node=dojo.byId(node);
for(var i=0;i<w.length;i++){
if(w[i].domNode==node){
return w[i];
}
}
return null;
};
this.byId=this.getWidgetById;
this.byType=this.getWidgetsByType;
this.byFilter=this.getWidgetsByFilter;
this.byNode=this.getWidgetByNode;
var _2ed={};
var _2ee=["dojo.widget"];
for(var i=0;i<_2ee.length;i++){
_2ee[_2ee[i]]=true;
}
this.registerWidgetPackage=function(_2f0){
if(!_2ee[_2f0]){
_2ee[_2f0]=true;
_2ee.push(_2f0);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_2ee,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_2f2,_2f3,_2f4,ns){
var impl=this.getImplementationName(_2f2,ns);
if(impl){
var ret=_2f3?new impl(_2f3):new impl();
return ret;
}
};
function buildPrefixCache(){
for(var _2f8 in dojo.render){
if(dojo.render[_2f8]["capable"]===true){
var _2f9=dojo.render[_2f8].prefixes;
for(var i=0;i<_2f9.length;i++){
_2d5.push(_2f9[i].toLowerCase());
}
}
}
}
var _2fb=function(_2fc,_2fd){
if(!_2fd){
return null;
}
for(var i=0,l=_2d5.length,_300;i<=l;i++){
_300=(i<l?_2fd[_2d5[i]]:_2fd);
if(!_300){
continue;
}
for(var name in _300){
if(name.toLowerCase()==_2fc){
return _300[name];
}
}
}
return null;
};
var _302=function(_303,_304){
var _305=dojo.evalObjPath(_304,false);
return (_305?_2fb(_303,_305):null);
};
this.getImplementationName=function(_306,ns){
var _308=_306.toLowerCase();
ns=ns||"dojo";
var imps=_2ed[ns]||(_2ed[ns]={});
var impl=imps[_308];
if(impl){
return impl;
}
if(!_2d5.length){
buildPrefixCache();
}
var _30b=dojo.ns.get(ns);
if(!_30b){
dojo.ns.register(ns,ns+".widget");
_30b=dojo.ns.get(ns);
}
if(_30b){
_30b.resolve(_306);
}
impl=_302(_308,_30b.module);
if(impl){
return (imps[_308]=impl);
}
_30b=dojo.ns.require(ns);
if((_30b)&&(_30b.resolver)){
_30b.resolve(_306);
impl=_302(_308,_30b.module);
if(impl){
return (imps[_308]=impl);
}
}
dojo.deprecated("dojo.widget.Manager.getImplementationName","Could not locate widget implementation for \""+_306+"\" in \""+_30b.module+"\" registered to namespace \""+_30b.name+"\". "+"Developers must specify correct namespaces for all non-Dojo widgets","0.5");
for(var i=0;i<_2ee.length;i++){
impl=_302(_308,_2ee[i]);
if(impl){
return (imps[_308]=impl);
}
}
throw new Error("Could not locate widget implementation for \""+_306+"\" in \""+_30b.module+"\" registered to namespace \""+_30b.name+"\"");
};
this.resizing=false;
this.onWindowResized=function(){
if(this.resizing){
return;
}
try{
this.resizing=true;
for(var id in this.topWidgets){
var _30e=this.topWidgets[id];
if(_30e.checkSize){
_30e.checkSize();
}
}
}
catch(e){
}
finally{
this.resizing=false;
}
};
if(typeof window!="undefined"){
dojo.addOnLoad(this,"onWindowResized");
dojo.event.connect(window,"onresize",this,"onWindowResized");
}
};
(function(){
var dw=dojo.widget;
var dwm=dw.manager;
var h=dojo.lang.curry(dojo.lang,"hitch",dwm);
var g=function(_313,_314){
dw[(_314||_313)]=h(_313);
};
g("add","addWidget");
g("destroyAll","destroyAllWidgets");
g("remove","removeWidget");
g("removeById","removeWidgetById");
g("getWidgetById");
g("getWidgetById","byId");
g("getWidgetsByType");
g("getWidgetsByFilter");
g("getWidgetsByType","byType");
g("getWidgetsByFilter","byFilter");
g("getWidgetByNode","byNode");
dw.all=function(n){
var _316=dwm.getAllWidgets.apply(dwm,arguments);
if(arguments.length>0){
return _316[n];
}
return _316;
};
g("registerWidgetPackage");
g("getImplementation","getWidgetImplementation");
g("getImplementationName","getWidgetImplementationName");
dw.widgets=dwm.widgets;
dw.widgetIds=dwm.widgetIds;
dw.root=dwm.root;
})();
dojo.provide("dojo.uri.Uri");
dojo.uri=new function(){
this.dojoUri=function(uri){
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(),uri);
};
this.moduleUri=function(_318,uri){
var loc=dojo.hostenv.getModulePrefix(_318);
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri()+loc,uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _31d=new dojo.uri.Uri(arguments[i].toString());
var _31e=new dojo.uri.Uri(uri.toString());
if((_31d.path=="")&&(_31d.scheme==null)&&(_31d.authority==null)&&(_31d.query==null)){
if(_31d.fragment!=null){
_31e.fragment=_31d.fragment;
}
_31d=_31e;
}else{
if(_31d.scheme==null){
_31d.scheme=_31e.scheme;
if(_31d.authority==null){
_31d.authority=_31e.authority;
if(_31d.path.charAt(0)!="/"){
var path=_31e.path.substring(0,_31e.path.lastIndexOf("/")+1)+_31d.path;
var segs=path.split("/");
for(var j=0;j<segs.length;j++){
if(segs[j]=="."){
if(j==segs.length-1){
segs[j]="";
}else{
segs.splice(j,1);
j--;
}
}else{
if(j>0&&!(j==1&&segs[0]=="")&&segs[j]==".."&&segs[j-1]!=".."){
if(j==segs.length-1){
segs.splice(j,1);
segs[j-1]="";
}else{
segs.splice(j-1,2);
j-=2;
}
}
}
}
_31d.path=segs.join("/");
}
}
}
}
uri="";
if(_31d.scheme!=null){
uri+=_31d.scheme+":";
}
if(_31d.authority!=null){
uri+="//"+_31d.authority;
}
uri+=_31d.path;
if(_31d.query!=null){
uri+="?"+_31d.query;
}
if(_31d.fragment!=null){
uri+="#"+_31d.fragment;
}
}
this.uri=uri.toString();
var _322="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_322));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_322="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_322));
this.user=r[3]||null;
this.password=r[4]||null;
this.host=r[5];
this.port=r[7]||null;
}
this.toString=function(){
return this.uri;
};
};
};
dojo.provide("dojo.uri.*");
dojo.provide("dojo.html.common");
dojo.lang.mixin(dojo.html,dojo.dom);
dojo.html.body=function(){
dojo.deprecated("dojo.html.body() moved to dojo.body()","0.5");
return dojo.body();
};
dojo.html.getEventTarget=function(evt){
if(!evt){
evt=dojo.global().event||{};
}
var t=(evt.srcElement?evt.srcElement:(evt.target?evt.target:null));
while((t)&&(t.nodeType!=1)){
t=t.parentNode;
}
return t;
};
dojo.html.getViewport=function(){
var _326=dojo.global();
var _327=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_327.documentElement.clientWidth;
h=_326.innerHeight;
}else{
if(!dojo.render.html.opera&&_326.innerWidth){
w=_326.innerWidth;
h=_326.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_327,"documentElement.clientWidth")){
var w2=_327.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_327.documentElement.clientHeight;
}else{
if(dojo.body().clientWidth){
w=dojo.body().clientWidth;
h=dojo.body().clientHeight;
}
}
}
}
return {width:w,height:h};
};
dojo.html.getScroll=function(){
var _32b=dojo.global();
var _32c=dojo.doc();
var top=_32b.pageYOffset||_32c.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_32b.pageXOffset||_32c.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _331=dojo.doc();
var _332=dojo.byId(node);
type=type.toLowerCase();
while((_332)&&(_332.nodeName.toLowerCase()!=type)){
if(_332==(_331["body"]||_331["documentElement"])){
return null;
}
_332=_332.parentNode;
}
return _332;
};
dojo.html.getAttribute=function(node,attr){
node=dojo.byId(node);
if((!node)||(!node.getAttribute)){
return null;
}
var ta=typeof attr=="string"?attr:new String(attr);
var v=node.getAttribute(ta.toUpperCase());
if((v)&&(typeof v=="string")&&(v!="")){
return v;
}
if(v&&v.value){
return v.value;
}
if((node.getAttributeNode)&&(node.getAttributeNode(ta))){
return (node.getAttributeNode(ta)).value;
}else{
if(node.getAttribute(ta)){
return node.getAttribute(ta);
}else{
if(node.getAttribute(ta.toLowerCase())){
return node.getAttribute(ta.toLowerCase());
}
}
}
return null;
};
dojo.html.hasAttribute=function(node,attr){
return dojo.html.getAttribute(dojo.byId(node),attr)?true:false;
};
dojo.html.getCursorPosition=function(e){
e=e||dojo.global().event;
var _33a={x:0,y:0};
if(e.pageX||e.pageY){
_33a.x=e.pageX;
_33a.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_33a.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_33a.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _33a;
};
dojo.html.isTag=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName.toLowerCase()==String(arguments[i]).toLowerCase()){
return String(arguments[i]).toLowerCase();
}
}
}
return "";
};
if(dojo.render.html.ie&&!dojo.render.html.ie70){
if(window.location.href.substr(0,6).toLowerCase()!="https:"){
(function(){
var _33f=dojo.doc().createElement("script");
_33f.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_33f);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_342,_343,args,_345,_346){
dojo.deprecated("dojo.html."+_342,"replaced by dojo.html."+_343+"("+(_345?"node, {"+_345+": "+_345+"}":"")+")"+(_346?"."+_346:""),"0.5");
var _347=[];
if(_345){
var _348={};
_348[_345]=args[1];
_347.push(args[0]);
_347.push(_348);
}else{
_347=args;
}
var ret=dojo.html[_343].apply(dojo.html,args);
if(_346){
return ret[_346];
}else{
return ret;
}
};
dojo.html.getViewportWidth=function(){
return dojo.html._callDeprecated("getViewportWidth","getViewport",arguments,null,"width");
};
dojo.html.getViewportHeight=function(){
return dojo.html._callDeprecated("getViewportHeight","getViewport",arguments,null,"height");
};
dojo.html.getViewportSize=function(){
return dojo.html._callDeprecated("getViewportSize","getViewport",arguments);
};
dojo.html.getScrollTop=function(){
return dojo.html._callDeprecated("getScrollTop","getScroll",arguments,null,"top");
};
dojo.html.getScrollLeft=function(){
return dojo.html._callDeprecated("getScrollLeft","getScroll",arguments,null,"left");
};
dojo.html.getScrollOffset=function(){
return dojo.html._callDeprecated("getScrollOffset","getScroll",arguments,null,"offset");
};
dojo.provide("dojo.a11y");
dojo.a11y={imgPath:dojo.uri.dojoUri("src/widget/templates/images"),doAccessibleCheck:true,accessible:null,checkAccessible:function(){
if(this.accessible===null){
this.accessible=false;
if(this.doAccessibleCheck==true){
this.accessible=this.testAccessible();
}
}
return this.accessible;
},testAccessible:function(){
this.accessible=false;
if(dojo.render.html.ie||dojo.render.html.mozilla){
var div=document.createElement("div");
div.style.backgroundImage="url(\""+this.imgPath+"/tab_close.gif\")";
dojo.body().appendChild(div);
var _34b=null;
if(window.getComputedStyle){
var _34c=getComputedStyle(div,"");
_34b=_34c.getPropertyValue("background-image");
}else{
_34b=div.currentStyle.backgroundImage;
}
var _34d=false;
if(_34b!=null&&(_34b=="none"||_34b=="url(invalid-url:)")){
this.accessible=true;
}
dojo.body().removeChild(div);
}
return this.accessible;
},setCheckAccessible:function(_34e){
this.doAccessibleCheck=_34e;
},setAccessibleMode:function(){
if(this.accessible===null){
if(this.checkAccessible()){
dojo.render.html.prefixes.unshift("a11y");
}
}
return this.accessible;
}};
dojo.provide("dojo.widget.Widget");
dojo.declare("dojo.widget.Widget",null,function(){
this.children=[];
this.extraArgs={};
},{parent:null,children:[],extraArgs:{},isTopLevel:false,isModal:false,isEnabled:true,isHidden:false,isContainer:false,widgetId:"",widgetType:"Widget",ns:"dojo",getNamespacedType:function(){
return (this.ns?this.ns+":"+this.widgetType:this.widgetType).toLowerCase();
},toString:function(){
return "[Widget "+this.getNamespacedType()+", "+(this.widgetId||"NO ID")+"]";
},repr:function(){
return this.toString();
},enable:function(){
this.isEnabled=true;
},disable:function(){
this.isEnabled=false;
},hide:function(){
this.isHidden=true;
},show:function(){
this.isHidden=false;
},onResized:function(){
this.notifyChildrenOfResize();
},notifyChildrenOfResize:function(){
for(var i=0;i<this.children.length;i++){
var _350=this.children[i];
if(_350.onResized){
_350.onResized();
}
}
},create:function(args,_352,_353,ns){
if(ns){
this.ns=ns;
}
this.satisfyPropertySets(args,_352,_353);
this.mixInProperties(args,_352,_353);
this.postMixInProperties(args,_352,_353);
dojo.widget.manager.add(this);
this.buildRendering(args,_352,_353);
this.initialize(args,_352,_353);
this.postInitialize(args,_352,_353);
this.postCreate(args,_352,_353);
return this;
},destroy:function(_355){
this.destroyChildren();
this.uninitialize();
this.destroyRendering(_355);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(){
var _356;
var i=0;
while(this.children.length>i){
_356=this.children[i];
if(_356 instanceof dojo.widget.Widget){
this.removeChild(_356);
_356.destroy();
continue;
}
i++;
}
},getChildrenOfType:function(type,_359){
var ret=[];
var _35b=dojo.lang.isFunction(type);
if(!_35b){
type=type.toLowerCase();
}
for(var x=0;x<this.children.length;x++){
if(_35b){
if(this.children[x] instanceof type){
ret.push(this.children[x]);
}
}else{
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
}
if(_359){
ret=ret.concat(this.children[x].getChildrenOfType(type,_359));
}
}
return ret;
},getDescendants:function(){
var _35d=[];
var _35e=[this];
var elem;
while((elem=_35e.pop())){
_35d.push(elem);
if(elem.children){
dojo.lang.forEach(elem.children,function(elem){
_35e.push(elem);
});
}
}
return _35d;
},isFirstChild:function(){
return this===this.parent.children[0];
},isLastChild:function(){
return this===this.parent.children[this.parent.children.length-1];
},satisfyPropertySets:function(args){
return args;
},mixInProperties:function(args,frag){
if((args["fastMixIn"])||(frag["fastMixIn"])){
for(var x in args){
this[x]=args[x];
}
return;
}
var _365;
var _366=dojo.widget.lcArgsCache[this.widgetType];
if(_366==null){
_366={};
for(var y in this){
_366[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_366;
}
var _368={};
for(var x in args){
if(!this[x]){
var y=_366[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_368[x]){
continue;
}
_368[x]=true;
if((typeof this[x])!=(typeof _365)){
if(typeof args[x]!="string"){
this[x]=args[x];
}else{
if(dojo.lang.isString(this[x])){
this[x]=args[x];
}else{
if(dojo.lang.isNumber(this[x])){
this[x]=new Number(args[x]);
}else{
if(dojo.lang.isBoolean(this[x])){
this[x]=(args[x].toLowerCase()=="false")?false:true;
}else{
if(dojo.lang.isFunction(this[x])){
if(args[x].search(/[^\w\.]+/i)==-1){
this[x]=dojo.evalObjPath(args[x],false);
}else{
var tn=dojo.lang.nameAnonFunc(new Function(args[x]),this);
dojo.event.kwConnect({srcObj:this,srcFunc:x,adviceObj:this,adviceFunc:tn});
}
}else{
if(dojo.lang.isArray(this[x])){
this[x]=args[x].split(";");
}else{
if(this[x] instanceof Date){
this[x]=new Date(Number(args[x]));
}else{
if(typeof this[x]=="object"){
if(this[x] instanceof dojo.uri.Uri){
this[x]=args[x];
}else{
var _36a=args[x].split(";");
for(var y=0;y<_36a.length;y++){
var si=_36a[y].indexOf(":");
if((si!=-1)&&(_36a[y].length>si)){
this[x][_36a[y].substr(0,si).replace(/^\s+|\s+$/g,"")]=_36a[y].substr(si+1);
}
}
}
}else{
this[x]=args[x];
}
}
}
}
}
}
}
}
}else{
this.extraArgs[x.toLowerCase()]=args[x];
}
}
},postMixInProperties:function(args,frag,_36e){
},initialize:function(args,frag,_371){
return false;
},postInitialize:function(args,frag,_374){
return false;
},postCreate:function(args,frag,_377){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(args,frag,_37a){
dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dojo.unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},cleanUp:function(){
dojo.unimplemented("dojo.widget.Widget.cleanUp");
return false;
},addedTo:function(_37b){
},addChild:function(_37c){
dojo.unimplemented("dojo.widget.Widget.addChild");
return false;
},removeChild:function(_37d){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_37d){
this.children.splice(x,1);
break;
}
}
return _37d;
},resize:function(_37f,_380){
this.setWidth(_37f);
this.setHeight(_380);
},setWidth:function(_381){
if((typeof _381=="string")&&(_381.substr(-1)=="%")){
this.setPercentageWidth(_381);
}else{
this.setNativeWidth(_381);
}
},setHeight:function(_382){
if((typeof _382=="string")&&(_382.substr(-1)=="%")){
this.setPercentageHeight(_382);
}else{
this.setNativeHeight(_382);
}
},setPercentageHeight:function(_383){
return false;
},setNativeHeight:function(_384){
return false;
},setPercentageWidth:function(_385){
return false;
},setNativeWidth:function(_386){
return false;
},getPreviousSibling:function(){
var idx=this.getParentIndex();
if(idx<=0){
return null;
}
return this.parent.children[idx-1];
},getSiblings:function(){
return this.parent.children;
},getParentIndex:function(){
return dojo.lang.indexOf(this.parent.children,this,true);
},getNextSibling:function(){
var idx=this.getParentIndex();
if(idx==this.parent.children.length-1){
return null;
}
if(idx<0){
return null;
}
return this.parent.children[idx+1];
}});
dojo.widget.lcArgsCache={};
dojo.widget.tags={};
dojo.widget.tags.addParseTreeHandler=function(type){
dojo.deprecated("addParseTreeHandler",". ParseTreeHandlers are now reserved for components. Any unfiltered DojoML tag without a ParseTreeHandler is assumed to be a widget","0.5");
};
dojo.widget.tags["dojo:propertyset"]=function(_38a,_38b,_38c){
var _38d=_38b.parseProperties(_38a["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_38e,_38f,_390){
var _391=_38f.parseProperties(_38e["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_394,_395,_396,_397){
dojo.a11y.setAccessibleMode();
var _398=type.split(":");
_398=(_398.length==2)?_398[1]:type;
var _399=_397||_394.parseProperties(frag[frag["ns"]+":"+_398]);
var _39a=dojo.widget.manager.getImplementation(_398,null,null,frag["ns"]);
if(!_39a){
throw new Error("cannot find \""+type+"\" widget");
}else{
if(!_39a.create){
throw new Error("\""+type+"\" widget object has no \"create\" method and does not appear to implement *Widget");
}
}
_399["dojoinsertionindex"]=_396;
var ret=_39a.create(_399,frag,_395,frag["ns"]);
return ret;
};
dojo.widget.defineWidget=function(_39c,_39d,_39e,init,_3a0){
if(dojo.lang.isString(arguments[3])){
dojo.widget._defineWidget(arguments[0],arguments[3],arguments[1],arguments[4],arguments[2]);
}else{
var args=[arguments[0]],p=3;
if(dojo.lang.isString(arguments[1])){
args.push(arguments[1],arguments[2]);
}else{
args.push("",arguments[1]);
p=2;
}
if(dojo.lang.isFunction(arguments[p])){
args.push(arguments[p],arguments[p+1]);
}else{
args.push(null,arguments[p]);
}
dojo.widget._defineWidget.apply(this,args);
}
};
dojo.widget.defineWidget.renderers="html|svg|vml";
dojo.widget._defineWidget=function(_3a3,_3a4,_3a5,init,_3a7){
var _3a8=_3a3.split(".");
var type=_3a8.pop();
var regx="\\.("+(_3a4?_3a4+"|":"")+dojo.widget.defineWidget.renderers+")\\.";
var r=_3a3.search(new RegExp(regx));
_3a8=(r<0?_3a8.join("."):_3a3.substr(0,r));
dojo.widget.manager.registerWidgetPackage(_3a8);
var pos=_3a8.indexOf(".");
var _3ad=(pos>-1)?_3a8.substring(0,pos):_3a8;
_3a7=(_3a7)||{};
_3a7.widgetType=type;
if((!init)&&(_3a7["classConstructor"])){
init=_3a7.classConstructor;
delete _3a7.classConstructor;
}
dojo.declare(_3a3,_3a5,init,_3a7);
};
dojo.provide("dojo.widget.Parse");
dojo.widget.Parse=function(_3ae){
this.propertySetsList=[];
this.fragment=_3ae;
this.createComponents=function(frag,_3b0){
var _3b1=[];
var _3b2=false;
try{
if((frag)&&(frag["tagName"])&&(frag!=frag["nodeRef"])){
var _3b3=dojo.widget.tags;
var tna=String(frag["tagName"]).split(";");
for(var x=0;x<tna.length;x++){
var ltn=(tna[x].replace(/^\s+|\s+$/g,"")).toLowerCase();
frag.tagName=ltn;
if(_3b3[ltn]){
_3b2=true;
var ret=_3b3[ltn](frag,this,_3b0,frag["index"]);
_3b1.push(ret);
}else{
if(ltn.indexOf(":")==-1){
ltn="dojo:"+ltn;
}
var ret=dojo.widget.buildWidgetFromParseTree(ltn,frag,this,_3b0,frag["index"]);
if(ret){
_3b2=true;
_3b1.push(ret);
}
}
}
}
}
catch(e){
dojo.debug("dojo.widget.Parse: error:"+e);
}
if(!_3b2){
_3b1=_3b1.concat(this.createSubComponents(frag,_3b0));
}
return _3b1;
};
this.createSubComponents=function(_3b8,_3b9){
var frag,_3bb=[];
for(var item in _3b8){
frag=_3b8[item];
if((frag)&&(typeof frag=="object")&&(frag!=_3b8.nodeRef)&&(frag!=_3b8["tagName"])){
_3bb=_3bb.concat(this.createComponents(frag,_3b9));
}
}
return _3bb;
};
this.parsePropertySets=function(_3bd){
return [];
};
this.parseProperties=function(_3be){
var _3bf={};
for(var item in _3be){
if((_3be[item]==_3be["tagName"])||(_3be[item]==_3be.nodeRef)){
}else{
if((_3be[item]["tagName"])&&(dojo.widget.tags[_3be[item].tagName.toLowerCase()])){
}else{
if((_3be[item][0])&&(_3be[item][0].value!="")&&(_3be[item][0].value!=null)){
try{
if(item.toLowerCase()=="dataprovider"){
var _3c1=this;
this.getDataProvider(_3c1,_3be[item][0].value);
_3bf.dataProvider=this.dataProvider;
}
_3bf[item]=_3be[item][0].value;
var _3c2=this.parseProperties(_3be[item]);
for(var _3c3 in _3c2){
_3bf[_3c3]=_3c2[_3c3];
}
}
catch(e){
dojo.debug(e);
}
}
}
switch(item.toLowerCase()){
case "checked":
case "disabled":
if(typeof _3bf[item]!="boolean"){
_3bf[item]=true;
}
break;
}
}
}
return _3bf;
};
this.getDataProvider=function(_3c4,_3c5){
dojo.io.bind({url:_3c5,load:function(type,_3c7){
if(type=="load"){
_3c4.dataProvider=_3c7;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_3c8){
for(var x=0;x<this.propertySetsList.length;x++){
if(_3c8==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_3ca){
var _3cb=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl["componentClass"]||cpl["componentType"]||null;
var _3cf=this.propertySetsList[x]["id"][0].value;
if((cpcc)&&(_3cf==cpcc[0].value)){
_3cb.push(cpl);
}
}
return _3cb;
};
this.getPropertySets=function(_3d0){
var ppl="dojo:propertyproviderlist";
var _3d2=[];
var _3d3=_3d0["tagName"];
if(_3d0[ppl]){
var _3d4=_3d0[ppl].value.split(" ");
for(var _3d5 in _3d4){
if((_3d5.indexOf("..")==-1)&&(_3d5.indexOf("://")==-1)){
var _3d6=this.getPropertySetById(_3d5);
if(_3d6!=""){
_3d2.push(_3d6);
}
}else{
}
}
}
return (this.getPropertySetsByType(_3d3)).concat(_3d2);
};
this.createComponentFromScript=function(_3d7,_3d8,_3d9,ns){
_3d9.fastMixIn=true;
var ltn=(ns||"dojo")+":"+_3d8.toLowerCase();
if(dojo.widget.tags[ltn]){
return [dojo.widget.tags[ltn](_3d9,this,null,null,_3d9)];
}
return [dojo.widget.buildWidgetFromParseTree(ltn,_3d9,this,null,null,_3d9)];
};
};
dojo.widget._parser_collection={"dojo":new dojo.widget.Parse()};
dojo.widget.getParser=function(name){
if(!name){
name="dojo";
}
if(!this._parser_collection[name]){
this._parser_collection[name]=new dojo.widget.Parse();
}
return this._parser_collection[name];
};
dojo.widget.createWidget=function(name,_3de,_3df,_3e0){
var _3e1=false;
var _3e2=(typeof name=="string");
if(_3e2){
var pos=name.indexOf(":");
var ns=(pos>-1)?name.substring(0,pos):"dojo";
if(pos>-1){
name=name.substring(pos+1);
}
var _3e5=name.toLowerCase();
var _3e6=ns+":"+_3e5;
_3e1=(dojo.byId(name)&&(!dojo.widget.tags[_3e6]));
}
if((arguments.length==1)&&((_3e1)||(!_3e2))){
var xp=new dojo.xml.Parse();
var tn=(_3e1)?dojo.byId(name):name;
return dojo.widget.getParser().createComponents(xp.parseElement(tn,null,true))[0];
}
function fromScript(_3e9,name,_3eb,ns){
_3eb[_3e6]={dojotype:[{value:_3e5}],nodeRef:_3e9,fastMixIn:true};
_3eb.ns=ns;
return dojo.widget.getParser().createComponentFromScript(_3e9,name,_3eb,ns);
}
_3de=_3de||{};
var _3ed=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_3df){
_3ed=true;
_3df=tn;
if(h){
dojo.body().appendChild(_3df);
}
}else{
if(_3e0){
dojo.dom.insertAtPosition(tn,_3df,_3e0);
}else{
tn=_3df;
}
}
var _3ef=fromScript(tn,name.toLowerCase(),_3de,ns);
if((!_3ef)||(!_3ef[0])||(typeof _3ef[0].widgetType=="undefined")){
throw new Error("createWidget: Creation of \""+name+"\" widget failed.");
}
try{
if(_3ed){
if(_3ef[0].domNode.parentNode){
_3ef[0].domNode.parentNode.removeChild(_3ef[0].domNode);
}
}
}
catch(e){
dojo.debug(e);
}
return _3ef[0];
};
dojo.provide("dojo.string.common");
dojo.string.trim=function(str,wh){
if(!str.replace){
return str;
}
if(!str.length){
return str;
}
var re=(wh>0)?(/^\s+/):(wh<0)?(/\s+$/):(/^\s+|\s+$/g);
return str.replace(re,"");
};
dojo.string.trimStart=function(str){
return dojo.string.trim(str,1);
};
dojo.string.trimEnd=function(str){
return dojo.string.trim(str,-1);
};
dojo.string.repeat=function(str,_3f6,_3f7){
var out="";
for(var i=0;i<_3f6;i++){
out+=str;
if(_3f7&&i<_3f6-1){
out+=_3f7;
}
}
return out;
};
dojo.string.pad=function(str,len,c,dir){
var out=String(str);
if(!c){
c="0";
}
if(!dir){
dir=1;
}
while(out.length<len){
if(dir>0){
out=c+out;
}else{
out+=c;
}
}
return out;
};
dojo.string.padLeft=function(str,len,c){
return dojo.string.pad(str,len,c,1);
};
dojo.string.padRight=function(str,len,c){
return dojo.string.pad(str,len,c,-1);
};
dojo.provide("dojo.string");
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_406,_407,_408){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_406){
this.mimetype=_406;
}
if(_407){
this.transport=_407;
}
if(arguments.length>=4){
this.changeUrl=_408;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,_40b,_40c){
},error:function(type,_40e,_40f,_410){
},timeout:function(type,_412,_413,_414){
},handle:function(type,data,_417,_418){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_419){
if(_419["url"]){
_419.url=_419.url.toString();
}
if(_419["formNode"]){
_419.formNode=dojo.byId(_419.formNode);
}
if(!_419["method"]&&_419["formNode"]&&_419["formNode"].method){
_419.method=_419["formNode"].method;
}
if(!_419["handle"]&&_419["handler"]){
_419.handle=_419.handler;
}
if(!_419["load"]&&_419["loaded"]){
_419.load=_419.loaded;
}
if(!_419["changeUrl"]&&_419["changeURL"]){
_419.changeUrl=_419.changeURL;
}
_419.encoding=dojo.lang.firstValued(_419["encoding"],djConfig["bindEncoding"],"");
_419.sendTransport=dojo.lang.firstValued(_419["sendTransport"],djConfig["ioSendTransport"],false);
var _41a=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_419[fn]&&_41a(_419[fn])){
continue;
}
if(_419["handle"]&&_41a(_419["handle"])){
_419[fn]=_419.handle;
}
}
dojo.lang.mixin(this,_419);
}});
dojo.io.Error=function(msg,type,num){
this.message=msg;
this.type=type||"unknown";
this.number=num||0;
};
dojo.io.transports.addTransport=function(name){
this.push(name);
this[name]=dojo.io[name];
};
dojo.io.bind=function(_421){
if(!(_421 instanceof dojo.io.Request)){
try{
_421=new dojo.io.Request(_421);
}
catch(e){
dojo.debug(e);
}
}
var _422="";
if(_421["transport"]){
_422=_421["transport"];
if(!this[_422]){
dojo.io.sendBindError(_421,"No dojo.io.bind() transport with name '"+_421["transport"]+"'.");
return _421;
}
if(!this[_422].canHandle(_421)){
dojo.io.sendBindError(_421,"dojo.io.bind() transport with name '"+_421["transport"]+"' cannot handle this type of request.");
return _421;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_421))){
_422=tmp;
break;
}
}
if(_422==""){
dojo.io.sendBindError(_421,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _421;
}
}
this[_422].bind(_421);
_421.bindSuccess=true;
return _421;
};
dojo.io.sendBindError=function(_425,_426){
if((typeof _425.error=="function"||typeof _425.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _427=new dojo.io.Error(_426);
setTimeout(function(){
_425[(typeof _425.error=="function")?"error":"handle"]("error",_427,null,_425);
},50);
}else{
dojo.raise(_426);
}
};
dojo.io.queueBind=function(_428){
if(!(_428 instanceof dojo.io.Request)){
try{
_428=new dojo.io.Request(_428);
}
catch(e){
dojo.debug(e);
}
}
var _429=_428.load;
_428.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_429.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _42b=_428.error;
_428.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_42b.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_428);
dojo.io._dispatchNextQueueBind();
return _428;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
if(dojo.io._bindQueue.length>0){
dojo.io.bind(dojo.io._bindQueue.shift());
}else{
dojo.io._queueBindInFlight=false;
}
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_42e,last){
var enc=/utf/i.test(_42e||"")?encodeURIComponent:dojo.string.encodeAscii;
var _431=[];
var _432=new Object();
for(var name in map){
var _434=function(elt){
var val=enc(name)+"="+enc(elt);
_431[(last==name)?"push":"unshift"](val);
};
if(!_432[name]){
var _437=map[name];
if(dojo.lang.isArray(_437)){
dojo.lang.forEach(_437,_434);
}else{
_434(_437);
}
}
}
return _431.join("&");
};
dojo.io.setIFrameSrc=function(_438,src,_43a){
try{
var r=dojo.render.html;
if(!_43a){
if(r.safari){
_438.location=src;
}else{
frames[_438.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_438.contentWindow.document;
}else{
if(r.safari){
idoc=_438.document;
}else{
idoc=_438.contentWindow;
}
}
if(!idoc){
_438.location=src;
return;
}else{
idoc.location.replace(src);
}
}
}
catch(e){
dojo.debug(e);
dojo.debug("setIFrameSrc: "+e);
}
};
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_43d,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _43d.replace(/\%\{(\w+)\}/g,function(_440,key){
if(typeof (map[key])!="undefined"&&map[key]!=null){
return map[key];
}
dojo.raise("Substitution not found: "+key);
});
};
dojo.string.capitalize=function(str){
if(!dojo.lang.isString(str)){
return "";
}
if(arguments.length==0){
str=this;
}
var _443=str.split(" ");
for(var i=0;i<_443.length;i++){
_443[i]=_443[i].charAt(0).toUpperCase()+_443[i].substring(1);
}
return _443.join(" ");
};
dojo.string.isBlank=function(str){
if(!dojo.lang.isString(str)){
return true;
}
return (dojo.string.trim(str).length==0);
};
dojo.string.encodeAscii=function(str){
if(!dojo.lang.isString(str)){
return str;
}
var ret="";
var _448=escape(str);
var _449,re=/%u([0-9A-F]{4})/i;
while((_449=_448.match(re))){
var num=Number("0x"+_449[1]);
var _44c=escape("&#"+num+";");
ret+=_448.substring(0,_449.index)+_44c;
_448=_448.substring(_449.index+_449[0].length);
}
ret+=_448.replace(/\+/g,"%2B");
return ret;
};
dojo.string.escape=function(type,str){
var args=dojo.lang.toArray(arguments,1);
switch(type.toLowerCase()){
case "xml":
case "html":
case "xhtml":
return dojo.string.escapeXml.apply(this,args);
case "sql":
return dojo.string.escapeSql.apply(this,args);
case "regexp":
case "regex":
return dojo.string.escapeRegExp.apply(this,args);
case "javascript":
case "jscript":
case "js":
return dojo.string.escapeJavaScript.apply(this,args);
case "ascii":
return dojo.string.encodeAscii.apply(this,args);
default:
return str;
}
};
dojo.string.escapeXml=function(str,_451){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_451){
str=str.replace(/'/gm,"&#39;");
}
return str;
};
dojo.string.escapeSql=function(str){
return str.replace(/'/gm,"''");
};
dojo.string.escapeRegExp=function(str){
return str.replace(/\\/gm,"\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm,"\\$1");
};
dojo.string.escapeJavaScript=function(str){
return str.replace(/(["'\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.escapeString=function(str){
return ("\""+str.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");
};
dojo.string.summary=function(str,len){
if(!len||str.length<=len){
return str;
}
return str.substring(0,len).replace(/\.+$/,"")+"...";
};
dojo.string.endsWith=function(str,end,_45a){
if(_45a){
str=str.toLowerCase();
end=end.toLowerCase();
}
if((str.length-end.length)<0){
return false;
}
return str.lastIndexOf(end)==str.length-end.length;
};
dojo.string.endsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.endsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.startsWith=function(str,_45e,_45f){
if(_45f){
str=str.toLowerCase();
_45e=_45e.toLowerCase();
}
return str.indexOf(_45e)==0;
};
dojo.string.startsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.startsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.has=function(str){
for(var i=1;i<arguments.length;i++){
if(str.indexOf(arguments[i])>-1){
return true;
}
}
return false;
};
dojo.string.normalizeNewlines=function(text,_465){
if(_465=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_465=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_467){
var _468=[];
for(var i=0,_46a=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_467){
_468.push(str.substring(_46a,i));
_46a=i+1;
}
}
_468.push(str.substr(_46a));
return _468;
};
dojo.provide("dojo.undo.browser");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
if(dojo.render.html.opera){
dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser={initialHref:window.location.href,initialHash:window.location.hash,moveForward:false,historyStack:[],forwardStack:[],historyIframe:null,bookmarkAnchor:null,locationTimer:null,setInitialState:function(args){
this.initialState=this._createState(this.initialHref,args,this.initialHash);
},addToHistory:function(args){
this.forwardStack=[];
var hash=null;
var url=null;
if(!this.historyIframe){
this.historyIframe=window.frames["djhistory"];
}
if(!this.bookmarkAnchor){
this.bookmarkAnchor=document.createElement("a");
dojo.body().appendChild(this.bookmarkAnchor);
this.bookmarkAnchor.style.display="none";
}
if(args["changeUrl"]){
hash="#"+((args["changeUrl"]!==true)?args["changeUrl"]:(new Date()).getTime());
if(this.historyStack.length==0&&this.initialState.urlHash==hash){
this.initialState=this._createState(url,args,hash);
return;
}else{
if(this.historyStack.length>0&&this.historyStack[this.historyStack.length-1].urlHash==hash){
this.historyStack[this.historyStack.length-1]=this._createState(url,args,hash);
return;
}
}
this.changingUrl=true;
setTimeout("window.location.href = '"+hash+"'; dojo.undo.browser.changingUrl = false;",1);
this.bookmarkAnchor.href=hash;
if(dojo.render.html.ie){
url=this._loadIframeHistory();
var _46f=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_471){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_46f.apply(this,[_471]);
};
if(args["back"]){
args.back=tcb;
}else{
if(args["backButton"]){
args.backButton=tcb;
}else{
if(args["handle"]){
args.handle=tcb;
}
}
}
var _472=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_474){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_472){
_472.apply(this,[_474]);
}
};
if(args["forward"]){
args.forward=tfw;
}else{
if(args["forwardButton"]){
args.forwardButton=tfw;
}else{
if(args["handle"]){
args.handle=tfw;
}
}
}
}else{
if(dojo.render.html.moz){
if(!this.locationTimer){
this.locationTimer=setInterval("dojo.undo.browser.checkLocation();",200);
}
}
}
}else{
url=this._loadIframeHistory();
}
this.historyStack.push(this._createState(url,args,hash));
},checkLocation:function(){
if(!this.changingUrl){
var hsl=this.historyStack.length;
if((window.location.hash==this.initialHash||window.location.href==this.initialHref)&&(hsl==1)){
this.handleBackButton();
return;
}
if(this.forwardStack.length>0){
if(this.forwardStack[this.forwardStack.length-1].urlHash==window.location.hash){
this.handleForwardButton();
return;
}
}
if((hsl>=2)&&(this.historyStack[hsl-2])){
if(this.historyStack[hsl-2].urlHash==window.location.hash){
this.handleBackButton();
return;
}
}
}
},iframeLoaded:function(evt,_477){
if(!dojo.render.html.opera){
var _478=this._getUrlQuery(_477.href);
if(_478==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_478==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_478==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _479=this.historyStack.pop();
if(!_479){
return;
}
var last=this.historyStack[this.historyStack.length-1];
if(!last&&this.historyStack.length==0){
last=this.initialState;
}
if(last){
if(last.kwArgs["back"]){
last.kwArgs["back"]();
}else{
if(last.kwArgs["backButton"]){
last.kwArgs["backButton"]();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("back");
}
}
}
}
this.forwardStack.push(_479);
},handleForwardButton:function(){
var last=this.forwardStack.pop();
if(!last){
return;
}
if(last.kwArgs["forward"]){
last.kwArgs.forward();
}else{
if(last.kwArgs["forwardButton"]){
last.kwArgs.forwardButton();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("forward");
}
}
}
this.historyStack.push(last);
},_createState:function(url,args,hash){
return {"url":url,"kwArgs":args,"urlHash":hash};
},_getUrlQuery:function(url){
var _480=url.split("?");
if(_480.length<2){
return null;
}else{
return _480[1];
}
},_loadIframeHistory:function(){
var url=dojo.hostenv.getBaseScriptUri()+"iframe_history.html?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
return url;
}};
dojo.provide("dojo.io.BrowserIO");
dojo.io.checkChildrenForFile=function(node){
var _483=false;
var _484=node.getElementsByTagName("input");
dojo.lang.forEach(_484,function(_485){
if(_483){
return;
}
if(_485.getAttribute("type")=="file"){
_483=true;
}
});
return _483;
};
dojo.io.formHasFile=function(_486){
return dojo.io.checkChildrenForFile(_486);
};
dojo.io.updateNode=function(node,_488){
node=dojo.byId(node);
var args=_488;
if(dojo.lang.isString(_488)){
args={url:_488};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
if(dojo["event"]){
try{
dojo.event.browser.clean(node.firstChild);
}
catch(e){
}
}
node.removeChild(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_48f,_490,_491){
if((!_48f)||(!_48f.tagName)||(!_48f.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_491){
_491=dojo.io.formFilter;
}
var enc=/utf/i.test(_490||"")?encodeURIComponent:dojo.string.encodeAscii;
var _493=[];
for(var i=0;i<_48f.elements.length;i++){
var elm=_48f.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_491(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_493.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_493.push(name+"="+enc(elm.value));
}
}else{
_493.push(name+"="+enc(elm.value));
}
}
}
var _499=_48f.getElementsByTagName("input");
for(var i=0;i<_499.length;i++){
var _49a=_499[i];
if(_49a.type.toLowerCase()=="image"&&_49a.form==_48f&&_491(_49a)){
var name=enc(_49a.name);
_493.push(name+"="+enc(_49a.value));
_493.push(name+".x=0");
_493.push(name+".y=0");
}
}
return _493.join("&")+"&";
};
dojo.io.FormBind=function(args){
this.bindArgs={};
if(args&&args.formNode){
this.init(args);
}else{
if(args){
this.init({formNode:args});
}
}
};
dojo.lang.extend(dojo.io.FormBind,{form:null,bindArgs:null,clickedButton:null,init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _4a0=form.getElementsByTagName("input");
for(var i=0;i<_4a0.length;i++){
var _4a1=_4a0[i];
if(_4a1.type.toLowerCase()=="image"&&_4a1.form==form){
this.connect(_4a1,"onclick","click");
}
}
},onSubmit:function(form){
return true;
},submit:function(e){
e.preventDefault();
if(this.onSubmit(this.form)){
dojo.io.bind(dojo.lang.mixin(this.bindArgs,{formFilter:dojo.lang.hitch(this,"formFilter")}));
}
},click:function(e){
var node=e.currentTarget;
if(node.disabled){
return;
}
this.clickedButton=node;
},formFilter:function(node){
var type=(node.type||"").toLowerCase();
var _4a8=false;
if(node.disabled||!node.name){
_4a8=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_4a8=node==this.clickedButton;
}else{
_4a8=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _4a8;
},connect:function(_4a9,_4aa,_4ab){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_4a9,_4aa,this,_4ab);
}else{
var fcn=dojo.lang.hitch(this,_4ab);
_4a9[_4aa]=function(e){
if(!e){
e=window.event;
}
if(!e.currentTarget){
e.currentTarget=e.srcElement;
}
if(!e.preventDefault){
e.preventDefault=function(){
window.event.returnValue=false;
};
}
fcn(e);
};
}
}});
dojo.io.XMLHTTPTransport=new function(){
var _4ae=this;
var _4af={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_4b1,_4b2){
return url+"|"+_4b1+"|"+_4b2.toLowerCase();
}
function addToCache(url,_4b4,_4b5,http){
_4af[getCacheKey(url,_4b4,_4b5)]=http;
}
function getFromCache(url,_4b8,_4b9){
return _4af[getCacheKey(url,_4b8,_4b9)];
}
this.clearCache=function(){
_4af={};
};
function doLoad(_4ba,http,url,_4bd,_4be){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_4ba.method.toLowerCase()=="head"){
var _4c0=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _4c0;
};
var _4c1=_4c0.split(/[\r\n]+/g);
for(var i=0;i<_4c1.length;i++){
var pair=_4c1[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_4ba.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_4ba.mimetype=="text/json"||_4ba.mimetype=="application/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_4ba.mimetype=="application/xml")||(_4ba.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"||!http.getResponseHeader("Content-Type")){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_4be){
addToCache(url,_4bd,_4ba.method,http);
}
_4ba[(typeof _4ba.load=="function")?"load":"handle"]("load",ret,http,_4ba);
}else{
var _4c4=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_4ba[(typeof _4ba.error=="function")?"error":"handle"]("error",_4c4,http,_4ba);
}
}
function setHeaders(http,_4c6){
if(_4c6["headers"]){
for(var _4c7 in _4c6["headers"]){
if(_4c7.toLowerCase()=="content-type"&&!_4c6["contentType"]){
_4c6["contentType"]=_4c6["headers"][_4c7];
}else{
http.setRequestHeader(_4c7,_4c6["headers"][_4c7]);
}
}
}
}
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
var now=null;
if(!dojo.hostenv._blockAsync&&!_4ae._blockAsync){
for(var x=this.inFlight.length-1;x>=0;x--){
try{
var tif=this.inFlight[x];
if(!tif||tif.http._aborted||!tif.http.readyState){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
}else{
if(tif.startTime){
if(!now){
now=(new Date()).getTime();
}
if(tif.startTime+(tif.req.timeoutSeconds*1000)<now){
if(typeof tif.http.abort=="function"){
tif.http.abort();
}
this.inFlight.splice(x,1);
tif.req[(typeof tif.req.timeout=="function")?"timeout":"handle"]("timeout",null,tif.http,tif.req);
}
}
}
}
catch(e){
try{
var _4cb=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_4cb,tif.http,tif.req);
}
catch(e2){
dojo.debug("XMLHttpTransport error callback failed: "+e2);
}
}
}
}
clearTimeout(this.inFlightTimer);
if(this.inFlight.length==0){
this.inFlightTimer=null;
return;
}
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
};
var _4cc=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_4cd){
return _4cc&&dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript","text/json","application/json"],(_4cd["mimetype"].toLowerCase()||""))&&!(_4cd["formNode"]&&dojo.io.formHasFile(_4cd["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_4ce){
if(!_4ce["url"]){
if(!_4ce["formNode"]&&(_4ce["backButton"]||_4ce["back"]||_4ce["changeUrl"]||_4ce["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_4ce);
return true;
}
}
var url=_4ce.url;
var _4d0="";
if(_4ce["formNode"]){
var ta=_4ce.formNode.getAttribute("action");
if((ta)&&(!_4ce["url"])){
url=ta;
}
var tp=_4ce.formNode.getAttribute("method");
if((tp)&&(!_4ce["method"])){
_4ce.method=tp;
}
_4d0+=dojo.io.encodeForm(_4ce.formNode,_4ce.encoding,_4ce["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_4ce["file"]){
_4ce.method="post";
}
if(!_4ce["method"]){
_4ce.method="get";
}
if(_4ce.method.toLowerCase()=="get"){
_4ce.multipart=false;
}else{
if(_4ce["file"]){
_4ce.multipart=true;
}else{
if(!_4ce["multipart"]){
_4ce.multipart=false;
}
}
}
if(_4ce["backButton"]||_4ce["back"]||_4ce["changeUrl"]){
dojo.undo.browser.addToHistory(_4ce);
}
var _4d3=_4ce["content"]||{};
if(_4ce.sendTransport){
_4d3["dojo.transport"]="xmlhttp";
}
do{
if(_4ce.postContent){
_4d0=_4ce.postContent;
break;
}
if(_4d3){
_4d0+=dojo.io.argsFromMap(_4d3,_4ce.encoding);
}
if(_4ce.method.toLowerCase()=="get"||!_4ce.multipart){
break;
}
var t=[];
if(_4d0.length){
var q=_4d0.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_4ce.file){
if(dojo.lang.isArray(_4ce.file)){
for(var i=0;i<_4ce.file.length;++i){
var o=_4ce.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_4ce.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_4d0=t.join("\r\n");
}
}while(false);
var _4d9=_4ce["sync"]?false:true;
var _4da=_4ce["preventCache"]||(this.preventCache==true&&_4ce["preventCache"]!=false);
var _4db=_4ce["useCache"]==true||(this.useCache==true&&_4ce["useCache"]!=false);
if(!_4da&&_4db){
var _4dc=getFromCache(url,_4d0,_4ce.method);
if(_4dc){
doLoad(_4ce,_4dc,url,_4d0,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_4ce);
var _4de=false;
if(_4d9){
var _4df=this.inFlight.push({"req":_4ce,"http":http,"url":url,"query":_4d0,"useCache":_4db,"startTime":_4ce.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_4ae._blockAsync=true;
}
if(_4ce.method.toLowerCase()=="post"){
if(!_4ce.user){
http.open("POST",url,_4d9);
}else{
http.open("POST",url,_4d9,_4ce.user,_4ce.password);
}
setHeaders(http,_4ce);
http.setRequestHeader("Content-Type",_4ce.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_4ce.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_4d0);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_4ce,{status:404},url,_4d0,_4db);
}
}else{
var _4e0=url;
if(_4d0!=""){
_4e0+=(_4e0.indexOf("?")>-1?"&":"?")+_4d0;
}
if(_4da){
_4e0+=(dojo.string.endsWithAny(_4e0,"?","&")?"":(_4e0.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_4ce.user){
http.open(_4ce.method.toUpperCase(),_4e0,_4d9);
}else{
http.open(_4ce.method.toUpperCase(),_4e0,_4d9,_4ce.user,_4ce.password);
}
setHeaders(http,_4ce);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_4ce,{status:404},url,_4d0,_4db);
}
}
if(!_4d9){
doLoad(_4ce,http,url,_4d0,_4db);
_4ae._blockAsync=false;
}
_4ce.abort=function(){
try{
http._aborted=true;
}
catch(e){
}
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_4e2,days,path,_4e5,_4e6){
var _4e7=-1;
if(typeof days=="number"&&days>=0){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_4e7=d.toGMTString();
}
_4e2=escape(_4e2);
document.cookie=name+"="+_4e2+";"+(_4e7!=-1?" expires="+_4e7+";":"")+(path?"path="+path:"")+(_4e5?"; domain="+_4e5:"")+(_4e6?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _4eb=document.cookie.substring(idx+name.length+1);
var end=_4eb.indexOf(";");
if(end==-1){
end=_4eb.length;
}
_4eb=_4eb.substring(0,end);
_4eb=unescape(_4eb);
return _4eb;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_4f2,_4f3,_4f4){
if(arguments.length==5){
_4f4=_4f2;
_4f2=null;
_4f3=null;
}
var _4f5=[],_4f6,_4f7="";
if(!_4f4){
_4f6=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_4f6){
_4f6={};
}
for(var prop in obj){
if(prop==null){
delete _4f6[prop];
}else{
if(typeof obj[prop]=="string"||typeof obj[prop]=="number"){
_4f6[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _4f6){
_4f5.push(escape(prop)+"="+escape(_4f6[prop]));
}
_4f7=_4f5.join("&");
}
dojo.io.cookie.setCookie(name,_4f7,days,path,_4f2,_4f3);
};
dojo.io.cookie.getObjectCookie=function(name){
var _4fa=null,_4fb=dojo.io.cookie.getCookie(name);
if(_4fb){
_4fa={};
var _4fc=_4fb.split("&");
for(var i=0;i<_4fc.length;i++){
var pair=_4fc[i].split("=");
var _4ff=pair[1];
if(isNaN(_4ff)){
_4ff=unescape(pair[1]);
}
_4fa[unescape(pair[0])]=_4ff;
}
}
return _4fa;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _500=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_500=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.provide("dojo.io.*");
dojo.provide("dojo.html.style");
dojo.html.getClass=function(node){
node=dojo.byId(node);
if(!node){
return "";
}
var cs="";
if(node.className){
cs=node.className;
}else{
if(dojo.html.hasAttribute(node,"class")){
cs=dojo.html.getAttribute(node,"class");
}
}
return cs.replace(/^\s+|\s+$/g,"");
};
dojo.html.getClasses=function(node){
var c=dojo.html.getClass(node);
return (c=="")?[]:c.split(/\s+/g);
};
dojo.html.hasClass=function(node,_506){
return (new RegExp("(^|\\s+)"+_506+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_508){
_508+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_508);
};
dojo.html.addClass=function(node,_50a){
if(dojo.html.hasClass(node,_50a)){
return false;
}
_50a=(dojo.html.getClass(node)+" "+_50a).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_50a);
};
dojo.html.setClass=function(node,_50c){
node=dojo.byId(node);
var cs=new String(_50c);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_50c);
node.className=cs;
}else{
return false;
}
}
}
catch(e){
dojo.debug("dojo.html.setClass() failed",e);
}
return true;
};
dojo.html.removeClass=function(node,_50f,_510){
try{
if(!_510){
var _511=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_50f+"(\\s+|$)"),"$1$2");
}else{
var _511=dojo.html.getClass(node).replace(_50f,"");
}
dojo.html.setClass(node,_511);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_513,_514){
dojo.html.removeClass(node,_514);
dojo.html.addClass(node,_513);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_515,_516,_517,_518,_519){
_519=false;
var _51a=dojo.doc();
_516=dojo.byId(_516)||_51a;
var _51b=_515.split(/\s+/g);
var _51c=[];
if(_518!=1&&_518!=2){
_518=0;
}
var _51d=new RegExp("(\\s|^)(("+_51b.join(")|(")+"))(\\s|$)");
var _51e=_51b.join(" ").length;
var _51f=[];
if(!_519&&_51a.evaluate){
var _520=".//"+(_517||"*")+"[contains(";
if(_518!=dojo.html.classMatchType.ContainsAny){
_520+="concat(' ',@class,' '), ' "+_51b.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_518==2){
_520+=" and string-length(@class)="+_51e+"]";
}else{
_520+="]";
}
}else{
_520+="concat(' ',@class,' '), ' "+_51b.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _521=_51a.evaluate(_520,_516,null,XPathResult.ANY_TYPE,null);
var _522=_521.iterateNext();
while(_522){
try{
_51f.push(_522);
_522=_521.iterateNext();
}
catch(e){
break;
}
}
return _51f;
}else{
if(!_517){
_517="*";
}
_51f=_516.getElementsByTagName(_517);
var node,i=0;
outer:
while(node=_51f[i++]){
var _525=dojo.html.getClasses(node);
if(_525.length==0){
continue outer;
}
var _526=0;
for(var j=0;j<_525.length;j++){
if(_51d.test(_525[j])){
if(_518==dojo.html.classMatchType.ContainsAny){
_51c.push(node);
continue outer;
}else{
_526++;
}
}else{
if(_518==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_526==_51b.length){
if((_518==dojo.html.classMatchType.IsOnly)&&(_526==_525.length)){
_51c.push(node);
}else{
if(_518==dojo.html.classMatchType.ContainsAll){
_51c.push(node);
}
}
}
}
return _51c;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_528){
var arr=_528.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_52c){
return _52c.replace(/([A-Z])/g,"-$1").toLowerCase();
};
dojo.html.getComputedStyle=function(node,_52e,_52f){
node=dojo.byId(node);
var _52e=dojo.html.toSelectorCase(_52e);
var _530=dojo.html.toCamelCase(_52e);
if(!node||!node.style){
return _52f;
}else{
if(document.defaultView&&dojo.html.isDescendantOf(node,node.ownerDocument)){
try{
var cs=document.defaultView.getComputedStyle(node,"");
if(cs){
return cs.getPropertyValue(_52e);
}
}
catch(e){
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_52e);
}else{
return _52f;
}
}
}else{
if(node.currentStyle){
return node.currentStyle[_530];
}
}
}
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_52e);
}else{
return _52f;
}
};
dojo.html.getStyleProperty=function(node,_533){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_533)]:undefined);
};
dojo.html.getStyle=function(node,_535){
var _536=dojo.html.getStyleProperty(node,_535);
return (_536?_536:dojo.html.getComputedStyle(node,_535));
};
dojo.html.setStyle=function(node,_538,_539){
node=dojo.byId(node);
if(node&&node.style){
var _53a=dojo.html.toCamelCase(_538);
node.style[_53a]=_539;
}
};
dojo.html.setStyleText=function(_53b,text){
try{
_53b.style.cssText=text;
}
catch(e){
_53b.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_53d,_53e){
if(!_53e.style.cssText){
_53d.setAttribute("style",_53e.getAttribute("style"));
}else{
_53d.style.cssText=_53e.style.cssText;
}
dojo.html.addClass(_53d,dojo.html.getClass(_53e));
};
dojo.html.getUnitValue=function(node,_540,_541){
var s=dojo.html.getComputedStyle(node,_540);
if((!s)||((s=="auto")&&(_541))){
return {value:0,units:"px"};
}
var _543=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_543){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_543[1]),units:_543[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
dojo.html.getPixelValue=function(node,_545,_546){
var _547=dojo.html.getUnitValue(node,_545,_546);
if(isNaN(_547.value)){
return 0;
}
if((_547.value)&&(_547.units!="px")){
return NaN;
}
return _547.value;
};
dojo.html.setPositivePixelValue=function(node,_549,_54a){
if(isNaN(_54a)){
return false;
}
node.style[_549]=Math.max(0,_54a)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_54b,_54c,_54d){
if(!dojo.html.styleSheet){
if(document.createStyleSheet){
dojo.html.styleSheet=document.createStyleSheet();
}else{
if(document.styleSheets[0]){
dojo.html.styleSheet=document.styleSheets[0];
}else{
return null;
}
}
}
if(arguments.length<3){
if(dojo.html.styleSheet.cssRules){
_54d=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_54d=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_54b+" { "+_54c+" }";
return dojo.html.styleSheet.insertRule(rule,_54d);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_54b,_54c,_54d);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_54f){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_54f){
_54f=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_54f);
}
}else{
if(document.styleSheets[0]){
if(!_54f){
_54f=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_54f);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_552,_553){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _554=dojo.hostenv.getText(URI,false,_553);
if(_554===null){
return;
}
_554=dojo.html.fixPathsInCssText(_554,URI);
if(_552){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_554)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _559=doc.getElementsByTagName("style");
for(var i=0;i<_559.length;i++){
if(_559[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _55a=dojo.html.insertCssText(_554);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_554,"nodeRef":_55a});
if(_55a&&djConfig.isDebug){
_55a.setAttribute("dbgHref",URI);
}
return _55a;
};
dojo.html.insertCssText=function(_55b,doc,URI){
if(!_55b){
return;
}
if(!doc){
doc=document;
}
if(URI){
_55b=dojo.html.fixPathsInCssText(_55b,URI);
}
var _55e=doc.createElement("style");
_55e.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_55e);
}
if(_55e.styleSheet){
_55e.styleSheet.cssText=_55b;
}else{
var _560=doc.createTextNode(_55b);
_55e.appendChild(_560);
}
return _55e;
};
dojo.html.fixPathsInCssText=function(_561,URI){
function iefixPathsInCssText(){
var _563=/AlphaImageLoader\(src\=['"]([\t\s\w()\/.\\'"-:#=&?~]*)['"]/;
while(_564=_563.exec(_561)){
url=_564[1].replace(_566,"$2");
if(!_567.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_561.substring(0,_564.index)+"AlphaImageLoader(src='"+url+"'";
_561=_561.substr(_564.index+_564[0].length);
}
return str+_561;
}
if(!_561||!URI){
return;
}
var _564,str="",url="";
var _569=/url\(\s*([\t\s\w()\/.\\'"-:#=&?]+)\s*\)/;
var _567=/(file|https?|ftps?):\/\//;
var _566=/^[\s]*(['"]?)([\w()\/.\\'"-:#=&?]*)\1[\s]*?$/;
if(dojo.render.html.ie55||dojo.render.html.ie60){
_561=iefixPathsInCssText();
}
while(_564=_569.exec(_561)){
url=_564[1].replace(_566,"$2");
if(!_567.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_561.substring(0,_564.index)+"url("+url+")";
_561=_561.substr(_564.index+_564[0].length);
}
return str+_561;
};
dojo.html.setActiveStyleSheet=function(_56a){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_56a){
a.disabled=false;
}
}
}
};
dojo.html.getActiveStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")&&!a.disabled){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.getPreferredStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("rel").indexOf("alt")==-1&&a.getAttribute("title")){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.applyBrowserClass=function(node){
var drh=dojo.render.html;
var _576={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _576){
if(_576[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.provide("dojo.widget.DomWidget");
dojo.widget._cssFiles={};
dojo.widget._cssStrings={};
dojo.widget._templateCache={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache=function(obj,_579,_57a,_57b){
var _57c=_579||obj.templatePath;
var _57d=dojo.widget._templateCache;
if(!obj["widgetType"]){
do{
var _57e="__dummyTemplate__"+dojo.widget._templateCache.dummyCount++;
}while(_57d[_57e]);
obj.widgetType=_57e;
}
var wt=obj.widgetType;
var ts=_57d[wt];
if(!ts){
_57d[wt]={"string":null,"node":null};
if(_57b){
ts={};
}else{
ts=_57d[wt];
}
}
if((!obj.templateString)&&(!_57b)){
obj.templateString=_57a||ts["string"];
}
if((!obj.templateNode)&&(!_57b)){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_57c)){
var _581=dojo.hostenv.getText(_57c);
if(_581){
_581=_581.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im,"");
var _582=_581.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_582){
_581=_582[1];
}
}else{
_581="";
}
obj.templateString=_581;
if(!_57b){
_57d[wt]["string"]=_581;
}
}
if((!ts["string"])&&(!_57b)){
ts.string=obj.templateString;
}
};
dojo.widget._templateCache.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.waiNames=["waiRole","waiState"];
dojo.widget.wai={waiRole:{name:"waiRole","namespace":"http://www.w3.org/TR/xhtml2",alias:"x2",prefix:"wairole:"},waiState:{name:"waiState","namespace":"http://www.w3.org/2005/07/aaa",alias:"aaa",prefix:""},setAttr:function(node,ns,attr,_586){
if(dojo.render.html.ie){
node.setAttribute(this[ns].alias+":"+attr,this[ns].prefix+_586);
}else{
node.setAttributeNS(this[ns]["namespace"],attr,this[ns].prefix+_586);
}
},getAttr:function(node,ns,attr){
if(dojo.render.html.ie){
return node.getAttribute(this[ns].alias+":"+attr);
}else{
return node.getAttributeNS(this[ns]["namespace"],attr);
}
},removeAttr:function(node,ns,attr){
var _58d=true;
if(dojo.render.html.ie){
_58d=node.removeAttribute(this[ns].alias+":"+attr);
}else{
node.removeAttributeNS(this[ns]["namespace"],attr);
}
return _58d;
}};
dojo.widget.attachTemplateNodes=function(_58e,_58f,_590){
var _591=dojo.dom.ELEMENT_NODE;
function trim(str){
return str.replace(/^\s+|\s+$/g,"");
}
if(!_58e){
_58e=_58f.domNode;
}
if(_58e.nodeType!=_591){
return;
}
var _593=_58e.all||_58e.getElementsByTagName("*");
var _594=_58f;
for(var x=-1;x<_593.length;x++){
var _596=(x==-1)?_58e:_593[x];
var _597=[];
if(!_58f.widgetsInTemplate||!_596.getAttribute("dojoType")){
for(var y=0;y<this.attachProperties.length;y++){
var _599=_596.getAttribute(this.attachProperties[y]);
if(_599){
_597=_599.split(";");
for(var z=0;z<_597.length;z++){
if(dojo.lang.isArray(_58f[_597[z]])){
_58f[_597[z]].push(_596);
}else{
_58f[_597[z]]=_596;
}
}
break;
}
}
var _59b=_596.getAttribute(this.eventAttachProperty);
if(_59b){
var evts=_59b.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _59d=null;
var tevt=trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _59f=tevt.split(":");
tevt=trim(_59f[0]);
_59d=trim(_59f[1]);
}
if(!_59d){
_59d=tevt;
}
var tf=function(){
var ntf=new String(_59d);
return function(evt){
if(_594[ntf]){
_594[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_596,tevt,tf,false,true);
}
}
for(var y=0;y<_590.length;y++){
var _5a3=_596.getAttribute(_590[y]);
if((_5a3)&&(_5a3.length)){
var _59d=null;
var _5a4=_590[y].substr(4);
_59d=trim(_5a3);
var _5a5=[_59d];
if(_59d.indexOf(";")>=0){
_5a5=dojo.lang.map(_59d.split(";"),trim);
}
for(var z=0;z<_5a5.length;z++){
if(!_5a5[z].length){
continue;
}
var tf=function(){
var ntf=new String(_5a5[z]);
return function(evt){
if(_594[ntf]){
_594[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_596,_5a4,tf,false,true);
}
}
}
}
var _5a8=_596.getAttribute(this.templateProperty);
if(_5a8){
_58f[_5a8]=_596;
}
dojo.lang.forEach(dojo.widget.waiNames,function(name){
var wai=dojo.widget.wai[name];
var val=_596.getAttribute(wai.name);
if(val){
if(val.indexOf("-")==-1){
dojo.widget.wai.setAttr(_596,wai.name,"role",val);
}else{
var _5ac=val.split("-");
dojo.widget.wai.setAttr(_596,wai.name,_5ac[0],_5ac[1]);
}
}
},this);
var _5ad=_596.getAttribute(this.onBuildProperty);
if(_5ad){
eval("var node = baseNode; var widget = targetObj; "+_5ad);
}
}
};
dojo.widget.getDojoEventsFromStr=function(str){
var re=/(dojoOn([a-z]+)(\s?))=/gi;
var evts=str?str.match(re)||[]:[];
var ret=[];
var lem={};
for(var x=0;x<evts.length;x++){
if(evts[x].length<1){
continue;
}
var cm=evts[x].replace(/\s/,"");
cm=(cm.slice(0,cm.length-1));
if(!lem[cm]){
lem[cm]=true;
ret.push(cm);
}
}
return ret;
};
dojo.declare("dojo.widget.DomWidget",dojo.widget.Widget,function(){
if((arguments.length>0)&&(typeof arguments[0]=="object")){
this.create(arguments[0]);
}
},{templateNode:null,templateString:null,templateCssString:null,preventClobber:false,domNode:null,containerNode:null,widgetsInTemplate:false,addChild:function(_5b5,_5b6,pos,ref,_5b9){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
if(_5b9==undefined){
_5b9=this.children.length;
}
this.addWidgetAsDirectChild(_5b5,_5b6,pos,ref,_5b9);
this.registerChild(_5b5,_5b9);
}
return _5b5;
},addWidgetAsDirectChild:function(_5ba,_5bb,pos,ref,_5be){
if((!this.containerNode)&&(!_5bb)){
this.containerNode=this.domNode;
}
var cn=(_5bb)?_5bb:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
if(!cn){
cn=dojo.body();
}
ref=cn.lastChild;
}
if(!_5be){
_5be=0;
}
_5ba.domNode.setAttribute("dojoinsertionindex",_5be);
if(!ref){
cn.appendChild(_5ba.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_5ba.domNode,ref.parentNode,_5be);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_5ba.domNode);
}else{
dojo.dom.insertAtPosition(_5ba.domNode,cn,pos);
}
}
}
},registerChild:function(_5c0,_5c1){
_5c0.dojoInsertionIndex=_5c1;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<=_5c1){
idx=i;
}
}
this.children.splice(idx+1,0,_5c0);
_5c0.parent=this;
_5c0.addedTo(this,idx+1);
delete dojo.widget.manager.topWidgets[_5c0.widgetId];
},removeChild:function(_5c4){
dojo.dom.removeNode(_5c4.domNode);
return dojo.widget.DomWidget.superclass.removeChild.call(this,_5c4);
},getFragNodeRef:function(frag){
if(!frag){
return null;
}
if(!frag[this.getNamespacedType()]){
dojo.raise("Error: no frag for widget type "+this.getNamespacedType()+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return frag[this.getNamespacedType()]["nodeRef"];
},postInitialize:function(args,frag,_5c8){
var _5c9=this.getFragNodeRef(frag);
if(_5c8&&(_5c8.snarfChildDomOutput||!_5c9)){
_5c8.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_5c9);
}else{
if(_5c9){
if(this.domNode&&(this.domNode!==_5c9)){
var _5ca=_5c9.parentNode.replaceChild(this.domNode,_5c9);
}
}
}
if(_5c8){
_5c8.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.widgetsInTemplate){
var _5cb=new dojo.xml.Parse();
var _5cc;
var _5cd=this.domNode.getElementsByTagName("*");
for(var i=0;i<_5cd.length;i++){
if(_5cd[i].getAttribute("dojoAttachPoint")=="subContainerWidget"){
_5cc=_5cd[i];
}
if(_5cd[i].getAttribute("dojoType")){
_5cd[i].setAttribute("_isSubWidget",true);
}
}
if(this.isContainer&&!this.containerNode){
if(_5cc){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,_5cc);
frag["dojoDontFollow"]=true;
}
}else{
dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
}
}
var _5d0=_5cb.parseElement(this.domNode,null,true);
dojo.widget.getParser().createSubComponents(_5d0,this);
var _5d1=[];
var _5d2=[this];
var w;
while((w=_5d2.pop())){
for(var i=0;i<w.children.length;i++){
var _5d4=w.children[i];
if(_5d4._processedSubWidgets||!_5d4.extraArgs["_issubwidget"]){
continue;
}
_5d1.push(_5d4);
if(_5d4.isContainer){
_5d2.push(_5d4);
}
}
}
for(var i=0;i<_5d1.length;i++){
var _5d5=_5d1[i];
if(_5d5._processedSubWidgets){
dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
return;
}
_5d5._processedSubWidgets=true;
if(_5d5.extraArgs["dojoattachevent"]){
var evts=_5d5.extraArgs["dojoattachevent"].split(";");
for(var j=0;j<evts.length;j++){
var _5d8=null;
var tevt=dojo.string.trim(evts[j]);
if(tevt.indexOf(":")>=0){
var _5da=tevt.split(":");
tevt=dojo.string.trim(_5da[0]);
_5d8=dojo.string.trim(_5da[1]);
}
if(!_5d8){
_5d8=tevt;
}
if(dojo.lang.isFunction(_5d5[tevt])){
dojo.event.kwConnect({srcObj:_5d5,srcFunc:tevt,targetObj:this,targetFunc:_5d8});
}else{
alert(tevt+" is not a function in widget "+_5d5);
}
}
}
if(_5d5.extraArgs["dojoattachpoint"]){
this[_5d5.extraArgs["dojoattachpoint"]]=_5d5;
}
}
}
if(this.isContainer&&!frag["dojoDontFollow"]){
dojo.widget.getParser().createSubComponents(frag,this);
}
},buildRendering:function(args,frag){
var ts=dojo.widget._templateCache[this.widgetType];
if(args["templatecsspath"]){
args["templateCssPath"]=args["templatecsspath"];
}
var _5de=args["templateCssPath"]||this.templateCssPath;
if(_5de&&!dojo.widget._cssFiles[_5de.toString()]){
if((!this.templateCssString)&&(_5de)){
this.templateCssString=dojo.hostenv.getText(_5de);
this.templateCssPath=null;
}
dojo.widget._cssFiles[_5de.toString()]=true;
}
if((this["templateCssString"])&&(!this.templateCssString["loaded"])){
dojo.html.insertCssText(this.templateCssString,null,_5de);
if(!this.templateCssString){
this.templateCssString="";
}
this.templateCssString.loaded=true;
}
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var _5e1=false;
if(args["templatepath"]){
_5e1=true;
args["templatePath"]=args["templatepath"];
}
dojo.widget.fillFromTemplateCache(this,args["templatePath"],null,_5e1);
var ts=dojo.widget._templateCache[this.widgetType];
if((ts)&&(!_5e1)){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _5e3=false;
var node=null;
var tstr=this.templateString;
if((!this.templateNode)&&(this.templateString)){
_5e3=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_5e3){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_5e3.length;i++){
var key=_5e3[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?dojo.lang.getObjPathValue(key.substring(5),this):hash[key];
var _5ea;
if((kval)||(dojo.lang.isString(kval))){
_5ea=new String((dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval);
while(_5ea.indexOf("\"")>-1){
_5ea=_5ea.replace("\"","&quot;");
}
tstr=tstr.replace(_5e3[i],_5ea);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
if(!_5e1){
ts.node=this.templateNode;
}
}
}
if((!this.templateNode)&&(!_5e3)){
dojo.debug("DomWidget.buildFromTemplate: could not create template");
return false;
}else{
if(!_5e3){
node=this.templateNode.cloneNode(true);
if(!node){
return false;
}
}else{
node=this.createNodesFromText(tstr,true)[0];
}
}
this.domNode=node;
this.attachTemplateNodes();
if(this.isContainer&&this.containerNode){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,this.containerNode);
}
}
},attachTemplateNodes:function(_5ec,_5ed){
if(!_5ec){
_5ec=this.domNode;
}
if(!_5ed){
_5ed=this;
}
return dojo.widget.attachTemplateNodes(_5ec,_5ed,dojo.widget.getDojoEventsFromStr(this.templateString));
},fillInTemplate:function(){
},destroyRendering:function(){
try{
delete this.domNode;
}
catch(e){
}
},cleanUp:function(){
},getContainerHeight:function(){
dojo.unimplemented("dojo.widget.DomWidget.getContainerHeight");
},getContainerWidth:function(){
dojo.unimplemented("dojo.widget.DomWidget.getContainerWidth");
},createNodesFromText:function(){
dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_5ef,_5f0){
node=dojo.byId(node);
_5f0(node,!_5ef(node));
return _5ef(node);
};
dojo.html.show=function(node){
node=dojo.byId(node);
if(dojo.html.getStyleProperty(node,"display")=="none"){
dojo.html.setStyle(node,"display",(node.dojoDisplayCache||""));
node.dojoDisplayCache=undefined;
}
};
dojo.html.hide=function(node){
node=dojo.byId(node);
if(typeof node["dojoDisplayCache"]=="undefined"){
var d=dojo.html.getStyleProperty(node,"display");
if(d!="none"){
node.dojoDisplayCache=d;
}
}
dojo.html.setStyle(node,"display","none");
};
dojo.html.setShowing=function(node,_5f5){
dojo.html[(_5f5?"show":"hide")](node);
};
dojo.html.isShowing=function(node){
return (dojo.html.getStyleProperty(node,"display")!="none");
};
dojo.html.toggleShowing=function(node){
return dojo.html._toggle(node,dojo.html.isShowing,dojo.html.setShowing);
};
dojo.html.displayMap={tr:"",td:"",th:"",img:"inline",span:"inline",input:"inline",button:"inline"};
dojo.html.suggestDisplayByTagName=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
var tag=node.tagName.toLowerCase();
return (tag in dojo.html.displayMap?dojo.html.displayMap[tag]:"block");
}
};
dojo.html.setDisplay=function(node,_5fb){
dojo.html.setStyle(node,"display",((_5fb instanceof String||typeof _5fb=="string")?_5fb:(_5fb?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_5ff){
dojo.html.setStyle(node,"visibility",((_5ff instanceof String||typeof _5ff=="string")?_5ff:(_5ff?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_603,_604){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_604){
if(_603>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_603=0.999999;
}
}else{
if(_603<0){
_603=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_603*100+")";
}
}
node.style.filter="Alpha(Opacity="+_603*100+")";
}else{
if(h.moz){
node.style.opacity=_603;
node.style.MozOpacity=_603;
}else{
if(h.safari){
node.style.opacity=_603;
node.style.KhtmlOpacity=_603;
}else{
node.style.opacity=_603;
}
}
}
};
dojo.html.clearOpacity=function(node){
node=dojo.byId(node);
var ns=node.style;
var h=dojo.render.html;
if(h.ie){
try{
if(node.filters&&node.filters.alpha){
ns.filter="";
}
}
catch(e){
}
}else{
if(h.moz){
ns.opacity=1;
ns.MozOpacity=1;
}else{
if(h.safari){
ns.opacity=1;
ns.KhtmlOpacity=1;
}else{
ns.opacity=1;
}
}
}
};
dojo.html.getOpacity=function(node){
node=dojo.byId(node);
var h=dojo.render.html;
if(h.ie){
var opac=(node.filters&&node.filters.alpha&&typeof node.filters.alpha.opacity=="number"?node.filters.alpha.opacity:100)/100;
}else{
var opac=node.style.opacity||node.style.MozOpacity||node.style.KhtmlOpacity||1;
}
return opac>=0.999999?1:Number(opac);
};
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _610=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_610+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _610;
};
dojo.html.setStyleAttributes=function(node,_613){
node=dojo.byId(node);
var _614=_613.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_614.length;i++){
var _616=_614[i].split(":");
var name=_616[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _618=_616[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_618);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_618});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_618});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_618});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_618});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_618;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_61a,_61b){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_61b){
_61b=bs.CONTENT_BOX;
}
var _61e=2;
var _61f;
switch(_61b){
case bs.MARGIN_BOX:
_61f=3;
break;
case bs.BORDER_BOX:
_61f=2;
break;
case bs.PADDING_BOX:
default:
_61f=1;
break;
case bs.CONTENT_BOX:
_61f=0;
break;
}
var h=dojo.render.html;
var db=document["body"]||document["documentElement"];
if(h.ie){
with(node.getBoundingClientRect()){
ret.x=left-2;
ret.y=top-2;
}
}else{
if(document.getBoxObjectFor){
_61e=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _623;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_623=db;
}else{
_623=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _625=node;
do{
var n=_625["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_625["offsetTop"];
ret.y+=isNaN(m)?0:m;
_625=_625.offsetParent;
}while((_625!=_623)&&(_625!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_61a){
var _628=dojo.html.getScroll();
ret.y+=_628.top;
ret.x+=_628.left;
}
var _629=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_61e>_61f){
for(var i=_61f;i<_61e;++i){
ret.y+=_629[i](node,"top");
ret.x+=_629[i](node,"left");
}
}else{
if(_61e<_61f){
for(var i=_61f;i>_61e;--i){
ret.y-=_629[i-1](node,"top");
ret.x-=_629[i-1](node,"left");
}
}
}
ret.top=ret.y;
ret.left=ret.x;
return ret;
};
dojo.html.isPositionAbsolute=function(node){
return (dojo.html.getComputedStyle(node,"position")=="absolute");
};
dojo.html._sumPixelValues=function(node,_62d,_62e){
var _62f=0;
for(var x=0;x<_62d.length;x++){
_62f+=dojo.html.getPixelValue(node,_62d[x],_62e);
}
return _62f;
};
dojo.html.getMargin=function(node){
return {width:dojo.html._sumPixelValues(node,["margin-left","margin-right"],(dojo.html.getComputedStyle(node,"position")=="absolute")),height:dojo.html._sumPixelValues(node,["margin-top","margin-bottom"],(dojo.html.getComputedStyle(node,"position")=="absolute"))};
};
dojo.html.getBorder=function(node){
return {width:dojo.html.getBorderExtent(node,"left")+dojo.html.getBorderExtent(node,"right"),height:dojo.html.getBorderExtent(node,"top")+dojo.html.getBorderExtent(node,"bottom")};
};
dojo.html.getBorderExtent=function(node,side){
return (dojo.html.getStyle(node,"border-"+side+"-style")=="none"?0:dojo.html.getPixelValue(node,"border-"+side+"-width"));
};
dojo.html.getMarginExtent=function(node,side){
return dojo.html._sumPixelValues(node,["margin-"+side],dojo.html.isPositionAbsolute(node));
};
dojo.html.getPaddingExtent=function(node,side){
return dojo.html._sumPixelValues(node,["padding-"+side],true);
};
dojo.html.getPadding=function(node){
return {width:dojo.html._sumPixelValues(node,["padding-left","padding-right"],true),height:dojo.html._sumPixelValues(node,["padding-top","padding-bottom"],true)};
};
dojo.html.getPadBorder=function(node){
var pad=dojo.html.getPadding(node);
var _63c=dojo.html.getBorder(node);
return {width:pad.width+_63c.width,height:pad.height+_63c.height};
};
dojo.html.getBoxSizing=function(node){
var h=dojo.render.html;
var bs=dojo.html.boxSizing;
if((h.ie)||(h.opera)){
var cm=document["compatMode"];
if((cm=="BackCompat")||(cm=="QuirksMode")){
return bs.BORDER_BOX;
}else{
return bs.CONTENT_BOX;
}
}else{
if(arguments.length==0){
node=document.documentElement;
}
var _641=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_641){
_641=dojo.html.getStyle(node,"box-sizing");
}
return (_641?_641:bs.CONTENT_BOX);
}
};
dojo.html.isBorderBox=function(node){
return (dojo.html.getBoxSizing(node)==dojo.html.boxSizing.BORDER_BOX);
};
dojo.html.getBorderBox=function(node){
node=dojo.byId(node);
return {width:node.offsetWidth,height:node.offsetHeight};
};
dojo.html.getPaddingBox=function(node){
var box=dojo.html.getBorderBox(node);
var _646=dojo.html.getBorder(node);
return {width:box.width-_646.width,height:box.height-_646.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _648=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_648.width,height:node.offsetHeight-_648.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _64b=0;
var _64c=0;
var isbb=dojo.html.isBorderBox(node);
var _64e=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_64b=args.width+_64e.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_64b);
}
if(typeof args.height!="undefined"){
_64c=args.height+_64e.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_64c);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _651=dojo.html.getBorderBox(node);
var _652=dojo.html.getMargin(node);
return {width:_651.width+_652.width,height:_651.height+_652.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _655=0;
var _656=0;
var isbb=dojo.html.isBorderBox(node);
var _658=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _659=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_655=args.width-_658.width;
_655-=_659.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_655);
}
if(typeof args.height!="undefined"){
_656=args.height-_658.height;
_656-=_659.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_656);
}
return ret;
};
dojo.html.getElementBox=function(node,type){
var bs=dojo.html.boxSizing;
switch(type){
case bs.MARGIN_BOX:
return dojo.html.getMarginBox(node);
case bs.BORDER_BOX:
return dojo.html.getBorderBox(node);
case bs.PADDING_BOX:
return dojo.html.getPaddingBox(node);
case bs.CONTENT_BOX:
default:
return dojo.html.getContentBox(node);
}
};
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_65e,_65f,_660){
if(_65e instanceof Array||typeof _65e=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_65e.length<4){
_65e.push(0);
}
while(_65e.length>4){
_65e.pop();
}
var ret={left:_65e[0],top:_65e[1],width:_65e[2],height:_65e[3]};
}else{
if(!_65e.nodeType&&!(_65e instanceof String||typeof _65e=="string")&&("width" in _65e||"height" in _65e||"left" in _65e||"x" in _65e||"top" in _65e||"y" in _65e)){
var ret={left:_65e.left||_65e.x||0,top:_65e.top||_65e.y||0,width:_65e.width||0,height:_65e.height||0};
}else{
var node=dojo.byId(_65e);
var pos=dojo.html.abs(node,_65f,_660);
var _664=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_664.width,height:_664.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_666){
return dojo.html._callDeprecated("setMarginBoxWidth","setMarginBox",arguments,"width");
};
dojo.html.setMarginBoxHeight=dojo.html.setOuterHeight=function(){
return dojo.html._callDeprecated("setMarginBoxHeight","setMarginBox",arguments,"height");
};
dojo.html.getMarginBoxWidth=dojo.html.getOuterWidth=function(){
return dojo.html._callDeprecated("getMarginBoxWidth","getMarginBox",arguments,null,"width");
};
dojo.html.getMarginBoxHeight=dojo.html.getOuterHeight=function(){
return dojo.html._callDeprecated("getMarginBoxHeight","getMarginBox",arguments,null,"height");
};
dojo.html.getTotalOffset=function(node,type,_669){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_66b){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_66d){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_66f){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_671){
return dojo.html._callDeprecated("totalOffsetTop","getAbsolutePosition",arguments,null,"top");
};
dojo.html.getMarginWidth=function(node){
return dojo.html._callDeprecated("getMarginWidth","getMargin",arguments,null,"width");
};
dojo.html.getMarginHeight=function(node){
return dojo.html._callDeprecated("getMarginHeight","getMargin",arguments,null,"height");
};
dojo.html.getBorderWidth=function(node){
return dojo.html._callDeprecated("getBorderWidth","getBorder",arguments,null,"width");
};
dojo.html.getBorderHeight=function(node){
return dojo.html._callDeprecated("getBorderHeight","getBorder",arguments,null,"height");
};
dojo.html.getPaddingWidth=function(node){
return dojo.html._callDeprecated("getPaddingWidth","getPadding",arguments,null,"width");
};
dojo.html.getPaddingHeight=function(node){
return dojo.html._callDeprecated("getPaddingHeight","getPadding",arguments,null,"height");
};
dojo.html.getPadBorderWidth=function(node){
return dojo.html._callDeprecated("getPadBorderWidth","getPadBorder",arguments,null,"width");
};
dojo.html.getPadBorderHeight=function(node){
return dojo.html._callDeprecated("getPadBorderHeight","getPadBorder",arguments,null,"height");
};
dojo.html.getBorderBoxWidth=dojo.html.getInnerWidth=function(){
return dojo.html._callDeprecated("getBorderBoxWidth","getBorderBox",arguments,null,"width");
};
dojo.html.getBorderBoxHeight=dojo.html.getInnerHeight=function(){
return dojo.html._callDeprecated("getBorderBoxHeight","getBorderBox",arguments,null,"height");
};
dojo.html.getContentBoxWidth=dojo.html.getContentWidth=function(){
return dojo.html._callDeprecated("getContentBoxWidth","getContentBox",arguments,null,"width");
};
dojo.html.getContentBoxHeight=dojo.html.getContentHeight=function(){
return dojo.html._callDeprecated("getContentBoxHeight","getContentBox",arguments,null,"height");
};
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_67b){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_67d){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_67e){
return dojo.html.getDocumentWindow(_67e.ownerDocument);
};
dojo.html.getDocumentWindow=function(doc){
if(dojo.render.html.safari&&!doc._parentWindow){
var fix=function(win){
win.document._parentWindow=win;
for(var i=0;i<win.frames.length;i++){
fix(win.frames[i]);
}
};
fix(window.top);
}
if(dojo.render.html.ie&&window!==document.parentWindow&&!doc._parentWindow){
doc.parentWindow.execScript("document._parentWindow = window;","Javascript");
var win=doc._parentWindow;
doc._parentWindow=null;
return win;
}
return doc._parentWindow||doc.parentWindow||doc.defaultView;
};
dojo.html.gravity=function(node,e){
node=dojo.byId(node);
var _686=dojo.html.getCursorPosition(e);
with(dojo.html){
var _687=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _689=_687.x+(bb.width/2);
var _68a=_687.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_686.x<_689?WEST:EAST)|(_686.y<_68a?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_68b,e){
_68b=dojo.byId(_68b);
var _68d=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_68b);
var _68f=dojo.html.getAbsolutePosition(_68b,true,dojo.html.boxSizing.BORDER_BOX);
var top=_68f.y;
var _691=top+bb.height;
var left=_68f.x;
var _693=left+bb.width;
return (_68d.x>=left&&_68d.x<=_693&&_68d.y>=top&&_68d.y<=_691);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _695="";
if(node==null){
return _695;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _697="unknown";
try{
_697=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_697){
case "block":
case "list-item":
case "run-in":
case "table":
case "table-row-group":
case "table-header-group":
case "table-footer-group":
case "table-row":
case "table-column-group":
case "table-column":
case "table-cell":
case "table-caption":
_695+="\n";
_695+=dojo.html.renderedTextContent(node.childNodes[i]);
_695+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_695+="\n";
}else{
_695+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _699="unknown";
try{
_699=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_699){
case "capitalize":
var _69a=text.split(" ");
for(var i=0;i<_69a.length;i++){
_69a[i]=_69a[i].charAt(0).toUpperCase()+_69a[i].substring(1);
}
text=_69a.join(" ");
break;
case "uppercase":
text=text.toUpperCase();
break;
case "lowercase":
text=text.toLowerCase();
break;
default:
break;
}
switch(_699){
case "nowrap":
break;
case "pre-wrap":
break;
case "pre-line":
break;
case "pre":
break;
default:
text=text.replace(/\s+/," ");
if(/\s$/.test(_695)){
text.replace(/^\s/,"");
}
break;
}
_695+=text;
break;
default:
break;
}
}
return _695;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _69e="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_69e="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_69e="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_69e="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _69f=null;
switch(_69e){
case "cell":
_69f=tn.getElementsByTagName("tr")[0];
break;
case "row":
_69f=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_69f=tn.getElementsByTagName("table")[0];
break;
default:
_69f=tn;
break;
}
var _6a0=[];
for(var x=0;x<_69f.childNodes.length;x++){
_6a0.push(_69f.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.body().removeChild(tn);
return _6a0;
};
dojo.html.placeOnScreen=function(node,_6a3,_6a4,_6a5,_6a6,_6a7,_6a8){
if(_6a3 instanceof Array||typeof _6a3=="array"){
_6a8=_6a7;
_6a7=_6a6;
_6a6=_6a5;
_6a5=_6a4;
_6a4=_6a3[1];
_6a3=_6a3[0];
}
if(_6a7 instanceof String||typeof _6a7=="string"){
_6a7=_6a7.split(",");
}
if(!isNaN(_6a5)){
_6a5=[Number(_6a5),Number(_6a5)];
}else{
if(!(_6a5 instanceof Array||typeof _6a5=="array")){
_6a5=[0,0];
}
}
var _6a9=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _6ab=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_6ab;
if(!(_6a7 instanceof Array||typeof _6a7=="array")){
_6a7=["TL"];
}
var _6af,_6b0,_6b1=Infinity,_6b2;
for(var _6b3=0;_6b3<_6a7.length;++_6b3){
var _6b4=_6a7[_6b3];
var _6b5=true;
var tryX=_6a3-(_6b4.charAt(1)=="L"?0:w)+_6a5[0]*(_6b4.charAt(1)=="L"?1:-1);
var tryY=_6a4-(_6b4.charAt(0)=="T"?0:h)+_6a5[1]*(_6b4.charAt(0)=="T"?1:-1);
if(_6a6){
tryX-=_6a9.x;
tryY-=_6a9.y;
}
if(tryX<0){
tryX=0;
_6b5=false;
}
if(tryY<0){
tryY=0;
_6b5=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_6b5=false;
}else{
x=tryX;
}
x=Math.max(_6a5[0],x)+_6a9.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_6b5=false;
}else{
y=tryY;
}
y=Math.max(_6a5[1],y)+_6a9.y;
if(_6b5){
_6af=x;
_6b0=y;
_6b1=0;
_6b2=_6b4;
break;
}else{
var dist=Math.pow(x-tryX-_6a9.x,2)+Math.pow(y-tryY-_6a9.y,2);
if(_6b1>dist){
_6b1=dist;
_6af=x;
_6b0=y;
_6b2=_6b4;
}
}
}
if(!_6a8){
node.style.left=_6af+"px";
node.style.top=_6b0+"px";
}
return {left:_6af,top:_6b0,x:_6af,y:_6b0,dist:_6b1,corner:_6b2};
};
dojo.html.placeOnScreenPoint=function(node,_6bc,_6bd,_6be,_6bf){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_6bc,_6bd,_6be,_6bf,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_6c1,_6c2,_6c3,_6c4,_6c5){
var best,_6c7=Infinity;
_6c1=dojo.byId(_6c1);
var _6c8=_6c1.style.display;
_6c1.style.display="";
var mb=dojo.html.getElementBox(_6c1,_6c3);
var _6ca=mb.width;
var _6cb=mb.height;
var _6cc=dojo.html.getAbsolutePosition(_6c1,true,_6c3);
_6c1.style.display=_6c8;
for(var _6cd in _6c4){
var pos,_6cf,_6d0;
var _6d1=_6c4[_6cd];
_6cf=_6cc.x+(_6cd.charAt(1)=="L"?0:_6ca);
_6d0=_6cc.y+(_6cd.charAt(0)=="T"?0:_6cb);
pos=dojo.html.placeOnScreen(node,_6cf,_6d0,_6c2,true,_6d1,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_6c7>pos.dist){
_6c7=pos.dist;
best=pos;
}
}
}
if(!_6c5){
node.style.left=best.left+"px";
node.style.top=best.top+"px";
}
return best;
};
dojo.html.scrollIntoView=function(node){
if(!node){
return;
}
if(dojo.render.html.ie){
if(dojo.html.getBorderBox(node.parentNode).height<node.parentNode.scrollHeight){
node.scrollIntoView(false);
}
}else{
if(dojo.render.html.mozilla){
node.scrollIntoView(false);
}else{
var _6d3=node.parentNode;
var _6d4=_6d3.scrollTop+dojo.html.getBorderBox(_6d3).height;
var _6d5=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_6d4<_6d5){
_6d3.scrollTop+=(_6d5-_6d4);
}else{
if(_6d3.scrollTop>node.offsetTop){
_6d3.scrollTop-=(_6d3.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.gfx.color");
dojo.gfx.color.Color=function(r,g,b,a){
if(dojo.lang.isArray(r)){
this.r=r[0];
this.g=r[1];
this.b=r[2];
this.a=r[3]||1;
}else{
if(dojo.lang.isString(r)){
var rgb=dojo.gfx.color.extractRGB(r);
this.r=rgb[0];
this.g=rgb[1];
this.b=rgb[2];
this.a=g||1;
}else{
if(r instanceof dojo.gfx.color.Color){
this.r=r.r;
this.b=r.b;
this.g=r.g;
this.a=r.a;
}else{
this.r=r;
this.g=g;
this.b=b;
this.a=a;
}
}
}
};
dojo.gfx.color.Color.fromArray=function(arr){
return new dojo.gfx.color.Color(arr[0],arr[1],arr[2],arr[3]);
};
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_6dc){
if(_6dc){
return this.toRgba();
}else{
return [this.r,this.g,this.b];
}
},toRgba:function(){
return [this.r,this.g,this.b,this.a];
},toHex:function(){
return dojo.gfx.color.rgb2hex(this.toRgb());
},toCss:function(){
return "rgb("+this.toRgb().join()+")";
},toString:function(){
return this.toHex();
},blend:function(_6dd,_6de){
var rgb=null;
if(dojo.lang.isArray(_6dd)){
rgb=_6dd;
}else{
if(_6dd instanceof dojo.gfx.color.Color){
rgb=_6dd.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_6dd).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_6de);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_6e2){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_6e2);
}
if(!_6e2){
_6e2=0;
}
_6e2=Math.min(Math.max(-1,_6e2),1);
_6e2=((_6e2+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_6e2));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_6e7){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_6e7));
};
dojo.gfx.color.extractRGB=function(_6e8){
var hex="0123456789abcdef";
_6e8=_6e8.toLowerCase();
if(_6e8.indexOf("rgb")==0){
var _6ea=_6e8.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_6ea.splice(1,3);
return ret;
}else{
var _6ec=dojo.gfx.color.hex2rgb(_6e8);
if(_6ec){
return _6ec;
}else{
return dojo.gfx.color.named[_6e8]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _6ee="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_6ee+"]","g"),"")!=""){
return null;
}
if(hex.length==3){
rgb[0]=hex.charAt(0)+hex.charAt(0);
rgb[1]=hex.charAt(1)+hex.charAt(1);
rgb[2]=hex.charAt(2)+hex.charAt(2);
}else{
rgb[0]=hex.substring(0,2);
rgb[1]=hex.substring(2,4);
rgb[2]=hex.substring(4);
}
for(var i=0;i<rgb.length;i++){
rgb[i]=_6ee.indexOf(rgb[i].charAt(0))*16+_6ee.indexOf(rgb[i].charAt(1));
}
return rgb;
};
dojo.gfx.color.rgb2hex=function(r,g,b){
if(dojo.lang.isArray(r)){
g=r[1]||0;
b=r[2]||0;
r=r[0]||0;
}
var ret=dojo.lang.map([r,g,b],function(x){
x=new Number(x);
var s=x.toString(16);
while(s.length<2){
s="0"+s;
}
return s;
});
ret.unshift("#");
return ret.join("");
};
dojo.provide("dojo.lfx.Animation");
dojo.lfx.Line=function(_6f7,end){
this.start=_6f7;
this.end=end;
if(dojo.lang.isArray(_6f7)){
var diff=[];
dojo.lang.forEach(this.start,function(s,i){
diff[i]=this.end[i]-s;
},this);
this.getValue=function(n){
var res=[];
dojo.lang.forEach(this.start,function(s,i){
res[i]=(diff[i]*n)+s;
},this);
return res;
};
}else{
var diff=end-_6f7;
this.getValue=function(n){
return (diff*n)+this.start;
};
}
};
dojo.lfx.easeDefault=function(n){
if(dojo.render.html.khtml){
return (parseFloat("0.5")+((Math.sin((n+parseFloat("1.5"))*Math.PI))/2));
}else{
return (0.5+((Math.sin((n+1.5)*Math.PI))/2));
}
};
dojo.lfx.easeIn=function(n){
return Math.pow(n,3);
};
dojo.lfx.easeOut=function(n){
return (1-Math.pow(1-n,3));
};
dojo.lfx.easeInOut=function(n){
return ((3*Math.pow(n,2))-(2*Math.pow(n,3)));
};
dojo.lfx.IAnimation=function(){
};
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:25,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_706,_707){
if(!_707){
_707=_706;
_706=this;
}
_707=dojo.lang.hitch(_706,_707);
var _708=this[evt]||function(){
};
this[evt]=function(){
var ret=_708.apply(this,arguments);
_707.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_70c){
this.repeatCount=_70c;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_70d,_70e,_70f,_710,_711,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_70d)||(!_70d&&_70e.getValue)){
rate=_711;
_711=_710;
_710=_70f;
_70f=_70e;
_70e=_70d;
_70d=null;
}else{
if(_70d.getValue||dojo.lang.isArray(_70d)){
rate=_710;
_711=_70f;
_710=_70e;
_70f=_70d;
_70e=null;
_70d=null;
}
}
if(dojo.lang.isArray(_70f)){
this.curve=new dojo.lfx.Line(_70f[0],_70f[1]);
}else{
this.curve=_70f;
}
if(_70e!=null&&_70e>0){
this.duration=_70e;
}
if(_711){
this.repeatCount=_711;
}
if(rate){
this.rate=rate;
}
if(_70d){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_70d[item]){
this.connect(item,_70d[item]);
}
},this);
}
if(_710&&dojo.lang.isFunction(_710)){
this.easing=_710;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_714,_715){
if(_715){
clearTimeout(this._timer);
this._active=false;
this._paused=false;
this._percent=0;
}else{
if(this._active&&!this._paused){
return this;
}
}
this.fire("handler",["beforeBegin"]);
this.fire("beforeBegin");
if(_714>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_715);
}),_714);
return this;
}
this._startTime=new Date().valueOf();
if(this._paused){
this._startTime-=(this.duration*this._percent/100);
}
this._endTime=this._startTime+this.duration;
this._active=true;
this._paused=false;
var step=this._percent/100;
var _717=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_717]);
this.fire("onBegin",[_717]);
}
this.fire("handler",["play",_717]);
this.fire("onPlay",[_717]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _718=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_718]);
this.fire("onPause",[_718]);
return this;
},gotoPercent:function(pct,_71a){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_71a){
this.play();
}
return this;
},stop:function(_71b){
clearTimeout(this._timer);
var step=this._percent/100;
if(_71b){
step=1;
}
var _71d=this.curve.getValue(step);
this.fire("handler",["stop",_71d]);
this.fire("onStop",[_71d]);
this._active=false;
this._paused=false;
return this;
},status:function(){
if(this._active){
return this._paused?"paused":"playing";
}else{
return "stopped";
}
return this;
},_cycle:function(){
clearTimeout(this._timer);
if(this._active){
var curr=new Date().valueOf();
var step=(curr-this._startTime)/(this._endTime-this._startTime);
if(step>=1){
step=1;
this._percent=100;
}else{
this._percent=step*100;
}
if((this.easing)&&(dojo.lang.isFunction(this.easing))){
step=this.easing(step);
}
var _720=this.curve.getValue(step);
this.fire("handler",["animate",_720]);
this.fire("onAnimate",[_720]);
if(step<1){
this._timer=setTimeout(dojo.lang.hitch(this,"_cycle"),this.rate);
}else{
this._active=false;
this.fire("handler",["end"]);
this.fire("onEnd");
if(this.repeatCount>0){
this.repeatCount--;
this.play(null,true);
}else{
if(this.repeatCount==-1){
this.play(null,true);
}else{
if(this._startRepeatCount){
this.repeatCount=this._startRepeatCount;
this._startRepeatCount=0;
}
}
}
}
}
return this;
}});
dojo.lfx.Combine=function(_721){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _722=arguments;
if(_722.length==1&&(dojo.lang.isArray(_722[0])||dojo.lang.isArrayLike(_722[0]))){
_722=_722[0];
}
dojo.lang.forEach(_722,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_724,_725){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_724>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_725);
}),_724);
return this;
}
if(_725||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_725);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_726){
this.fire("onStop");
this._animsCall("stop",_726);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_727){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _72a=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_727](args);
},_72a);
return this;
}});
dojo.lfx.Chain=function(_72c){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _72d=arguments;
if(_72d.length==1&&(dojo.lang.isArray(_72d[0])||dojo.lang.isArrayLike(_72d[0]))){
_72d=_72d[0];
}
var _72e=this;
dojo.lang.forEach(_72d,function(anim,i,_731){
this._anims.push(anim);
if(i<_731.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_732,_733){
if(!this._anims.length){
return this;
}
if(_733||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _734=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_732>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_733);
}),_732);
return this;
}
if(_734){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_734.play(null,_733);
}
return this;
},pause:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].pause();
this.fire("onPause",[this._currAnim]);
}
return this;
},playPause:function(){
if(this._anims.length==0){
return this;
}
if(this._currAnim==-1){
this._currAnim=0;
}
var _735=this._anims[this._currAnim];
if(_735){
if(!_735._active||_735._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _736=this._anims[this._currAnim];
if(_736){
_736.stop();
this.fire("onStop",[this._currAnim]);
}
return _736;
},_playNext:function(){
if(this._currAnim==-1||this._anims.length==0){
return this;
}
this._currAnim++;
if(this._anims[this._currAnim]){
this._anims[this._currAnim].play(null,true);
}
return this;
}});
dojo.lfx.combine=function(_737){
var _738=arguments;
if(dojo.lang.isArray(arguments[0])){
_738=arguments[0];
}
if(_738.length==1){
return _738[0];
}
return new dojo.lfx.Combine(_738);
};
dojo.lfx.chain=function(_739){
var _73a=arguments;
if(dojo.lang.isArray(arguments[0])){
_73a=arguments[0];
}
if(_73a.length==1){
return _73a[0];
}
return new dojo.lfx.Chain(_73a);
};
dojo.provide("dojo.html.color");
dojo.html.getBackgroundColor=function(node){
node=dojo.byId(node);
var _73c;
do{
_73c=dojo.html.getStyle(node,"background-color");
if(_73c.toLowerCase()=="rgba(0, 0, 0, 0)"){
_73c="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_73c));
if(_73c=="transparent"){
_73c=[255,255,255,0];
}else{
_73c=dojo.gfx.color.extractRGB(_73c);
}
return _73c;
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_73d){
if(!_73d){
return [];
}
if(dojo.lang.isArrayLike(_73d)){
if(!_73d.alreadyChecked){
var n=[];
dojo.lang.forEach(_73d,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _73d;
}
}else{
var n=[];
n.push(dojo.byId(_73d));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_740,_741,_742,_743,_744){
_740=dojo.lfx.html._byId(_740);
var _745={"propertyMap":_741,"nodes":_740,"duration":_742,"easing":_743||dojo.lfx.easeDefault};
var _746=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _74a in pm){
pm[_74a].property=_74a;
parr.push(pm[_74a]);
}
pm=args.propertyMap=parr;
}
dojo.lang.forEach(pm,function(prop){
if(dj_undef("start",prop)){
if(prop.property!="opacity"){
prop.start=parseInt(dojo.html.getComputedStyle(args.nodes[0],prop.property));
}else{
prop.start=dojo.html.getOpacity(args.nodes[0]);
}
}
});
}
};
var _74c=function(_74d){
var _74e=[];
dojo.lang.forEach(_74d,function(c){
_74e.push(Math.round(c));
});
return _74e;
};
var _750=function(n,_752){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _752){
if(s=="opacity"){
dojo.html.setOpacity(n,_752[s]);
}else{
n.style[s]=_752[s];
}
}
};
var _754=function(_755){
this._properties=_755;
this.diffs=new Array(_755.length);
dojo.lang.forEach(_755,function(prop,i){
if(dojo.lang.isFunction(prop.start)){
prop.start=prop.start(prop,i);
}
if(dojo.lang.isFunction(prop.end)){
prop.end=prop.end(prop,i);
}
if(dojo.lang.isArray(prop.start)){
this.diffs[i]=null;
}else{
if(prop.start instanceof dojo.gfx.color.Color){
prop.startRgb=prop.start.toRgb();
prop.endRgb=prop.end.toRgb();
}else{
this.diffs[i]=prop.end-prop.start;
}
}
},this);
this.getValue=function(n){
var ret={};
dojo.lang.forEach(this._properties,function(prop,i){
var _75c=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_75c=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_75c+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_75c+=")";
}else{
_75c=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_75c;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_746(_745);
anim.curve=new _754(_745.propertyMap);
},onAnimate:function(_75f){
dojo.lang.forEach(_745.nodes,function(node){
_750(node,_75f);
});
}},_745.duration,null,_745.easing);
if(_744){
for(var x in _744){
if(dojo.lang.isFunction(_744[x])){
anim.connect(x,anim,_744[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_762){
var _763=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_762)){
dojo.lang.forEach(_762,_763);
}else{
_763(_762);
}
};
dojo.lfx.html.fade=function(_765,_766,_767,_768,_769){
_765=dojo.lfx.html._byId(_765);
var _76a={property:"opacity"};
if(!dj_undef("start",_766)){
_76a.start=_766.start;
}else{
_76a.start=function(){
return dojo.html.getOpacity(_765[0]);
};
}
if(!dj_undef("end",_766)){
_76a.end=_766.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_765,[_76a],_767,_768);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_765);
});
if(_769){
anim.connect("onEnd",function(){
_769(_765,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_76c,_76d,_76e,_76f){
return dojo.lfx.html.fade(_76c,{end:1},_76d,_76e,_76f);
};
dojo.lfx.html.fadeOut=function(_770,_771,_772,_773){
return dojo.lfx.html.fade(_770,{end:0},_771,_772,_773);
};
dojo.lfx.html.fadeShow=function(_774,_775,_776,_777){
_774=dojo.lfx.html._byId(_774);
dojo.lang.forEach(_774,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_774,_775,_776,_777);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_774)){
dojo.lang.forEach(_774,dojo.html.show);
}else{
dojo.html.show(_774);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_77a,_77b,_77c,_77d){
var anim=dojo.lfx.html.fadeOut(_77a,_77b,_77c,function(){
if(dojo.lang.isArrayLike(_77a)){
dojo.lang.forEach(_77a,dojo.html.hide);
}else{
dojo.html.hide(_77a);
}
if(_77d){
_77d(_77a,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_77f,_780,_781,_782){
_77f=dojo.lfx.html._byId(_77f);
var _783=[];
dojo.lang.forEach(_77f,function(node){
var _785={};
dojo.html.show(node);
var _786=dojo.html.getBorderBox(node).height;
dojo.html.hide(node);
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _786;
}}},_780,_781);
anim.connect("beforeBegin",function(){
_785.overflow=node.style.overflow;
_785.height=node.style.height;
with(node.style){
overflow="hidden";
_786="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_785.overflow;
_786=_785.height;
}
if(_782){
_782(node,anim);
}
});
_783.push(anim);
});
return dojo.lfx.combine(_783);
};
dojo.lfx.html.wipeOut=function(_788,_789,_78a,_78b){
_788=dojo.lfx.html._byId(_788);
var _78c=[];
dojo.lang.forEach(_788,function(node){
var _78e={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_789,_78a,{"beforeBegin":function(){
_78e.overflow=node.style.overflow;
_78e.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_78e.overflow;
height=_78e.height;
}
if(_78b){
_78b(node,anim);
}
}});
_78c.push(anim);
});
return dojo.lfx.combine(_78c);
};
dojo.lfx.html.slideTo=function(_790,_791,_792,_793,_794){
_790=dojo.lfx.html._byId(_790);
var _795=[];
var _796=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_791)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_791={top:_791[0],left:_791[1]};
}
dojo.lang.forEach(_790,function(node){
var top=null;
var left=null;
var init=(function(){
var _79b=node;
return function(){
var pos=_796(_79b,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_796(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_796(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_79b,true);
dojo.html.setStyleAttributes(_79b,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_791.top||0)},"left":{start:left,end:(_791.left||0)}},_792,_793,{"beforeBegin":init});
if(_794){
anim.connect("onEnd",function(){
_794(_790,anim);
});
}
_795.push(anim);
});
return dojo.lfx.combine(_795);
};
dojo.lfx.html.slideBy=function(_79f,_7a0,_7a1,_7a2,_7a3){
_79f=dojo.lfx.html._byId(_79f);
var _7a4=[];
var _7a5=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_7a0)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_7a0={top:_7a0[0],left:_7a0[1]};
}
dojo.lang.forEach(_79f,function(node){
var top=null;
var left=null;
var init=(function(){
var _7aa=node;
return function(){
var pos=_7a5(_7aa,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_7a5(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_7a5(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_7aa,true);
dojo.html.setStyleAttributes(_7aa,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_7a0.top||0)},"left":{start:left,end:left+(_7a0.left||0)}},_7a1,_7a2).connect("beforeBegin",init);
if(_7a3){
anim.connect("onEnd",function(){
_7a3(_79f,anim);
});
}
_7a4.push(anim);
});
return dojo.lfx.combine(_7a4);
};
dojo.lfx.html.explode=function(_7ae,_7af,_7b0,_7b1,_7b2){
var h=dojo.html;
_7ae=dojo.byId(_7ae);
_7af=dojo.byId(_7af);
var _7b4=h.toCoordinateObject(_7ae,true);
var _7b5=document.createElement("div");
h.copyStyle(_7b5,_7af);
if(_7af.explodeClassName){
_7b5.className=_7af.explodeClassName;
}
with(_7b5.style){
position="absolute";
display="none";
}
dojo.body().appendChild(_7b5);
with(_7af.style){
visibility="hidden";
display="block";
}
var _7b6=h.toCoordinateObject(_7af,true);
with(_7af.style){
display="none";
visibility="visible";
}
var _7b7={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_7b7[type]={start:_7b4[type],end:_7b6[type]};
});
var anim=new dojo.lfx.propertyAnimation(_7b5,_7b7,_7b0,_7b1,{"beforeBegin":function(){
h.setDisplay(_7b5,"block");
},"onEnd":function(){
h.setDisplay(_7af,"block");
_7b5.parentNode.removeChild(_7b5);
}});
if(_7b2){
anim.connect("onEnd",function(){
_7b2(_7af,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_7ba,end,_7bc,_7bd,_7be){
var h=dojo.html;
_7ba=dojo.byId(_7ba);
end=dojo.byId(end);
var _7c0=dojo.html.toCoordinateObject(_7ba,true);
var _7c1=dojo.html.toCoordinateObject(end,true);
var _7c2=document.createElement("div");
dojo.html.copyStyle(_7c2,_7ba);
if(_7ba.explodeClassName){
_7c2.className=_7ba.explodeClassName;
}
dojo.html.setOpacity(_7c2,0.3);
with(_7c2.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_7ba,"background-color").toLowerCase();
}
dojo.body().appendChild(_7c2);
var _7c3={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_7c3[type]={start:_7c0[type],end:_7c1[type]};
});
var anim=new dojo.lfx.propertyAnimation(_7c2,_7c3,_7bc,_7bd,{"beforeBegin":function(){
dojo.html.hide(_7ba);
dojo.html.show(_7c2);
},"onEnd":function(){
_7c2.parentNode.removeChild(_7c2);
}});
if(_7be){
anim.connect("onEnd",function(){
_7be(_7ba,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_7c6,_7c7,_7c8,_7c9,_7ca){
_7c6=dojo.lfx.html._byId(_7c6);
var _7cb=[];
dojo.lang.forEach(_7c6,function(node){
var _7cd=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _7cf=dojo.html.getStyle(node,"background-image");
var _7d0=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_7cd.length>3){
_7cd.pop();
}
var rgb=new dojo.gfx.color.Color(_7c7);
var _7d2=new dojo.gfx.color.Color(_7cd);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_7d2}},_7c8,_7c9,{"beforeBegin":function(){
if(_7cf){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_7cf){
node.style.backgroundImage=_7cf;
}
if(_7d0){
node.style.backgroundColor="transparent";
}
if(_7ca){
_7ca(node,anim);
}
}});
_7cb.push(anim);
});
return dojo.lfx.combine(_7cb);
};
dojo.lfx.html.unhighlight=function(_7d4,_7d5,_7d6,_7d7,_7d8){
_7d4=dojo.lfx.html._byId(_7d4);
var _7d9=[];
dojo.lang.forEach(_7d4,function(node){
var _7db=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_7d5);
var _7dd=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_7db,end:rgb}},_7d6,_7d7,{"beforeBegin":function(){
if(_7dd){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_7db.toRgb().join(",")+")";
},"onEnd":function(){
if(_7d8){
_7d8(node,anim);
}
}});
_7d9.push(anim);
});
return dojo.lfx.combine(_7d9);
};
dojo.lang.mixin(dojo.lfx,dojo.lfx.html);
dojo.provide("dojo.lfx.*");
dojo.provide("dojo.lfx.toggle");
dojo.lfx.toggle.plain={show:function(node,_7e0,_7e1,_7e2){
dojo.html.show(node);
if(dojo.lang.isFunction(_7e2)){
_7e2();
}
},hide:function(node,_7e4,_7e5,_7e6){
dojo.html.hide(node);
if(dojo.lang.isFunction(_7e6)){
_7e6();
}
}};
dojo.lfx.toggle.fade={show:function(node,_7e8,_7e9,_7ea){
dojo.lfx.fadeShow(node,_7e8,_7e9,_7ea).play();
},hide:function(node,_7ec,_7ed,_7ee){
dojo.lfx.fadeHide(node,_7ec,_7ed,_7ee).play();
}};
dojo.lfx.toggle.wipe={show:function(node,_7f0,_7f1,_7f2){
dojo.lfx.wipeIn(node,_7f0,_7f1,_7f2).play();
},hide:function(node,_7f4,_7f5,_7f6){
dojo.lfx.wipeOut(node,_7f4,_7f5,_7f6).play();
}};
dojo.lfx.toggle.explode={show:function(node,_7f8,_7f9,_7fa,_7fb){
dojo.lfx.explode(_7fb||{x:0,y:0,width:0,height:0},node,_7f8,_7f9,_7fa).play();
},hide:function(node,_7fd,_7fe,_7ff,_800){
dojo.lfx.implode(node,_800||{x:0,y:0,width:0,height:0},_7fd,_7fe,_7ff).play();
}};
dojo.provide("dojo.widget.HtmlWidget");
dojo.declare("dojo.widget.HtmlWidget",dojo.widget.DomWidget,{widgetType:"HtmlWidget",templateCssPath:null,templatePath:null,lang:"",toggle:"plain",toggleDuration:150,animationInProgress:false,initialize:function(args,frag){
},postMixInProperties:function(args,frag){
if(this.lang===""){
this.lang=null;
}
this.toggleObj=dojo.lfx.toggle[this.toggle.toLowerCase()]||dojo.lfx.toggle.plain;
},getContainerHeight:function(){
dojo.unimplemented("dojo.widget.HtmlWidget.getContainerHeight");
},getContainerWidth:function(){
return this.parent.domNode.offsetWidth;
},setNativeHeight:function(_805){
var ch=this.getContainerHeight();
},createNodesFromText:function(txt,wrap){
return dojo.html.createNodesFromText(txt,wrap);
},destroyRendering:function(_809){
try{
if(!_809&&this.domNode){
dojo.event.browser.clean(this.domNode);
}
this.domNode.parentNode.removeChild(this.domNode);
delete this.domNode;
}
catch(e){
}
},isShowing:function(){
return dojo.html.isShowing(this.domNode);
},toggleShowing:function(){
if(this.isHidden){
this.show();
}else{
this.hide();
}
},show:function(){
this.animationInProgress=true;
this.isHidden=false;
this.toggleObj.show(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onShow),this.explodeSrc);
},onShow:function(){
this.animationInProgress=false;
this.checkSize();
},hide:function(){
this.animationInProgress=true;
this.isHidden=true;
this.toggleObj.hide(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onHide),this.explodeSrc);
},onHide:function(){
this.animationInProgress=false;
},_isResized:function(w,h){
if(!this.isShowing()){
return false;
}
var wh=dojo.html.getMarginBox(this.domNode);
var _80d=w||wh.width;
var _80e=h||wh.height;
if(this.width==_80d&&this.height==_80e){
return false;
}
this.width=_80d;
this.height=_80e;
return true;
},checkSize:function(){
if(!this._isResized()){
return;
}
this.onResized();
},resizeTo:function(w,h){
dojo.html.setMarginBox(this.domNode,{width:w,height:h});
if(this.isShowing()){
this.onResized();
}
},resizeSoon:function(){
if(this.isShowing()){
dojo.lang.setTimeout(this,this.onResized,0);
}
},onResized:function(){
dojo.lang.forEach(this.children,function(_811){
if(_811.checkSize){
_811.checkSize();
}
});
}});
dojo.provide("dojo.widget.*");

