var oauthClient = require('stormpath-restify/oauth-client');

module.exports = {
    createClient: function(opts) {
        opts.url = opts.url || 'http://127.0.0.1:8080';

        // This creates an instance of the oauth client,
        // which will handle all HTTP communication with your API

        var myOauthClient = oauthClient.createClient(opts);

        // Here we directly bind to the underlying GET method,
        // as this is a simple request

        myOauthClient.getThings = myOauthClient.get.bind(myOauthClient,'/things');

        myOauthClient.getCurrentUser = myOauthClient.get.bind(myOauthClient,'/me');

        myOauthClient.addThing = function addThing(thing,cb) {
            if(typeof thing!=='object') {
                process.nextTick(function() {
                    cb(new Error('Things must be an object'));
                });
            }
            else {
                myOauthClient.post('/things',thing,cb);
            }
        };

        myOauthClient.deleteThing = function deleteThing(thing,cb) {
            if(typeof thing!=='object') {
                process.nextTick(function() {
                    cb(new Error('Things must be an object'));
                });
            }
            if(typeof thing.href!=='string') {
                process.nextTick(function() {
                 cb(new Error('Missing property: href'));
                });
            }
            myOauthClient.del(thing.href,function(err) {
                if(err) {
                    cb(err); // If the API errors, just pass that along
                }
                else {
                    // Here you could do something custom before
                    // calling back to the original callback
                    cb();
                }
            });
        };

        return myOauthClient;
    }
};
