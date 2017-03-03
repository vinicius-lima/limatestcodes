// Here we use a local, relative require path to require your
// client library. When you publish on NPM you should change
// it to the absolute module name

// Note: in practice, the developer should supply their API
// Key and Secret through environment variables, and not
// hard-code them here.

var thingsApi = require('../things-api');

var prettyjson = require('prettyjson');

var client = thingsApi.createClient({
    key:'1855NOAMBJ4TU3FYDQ6IM0M2E',
    secret:'CuVDWJT++PPgp2TY92VqNX7/wTWxS3mR+rzySggG3yA'
});

client.getCurrentUser(function(err,user) {
    if(err) {
        console.error(err);
    }
    else {
        console.log('Who am I?');
        console.log(user.fullName + ' (' + user.email + ')');
    }
});

// Read all the things in the collection
client.getThings(function(err,things) {
    if(err) {
        console.error(err);
    }
    else {
        console.log('Things collection has these items:');
        console.log(prettyjson.render(things));
    }
});

// Create a new thing in the collection
client.addThing(
    {
        myNameIs: 'what?'
    },
    function(err,thing) {
        if(err) {
            console.error(err);
        }
        else {
            console.log('New thing created:');
            console.log(prettyjson.render(thing));

            // Delete the new thing that was just added.
            client.deleteThing(thing,function(err) {
                if(err) {
                    console.error(err);
                }
                else {
                console.log('Thing was deleted');
                }
            });
        }
    }
);
