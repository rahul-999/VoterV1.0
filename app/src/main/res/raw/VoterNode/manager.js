var express = require('express');
var app  = express();
var db = require('mongodb').MongoClient, assert = require('assert');

var conn = 'mongodb://localhost:27017/node-android';
var manager = module.exports = function(){
db.connect(conn,function(err,dbdata){
	
	assert.equal(null,err);
	//All communication are implemented below...
	//console.log("databse connection successfull...");
	app.get('/insertTemp',function(){
		dbdata.collection('User').insert({username:"ravi",password:"ravi"});
		dbdata.close();
	});

	var resData='';
	app.post('/login',function(req,res){
		//res.send("321");
		//console.log(req.body);
		req.on('data',function(chunk){
			//collects the data returned from user
			resData+=chunk;
			console.log("-"+chunk);
			var myData = JSON.parse(resData);
			resData='';
			console.log(myData.email);
			//got the json package, now verify the credentials and rewponse appropriately
			dbdata.collection('User').find({username:myData.email},{}).toArray(function(err,docs){
				assert.equal(null,err);
				if(docs.length>0)
					console.log(docs[0].password);
				if(myData.password == docs[0].password){
					resJson = "{ status: true }";
				}else{
					resJson = "{ status: false }";
				}
				res.send(resJson);
			});
		});
		req.on('end',function(){
			
			//console.log("response ended...")
		});

	});
});

}


//create server
app.listen(5657,function(){
	console.log("manager is running at : 5657");
});



manager();