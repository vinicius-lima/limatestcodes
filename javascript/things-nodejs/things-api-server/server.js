// Code from: https://stormpath.com/blog/build-api-restify-stormpath

var restify = require('restify');
var host = process.env.HOST || '127.0.0.1';
var port = process.env.PORT || '8080';

var stormpathRestify = require('stormpath-restify');
var stormpathFilters = stormpathRestify.createFilterSet();

var oauthFilter = stormpathFilters.createOauthFilter();
var trustedFilter = stormpathFilters.createGroupFilter({
    inGroup: 'trusted'
});

var thingDatabse = require('./things-db');
var db = thingDatabse({
    baseHref: 'http://' + host + ( port ? (':'+ port): '' ) + '/things/'
});

var server = restify.createServer({
    name: 'Things API Server'
});

server.use(restify.queryParser());
server.use(restify.bodyParser());

server.use(restify.CORS());
server.opts(/.*/, function (req,res,next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Methods", req.header("Access-Control-Request-Method"));
    res.header("Access-Control-Allow-Headers", req.header("Access-Control-Request-Headers"));
    res.send(200);
    return next();
});
server.use(restify.fullResponse());

server.use(function logger(req,res,next) {
    console.log(new Date(),req.method,req.url);
    next();
});

server.post('/oauth/token', oauthFilter);

server.get('/things',function(req,res){
    res.json(db.all());
});

server.get('/things/:id',function(req,res,next){
    var id = req.params.id;
    var thing = db.getThingById(id);
    if(!thing) {
        next(new restify.errors.ResourceNotFoundError());
    }
    else {
        res.json(thing);
    }
});

server.get('/me',[oauthFilter,function(req,res){
    res.json(req.account);
}]);

server.post('/things', [oauthFilter, function(req,res){
    res.json(db.createThing(req.body));
}]);

server.del('/things/:id',[oauthFilter,trustedFilter,function(req,res,next){
    var id = req.params.id;
    var thing = db.getThingById(id);
    if(!thing) {
        next(new restify.errors.ResourceNotFoundError());
    }
    else {
        db.deleteThingById(id);
        res.send(204);
    }
}]);

server.on('uncaughtException',function(request, response, route, error){
    console.error(error.stack);
    response.send(error);
});

server.listen(port,host, function() {
    console.log('%s listening at %s', server.name, server.url);
});
