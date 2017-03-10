var express = require('express');
var app = express();
var ip = require('ip');
var manager = require('./manager.js')

//sending broadcast to androids
var dgram = require('dgram');
var server = dgram.createSocket('udp4');

manager();


server.bind(function(){
	server.setBroadcast(true);
	var timer = setInterval(sendemessage,3000);
});
server.on('error',function(){
	console.log("Can't send broadcast, Something went wrong...");
	server.close();
});
function sendemessage(){
	var myip = ip.address();
	var mask = myip.split('.');
	server.send("hello",0,5,2566,mask[0]+"."+mask[1]+"."+mask[2]+".255",function(){
		//console.log("message sent");
	});
}
server.on('listening',function(){
	console.log("server is active for android");
	
});



app.listen(5565, function () {
  console.log('Server is running on  http://localhost:5565');
});

app.get("/",function(req,res){
	res.setHeader('content-type','text/html')
	res.write("hello world");
});
