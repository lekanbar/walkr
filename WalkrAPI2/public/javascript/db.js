
var mysql = require('mysql');

exports.db = (function(){
    var out = {};
    var connection = mysql.createConnection({
        user: 'root',
        password: 'mysqlPassword',
        database: 'walkrdb'
    }); 

    connection.connect(function(err){
        if(err){
            throw err;
        }
    });

    //=====QUERIES=======

   out.getUserByID = function(id, cb){
        var sql = "select id, display_name from user where id = ?";
        connection.query(sql, id, function(err, res){
            console.log(res);
            cb(err, res);
        });
   };

   //{GeoLocationString: "-3.423542134, 5.7453543", StartIndex : 23}
   out.getPostsByLocation = function(data, cb){
       console.log(data);
       var startIndex = data.StartIndex,
           location = data.GeoLocationString.replace(/ /g,''),
           coordinates = location.split(','),
           latitude = connection.escape(coordinates[0]).replace(/'/g, ""),
           longitude = connection.escape(coordinates[1]).replace(/'/g, "");
            
       var sql = "SELECT p.id, p.text, p.date_time, p.geolocation, u.id as user, u.display_name, (GLength(LineStringFromWKB(LineString(geoLocation, GeomFromText('POINT(" + latitude + " " + longitude + ")'))))) * 100000 AS distance FROM post p INNER JOIN user u ON u.id = p.user HAVING distance <= 100 AND id >= ?";
       console.log(sql);
       connection.query(sql, startIndex, function(err, res){
           cb(err, res);
       });
   };

   //======COMMANDS=======

   //data = {DisplayName: 'username'}
   out.insertUser = function(data, cb){
       
       var dn = data.DisplayName;
       var sql = "INSERT INTO user (id, display_name, registration_date) SELECT * FROM (SELECT 0, ?, NOW()) AS tmp WHERE NOT EXISTS (SELECT display_name FROM user WHERE display_name = ?) LIMIT 1;";
       console.log(sql);
       connection.query(sql, [dn, dn], function(err, res){
           console.log(err);
            cb(err, res);
       });
   };

   //{"UserID" : 1, "GeoLocationSring": "sample location", "Text" : "Sample text"}
   out.insertPost = function(data, cb){
       var id = data.UserID,
           location = data.GeoLocationString,
           text = data.Text,
           location = location.replace(/ /g,''), //remove all whitespace
           coordinates = location.split(','),
           latitude = connection.escape(coordinates[0]).replace(/'/g, ""),
           longitude = connection.escape(coordinates[1]).replace(/'/g, "");

       var sql = "INSERT INTO post (id, text, date_time, geolocation, user) SELECT * FROM (SELECT 0, ?, NOW(), GeomFromText('POINT(" + latitude + " " + longitude + ")'), ?) AS tmp WHERE EXISTS (SELECT id FROM user WHERE id = ?) LIMIT 1;";
       connection.query(sql, [text, id, id], function(err, res){
           console.log(err);
           cb(err, res);
       });
   };

   return out;
}());
