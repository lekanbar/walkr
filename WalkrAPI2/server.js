
var express = require('express.io'),
    http = require('http'),
    app = express(),
    db = require('./public/javascript/db').db;

// all environments
app.set('port', process.env.PORT || 3000);
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());

/*app.use(express.cookieParser());
app.use(express.session({secret: 'express.io makes me happy'}));*/

//initiate socket.io layer
app.http().io();

var io = app.io;

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

io.sockets.on('connection', function (socket) {

  socket.on('registerLocation', function (data) {
    //on start-up users register location and update via this method
    //{userid: 1, geolocation:1.1231434321, -5.23414123}
  });

  socket.on('newPost', function (data) {
    //when a new post is sent
    //{userID:4, postID}

    socket.emit('newMessageToClient', data);
  });
});

app.get("/", function(req, res){
    res.send("Welcome to node");
});

app.get('/api/users', function(req, res){
    var id = req.query.id;
    db.getUserByID(id, function(err, data){
        console.log(data);
        res.send(data);
    });
});

app.post('/api/users', function(req, res){
    var user = req.body,
        user = user.DisplayName; //TODO: check for error here

    db.insertUser(req.body, function(err, result){
        if(err){
            console.log("encountered error");
            res.send("encountered error");
        }
        else{
            console.log("user added!");
            res.send("user added!");
        }
    });
});

app.get('/api/posts', function(req, res){
    var obj = {
        'GeoLocationString': req.query.GeoLocationString, 
        'StartIndex': req.query.StartIndex
    };
    db.getPostsByLocation(obj, function(err, results){
        if(err){
            console.log(err);
            res.send("error occured");
        }
        else{
            res.send(results);
        }
    });
});

app.post('/api/posts', function(req, res){
    var obj = req.body;
    db.insertPost(obj, function(err, result){
        if(err){
            res.send("an error occured");
        }
        else{
            res.send("inserted");
        }
    });
});

/*http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});*/

app.listen(app.get('port'));
console.log('Express server listening on port ' + app.get('port'));