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

	app.post('/voter',function(req,res){
		console.log("post request");
		var mdata;
		console.log("Got post request.....");
		req.on('data',function(chunk){
			mdata+=chunk;
			console.log("read data ....");
			mdata = mdata.substring(9,mdata.length);
			var json = JSON.parse(mdata);
			console.log(json);
			res.end();
			dbdata.collection('Candidate').find({name:json.selection}).toArray(function(err,dat){
				for(var i=0;i<dat.length;i++){
					console.log(dat[i].name);
				}
				var count = dat[0].voteCount;
				console.log(count);
				count++;
				dbdata.collection('Candidate').update({name:dat[0].name},{$set :{ voteCount:count }});
				console.log("update successful...");
			});

		});
		res.send("hello");
	});


	app.get('/vote',function(req,res){
		//console.log("vote get request.....")
		var str;
		dbdata.collection('Candidate').find({},{},{}).toArray(function(err,doc){
			res.send(doc);
		});
	});

	//Handles login for user
	var resData='';
	app.post('/login',function(req,res){
		//res.send("321");
		//console.log(req.body);
		req.on('data',function(chunk){
			//collects the data returned from user
			resData+=chunk;
			//console.log("-"+chunk);
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